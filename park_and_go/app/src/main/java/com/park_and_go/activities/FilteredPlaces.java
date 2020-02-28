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

import com.park_and_go.MapsActivity;
import com.park_and_go.R;
import com.park_and_go.adapters.MyAdapter;
import com.park_and_go.assets.Constants;
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

import static com.park_and_go.assets.Constants.ARRAYLIST;
import static com.park_and_go.assets.Constants.CONSULADO;
import static com.park_and_go.assets.Constants.DISTANCIA;
import static com.park_and_go.assets.Constants.LATITUDE;
import static com.park_and_go.assets.Constants.LOCATION;
import static com.park_and_go.assets.Constants.LONGITUDE;
import static com.park_and_go.assets.Constants.PARKING;
import static com.park_and_go.assets.Constants.THEATRE;

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
    private boolean mEmbajada;
    private boolean mParking;
    private boolean mTeatro;
    private int mDistancia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtered_places);

        Intent intent = getIntent();
        mCurrentLocation = intent.getParcelableExtra(LOCATION);
        mParking = intent.getBooleanExtra(PARKING, false);
        mTeatro = intent.getBooleanExtra(THEATRE, false);
        mEmbajada = intent.getBooleanExtra(CONSULADO, false);
        mDistancia = intent.getIntExtra(DISTANCIA, 500);

        lv = findViewById(R.id.lvFiltered);
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
                Intent intent = new Intent(FilteredPlaces.this, MapsActivity.class);
                if (i == 0) {
                    option = true;
                    intent.putExtra("OPTION", option);
                    intent.putParcelableArrayListExtra(ARRAYLIST, mPlaces);
                    startActivity(intent);
                } else if (i > 0) {
                    option = false;
                    intent.putExtra(LATITUDE, mPlaces.get(i).location.latitude);
                    intent.putExtra(LONGITUDE, mPlaces.get(i).location.longitude);
                    intent.putExtra(Constants.TITLE, mPlaces.get(i).title);
                    intent.putExtra("OPTION", option);
                    startActivity(intent);
                }
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

        Log.d("PRUEBA", String.valueOf(mPlaces.size()));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        if (item.getItemId() == 1) {
            PlacesResponse.Places p = mPlaces.get(info.position);
            if (p.title.contains("Aparcamiento")) {
                Favorito.writeFav(getFilesDir() + "/fav.json", p, Constants.PARKING);
            } else if (p.title.contains("Consulado") || p.title.contains("Embajada")) {
                Favorito.writeFav(getFilesDir() + "/fav.json", p, CONSULADO);
            } else {
                Favorito.writeFav(getFilesDir() + "/fav.json", p, THEATRE);
            }

            mAdapter.notifyDataSetChanged();
            Toast.makeText(FilteredPlaces.this, "Añadido correctamente a favoritos", Toast.LENGTH_SHORT).show();
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
                int code = 2;
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
                    mAdapter = new MyAdapter(FilteredPlaces.this, R.layout.list_places, mPlaces, code);
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
                int code = 3;
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
                    mAdapter = new MyAdapter(FilteredPlaces.this, R.layout.list_places, mPlaces, code);
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
                int code = 1;
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

                    mAdapter = new MyAdapter(FilteredPlaces.this, R.layout.list_places, mPlaces, code);
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
