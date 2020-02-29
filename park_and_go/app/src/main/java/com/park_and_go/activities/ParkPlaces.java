package com.park_and_go.activities;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.park_and_go.MapsActivity;
import com.park_and_go.R;
import com.park_and_go.adapters.MyAdapter;
import com.park_and_go.common.DataMadrid;
import com.park_and_go.common.Favorito;
import com.park_and_go.common.PlacesResponse;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.park_and_go.assets.Constants.ADD_FAV;
import static com.park_and_go.assets.Constants.ALL_ITEMS;
import static com.park_and_go.assets.Constants.DISTANCIA;
import static com.park_and_go.assets.Constants.LATITUDE;
import static com.park_and_go.assets.Constants.LOCATION;
import static com.park_and_go.assets.Constants.LONGITUDE;
import static com.park_and_go.assets.Constants.OPTION;
import static com.park_and_go.assets.Constants.PARKING;
import static com.park_and_go.assets.Constants.PLACES;
import static com.park_and_go.assets.Constants.TITLE;

public class ParkPlaces extends AppCompatActivity{

    private final String TAG = getClass().getSimpleName();
    private ArrayList<PlacesResponse.Places> mPlaces;
    private MyAdapter mAdapter = null;
    private Location mCurrentLocation;
    private ListView lv = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_park_places);
        Log.d(TAG, "En el onCreate de park places");

        Intent location = getIntent();

        mCurrentLocation = location.getParcelableExtra(LOCATION);

        getParks(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude());

        lv = (ListView) findViewById(R.id.listview_parks);
        lv.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.add(0, 1, 0, ADD_FAV);
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                boolean option;
                Intent intent = new Intent(ParkPlaces.this, MapsActivity.class);
                if (i == 0) {
                    option = true;
                    intent.putExtra(OPTION, option);
                    intent.putParcelableArrayListExtra(ALL_ITEMS, mPlaces);
                    startActivity(intent);
                } else if (i > 0) {
                    option = false;
                    Log.d(TAG, "Intent  MapsActivity: " + mPlaces.get(i).location.latitude + ", " + mPlaces.get(i).location.longitude);
                    intent.putExtra(PLACES,mPlaces.get(i));
                    intent.putExtra(OPTION, option);
                    startActivity(intent);
                }
            }

        });
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        if (item.getItemId() == 1) {
            PlacesResponse.Places p = mPlaces.get(info.position);
            Favorito.writeFav(getFilesDir() + "/fav.json", p, PARKING);
            mAdapter.notifyDataSetChanged();
            Toast.makeText(ParkPlaces.this, "Añadido correctamente a favoritos", Toast.LENGTH_SHORT).show();
        }
        return true;
    }



    public void getParks(double latitude, double longitude) {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();

        Retrofit retrofit =
                new Retrofit.Builder()
                        .baseUrl("https://datos.madrid.es/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(client)
                        .build();

        DataMadrid dm = retrofit.create(DataMadrid.class);

        Log.d(TAG, "En getPlaces");

        dm.getPlaces(latitude, longitude, 1000).enqueue(new Callback<PlacesResponse>() {
            @Override
            public void onResponse(Call<PlacesResponse> call, Response<PlacesResponse> response) {

                mPlaces = response.body().graph;

                Log.d(TAG, "Valor de response code " + String.valueOf(response.code()));
                if (response.body() != null && !mPlaces.isEmpty()) {
                    for (int i = 0; i < mPlaces.size(); i++) {
                        Location location = new Location("");
                        location.setLatitude(mPlaces.get(i).location.latitude);
                        location.setLongitude(mPlaces.get(i).location.longitude);
                        float distance = mCurrentLocation.distanceTo(location);
                        mPlaces.get(i).distance = distance;
                        mPlaces.get(i).setTipo(PARKING);
                    }
                    mAdapter = new MyAdapter(ParkPlaces.this, R.layout.list_places, mPlaces);
                    lv.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                } else {
                    Log.d(TAG, "Response: " + response.body().graph.size());
                }
            }

            @Override
            public void onFailure(Call<PlacesResponse> call, Throwable t) {
                Log.e(TAG, "Response: empty array");
            }
        });
    }
}
