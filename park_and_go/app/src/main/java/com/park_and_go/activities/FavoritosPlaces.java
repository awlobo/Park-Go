package com.park_and_go.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.park_and_go.MainActivity;
import com.park_and_go.MapsActivity;
import com.park_and_go.R;
import com.park_and_go.adapters.FavoritosAdapter;

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
    }
}
