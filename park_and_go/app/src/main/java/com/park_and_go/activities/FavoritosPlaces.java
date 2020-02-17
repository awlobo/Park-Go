package com.park_and_go.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.park_and_go.MainActivity;
import com.park_and_go.MapsActivity;
import com.park_and_go.R;
import com.park_and_go.adapters.FavoritosAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import static com.park_and_go.MainActivity.mFavs;

public class FavoritosPlaces extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();
    private ListView lv;
    private final String LATITUDE = "LATITUDE";
    private final String LONGITUDE = "LONGITUD";
    private FavoritosAdapter mAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favoritos_places);

        lv = findViewById(R.id.listaFavoritos);

        if (!MainActivity.mFavs.isEmpty()) {
            mAdapter = new FavoritosAdapter(FavoritosPlaces.this, R.layout.list_favoritos, MainActivity.mFavs);
            lv.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        }

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                Intent intent = new Intent(FavoritosPlaces.this, MapsActivity.class);
                intent.putExtra(LATITUDE, MainActivity.mFavs.get(i).getLatitude());
                intent.putExtra(LONGITUDE, MainActivity.mFavs.get(i).getLongitude());
                startActivityForResult(intent, 1);
            }
        });

        lv.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                // OPCIONES QUE APARECEN CUANDO MANTIENES PULSADO
                menu.add(0, 1, 0, "Borrar Favorito");
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
                    Favorito f = mFavs.get(info.position);
                    mFavs.remove(f);
                    gson.toJson(mFavs, writer);

                    Toast.makeText(FavoritosPlaces.this, "Borrado correctamente", Toast.LENGTH_SHORT).show();
                    mAdapter.notifyDataSetChanged();

                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
        return true;
    }
}
