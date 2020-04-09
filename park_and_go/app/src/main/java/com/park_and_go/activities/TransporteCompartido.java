package com.park_and_go.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.park_and_go.R;

import static com.park_and_go.assets.Constants.URL_WEBVIEW;

public class TransporteCompartido extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transporte_compartido);

        final Intent intent = new Intent(TransporteCompartido.this, WebTransporteCompartido.class);

        ImageView jump = findViewById(R.id.jump);
        ImageView lime = findViewById(R.id.lime);
        ImageView wind = findViewById(R.id.wind);
        ImageView voi = findViewById(R.id.voi);
        ImageView rentroll = findViewById(R.id.rentRoll);
        ImageView bicimad = findViewById(R.id.biciMad);
        ImageView zity = findViewById(R.id.zity);
        ImageView wible = findViewById(R.id.wible);
        ImageView emov = findViewById(R.id.emov);
        ImageView muving = findViewById(R.id.muving);
        ImageView coup = findViewById(R.id.coup);
        ImageView ecool = findViewById(R.id.ecool);
        ImageView movo = findViewById(R.id.movo);

        muving.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra(URL_WEBVIEW, "https://www.muving.com/");
                startActivity(intent);
            }
        });

        coup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra(URL_WEBVIEW, "https://joincoup.com/es/madrid");
                startActivity(intent);
            }
        });

        ecool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra(URL_WEBVIEW, "https://www.ecooltra.com/es/");
                startActivity(intent);
            }
        });

        movo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra(URL_WEBVIEW, "https://movo.me/es/");
                startActivity(intent);
            }
        });

        zity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra(URL_WEBVIEW, "https://zitycar.es/");
                startActivity(intent);
            }
        });

        wible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra(URL_WEBVIEW, "https://www.wible.es/");
                startActivity(intent);
            }
        });

        emov.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra(URL_WEBVIEW, "https://www.emov.eco/");
                startActivity(intent);
            }
        });

        rentroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra(URL_WEBVIEW, "https://rentandrollmadrid.com/alquiler-de-bicicletas/");
                startActivity(intent);
            }
        });

        bicimad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra(URL_WEBVIEW, "https://www.bicimad.com/");
                startActivity(intent);
            }
        });

        jump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra(URL_WEBVIEW, "https://www.jump.com/es/es-es/cities/madrid/");
                startActivity(intent);
            }
        });

        lime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra(URL_WEBVIEW, "https://www.li.me/es/");
                startActivity(intent);
            }
        });

        wind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra(URL_WEBVIEW, "https://www.wind.co/spain");
                startActivity(intent);
            }
        });

        voi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra(URL_WEBVIEW, "https://www.voiscooters.com/es/");
                startActivity(intent);
            }
        });
    }
}
