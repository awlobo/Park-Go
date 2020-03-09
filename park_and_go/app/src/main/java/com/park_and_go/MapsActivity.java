package com.park_and_go;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

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

import static com.park_and_go.assets.Constants.ALL_ITEMS;
import static com.park_and_go.assets.Constants.LOCATION;
import static com.park_and_go.assets.Constants.OPTION;
import static com.park_and_go.assets.Constants.PARKING;
import static com.park_and_go.assets.Constants.PLACES;
import static com.park_and_go.assets.Constants.THEATRE;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private ArrayList<PlacesResponse.Places> mPlaces;
    private PlacesResponse.Places mPlaceAlone;
    private boolean option;
    private Double mLatitude;
    private Double mLongitude;
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
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    public void result(Intent data) {
        option = data.getBooleanExtra(OPTION, true);
        if (data.getExtras() != null && data.getExtras().containsKey(LOCATION)) {
            location = data.getParcelableExtra(LOCATION);
        }

        if (option) {
            mPlaces = data.getParcelableArrayListExtra(ALL_ITEMS);
        } else {
            mPlaceAlone = data.getParcelableExtra(PLACES);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        UiSettings mapUiSettings = mMap.getUiSettings();
        mapUiSettings.setZoomControlsEnabled(true);

        if (location != null) {
            LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(location.getLatitude(), location.getLongitude()))
                    .title(getString(R.string.your_location))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 13));
        }

        int limit = 0;
        if (option) {
            for (PlacesResponse.Places p : mPlaces) {
                if (limit < 10) {
                    LatLng loc = new LatLng(p.location.latitude, p.location.longitude);
                    if (p.getTipo().equals(THEATRE)) {
                        mMap.addMarker(new MarkerOptions().position(loc).title(p.title).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                    } else if (p.getTipo().equals(PARKING)) {
                        mMap.addMarker(new MarkerOptions().position(loc).title(p.title).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                    } else {
                        mMap.addMarker(new MarkerOptions().position(loc).title(p.title).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                    }
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 15));
                    mMap.setMyLocationEnabled(true);
                } else {
                    break;
                }
                limit++;
            }
        } else {
            LatLng loc = new LatLng(mPlaceAlone.location.latitude, mPlaceAlone.location.longitude);
            mMap.addMarker(new MarkerOptions().position(loc).snippet(mSnip).title(mTitle));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 16));
            if (mPlaceAlone.getTipo().equals(THEATRE)) {
                mMap.addMarker(new MarkerOptions().position(loc).snippet(mSnip).title(mPlaceAlone.title).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            } else if (mPlaceAlone.getTipo().equals(PARKING)) {
                mMap.addMarker(new MarkerOptions().position(loc).snippet(mSnip).title(mPlaceAlone.title).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            } else {
                mMap.addMarker(new MarkerOptions().position(loc).snippet(mSnip).title(mPlaceAlone.title).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            }
            mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
            CameraUpdate camera = CameraUpdateFactory.newLatLngZoom(loc, 16);
            mMap.moveCamera(camera);
            mMap.setMyLocationEnabled(true);
        }
    }
}
