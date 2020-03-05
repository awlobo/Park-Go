package com.park_and_go.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.park_and_go.MapsActivity;
import com.park_and_go.R;
import com.park_and_go.assets.Constants;
import com.park_and_go.common.MyLocation;
import com.park_and_go.common.PlacesResponse;

import static com.park_and_go.assets.Constants.CHANNEL_CARLOCATION;
import static com.park_and_go.assets.Constants.CHANNEL_DESCRIPTION;
import static com.park_and_go.assets.Constants.CHANNEL_ID;
import static com.park_and_go.assets.Constants.CHANNEL_ID_PERMANENTE;
import static com.park_and_go.assets.Constants.CHANNEL_NAME;
import static com.park_and_go.assets.Constants.CHANNEL_PERMANENTE;
import static com.park_and_go.assets.Constants.KEY_PREFERENCES;
import static com.park_and_go.assets.Constants.LOCATION;
import static com.park_and_go.assets.Constants.MY_CAR;
import static com.park_and_go.assets.Constants.MY_CAR_LAT;
import static com.park_and_go.assets.Constants.MY_CAR_LON;
import static com.park_and_go.assets.Constants.OPTION;
import static com.park_and_go.assets.Constants.PARKING;
import static com.park_and_go.assets.Constants.PLACES;

public class GpsService extends Service implements LocationListener {

    private LocationManager mLocManager = null;
    private Location mCurrentLocation;
    private Location mMyCarLocation;
    SharedPreferences mPref;
    float mDistance;

    public GpsService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mPref = getSharedPreferences(KEY_PREFERENCES, MODE_PRIVATE);
    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    "1",
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }
    }

    @SuppressWarnings({"MissingPermission"})
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        createNotificationChannel();
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID_PERMANENTE)
                .setContentTitle(getString(R.string.location_activated))
                .setSmallIcon(R.drawable.location)
                .build();

        startForeground(CHANNEL_PERMANENTE, notification);
        startLocation();

        return START_STICKY;
    }

    @SuppressWarnings({"MissingPermission"})
    private void startLocation() {
        mLocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!mLocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Intent callGPSSettingIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            callGPSSettingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(callGPSSettingIntent);
        } else {
            mLocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 100, this);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLocManager != null) {
            mLocManager.removeUpdates(this);
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }


    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        mMyCarLocation = new Location(location);

        mMyCarLocation.setLatitude(mPref.getFloat(MY_CAR_LAT, 0));
        mMyCarLocation.setLongitude(mPref.getFloat(MY_CAR_LON, 0));
        mDistance = location.distanceTo(mMyCarLocation);

        if (mDistance < 500) {
            showNotification();
        }

        Intent intent = new Intent(Constants.INTENT_LOCALIZATION_ACTION);
        intent.putExtra(LOCATION, mCurrentLocation);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void showNotification() {
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChanel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            mChanel.setDescription(CHANNEL_DESCRIPTION);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(mChanel);
            }
        }

        PlacesResponse.Places carPlace = new PlacesResponse.Places(MY_CAR, new MyLocation(mMyCarLocation.getLatitude(), mMyCarLocation.getLongitude()), mDistance, PARKING);
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra(OPTION, false);
        intent.putExtra(PLACES, carPlace);
        intent.putExtra(LOCATION, mCurrentLocation);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.car)
                .setContentTitle(getString(R.string.alerta_proximidad))
                .setContentText(getString(R.string.distancia_coche, mDistance))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        if (notificationManager != null) {
            notificationManager.notify(CHANNEL_CARLOCATION, builder.build());
        }
    }
}
