package com.park_and_go;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.park_and_go.activities.ConsulatePlaces;
import com.park_and_go.activities.FavoritosPlaces;
import com.park_and_go.activities.FiltrosPlaces;
import com.park_and_go.activities.ParkPlaces;
import com.park_and_go.activities.TheatrePlaces;
import com.park_and_go.activities.TransporteCompartido;
import com.park_and_go.services.GpsService;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView mNavigationView;
    Intent mServiceIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* CREA UN TOOLBAR NUEVO, NECESARIO PARA PODER USAR EL MENU LATERAL*/
        setToolBar();
        drawerLayout = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.navview);

        // CONFIGURACION BOTONES MENU LATERAL
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

        if (PackageManager.PERMISSION_GRANTED !=
                ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);

        } else {
            startService();
        }


    }

    public void startService() {
        mServiceIntent = new Intent(getApplicationContext(), GpsService.class);
        startService(mServiceIntent);
//        stopService(mServiceIntent);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "GPS Permission granted!", Toast.LENGTH_SHORT).show();
                startService();
            } else {
                Toast.makeText(getApplicationContext(), "Permission denied by user!", Toast.LENGTH_SHORT).show();
            }
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
        if (item.getItemId() == android.R.id.home) {
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
        if (id == R.id.menu_teatros) {
            Intent intent = new Intent(MainActivity.this, TheatrePlaces.class);
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
            startActivity(intent);
        }


        return false;
    }
}
