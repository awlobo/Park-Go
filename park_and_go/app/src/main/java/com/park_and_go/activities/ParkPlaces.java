package com.park_and_go.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.park_and_go.MapsActivity;
import com.park_and_go.R;
import com.park_and_go.adapters.MyAdapter;
import com.park_and_go.common.DataMadrid;
import com.park_and_go.common.PlacesResponse;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.park_and_go.activities.FavoritosPlaces.mFavsPlaces;
import static com.park_and_go.assets.Constants.ALL_ITEMS;
import static com.park_and_go.assets.Constants.BASE_URL;
import static com.park_and_go.assets.Constants.LOCATION;
import static com.park_and_go.assets.Constants.OPTION;
import static com.park_and_go.assets.Constants.PARKING;
import static com.park_and_go.assets.Constants.PLACES;
import static com.park_and_go.assets.Constants.SERVER_DOWN;
import static com.park_and_go.assets.Constants.URL_FAV;
import static com.park_and_go.assets.Constants.VACIO;

public class ParkPlaces extends AppCompatActivity {

    private ArrayList<PlacesResponse.Places> mPlaces;
    private MyAdapter mAdapter = null;
    private Location mCurrentLocation;
    private ListView lv = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_park_places);

        Intent location = getIntent();

        mCurrentLocation = location.getParcelableExtra(LOCATION);


        if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(ParkPlaces.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(ParkPlaces.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            checkGps();
        }


        lv = (ListView) findViewById(R.id.listview_parks);
        lv.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.add(0, 1, 0, R.string.add_fav);
                menu.add(0, 2, 1, R.string.mostrar_todo);
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                Intent intent = new Intent(ParkPlaces.this, MapsActivity.class);
                intent.putExtra(PLACES, mPlaces.get(i));
                intent.putExtra(OPTION, false);
                intent.putExtra(LOCATION, mCurrentLocation);
                startActivity(intent);
            }

        });
    }

    public void checkGps() {
        if (mCurrentLocation != null) {
            ProgressDialog pd = new ProgressDialog(ParkPlaces.this);
            pd.setMessage(getString(R.string.cargando));
            pd.show();
            getParks(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            pd.dismiss();

        } else {
            Toast.makeText(this, R.string.no_gps, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), R.string.gps_granted, Toast.LENGTH_SHORT).show();
                checkGps();
            } else {
                Toast.makeText(getApplicationContext(), R.string.gps_denied, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        if (item.getItemId() == 1) {
            PlacesResponse.Places p = mPlaces.get(info.position);
            p.setFavorito(true);
            addFavoritos(p);
        } else if (item.getItemId() == 2) {
            Intent intent = new Intent(this, MapsActivity.class);
            intent.putExtra(OPTION, true);
            intent.putParcelableArrayListExtra(ALL_ITEMS, mPlaces);
            intent.putExtra(LOCATION, mCurrentLocation);
            startActivity(intent);
        }
        return true;
    }

    private void addFavoritos(PlacesResponse.Places p) {
        boolean fav = false;
        for (PlacesResponse.Places f : mFavsPlaces) {
            if (p.title.equals(f.title)) {
                fav = true;
                break;
            }
        }
        if (!fav) {
            FavoritosPlaces.writeFav(getFilesDir() + URL_FAV, p, PARKING);
            mAdapter.notifyDataSetChanged();
            Toast.makeText(ParkPlaces.this, getString(R.string.fav_correcto), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(ParkPlaces.this, R.string.ya_fav, Toast.LENGTH_SHORT).show();
        }
    }


    public void getParks(double latitude, double longitude) {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();

        Retrofit retrofit =
                new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(client)
                        .build();

        DataMadrid dm = retrofit.create(DataMadrid.class);

        dm.getPlaces(latitude, longitude, 1000).enqueue(new Callback<PlacesResponse>() {
            @Override
            public void onResponse(Call<PlacesResponse> call, Response<PlacesResponse> response) {

                mPlaces = response.body().graph;

                if (response.body() != null && !mPlaces.isEmpty()) {
                    for (int i = 0; i < mPlaces.size(); i++) {
                        Location location = new Location(VACIO);
                        location.setLatitude(mPlaces.get(i).location.latitude);
                        location.setLongitude(mPlaces.get(i).location.longitude);
                        float distance = mCurrentLocation.distanceTo(location);
                        mPlaces.get(i).distance = distance;
                        mPlaces.get(i).setTipo(PARKING);
                        for (PlacesResponse.Places f : mFavsPlaces) {
                            if (mPlaces.get(i).title.equals(f.title)) {
                                mPlaces.get(i).setFavorito(true);
                            }
                        }
                    }

                    PlacesResponse.Places.ordenarDistancia(mPlaces);
                    mAdapter = new MyAdapter(ParkPlaces.this, R.layout.list_places, mPlaces);
                    lv.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<PlacesResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), SERVER_DOWN, Toast.LENGTH_LONG).show();
            }
        });
    }
}
