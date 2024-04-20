package com.example.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.clevertap.android.geofence.CTGeofenceAPI;
import com.clevertap.android.geofence.CTGeofenceSettings;
import com.clevertap.android.geofence.Logger;
import com.clevertap.android.geofence.interfaces.CTGeofenceEventsListener;
import com.clevertap.android.sdk.CTInboxListener;
import com.clevertap.android.sdk.CTInboxStyleConfig;
import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.InAppNotificationListener;
import com.clevertap.android.sdk.InboxMessageListener;
import com.clevertap.android.sdk.displayunits.DisplayUnitListener;
import com.clevertap.android.sdk.displayunits.model.CleverTapDisplayUnit;
import com.clevertap.android.sdk.displayunits.model.CleverTapDisplayUnitContent;
import com.clevertap.android.sdk.inapp.CTInAppNotification;
import com.clevertap.android.sdk.inapp.CTLocalInApp;
import com.clevertap.android.sdk.inapp.InAppListener;
import com.clevertap.android.sdk.inbox.CTInboxMessage;
import com.smarteist.autoimageslider.SliderView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements CTInboxListener, DisplayUnitListener, LocationListener, InboxMessageListener, CTGeofenceEventsListener, InAppListener, InAppNotificationListener {
    protected LocationManager locationManager;
    protected LocationListener locationListener;
    Button createu, pushpbt, appinbox, getmsg, inappnotif;
    SliderView sliderView;
    ArrayList<SliderData> sliderDataArrayList = new ArrayList<>();
    //    private FirebaseAnalytics mFirebaseAnalytics;
    CleverTapAPI clevertapDefaultInstance;


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            CleverTapAPI.getDefaultInstance(getApplicationContext()).pushNotificationClickedEvent(intent.getExtras());
            NotificationUtils.dismissNotification(intent, this);
        }
    }

    @Override
    protected void onResume() {


        super.onResume();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(this);
        final Handler handler = new Handler();

//        CleverTapInstanceConfig clevertapmain =  CleverTapInstanceConfig.createInstance(this, "TEST-468-W87-546Z", "TEST-ab0-b64");
//        CleverTapInstanceConfig clevertapAdditionalInstanceConfig =  CleverTapInstanceConfig.createInstance(this, "TEST-86K-6R8-W66Z", "TEST-b26-36b");
//        clevertapAdditionalInstanceConfig.setDebugLevel(CleverTapAPI.LogLevel.VERBOSE); // default is CleverTapAPI.LogLevel.INFO
//        CleverTapAPI cleverTapAPIAdditionalInstance = CleverTapAPI.instanceWithConfig(this,clevertapAdditionalInstanceConfig);


        clevertapDefaultInstance.setCTInboxMessageListener(this);
        clevertapDefaultInstance.setDisplayUnitListener(this);
        clevertapDefaultInstance.setInAppNotificationListener((InAppNotificationListener) this);
//        mFirebaseAnalytics = FirebaseAnalytics.getInstance(MainActivity.this);
        if (clevertapDefaultInstance != null) {
            //Set the Notification Inbox Listener
            clevertapDefaultInstance.setCTNotificationInboxListener(this);
            //Initialize the inbox and wait for callbacks on overridden methods
            clevertapDefaultInstance.initializeInbox();

        }

        Log.d("clevertap123", "test");


        handler.postDelayed(() -> {
            JSONObject jsonObject = null;
            jsonObject = CTLocalInApp.builder()
                    .setInAppType(CTLocalInApp.InAppType.ALERT)
                    .setTitleText("Get Notified")
                    .setMessageText("Enable Notification permission")
                    .followDeviceOrientation(true)
                    .setPositiveBtnText("Allow")
                    .setNegativeBtnText("Cancel")
                    .setFallbackToSettings(true)
                    .build();

            clevertapDefaultInstance.promptPushPrimer(jsonObject);
//            clevertapDefaultInstance.promptForPushPermission(true);
        }, 500);
        clevertapDefaultInstance.setLocation(clevertapDefaultInstance.getLocation()); //android.location.Location
//        Log.d("CleverTap T Default", "location Default"+location.toString());
        Log.d("CleverTapT", "location CT" + clevertapDefaultInstance.getLocation());
        setupCleverTapGeofence();
        createu = findViewById(R.id.createuser);
        pushpbt = findViewById(R.id.pushnotification);
        appinbox = findViewById(R.id.appinbox);
        getmsg = findViewById(R.id.getmsg);
        inappnotif = findViewById(R.id.inappnotif);
        sliderView = findViewById(R.id.slider);
        appinbox.setOnClickListener(v -> {


            ArrayList<String> tabs = new ArrayList<>();
            tabs.add("PROMOTIONS");
            tabs.add("OFFERS");//We support upto 2 tabs only. Additional tabs will be ignored

            CTInboxStyleConfig styleConfig = new CTInboxStyleConfig();
            styleConfig.setFirstTabTitle("First Tab");
            styleConfig.setTabs(tabs);//Do not use this if you don't want to use tabs
            styleConfig.setTabBackgroundColor("#FF0000");
            styleConfig.setSelectedTabIndicatorColor("#0000FF");
            styleConfig.setSelectedTabColor("#0000FF");
            styleConfig.setUnselectedTabColor("#FFFFFF");
            styleConfig.setBackButtonColor("#FF0000");
            styleConfig.setNavBarTitleColor("#FF0000");
            styleConfig.setNavBarTitle("MY INBOX");
            styleConfig.setNavBarColor("#FFFFFF");
            styleConfig.setInboxBackgroundColor("#ADD8E6");
//            if (clevertapDefaultInstance != null) {
//            clevertapDefaultInstance.showAppInbox(styleConfig); //With Tabs
//            }
            clevertapDefaultInstance.showAppInbox();
        });

        getmsg.setOnClickListener(v -> {

            clevertapDefaultInstance.pushEvent("Abeezergetmsg");
        });
        findViewById(R.id.webview).setOnClickListener(view -> {
            Intent i = new Intent(MainActivity.this, webviewActivity.class);
            MainActivity.this.startActivity(i);

        });
        clevertapDefaultInstance.getAllDisplayUnits();
        pushpbt.setOnClickListener(v -> {
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


    }

    @Override
    public void onDisplayUnitsLoaded(ArrayList<CleverTapDisplayUnit> units) {
        if (sliderDataArrayList != null) {
            sliderDataArrayList.removeAll(sliderDataArrayList);
        }
        Log.d("CleverTap", "Displayunits" + units);
        CleverTapAPI.getDefaultInstance(getApplicationContext()).pushDisplayUnitViewedEventForID(units.get(0).getUnitID());
        for (int i = 0; i < units.size(); i++) {
            CleverTapDisplayUnit unit = units.get(i);
//            Log.d("CleverTap","Custom KeyValue"+unit.getCustomExtras());
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

    @SuppressLint("LongLogTag")
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

        CTGeofenceAPI.getInstance(this).setOnGeofenceApiInitializedListener(() -> {
            //App is notified on the main thread that CTGeofenceAPI is initialized
            Log.e("clevertap OnGeofenceApiInitialized-", "-----OnGeofenceApiInitialized----=");
        });
        CTGeofenceAPI.getInstance(this).setCtGeofenceEventsListener(this);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        CleverTapAPI cleverTapAPI = CleverTapAPI.getDefaultInstance(getApplicationContext());
//        cleverTapAPI.getLocation();
//        cleverTapAPI.setLocation(cleverTapAPI.getLocation()); //android.location.Location
        Log.d("CleverTap T Default", "location Default" + location.toString());

        Log.d("CleverTap T CT", "location CT" + cleverTapAPI.getLocation());
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onInboxItemClicked(CTInboxMessage message, int contentPageIndex, int buttonIndex) {

    }

    @Override
    public void onGeofenceEnteredEvent(JSONObject jsonObject) {
        //Callback on the main thread when the user enters Geofence with info in jsonObject
        Log.e("clevertap-", "-----onGeofenceEnteredEvent-----=" + jsonObject.toString());
    }

    @Override
    public void onGeofenceExitedEvent(JSONObject jsonObject) {
        //Callback on the main thread when user exits Geofence with info in jsonObject
        Log.e("-onGeofenceExitedEvent-", "-----onGeofenceEnteredEvent-----=" + jsonObject.toString());
    }

    @Override
    public void inAppNotificationDidClick(@SuppressLint("RestrictedApi") CTInAppNotification ctInAppNotification, Bundle bundle, HashMap<String, String> hashMap) {

    }

    @Override
    public void inAppNotificationDidDismiss(Context context, @SuppressLint("RestrictedApi") CTInAppNotification ctInAppNotification, Bundle bundle) {

    }

    @SuppressLint("RestrictedApi")
    @Override
    public void inAppNotificationDidShow(CTInAppNotification ctInAppNotification, Bundle bundle) {
        Log.d("CleverTap", "inapp data" + ctInAppNotification.getActionExtras());
        CleverTapAPI.getDefaultInstance(this).resumeInAppNotifications();


    }

    @Override
    public boolean beforeShow(Map<String, Object> map) {
        Log.d("CleverTap", "inapp data 1" + map);

        CleverTapAPI.getDefaultInstance(this).resumeInAppNotifications();

        return true;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onShow(@SuppressLint("RestrictedApi") CTInAppNotification ctInAppNotification) {
        Log.d("CleverTap", "inapp data 2" + ctInAppNotification.getButtons().get(0).getKeyValues());

        CleverTapAPI.getDefaultInstance(this).resumeInAppNotifications();
    }

    @Override
    public void onDismissed(Map<String, Object> map, @Nullable Map<String, Object> map1) {

    }
}
