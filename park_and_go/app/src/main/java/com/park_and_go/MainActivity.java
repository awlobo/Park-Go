package com.park_and_go;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
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
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.navigation.NavigationView;
import com.park_and_go.activities.ConsulatePlaces;
import com.park_and_go.activities.FavoritosPlaces;
import com.park_and_go.activities.FiltrosPlaces;
import com.park_and_go.activities.ParkPlaces;
import com.park_and_go.activities.TheatrePlaces;
import com.park_and_go.activities.TransporteCompartido;
import com.park_and_go.assets.Constants;
import com.park_and_go.common.MyLocation;
import com.park_and_go.common.PlacesResponse;
import com.park_and_go.services.GpsService;

import java.util.ArrayList;

import static com.park_and_go.assets.Constants.FAV;
import static com.park_and_go.assets.Constants.LATITUDE;
import static com.park_and_go.assets.Constants.LOCATION;
import static com.park_and_go.assets.Constants.LONGITUDE;
import static com.park_and_go.assets.Constants.OPTION;
import static com.park_and_go.assets.Constants.TITLE;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView mNavigationView;
    private Intent mServiceIntent;
    Location location;
    Location carLocation;
    SharedPreferences mPrefs;


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

        mPrefs = getPreferences(MODE_PRIVATE);

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter(Constants.INTENT_LOCALIZATION_ACTION));

        if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            startService();
        }
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            location = intent.getParcelableExtra(LOCATION);
            Log.d("PRUEBA", "BroadcastReceiver::Got message: " + location.getLongitude());
        }
    };

    public void startService() {
        mServiceIntent = new Intent(getApplicationContext(), GpsService.class);
        startService(mServiceIntent);
//        stopService(mServiceIntent);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
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

        } else if (id == R.id.menu_aparcamientos) {
            Intent intent = new Intent(MainActivity.this, ParkPlaces.class);
            intent.putExtra(LOCATION,location);
            startActivity(intent);

        } else if (id == R.id.menu_teatros) {
            Intent intent = new Intent(MainActivity.this, TheatrePlaces.class);
            intent.putExtra(LOCATION,location);
            startActivity(intent);

        } else if (id == R.id.menu_embajadas) {
            Intent intent = new Intent(MainActivity.this, ConsulatePlaces.class);
            intent.putExtra(LOCATION,location);
            startActivity(intent);

        } else if (id == R.id.menu_filtrar) {
            Intent intent = new Intent(MainActivity.this, FiltrosPlaces.class);
            intent.putExtra(LOCATION,location);
            startActivity(intent);

        } else if (id == R.id.menu_favoritos) {
            Intent intent = new Intent(this, FavoritosPlaces.class);
            intent.putExtra(LOCATION,location);
            startActivity(intent);

        } else if (id == R.id.menu_guardar_aparcamiento) {
            SharedPreferences.Editor prefsEditor = mPrefs.edit();
            prefsEditor.putFloat(Constants.MY_CAR_LAT, (float) location.getLatitude());
            prefsEditor.putFloat(Constants.MY_CAR_LON, (float) location.getLongitude());
            prefsEditor.commit();
            Toast.makeText(this, "Aparcamiento guardado correctamente", Toast.LENGTH_LONG).show();

        } else if (id == R.id.menu_recuperar_aparcamiento) {
            if (mPrefs != null) {
                carLocation = new Location("carLoc");
                carLocation.setLatitude(mPrefs.getFloat(Constants.MY_CAR_LAT, 0));
                carLocation.setLongitude(mPrefs.getFloat(Constants.MY_CAR_LON, 0));
                float distance = location.distanceTo(carLocation);
//                carPlaces = new PlacesResponse.Places("Your car", new MyLocation(carLocation.getLatitude(), carLocation.getLongitude()), distance);
                Log.d("PRUEBA", String.valueOf(distance) + " metros.");

                Toast.makeText(this,"Estás a " + distance + " de tu coche.", Toast.LENGTH_LONG);
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                intent.putExtra(OPTION,false);

                intent.putExtra(LATITUDE, carLocation.getLatitude());
                intent.putExtra(LONGITUDE, carLocation.getLongitude());
                intent.putExtra(TITLE, "My car");
                startActivity(intent);
            }
        }


        return false;
    }
}
