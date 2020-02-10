package com.park_and_go;

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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ParkPlaces extends AppCompatActivity implements LocationListener {

    private final String TAG = getClass().getSimpleName();
    private final String LATITUDE = "LATITUDE";
    private final String LONGITUDE = "LONGITUD";
    private static final Integer PERMIS_GPS_FINE = 1;
    private LocationManager mLocManager;
    private List<PlacesResponse.Places> mPlaces;
    private Location mCurrentLocation;
    private MyAdapter mAdapter=null;
    private ListView lv=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_park_places);
        Log.d(TAG, "En el onCreate de park places");

        lv = (ListView) findViewById(R.id.listview_parks);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                Intent intent = new Intent(ParkPlaces.this,MapsActivity.class);
                intent.putExtra(LATITUDE,mPlaces.get(i).location.latitude);
                intent.putExtra(LONGITUDE,mPlaces.get(i).location.longitude);
                startActivityForResult(intent,1);
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "New location: " + location.getLatitude() + "-" + location.getLongitude() + ", " + location.getAltitude());
        mCurrentLocation = location;
        Log.d(TAG, "En el onLocationChange: " + location.getLatitude() + ", " + location.getLongitude());
        //getPlaces(location.getLatitude(), location.getLongitude());
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
        Log.d(TAG, "En el onProviderDisabled" );
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

        dm.getPlaces(latitude, longitude,1000).enqueue(new Callback<PlacesResponse>() {
            @Override
            public void onResponse(Call<PlacesResponse> call, Response<PlacesResponse> response) {

                mPlaces = response.body().graph;

                Log.d(TAG, "Valor de response code " + String.valueOf(response.code()));
                if (response.body() != null && !mPlaces.isEmpty()) {
                    mAdapter = new MyAdapter(ParkPlaces.this, R.layout.list_parks, mPlaces);
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
