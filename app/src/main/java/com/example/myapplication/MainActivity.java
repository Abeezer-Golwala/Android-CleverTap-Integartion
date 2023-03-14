package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.clevertap.android.geofence.CTGeofenceAPI;
import com.clevertap.android.geofence.CTGeofenceSettings;
import com.clevertap.android.geofence.Logger;
import com.clevertap.android.geofence.interfaces.CTGeofenceEventsListener;
//import com.clevertap.android.pushtemplates.PushTemplateNotificationHandler;
import com.clevertap.android.sdk.CTInboxListener;
import com.clevertap.android.sdk.CTWebInterface;
import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.CleverTapInstanceConfig;
import com.clevertap.android.sdk.GeofenceCallback;
import com.clevertap.android.sdk.InAppNotificationListener;
import com.clevertap.android.sdk.InboxMessageListener;
import com.clevertap.android.sdk.displayunits.DisplayUnitListener;
import com.clevertap.android.sdk.displayunits.model.CleverTapDisplayUnit;
import com.clevertap.android.sdk.displayunits.model.CleverTapDisplayUnitContent;
import com.clevertap.android.sdk.inbox.CTInboxMessage;
import com.clevertap.android.sdk.interfaces.NotificationHandler;
import com.clevertap.android.sdk.pushnotification.CTPushNotificationListener;
import com.google.android.gms.location.Geofence;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.smarteist.autoimageslider.SliderView;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements CTInboxListener,DisplayUnitListener,LocationListener, InboxMessageListener {
    Button createu, pushpbt, appinbox, getmsg, inappnotif;
    SliderView sliderView;
    ArrayList<SliderData> sliderDataArrayList = new ArrayList<>();

    protected LocationManager locationManager;
    protected LocationListener locationListener;
    private FirebaseAnalytics mFirebaseAnalytics;
    CleverTapAPI clevertapDefaultInstance;


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            CleverTapAPI.getDefaultInstance(getApplicationContext()).pushNotificationClickedEvent(intent.getExtras());
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CleverTapAPI.getDefaultInstance(this).setDisplayUnitListener(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(this);
//        CleverTapInstanceConfig clevertapmain =  CleverTapInstanceConfig.createInstance(this, "TEST-468-W87-546Z", "TEST-ab0-b64");
//        CleverTapInstanceConfig clevertapAdditionalInstanceConfig =  CleverTapInstanceConfig.createInstance(this, "TEST-86K-6R8-W66Z", "TEST-b26-36b");
//        clevertapAdditionalInstanceConfig.setDebugLevel(CleverTapAPI.LogLevel.VERBOSE); // default is CleverTapAPI.LogLevel.INFO
        clevertapDefaultInstance.setDebugLevel(CleverTapAPI.LogLevel.VERBOSE);
//        CleverTapAPI cleverTapAPIAdditionalInstance = CleverTapAPI.instanceWithConfig(this,clevertapAdditionalInstanceConfig);
        clevertapDefaultInstance.setCTInboxMessageListener(this);
//        clevertapDefaultInstance.pushError("LaunchFailed",501);
        Log.d("clevertap",""+clevertapDefaultInstance.getProperty("push channel"));





        mFirebaseAnalytics = FirebaseAnalytics.getInstance(MainActivity.this);
        if (clevertapDefaultInstance != null) {
            //Set the Notification Inbox Listener
            clevertapDefaultInstance.setCTNotificationInboxListener(this);
            //Initialize the inbox and wait for callbacks on overridden methods
            clevertapDefaultInstance.initializeInbox();
        }

        Log.d("clevertap123","test");

        clevertapDefaultInstance.setLocation(clevertapDefaultInstance.getLocation()); //android.location.Location
//        Log.d("CleverTap T Default", "location Default"+location.toString());
              Log.d("CleverTapT", "location CT"+clevertapDefaultInstance.getLocation());
        setupCleverTapGeofence();
        createu = findViewById(R.id.createuser);
        pushpbt = findViewById(R.id.pushnotification);
        appinbox = findViewById(R.id.appinbox);
        getmsg = findViewById(R.id.getmsg);
        inappnotif = findViewById(R.id.inappnotif);
        sliderView = findViewById(R.id.slider);
        appinbox.setOnClickListener(v -> {
            clevertapDefaultInstance.showAppInbox();
        });
        getmsg.setOnClickListener(v -> {
            clevertapDefaultInstance.pushEvent("Abeezergetmsg");
        });
        findViewById(R.id.webview).setOnClickListener(view -> {
            Intent i = new Intent(MainActivity.this, webviewActivity.class);
            MainActivity.this.startActivity(i);
        });
        pushpbt.setOnClickListener(v -> {
//            Log.d("clevertap","push channel"+clevertapDefaultInstance.getProperty("push channel"));

            clevertapDefaultInstance.pushEvent("AbeezerPushEvent");
        });
        inappnotif.setOnClickListener(v -> {
            clevertapDefaultInstance.pushEvent("abeezerinapnotif");
        });
        findViewById(R.id.pushev).setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, CustomEventActivity.class);
            MainActivity.this.startActivity(i);
        });
        createu.setOnClickListener(view -> {
            Intent i = new Intent(MainActivity.this, CreateUser.class);
            HashMap test = new HashMap();
            test.put("test", "hello");
//            i.putExtra(test);
            MainActivity.this.startActivity(i);
        });


        clevertapDefaultInstance.recordScreen("Home");
        clevertapDefaultInstance.enableDeviceNetworkInfoReporting(true);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 1);
        }


//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        Log.d("CleverTap","log1");


        Log.d("CleverTap","log2");

    }

    @Override
    public void onDisplayUnitsLoaded(ArrayList<CleverTapDisplayUnit> units) {
        if (sliderDataArrayList != null) {
            sliderDataArrayList.removeAll(sliderDataArrayList);
        }
        Log.d("CleverTap","Displayunits"+units);

        CleverTapAPI.getDefaultInstance(getApplicationContext()).pushDisplayUnitViewedEventForID(units.get(0).getUnitID());
        for (int i = 0; i < units.size(); i++) {
            CleverTapDisplayUnit unit = units.get(i);

            for (CleverTapDisplayUnitContent j : unit.getContents()) {
                //getting urls and adding to array list
                sliderDataArrayList.add(new SliderData(j.getMedia()));

                //Notification Clicked Event
//                sliderView.setOnClickListener(v -> CleverTapAPI.getDefaultInstance(getApplicationContext()).pushDisplayUnitClickedEventForID(unit.getUnitID()));
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
            Log.e("clevertap Exception.triggerLocation", "=" + e.toString());
        }

        CTGeofenceAPI.getInstance(this)
                .setOnGeofenceApiInitializedListener(() -> {
                    //App is notified on the main thread that CTGeofenceAPI is initialized
                    Log.e("clevertap OnGeofenceApiInitialized-", "-----OnGeofenceApiInitialized----=");
                });
        CTGeofenceAPI.getInstance(this)
                .setCtGeofenceEventsListener(new CTGeofenceEventsListener() {
                    @Override
                    public void onGeofenceEnteredEvent(JSONObject jsonObject) {
                        //Callback on the main thread when the user enters Geofence with info in jsonObject
                        Log.e("clevertap onGeofenceEnteredEvent-", "-----onGeofenceEnteredEvent-----=" + jsonObject.toString());
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
//        cleverTapAPI.getLocation();
        cleverTapAPI.setLocation(cleverTapAPI.getLocation()); //android.location.Location
        Log.d("CleverTap T Default", "location Default"+location.toString());

        Log.d("CleverTap T CT", "location CT"+cleverTapAPI.getLocation());
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }



    @Override
    public void onInboxItemClicked(CTInboxMessage message) {
        Log.d("Clevertap","inbox clicked"+message.getData());
    }
}
