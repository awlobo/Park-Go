package com.park_and_go.activities;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.park_and_go.R;
import com.park_and_go.adapters.MyAdapter;
import com.park_and_go.assets.Constants;
import com.park_and_go.common.DataMadrid;
import com.park_and_go.common.Favorito;
import com.park_and_go.common.PlacesResponse;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.park_and_go.MainActivity.mFavs;

public class ConsulatePlaces extends AppCompatActivity implements LocationListener {
    private final String TAG = getClass().getSimpleName();
    private Context mContext = this;
    private static final Integer PERMIS_GPS_FINE = 1;
    private LocationManager mLocManager;
    private List<PlacesResponse.Places> mPlaces;
    private Location mCurrentLocation;
    private MyAdapter mAdapter = null;
    private ListView lv = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulates_places);
        Log.d(TAG, "En el onCreate de park places");

        lv = (ListView) findViewById(R.id.listview_consulates);

        lv.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                // OPCIONES QUE APARECEN CUANDO MANTIENES PULSADO
                menu.add(0, 1, 0, "Añadir Favorito");
            }
        });

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()) {
            case 1:
                try {
                    Writer writer = new FileWriter(getFilesDir() + "/fav.json");
                    Gson gson = new GsonBuilder()
                            .setPrettyPrinting()
                            .create();
                    PlacesResponse.Places p = mPlaces.get(info.position);
                    Favorito f = new Favorito(p.title, Constants.CONSULADO, p.location.latitude, p.location.longitude);
                    mFavs.add(f);
                    gson.toJson(mFavs, writer);

                    Toast.makeText(ConsulatePlaces.this, "Añadido correctamente a favoritos", Toast.LENGTH_SHORT).show();
                    mAdapter.notifyDataSetChanged();

                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
        return true;
    }


    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "New location: " + location.getLatitude() + "-" + location.getLongitude() + ", " + location.getAltitude());
        mCurrentLocation = location;
        Log.d(TAG, "En el onLocationChange: " + location.getLatitude() + ", " + location.getLongitude());
        //getPlaces(location.getLatitude(), location.getLongitude());
        getConsulates(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
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
        if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(ConsulatePlaces.this, Manifest.permission.ACCESS_FINE_LOCATION)) {

            ActivityCompat.requestPermissions(ConsulatePlaces.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMIS_GPS_FINE);
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

    public void getConsulates(double latitude, double longitude) {

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

        Log.d(TAG, "En getConsulates");

        dm.getConsulates(latitude, longitude, 5000).enqueue(new Callback<PlacesResponse>() {
            @Override
            public void onResponse(Call<PlacesResponse> call, Response<PlacesResponse> response) {

                mPlaces = response.body().graph;

                Log.d(TAG, "Valor de response code " + String.valueOf(response.code()));
                if (response.body() != null && !mPlaces.isEmpty()) {
                    mAdapter = new MyAdapter(ConsulatePlaces.this, R.layout.list_consulates, mPlaces);
                    lv.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                } else {

                }
            }

            @Override
            public void onFailure(Call<PlacesResponse> call, Throwable t) {
                Log.e(TAG, "Response: empty array");
            }
        });
    }
}
