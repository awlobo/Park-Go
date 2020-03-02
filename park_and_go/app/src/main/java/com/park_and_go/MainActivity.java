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

import static com.park_and_go.assets.Constants.KEY_PREFERENCES;
import static com.park_and_go.assets.Constants.LOCATION;
import static com.park_and_go.assets.Constants.MY_CAR;
import static com.park_and_go.assets.Constants.OPTION;
import static com.park_and_go.assets.Constants.PARKING;
import static com.park_and_go.assets.Constants.PLACES;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView mNavigationView;
    private Intent mServiceIntent;
    Location mCurrentLocation;
    Location mCarLocation;
    SharedPreferences mPrefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* CONFIGURACION TOOLBAR */
        setToolBar();
        drawerLayout = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.navview);

        /* CONFIGURACION BOTONES MENU LATERAL */
        if (mNavigationView != null) {
            mNavigationView.setNavigationItemSelectedListener(this);
        }

        /* BOTON PARA LLAMAR AL 112 */
        Button emergencias = findViewById(R.id.numTel);
        emergencias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:112"));
                startActivity(callIntent);
            }
        });

        mPrefs = getSharedPreferences(KEY_PREFERENCES, MODE_PRIVATE);
//        mPrefs= getPreferences(MODE_PRIVATE);

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
            mCurrentLocation = intent.getParcelableExtra(LOCATION);
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
                Toast.makeText(getApplicationContext(), R.string.gps_granted, Toast.LENGTH_SHORT).show();
                startService();
            } else {
                Toast.makeText(getApplicationContext(), R.string.gps_denied, Toast.LENGTH_SHORT).show();
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
            intent.putExtra(LOCATION, mCurrentLocation);
            startActivity(intent);

        } else if (id == R.id.menu_teatros) {
            Intent intent = new Intent(MainActivity.this, TheatrePlaces.class);
            intent.putExtra(LOCATION, mCurrentLocation);
            startActivity(intent);

        } else if (id == R.id.menu_embajadas) {
            Intent intent = new Intent(MainActivity.this, ConsulatePlaces.class);
            intent.putExtra(LOCATION, mCurrentLocation);
            startActivity(intent);

        } else if (id == R.id.menu_filtrar) {
            Intent intent = new Intent(MainActivity.this, FiltrosPlaces.class);
            intent.putExtra(LOCATION, mCurrentLocation);
            startActivity(intent);

        } else if (id == R.id.menu_favoritos) {
            Intent intent = new Intent(this, FavoritosPlaces.class);
            intent.putExtra(LOCATION, mCurrentLocation);
            startActivity(intent);

        } else if (id == R.id.menu_guardar_aparcamiento) {
            SharedPreferences.Editor prefsEditor = mPrefs.edit();
            prefsEditor.putFloat(Constants.MY_CAR_LAT, (float) mCurrentLocation.getLatitude());
            prefsEditor.putFloat(Constants.MY_CAR_LON, (float) mCurrentLocation.getLongitude());
            prefsEditor.apply();
            Toast.makeText(this, R.string.aparcamiento_guardado, Toast.LENGTH_LONG).show();

        } else if (id == R.id.menu_recuperar_aparcamiento) {
            if (mPrefs != null) {
                mCarLocation = new Location(MY_CAR);
                if (mPrefs.contains(Constants.MY_CAR_LAT) && mPrefs.contains(Constants.MY_CAR_LON)) {
                    mCarLocation.setLatitude(mPrefs.getFloat(Constants.MY_CAR_LAT, 0));
                    mCarLocation.setLongitude(mPrefs.getFloat(Constants.MY_CAR_LON, 0));
                    float distance = mCurrentLocation.distanceTo(mCarLocation);
                    PlacesResponse.Places carPlace = new PlacesResponse.Places(MY_CAR, new MyLocation(mCarLocation.getLatitude(), mCarLocation.getLongitude()), distance, PARKING);
                    Intent intent = new Intent(this, MapsActivity.class);
                    intent.putExtra(OPTION, false);
                    intent.putExtra(PLACES, carPlace);
                    intent.putExtra(LOCATION, mCurrentLocation);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, R.string.aparcamiento_noguardado, Toast.LENGTH_LONG).show();
                }
            }
        }
        return false;
    }
}
