package com.park_and_go;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.park_and_go.common.PlacesResponse;

import java.util.ArrayList;

import static com.park_and_go.assets.Constants.ARRAYLIST;
import static com.park_and_go.assets.Constants.FAV;
import static com.park_and_go.assets.Constants.LOCATION;
import static com.park_and_go.assets.Constants.MILOC;
import static com.park_and_go.assets.Constants.SNIPPET;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private final String TAG = getClass().getSimpleName();
    private final String LATITUDE = "LATITUDE";
    private final String LONGITUDE = "LONGITUD";
    private final String ALL_ITEMS = "ALL";
    private final String TITLE = "TITLE";
    private final String OPTION = "OPTION";
    private ArrayList<PlacesResponse.Places> mPlaces;
    private boolean option;
    private boolean fav;
    private boolean miLoc;
    private Double mLatitude, mLongitude;
    private String mTitle;
    private String mSnip;
    private GoogleMap mMap;
    private Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Intent intent = getIntent();
        result(intent);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void result(Intent data) {
        option = data.getBooleanExtra(OPTION, true);
        if (data.getExtras() != null && data.getExtras().containsKey(LOCATION)) {
            location = data.getParcelableExtra(LOCATION);
        }
        if (option) {
            mPlaces = data.getParcelableArrayListExtra(ALL_ITEMS);
        } else {
            mLatitude = data.getDoubleExtra(LATITUDE, 0.0);
            mLongitude = data.getDoubleExtra(LONGITUDE, 0.0);
            mTitle = data.getStringExtra(TITLE);
            mSnip = data.getStringExtra(SNIPPET);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        UiSettings mapUiSettings = mMap.getUiSettings();
        mapUiSettings.setZoomControlsEnabled(true);

        if (option) {
            for (PlacesResponse.Places p : mPlaces) {
                LatLng loc = new LatLng(p.location.latitude, p.location.longitude);
                mMap.addMarker(new MarkerOptions().position(loc).title(p.title));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc,13));
                mMap.setMyLocationEnabled(true);
            }

        } else {
            LatLng loc = new LatLng(mLatitude, mLongitude);
            mMap.addMarker(new MarkerOptions().position(loc).snippet(mSnip).title(mTitle));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc,16));
            mMap.setMyLocationEnabled(true);
        }

        if (location != null) {
            LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(location.getLatitude(), location.getLongitude()))
                    .title(getString(R.string.your_location))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 13));
        }
    }
}
