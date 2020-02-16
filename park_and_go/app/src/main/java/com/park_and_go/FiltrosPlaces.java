package com.park_and_go;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtros_places);

        mPrefs = getPreferences(MODE_PRIVATE);

        sP = findViewById(R.id.parkingSwitch);
        sO = findViewById(R.id.ocioSwitch);
        sE = findViewById(R.id.embajadasSwitch);
        eT = findViewById(R.id.editText);

        Button bGuardar = findViewById(R.id.buttonGuardarPrefs);
        bGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // For save
                SharedPreferences.Editor prefsEditor = mPrefs.edit();
                prefsEditor.putBoolean(KEY_PARK, sP.isChecked());
                prefsEditor.putBoolean(KEY_OCIO, sO.isChecked());
                prefsEditor.putBoolean(KEY_EMBA, sE.isChecked());
                prefsEditor.putString(KEY_DIST, String.valueOf(eT.getText()));
                prefsEditor.commit();
                Toast.makeText(FiltrosPlaces.this, "Preferences saved",Toast.LENGTH_SHORT).show();
            }
        });

        PersistData();
    }

    private void PersistData() {

        // Restore data
        if (mPrefs != null) {
            sP.setChecked(mPrefs.getBoolean(KEY_PARK, false));
            sO.setChecked(mPrefs.getBoolean(KEY_OCIO, false));
            sE.setChecked(mPrefs.getBoolean(KEY_EMBA, false));
            eT.setText(mPrefs.getString(KEY_DIST, "1000"));
        }
    }
}
