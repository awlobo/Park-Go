package com.park_and_go;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.park_and_go.assets.Constants;
import com.park_and_go.common.Favorito;
import com.park_and_go.common.PlacesResponse;

import java.util.ArrayList;

import static com.park_and_go.assets.Constants.ARRAYLIST;
import static com.park_and_go.assets.Constants.FAV;
import static com.park_and_go.assets.Constants.LOCATION;
import static com.park_and_go.assets.Constants.MILOC;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private final String TAG = getClass().getSimpleName();
    private final String LATITUDE = "LATITUDE";
    private final String LONGITUDE = "LONGITUD";
    private final String ALL_ITEMS = "ALL";
    private final String TITLE = "TITLE";
    private final String OPTION = "OPTION";
    private ArrayList<PlacesResponse.Places> mPlaces;
    private ArrayList<Favorito> mFavs;
    private boolean option;
    private boolean fav;
    private boolean miLoc;
    private Double mLatitude, mLongitude;
    private String mTitle;
    private GoogleMap mMap;
    private Location location;

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

    public void result(Intent data) {
        option = data.getBooleanExtra(OPTION, true);
        fav = data.getBooleanExtra(FAV, false);
        miLoc = data.getBooleanExtra(MILOC, false);

        if (miLoc) {
            location = data.getParcelableExtra(LOCATION);
        }

        if (option) {
            if (fav) {
                mFavs = data.getParcelableArrayListExtra(ARRAYLIST);

            } else {
                mPlaces = data.getParcelableArrayListExtra(ALL_ITEMS);
                option = true;
            }

        } else {
            mLatitude = data.getDoubleExtra(LATITUDE, 0.0);
            mLongitude = data.getDoubleExtra(LONGITUDE, 0.0);
            mTitle = data.getStringExtra(TITLE);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (option) {
            if (fav) {
                for (Favorito f : mFavs) {
                    LatLng loc = new LatLng(f.getLatitude(), f.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(loc).title(f.getTitle()));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
                    CameraUpdate camera = CameraUpdateFactory.newLatLngZoom(loc, 16);
                    mMap.moveCamera(camera);
                    mMap.setMyLocationEnabled(true);
                }

            } else {
                for (PlacesResponse.Places p : mPlaces) {
                    LatLng loc = new LatLng(p.location.latitude, p.location.longitude);
                    mMap.addMarker(new MarkerOptions().position(loc).title(p.title));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
                    CameraUpdate camera = CameraUpdateFactory.newLatLngZoom(loc, 16);
                    mMap.moveCamera(camera);
                    mMap.setMyLocationEnabled(true);
                }
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

        if (location != null) {
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(location.getLatitude(), location.getLongitude()))
                    .title("Your location")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        }
    }

}
