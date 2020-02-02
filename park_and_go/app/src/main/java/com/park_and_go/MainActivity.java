package com.park_and_go;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // CREA UN TOOLBAR NUEVO, NECESARIO PARA PODER USAR EL MENU LATERAL
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

        if (id == R.id.menu_transporte){
            Intent intent = new Intent(MainActivity.this, TransporteCompartido.class);
            startActivity(intent);

        }
        return false;
    }
}
