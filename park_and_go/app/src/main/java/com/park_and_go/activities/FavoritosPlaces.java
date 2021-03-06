package com.park_and_go.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.park_and_go.MapsActivity;
import com.park_and_go.R;
import com.park_and_go.adapters.MyAdapter;
import com.park_and_go.common.PlacesResponse;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.park_and_go.assets.Constants.ALL_ITEMS;
import static com.park_and_go.assets.Constants.LOCATION;
import static com.park_and_go.assets.Constants.OPTION;
import static com.park_and_go.assets.Constants.PLACES;
import static com.park_and_go.assets.Constants.URL_FAV;

public class FavoritosPlaces extends AppCompatActivity {

    private MyAdapter mAdapter = null;
    private ListView lv = null;
    protected static List<PlacesResponse.Places> mFavsPlaces = new ArrayList<>();
    Location mCurrentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favoritos_places);

        lv = findViewById(R.id.listaFavoritos);

        readFav(getFilesDir() + URL_FAV);
        Intent intent = getIntent();
        mCurrentLocation = intent.getParcelableExtra(LOCATION);

        if (mFavsPlaces != null && !mFavsPlaces.isEmpty()) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(FavoritosPlaces.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(FavoritosPlaces.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                checkGps();
            }
        } else {
            Toast.makeText(this, R.string.no_favs, Toast.LENGTH_LONG).show();
        }

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                Intent intent = new Intent(FavoritosPlaces.this, MapsActivity.class);
                intent.putExtra(LOCATION, mCurrentLocation);
                intent.putExtra(PLACES, mFavsPlaces.get(i));
                intent.putExtra(OPTION, false);
                startActivity(intent);
            }
        });

        lv.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.add(0, 1, 0, R.string.borrar_fav);
                menu.add(0, 2, 1, R.string.mostrar_todo);
            }
        });
    }

    public void checkGps() {
        if (mCurrentLocation != null) {
            PlacesResponse.Places.ordenarDistancia(mFavsPlaces);
            mAdapter = new MyAdapter(FavoritosPlaces.this, R.layout.list_places, mFavsPlaces);
            lv.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
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
            try {
                try (Writer writer = new FileWriter(getFilesDir() + URL_FAV)) {
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    PlacesResponse.Places p = mFavsPlaces.get(info.position);
                    mFavsPlaces.remove(p);
                    gson.toJson(mFavsPlaces, writer);
                    Toast.makeText(FavoritosPlaces.this, R.string.borrado_correcto, Toast.LENGTH_SHORT).show();
                    mAdapter.notifyDataSetChanged();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (item.getItemId() == 2) {
            Intent intent = new Intent(FavoritosPlaces.this, MapsActivity.class);
            intent.putExtra(OPTION, true);
            intent.putExtra(LOCATION, mCurrentLocation);
            intent.putParcelableArrayListExtra(ALL_ITEMS, (ArrayList<? extends Parcelable>) mFavsPlaces);
            startActivity(intent);
        }
        return true;
    }

    public static void writeFav(String file, PlacesResponse.Places p, String tipo) {
        try {
            try (Writer writer = new FileWriter(file)) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                p.setTipo(tipo);
                mFavsPlaces.add(p);
                gson.toJson(mFavsPlaces, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void readFav(String file) {
        try {
            try (Reader reader = new FileReader(file)) {
                Gson gson = new Gson();
                Type types = new TypeToken<ArrayList<PlacesResponse.Places>>() {
                }.getType();
                mFavsPlaces = gson.fromJson(reader, types);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
