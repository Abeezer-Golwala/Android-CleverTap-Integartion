package com.example.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.clevertap.android.geofence.CTGeofenceAPI;
import com.clevertap.android.geofence.CTGeofenceSettings;
import com.clevertap.android.geofence.Logger;
import com.clevertap.android.geofence.interfaces.CTGeofenceEventsListener;
import com.clevertap.android.sdk.CTInboxListener;
import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.displayunits.DisplayUnitListener;
import com.clevertap.android.sdk.displayunits.model.CleverTapDisplayUnit;
import com.clevertap.android.sdk.displayunits.model.CleverTapDisplayUnitContent;
import com.clevertap.android.sdk.pushnotification.CTPushNotificationListener;
import com.smarteist.autoimageslider.SliderView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivityBackup extends AppCompatActivity implements CTInboxListener, DisplayUnitListener, LocationListener, CTPushNotificationListener {
    protected LocationManager locationManager;
    Button createu, pushpbt, appinbox, getmsg, inappnotif;
    SliderView sliderView;
    ArrayList<SliderData> sliderDataArrayList = new ArrayList<>();

    //    private FirebaseAnalytics mFirebaseAnalytics ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CleverTapAPI clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(getApplicationContext());
//        mFirebaseAnalytics = FirebaseAnalytics.getInstance(MainActivityBackup.this);
        if (clevertapDefaultInstance != null) {
            //Set the Notification Inbox Listener
            clevertapDefaultInstance.setCTNotificationInboxListener(this);
            //Initialize the inbox and wait for callbacks on overridden methods
            clevertapDefaultInstance.initializeInbox();
        }
        CleverTapAPI.getDefaultInstance(this).setDisplayUnitListener(this);

        CleverTapAPI.getDefaultInstance(this).setCTPushNotificationListener(this);
        createu = findViewById(R.id.createuser);
        pushpbt = findViewById(R.id.pushnotification);
        appinbox = findViewById(R.id.appinbox);
        getmsg = findViewById(R.id.getmsg);
        inappnotif = findViewById(R.id.inappnotif);
        sliderView = findViewById(R.id.slider);
        appinbox.setOnClickListener(v -> clevertapDefaultInstance.showAppInbox());
        getmsg.setOnClickListener(v -> clevertapDefaultInstance.pushEvent("Abeezergetmsg"));


        pushpbt.setOnClickListener(v -> clevertapDefaultInstance.pushEvent("AbeezerPushEvent"));
        inappnotif.setOnClickListener(v -> clevertapDefaultInstance.pushEvent("abeezerinapnotif"));
        findViewById(R.id.pushev).setOnClickListener(v -> {
            Intent i = new Intent(MainActivityBackup.this, CustomEventActivity.class);
            MainActivityBackup.this.startActivity(i);
        });
        createu.setOnClickListener(view -> {
            Intent i = new Intent(MainActivityBackup.this, CreateUser.class);
            MainActivityBackup.this.startActivity(i);
        });

        clevertapDefaultInstance.enableDeviceNetworkInfoReporting(true);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivityBackup.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }

    @Override
    public void onDisplayUnitsLoaded(ArrayList<CleverTapDisplayUnit> units) {
        if (sliderDataArrayList != null) {
            sliderDataArrayList.removeAll(sliderDataArrayList);
        }
        CleverTapAPI.getDefaultInstance(getApplicationContext()).pushDisplayUnitViewedEventForID(units.get(0).getUnitID());
        for (int i = 0; i < units.size(); i++) {
            CleverTapDisplayUnit unit = units.get(i);
            for (CleverTapDisplayUnitContent j : unit.getContents()) {
                //getting urls and adding to array list
                sliderDataArrayList.add(new SliderData(j.getMedia()));
                //Notification Clicked Event
                sliderView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CleverTapAPI.getDefaultInstance(getApplicationContext()).pushDisplayUnitClickedEventForID(unit.getUnitID());

                    }
                });
            }
        }
        //CleverTapAPI.getDefaultInstance(this).pushDisplayUnitViewedEventForID(units.get(1).getUnitID());
        SliderAdapter adapter = new SliderAdapter(this, sliderDataArrayList);
        sliderView.setAutoCycleDirection(SliderView.LAYOUT_DIRECTION_LTR);
        sliderView.setSliderAdapter(adapter);
        sliderView.setScrollTimeInSec(3);
        sliderView.setAutoCycle(true);
        sliderView.startAutoCycle();
    }

    @Override
    public void inboxDidInitialize() {
    }

    @Override
    public void inboxMessagesDidUpdate() {
    }

    private void setupCleverTapGeofence() {
        CleverTapAPI clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(this);
        CTGeofenceSettings ctGeofenceSettings = new CTGeofenceSettings.Builder()
                .enableBackgroundLocationUpdates(true)//boolean to enable background location updates
                .setLogLevel(Logger.VERBOSE)//Log Level
                .setLocationAccuracy(CTGeofenceSettings.ACCURACY_HIGH)//byte value for Location Accuracy
                .setLocationFetchMode(CTGeofenceSettings.FETCH_CURRENT_LOCATION_PERIODIC)//byte value for Fetch Mode
                .setGeofenceMonitoringCount(100)//int value for number of Geofences CleverTap can monitor
                .setInterval(3600000)//long value for interval in milliseconds
                .setFastestInterval(1800000)//long value for fastest interval in milliseconds
                .setSmallestDisplacement(1000f)//float value for smallest Displacement in meters
                .setGeofenceNotificationResponsiveness(300000)// int value for geofence notification responsiveness in milliseconds
                .build();
        CTGeofenceAPI.getInstance(this)
                .init(ctGeofenceSettings, clevertapDefaultInstance);

        try {
            CTGeofenceAPI.getInstance(this).triggerLocation();
        } catch (Exception e) {
            // thrown when this method is called before geofence SDK initialization
            Log.e("-Exception.triggerLocation-", "=" + e);
        }

        CTGeofenceAPI.getInstance(this)
                .setOnGeofenceApiInitializedListener(() -> {
                    //App is notified on the main thread that CTGeofenceAPI is initialized
                    Log.e("-OnGeofenceApiInitialized-", "-----OnGeofenceApiInitialized----=");
                });
        CTGeofenceAPI.getInstance(this)
                .setCtGeofenceEventsListener(new CTGeofenceEventsListener() {
                    @Override
                    public void onGeofenceEnteredEvent(JSONObject jsonObject) {
                        //Callback on the main thread when the user enters Geofence with info in jsonObject
                        Log.e("-onGeofenceEnteredEvent-", "-----onGeofenceEnteredEvent-----=" + jsonObject.toString());
                    }

                    @Override
                    public void onGeofenceExitedEvent(JSONObject jsonObject) {
                        //Callback on the main thread when user exits Geofence with info in jsonObject
                        Log.e("-onGeofenceExitedEvent-", "-----onGeofenceEnteredEvent-----=" + jsonObject.toString());
                    }
                });

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        CleverTapAPI cleverTapAPI = CleverTapAPI.getDefaultInstance(getApplicationContext());
        cleverTapAPI.setLocation(location); //android.location.Location
        Log.d("Location", location.toString());
    }

    @Override
    public void onNotificationClickedPayloadReceived(HashMap<String, Object> payload) {
        Log.d("payload", "Here" + payload);
    }
}

