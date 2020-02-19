package com.park_and_go.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.park_and_go.MapsActivity;
import com.park_and_go.R;
import com.park_and_go.adapters.FavoritosAdapter;
import com.park_and_go.assets.Constants;
import com.park_and_go.common.Favorito;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class FavoritosPlaces extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();
    private ListView lv;
    private final String LATITUDE = "LATITUDE";
    private final String LONGITUDE = "LONGITUD";
    private FavoritosAdapter mAdapter = null;
    private List<Favorito> mFavs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favoritos_places);

        lv = findViewById(R.id.listaFavoritos);
        Intent intent = getIntent();
        mFavs = intent.getParcelableArrayListExtra(Constants.ARRAYLIST);

        if (!mFavs.isEmpty()) {
            mAdapter = new FavoritosAdapter(FavoritosPlaces.this, R.layout.list_favoritos, mFavs);
            lv.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        }

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                Intent intent = new Intent(FavoritosPlaces.this, MapsActivity.class);
                intent.putParcelableArrayListExtra(Constants.ARRAYLIST, (ArrayList<? extends Parcelable>) mFavs);
                intent.putExtra(LATITUDE, mFavs.get(i).getLatitude());
                intent.putExtra(LONGITUDE, mFavs.get(i).getLongitude());
                startActivityForResult(intent, 1);
            }
        });

        lv.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.add(0, 1, 0, Constants.BORRAR);
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

                    Toast.makeText(FavoritosPlaces.this, Constants.BORRAR_CORRECTO, Toast.LENGTH_SHORT).show();
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
