package com.park_and_go;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.park_and_go.activities.ConsulatePlaces;
import com.park_and_go.activities.FavoritosPlaces;
import com.park_and_go.activities.FiltrosPlaces;
import com.park_and_go.activities.ParkPlaces;
import com.park_and_go.activities.TransporteCompartido;
import com.park_and_go.assets.Constants;
import com.park_and_go.common.Favorito;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    public static List<Favorito> mFavs = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* CREA UN TOOLBAR NUEVO, NECESARIO PARA PODER USAR EL MENU LATERAL*/
        setToolBar();
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navview);

        // CONFIGURACION BOTONES MENU LATERAL
        NavigationView mNavigationView = findViewById(R.id.navview);
        if (mNavigationView != null) {
            mNavigationView.setNavigationItemSelectedListener(this);
        }

        // BOTON PARA LLAMAR AL 112
        Button emergencias = findViewById(R.id.numTel);
        emergencias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:112"));
                startActivity(callIntent);
            }
        });
        leerFavoritos();

    }

    private void leerFavoritos() {
        try {
            Reader reader = new FileReader(getFilesDir() + "/fav.json");
            Gson gson = new Gson();

            Type types = new TypeToken<ArrayList<Favorito>>() {
            }.getType();
            mFavs = gson.fromJson(reader, types);

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_home);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        if (id == R.id.menu_transporte) {
            Intent intent = new Intent(MainActivity.this, TransporteCompartido.class);
            startActivity(intent);

        }
        if (id == R.id.menu_aparcamientos) {
            Intent intent = new Intent(MainActivity.this, ParkPlaces.class);
            startActivity(intent);

        }
        if (id == R.id.menu_embajadas) {
            Intent intent = new Intent(MainActivity.this, ConsulatePlaces.class);
            startActivity(intent);

        }
        if (id == R.id.menu_filtrar) {
            Intent intent = new Intent(MainActivity.this, FiltrosPlaces.class);
            startActivity(intent);
        }

        if (id == R.id.menu_favoritos) {
            Intent intent = new Intent(this, FavoritosPlaces.class);
            intent.putParcelableArrayListExtra(Constants.ARRAYLIST, (ArrayList<? extends Parcelable>) mFavs);
            startActivity(intent);
        }


        return false;
    }
}
