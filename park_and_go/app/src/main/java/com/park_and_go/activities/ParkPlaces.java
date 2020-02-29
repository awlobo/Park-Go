package com.park_and_go.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.park_and_go.assets.Constants;
import com.park_and_go.common.DataMadrid;
import com.park_and_go.MapsActivity;
import com.park_and_go.adapters.MyAdapter;
import com.park_and_go.R;
import com.park_and_go.common.PlacesResponse;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.park_and_go.assets.Constants.ARRAYLIST;
import static com.park_and_go.assets.Constants.OPTION;
import static com.park_and_go.assets.Constants.PARKING;
import static com.park_and_go.assets.Constants.URL_FAV;

public class ParkPlaces extends AppCompatActivity implements LocationListener {

    private final String TAG = getClass().getSimpleName();
    private final String LATITUDE = "LATITUDE";
    private final String LONGITUDE = "LONGITUD";
    private final String TITLE = "TITLE";
    private static final Integer PERMIS_GPS_FINE = 1;
    private LocationManager mLocManager;
    private ArrayList<PlacesResponse.Places> mPlaces;
    private Location mCurrentLocation;
    private MyAdapter mAdapter = null;
    private ListView lv = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_park_places);
        Log.d(TAG, "En el onCreate de park places");

        lv = findViewById(R.id.listview_parks);
        lv.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.add(0, 1, 0, Constants.ADD_FAV);
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
                    intent.putParcelableArrayListExtra(ARRAYLIST, mPlaces);
                    startActivityForResult(intent, 10);
                } else if (i > 0) {
                    option = false;
                    Log.d(TAG, "Intent  MapsActivity: " + mPlaces.get(i).location.latitude + ", " + mPlaces.get(i).location.longitude);
                    intent.putExtra(LATITUDE, mPlaces.get(i).location.latitude);
                    intent.putExtra(LONGITUDE, mPlaces.get(i).location.longitude);
                    intent.putExtra(TITLE, mPlaces.get(i).title);
                    intent.putExtra("OPTION", option);
                    startActivityForResult(intent, 20);
                }
            }
        });
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        if (item.getItemId() == 1) {
            PlacesResponse.Places p = mPlaces.get(info.position);
            FavoritosPlaces.writeFav(getFilesDir() + URL_FAV, p, Constants.PARKING);
            mAdapter.notifyDataSetChanged();
            Toast.makeText(ParkPlaces.this, getString(R.string.fav_correcto), Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "New location: " + location.getLatitude() + "-" + location.getLongitude() + ", " + location.getAltitude());
        mCurrentLocation = location;
        Log.d(TAG, "En el onLocationChange: " + location.getLatitude() + ", " + location.getLongitude());
        getParks(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d(TAG, "En el onProviderDisabled");
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(ParkPlaces.this, Manifest.permission.ACCESS_FINE_LOCATION)) {

            ActivityCompat.requestPermissions(ParkPlaces.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMIS_GPS_FINE);
            Log.d(TAG, "En el onStart , start location");
        } else {

            Toast.makeText(getApplicationContext(), "[LOCATION] Permission granted in the past!", Toast.LENGTH_SHORT).show();
            startLocation();
        }
    }

    @SuppressWarnings({"MissingPermission"})
    private void startLocation() {
        mLocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!mLocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Intent callGPSSettingIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(callGPSSettingIntent);

        } else {
            mLocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 300, this);
            //mCurrentLocation = mLocManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "[LOCATION] Permission granted!", Toast.LENGTH_SHORT).show();
                startLocation();
            } else {
                Toast.makeText(getApplicationContext(), "[LOCATION] Permission denied!", Toast.LENGTH_SHORT).show();
            }
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
                        .baseUrl("https://datos.madrid.es/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(client)
                        .build();

        DataMadrid dm = retrofit.create(DataMadrid.class);

        Log.d(TAG, "En getPlaces");

        dm.getPlaces(latitude, longitude, 1000).enqueue(new Callback<PlacesResponse>() {
            @Override
            public void onResponse(Call<PlacesResponse> call, Response<PlacesResponse> response) {

                int code = 1;

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
                    mAdapter = new MyAdapter(ParkPlaces.this, R.layout.list_places, mPlaces, code);
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
