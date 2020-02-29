package com.park_and_go.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
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
import com.park_and_go.assets.Constants;
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

import static com.park_and_go.assets.Constants.ALL_ITEMS;
import static com.park_and_go.assets.Constants.CONSULADO;
import static com.park_and_go.assets.Constants.DISTANCIA;
import static com.park_and_go.assets.Constants.EMBAJADA;
import static com.park_and_go.assets.Constants.LATITUDE;
import static com.park_and_go.assets.Constants.LOCATION;
import static com.park_and_go.assets.Constants.LONGITUDE;
import static com.park_and_go.assets.Constants.OPTION;
import static com.park_and_go.assets.Constants.PARKING;
import static com.park_and_go.assets.Constants.PLACES;
import static com.park_and_go.assets.Constants.THEATRE;
import static com.park_and_go.assets.Constants.URL_FAV;

public class FilteredPlaces extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();
    private LocationManager mLocManager;
    private ArrayList<PlacesResponse.Places> mPlaces = new ArrayList<>();
    private ArrayList<PlacesResponse.Places> mPlacesEmbajadas;
    private ArrayList<PlacesResponse.Places> mPlacesParkings;
    private ArrayList<PlacesResponse.Places> mPlacesTeatros;
    private Location mCurrentLocation;
    private MyAdapter mAdapter = null;
    private ListView lv = null;
    private int mDistancia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtered_places);

        Intent intent = getIntent();
        mCurrentLocation = intent.getParcelableExtra(LOCATION);
        boolean mParking = intent.getBooleanExtra(PARKING, false);
        boolean mTeatro = intent.getBooleanExtra(THEATRE, false);
        boolean mEmbajada = intent.getBooleanExtra(CONSULADO, false);
        mDistancia = intent.getIntExtra(DISTANCIA, 1000);

        lv = findViewById(R.id.lvFiltered);
        lv.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.add(0, 1, 0, Constants.ADD_FAV);
                menu.add(0, 2, 1, Constants.MOSTRAR_TODOS);
            }
        });

        if (mEmbajada) {
            getEmbajadas(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        }
        if (mTeatro) {
            getTeatros(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        }
        if (mParking) {
            getParkings(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        }

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                Intent intent = new Intent(FilteredPlaces.this, MapsActivity.class);
                intent.putExtra(LOCATION, mCurrentLocation);
                intent.putExtra(OPTION, false);
                intent.putExtra(PLACES, mPlaces.get(i));
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if (item.getItemId() == 1) {
            PlacesResponse.Places p = mPlaces.get(info.position);
            if (p.title.contains(PARKING)) {
                FavoritosPlaces.writeFav(getFilesDir() + URL_FAV, p, PARKING);
            } else if (p.title.contains(CONSULADO) || p.title.contains(EMBAJADA)) {
                FavoritosPlaces.writeFav(getFilesDir() + URL_FAV, p, CONSULADO);
            } else {
                FavoritosPlaces.writeFav(getFilesDir() + URL_FAV, p, THEATRE);
            }
            mAdapter.notifyDataSetChanged();
            Toast.makeText(FilteredPlaces.this, getString(R.string.fav_correcto), Toast.LENGTH_SHORT).show();
        } else if (item.getItemId() == 2) {
            Intent intent = new Intent(FilteredPlaces.this, MapsActivity.class);
            intent.putExtra(OPTION, true);
            intent.putExtra(LOCATION, mCurrentLocation);
            intent.putParcelableArrayListExtra(ALL_ITEMS, mPlaces);
            startActivity(intent);
        }
        return true;
    }

    public void getEmbajadas(double latitude, double longitude) {
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
        dm.getConsulates(latitude, longitude, mDistancia).enqueue(new Callback<PlacesResponse>() {
            @Override
            public void onResponse(Call<PlacesResponse> call, Response<PlacesResponse> response) {
                mPlacesEmbajadas = response.body().graph;
                Log.d(TAG, "Valor de response code " + response.code());
                if (response.body() != null && !mPlacesEmbajadas.isEmpty()) {

                    for (int i = 0; i < mPlacesEmbajadas.size(); i++) {
                        Location location = new Location("");
                        location.setLatitude(mPlacesEmbajadas.get(i).location.latitude);
                        location.setLongitude(mPlacesEmbajadas.get(i).location.longitude);
                        float distance = mCurrentLocation.distanceTo(location);
                        mPlacesEmbajadas.get(i).distance = distance;
                        mPlacesEmbajadas.get(i).setTipo(CONSULADO);
                    }
                    mPlaces.addAll(mPlacesEmbajadas);
                    PlacesResponse.Places.ordenarDistancia(mPlaces);
                    mAdapter = new MyAdapter(FilteredPlaces.this, R.layout.list_places, mPlaces);
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

    public void getTeatros(double latitude, double longitude) {
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
        dm.getTheatres(latitude, longitude, mDistancia).enqueue(new Callback<PlacesResponse>() {
            @Override
            public void onResponse(Call<PlacesResponse> call, Response<PlacesResponse> response) {
                mPlacesTeatros = response.body().graph;
                Log.d(TAG, "Valor de response code " + response.code());
                if (response.body() != null && !mPlacesTeatros.isEmpty()) {

                    for (int i = 0; i < mPlacesTeatros.size(); i++) {
                        Location location = new Location("");
                        location.setLatitude(mPlacesTeatros.get(i).location.latitude);
                        location.setLongitude(mPlacesTeatros.get(i).location.longitude);
                        float distance = mCurrentLocation.distanceTo(location);
                        mPlacesTeatros.get(i).distance = distance;
                        mPlacesTeatros.get(i).setTipo(THEATRE);
                    }

                    mPlaces.addAll(mPlacesTeatros);
                    PlacesResponse.Places.ordenarDistancia(mPlaces);
                    mAdapter = new MyAdapter(FilteredPlaces.this, R.layout.list_places, mPlaces);
                    lv.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                } else {
                    Log.d(TAG, "Response: " + response.body().graph.size());
                }

                Log.d("PRUEBA", String.valueOf(mPlaces.size()));
            }

            @Override
            public void onFailure(Call<PlacesResponse> call, Throwable t) {
                Log.e(TAG, "Response: empty array");
            }
        });
    }

    public void getParkings(double latitude, double longitude) {
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
        dm.getPlaces(latitude, longitude, mDistancia).enqueue(new Callback<PlacesResponse>() {
            @Override
            public void onResponse(Call<PlacesResponse> call, Response<PlacesResponse> response) {
                mPlacesParkings = response.body().graph;
                Log.d(TAG, "Valor de response code " + response.code());
                if (response.body() != null && !mPlacesParkings.isEmpty()) {

                    for (int i = 0; i < mPlacesParkings.size(); i++) {
                        Location location = new Location("");
                        location.setLatitude(mPlacesParkings.get(i).location.latitude);
                        location.setLongitude(mPlacesParkings.get(i).location.longitude);
                        float distance = mCurrentLocation.distanceTo(location);
                        mPlacesParkings.get(i).distance = distance;
                        mPlacesParkings.get(i).setTipo(PARKING);
                    }

                    mPlaces.addAll(mPlacesParkings);
                    PlacesResponse.Places.ordenarDistancia(mPlaces);
                    mAdapter = new MyAdapter(FilteredPlaces.this, R.layout.list_places, mPlaces);
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
