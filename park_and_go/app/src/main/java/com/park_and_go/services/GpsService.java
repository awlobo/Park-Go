package com.park_and_go.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.park_and_go.MainActivity;
import com.park_and_go.MapsActivity;
import com.park_and_go.R;
import com.park_and_go.assets.Constants;

import static com.park_and_go.assets.Constants.KEY_PREFERENCES;
import static com.park_and_go.assets.Constants.LATITUDE;
import static com.park_and_go.assets.Constants.LOCATION;
import static com.park_and_go.assets.Constants.LONGITUDE;
import static com.park_and_go.assets.Constants.MILOC;
import static com.park_and_go.assets.Constants.NOTIFICATION_MESSAGE;
import static com.park_and_go.assets.Constants.NOTIFICATION_TITLE;
import static com.park_and_go.assets.Constants.OPTION;
import static com.park_and_go.assets.Constants.SNIPPET;
import static com.park_and_go.assets.Constants.TITLE;

public class GpsService extends Service implements LocationListener {

    private final String TAG = getClass().getSimpleName();
    private LocationManager mLocManager = null;
    private Location mCurrentLocation;
    private Location mMyCarLocation;
    SharedPreferences mPref;

    public GpsService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Servicio creado");
        mPref = getSharedPreferences(KEY_PREFERENCES,MODE_PRIVATE);
    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    "1",
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    @SuppressWarnings({"MissingPermission"})
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        // Set Foreground service

        createNotificationChannel();
        Notification notification = new NotificationCompat.Builder(this, "1")
                .setContentTitle("Location Service Activated")
                .setSmallIcon(R.drawable.location)
                .build();

        startForeground(1, notification);

        // Set GPS Listener
        startLocation();
        Log.d(TAG, "Listener set");

        return START_STICKY;
    }

    @SuppressWarnings({"MissingPermission"})
    private void startLocation() {
        mLocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!mLocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Intent callGPSSettingIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(callGPSSettingIntent);

        } else {
            mLocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 300, this);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
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
        Log.d(TAG, "new location");

        mCurrentLocation = location;

        mMyCarLocation = new Location(location);
        Log.d(TAG, "Location: "+location.getLongitude());

        mMyCarLocation.setLatitude(mPref.getFloat(Constants.MY_CAR_LAT, 0));
        mMyCarLocation.setLongitude(mPref.getFloat(Constants.MY_CAR_LON, 0));

        Log.d(TAG, "My car Location: "+mMyCarLocation.getLongitude());

        Log.d(TAG, "Location despues del coche: "+location.getLongitude());

        Log.d(TAG, "Distancia entre ambos: "+location.distanceTo(mMyCarLocation));

        if(location.distanceTo(mMyCarLocation)<200){
            showNotification();
        }

        Intent intent = new Intent(Constants.INTENT_LOCALIZATION_ACTION);
        intent.putExtra(LOCATION, mCurrentLocation);
        //intent.putExtra(Constants.KEY_MESSAGE, "New Location");

        Log.d(TAG, "Current Location: "+mCurrentLocation.getLongitude());
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
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(this.NOTIFICATION_SERVICE);

        String CHANNEL_ID = "my_chanel_01";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "my_chanel";
            String Description = "This is my chanel";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChanel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChanel.setDescription(Description);
            notificationManager.createNotificationChannel(mChanel);
        }

        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra(OPTION, false);
        intent.putExtra(LATITUDE, mMyCarLocation.getLatitude());
        intent.putExtra(LONGITUDE, mMyCarLocation.getLongitude());
        intent.putExtra(TITLE, "My car");
        intent.putExtra(SNIPPET, "Distancia: metros.");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        //Igual que un intent pero para mandar a posteriori, a diferencia del intent que se ejecuta al momento
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentTitle(NOTIFICATION_TITLE)
                .setContentText(NOTIFICATION_MESSAGE+mPref.getFloat(Constants.MY_CAR_LAT, 0))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        notificationManager.notify(1,builder.build());
    }
}
