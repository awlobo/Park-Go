package com.park_and_go.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.park_and_go.R;

import static com.park_and_go.assets.Constants.CONSULADO;
import static com.park_and_go.assets.Constants.DISTANCIA;
import static com.park_and_go.assets.Constants.LOCATION;
import static com.park_and_go.assets.Constants.PARKING;
import static com.park_and_go.assets.Constants.THEATRE;

public class FiltrosPlaces extends AppCompatActivity {

    public final static String KEY_PARK = "KEY_PARK";
    public final static String KEY_OCIO = "KEY_OCIO";
    public final static String KEY_EMBA = "KEY_EMBA";
    public final static String KEY_DIST = "KEY_DIST";
    SharedPreferences mPrefs;
    Switch sP;
    Switch sO;
    Switch sE;
    EditText eT;
    Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtros_places);

        mPrefs = getPreferences(MODE_PRIVATE);

        sP = findViewById(R.id.parkingSwitch);
        sO = findViewById(R.id.ocioSwitch);
        sE = findViewById(R.id.embajadasSwitch);
        eT = findViewById(R.id.editText);
        Button ir = findViewById(R.id.buttonIr);

        Intent intent = getIntent();
        location = intent.getParcelableExtra(LOCATION);

        sP.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor prefsEditor = mPrefs.edit();
                prefsEditor.putBoolean(KEY_PARK, sP.isChecked());
                prefsEditor.commit();
            }
        });

        sO.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor prefsEditor = mPrefs.edit();
                prefsEditor.putBoolean(KEY_OCIO, sO.isChecked());
                prefsEditor.commit();
            }
        });

        sE.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor prefsEditor = mPrefs.edit();
                prefsEditor.putBoolean(KEY_EMBA, sE.isChecked());
                prefsEditor.commit();
            }
        });

        eT.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                SharedPreferences.Editor prefsEditor = mPrefs.edit();
                prefsEditor.putString(KEY_DIST, String.valueOf(eT.getText()));
                prefsEditor.commit();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        ir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FiltrosPlaces.this, FilteredPlaces.class);
                intent.putExtra(LOCATION, location);
                intent.putExtra(PARKING, sP.isChecked());
                intent.putExtra(THEATRE, sO.isChecked());
                intent.putExtra(CONSULADO, sE.isChecked());
                intent.putExtra(DISTANCIA, Integer.parseInt(String.valueOf(eT.getText())));
                startActivity(intent);
            }
        });

        PersistData();
    }

    private void PersistData() {

        if (mPrefs != null) {
            sP.setChecked(mPrefs.getBoolean(KEY_PARK, false));
            sO.setChecked(mPrefs.getBoolean(KEY_OCIO, false));
            sE.setChecked(mPrefs.getBoolean(KEY_EMBA, false));
            eT.setText(mPrefs.getString(KEY_DIST, "1000"));
        }
    }
}
