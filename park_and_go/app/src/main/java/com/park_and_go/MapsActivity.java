package com.park_and_go;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.park_and_go.common.PlacesResponse;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private final String TAG = getClass().getSimpleName();
    private final String LATITUDE = "LATITUDE";
    private final String LONGITUDE = "LONGITUD";
    private final String TITLE = "TITLE";
    private ArrayList<PlacesResponse.Places> mPlaces;
    private boolean option;
    private Double mLatitude, mLongitude;
    private String mTitle;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Intent intent = getIntent();

        result(intent);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void result(Intent data){
        option = data.getBooleanExtra("OPTION",true);
        if(option){
            mPlaces = data.getParcelableArrayListExtra("ARRAY");
            option = true;
            Log.d(TAG, "requestcode = 1 ");
        }else{
            Log.d(TAG, "requestcode = 2 ");
            mLatitude = data.getDoubleExtra(LATITUDE, 0.0);
            mLongitude = data.getDoubleExtra(LONGITUDE, 0.0);
            mTitle = data.getStringExtra(TITLE);
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (option) {
            for (PlacesResponse.Places p : mPlaces) {
                LatLng loc = new LatLng(p.location.latitude, p.location.longitude);
                mMap.addMarker(new MarkerOptions().position(loc).title(p.title));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
                CameraUpdate camera = CameraUpdateFactory.newLatLngZoom(loc, 16);
                mMap.moveCamera(camera);
                mMap.setMyLocationEnabled(true);
            }
        } else {
            Log.d(TAG, "New location: " + mLatitude + "-" + mLongitude);
            LatLng loc = new LatLng(mLatitude, mLongitude);
            mMap.addMarker(new MarkerOptions().position(loc).title(mTitle));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
            CameraUpdate camera = CameraUpdateFactory.newLatLngZoom(loc, 16);
            mMap.moveCamera(camera);
            mMap.setMyLocationEnabled(true);
        }
    }
}
