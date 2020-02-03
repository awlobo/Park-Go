package com.park_and_go;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class TransporteCompartido extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transporte_compartido);

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
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://www.muving.com/")));
            }
        });

        coup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://joincoup.com/es/madrid")));
            }
        });

        ecool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://www.ecooltra.com/es/")));
            }
        });

        movo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://movo.me/es/")));
            }
        });

        zity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://zitycar.es/")));
            }
        });

        wible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://www.wible.es/")));
            }
        });

        emov.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://www.emov.eco/")));
            }
        });

        rentroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://rentandrollmadrid.com/alquiler-de-bicicletas/")));
            }
        });

        bicimad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://www.bicimad.com/")));
            }
        });

        jump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://www.jump.com/es/es-es/cities/madrid/")));
            }
        });

        lime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://www.li.me/es/")));
            }
        });

        wind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://www.wind.co/spain")));
            }
        });

        voi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://www.voiscooters.com/es/")));
            }
        });


    }
}
