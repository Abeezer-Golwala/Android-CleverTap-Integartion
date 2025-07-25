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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.clevertap.android.geofence.CTGeofenceAPI;
import com.clevertap.android.geofence.CTGeofenceSettings;
import com.clevertap.android.geofence.Logger;
import com.clevertap.android.geofence.interfaces.CTGeofenceEventsListener;
import com.clevertap.android.sdk.CTInboxListener;
import com.clevertap.android.sdk.CTInboxStyleConfig;
import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.InAppNotificationButtonListener;
import com.clevertap.android.sdk.InboxMessageListener;
import com.clevertap.android.sdk.PushPermissionResponseListener;
import com.clevertap.android.sdk.displayunits.DisplayUnitListener;
import com.clevertap.android.sdk.displayunits.model.CleverTapDisplayUnit;
import com.clevertap.android.sdk.displayunits.model.CleverTapDisplayUnitContent;
import com.clevertap.android.sdk.inapp.CTLocalInApp;
import com.clevertap.android.sdk.inbox.CTInboxMessage;
import com.smarteist.autoimageslider.SliderView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements CTInboxListener, DisplayUnitListener, LocationListener, InboxMessageListener, CTGeofenceEventsListener, InAppNotificationButtonListener, PushPermissionResponseListener {
    protected LocationManager locationManager;
    protected LocationListener locationListener;
    SliderView sliderView;
    ArrayList<SliderData> sliderDataArrayList = new ArrayList<>();
    //    private FirebaseAnalytics mFirebaseAnalytics;
    CleverTapAPI clevertapDefaultInstance;


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Log.d("Clevertap", "get_extras_foreground " + intent.getExtras().getString("wzrk_Accid"));

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
        clevertapDefaultInstance.enablePersonalization();
        final Handler handler = new Handler();
        findViewById(R.id.appinbox).setOnClickListener(v -> {
            ArrayList<String> tabs = new ArrayList<>();
            tabs.add("Promotions");
            tabs.add("Offers");
//            clevertapDefaultInstance.showAppInbox();
            CTInboxStyleConfig styleConfig = new CTInboxStyleConfig();
            styleConfig.setFirstTabTitle("First Tab");
            styleConfig.setNoMessageViewText("No Notifi");
            styleConfig.isUsingTabs();
            styleConfig.describeContents();
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
            if (clevertapDefaultInstance != null) {
                clevertapDefaultInstance.showAppInbox(styleConfig); //With Tabs
            }

        });
        findViewById(R.id.getmsg).setOnClickListener(v -> {
            clevertapDefaultInstance.pushEvent("Abeezergetmsg");
        });
        findViewById(R.id.webview).setOnClickListener(view -> MainActivity.this.startActivity(new Intent(MainActivity.this, webviewActivity.class)));
        clevertapDefaultInstance.setCTInboxMessageListener(this);
        clevertapDefaultInstance.setDisplayUnitListener(this);

        if (clevertapDefaultInstance != null) {
            //Set the Notification Inbox Listener
            clevertapDefaultInstance.setCTNotificationInboxListener(this);
            clevertapDefaultInstance.initializeInbox();
            clevertapDefaultInstance.pushEvent("App_launch1");
            clevertapDefaultInstance.pushEvent("App_launch2");
            clevertapDefaultInstance.pushEvent("App_launch3");
        }

        Log.d("clevertap123", "test");
        clevertapDefaultInstance.setInAppNotificationButtonListener(this);


        handler.postDelayed(() -> {
            JSONObject jsonObject = CTLocalInApp.builder()
                    .setInAppType(CTLocalInApp.InAppType.ALERT)
                    .setTitleText("Get Notified")
                    .setMessageText("Enable Notification permission")
                    .followDeviceOrientation(true)
                    .setPositiveBtnText("Allow")
                    .setNegativeBtnText("Cancel").setFallbackToSettings(true)
                    .build();
            clevertapDefaultInstance.promptPushPrimer(jsonObject);

        }, 500);

        clevertapDefaultInstance.setLocation(clevertapDefaultInstance.getLocation()); //android.location.Location
//        Log.d("CleverTap T Default", "location Default"+location.toString());
        clevertapDefaultInstance.syncVariables();
        Log.d("CleverTapT", "theme value CT" + clevertapDefaultInstance.getVariableValue("theme"));

        Log.d("CleverTapT", "location CT" + clevertapDefaultInstance.getLocation());
        setupCleverTapGeofence();


        ;
        sliderView = findViewById(R.id.slider);
        clevertapDefaultInstance.getAllDisplayUnits();
        findViewById(R.id.pushnotification).setOnClickListener(v -> clevertapDefaultInstance.pushEvent("AbeezerPushEvent"));
        findViewById(R.id.inappnotif).setOnClickListener(v -> {


            clevertapDefaultInstance.pushEvent("abeezerinapnotif");

            Log.d("CleverTap", "product_1" + clevertapDefaultInstance.getProperty("testt"));
        });
        findViewById(R.id.pushev).setOnClickListener(v -> MainActivity.this.startActivity(new Intent(MainActivity.this, CustomEventActivity.class)));
        findViewById(R.id.createuser).setOnClickListener(view -> MainActivity.this.startActivity(new Intent(MainActivity.this, CreateUser.class)));
        clevertapDefaultInstance.recordScreen("Home");
        clevertapDefaultInstance.enableDeviceNetworkInfoReporting(true);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 1);
        }
//        CleverTapInstanceConfig clevertapAdditionalInstanceConfig = CleverTapInstanceConfig.createInstance(this, "TEST-468-W87-546Z", "TEST-ab0-b64");
//        clevertapAdditionalInstanceConfig.setDebugLevel(CleverTapAPI.LogLevel.DEBUG); // default is CleverTapAPI.LogLevel.INFO
//
//        clevertapAdditionalInstanceConfig.setAnalyticsOnly(true); // disables the user engagement features of the instance, default is false
//
//        clevertapAdditionalInstanceConfig.useGoogleAdId(true); // enables the collection of the Google ADID by the instance, default is false
//
//        clevertapAdditionalInstanceConfig.enablePersonalization(false); //enables personalization, default is true.
//        CleverTapAPI clevertapAdditionalInstance = CleverTapAPI.instanceWithConfig(clevertapAdditionalInstanceConfig);


    }

    @Override
    public void onDisplayUnitsLoaded(ArrayList<CleverTapDisplayUnit> units) {
        if (sliderDataArrayList != null) {
            sliderDataArrayList.removeAll(sliderDataArrayList);
        }
        Log.d("CleverTap", "Display units" + units);
        CleverTapAPI.getDefaultInstance(getApplicationContext()).pushDisplayUnitViewedEventForID(units.get(0).getUnitID());
        for (int i = 0; i < units.size(); i++) {
            CleverTapDisplayUnit unit = units.get(i);
            for (CleverTapDisplayUnitContent j : unit.getContents()) {
                //getting urls and adding to array list
                sliderDataArrayList.add(new SliderData(j.getMedia()));

            }
        }
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
        CTGeofenceSettings ctGeofenceSettings = new CTGeofenceSettings.Builder().enableBackgroundLocationUpdates(true)//boolean to enable background location updates
                .setLogLevel(Logger.VERBOSE)//Log Level
                .setLocationAccuracy(CTGeofenceSettings.ACCURACY_HIGH)//byte value for Location Accuracy
                .setLocationFetchMode(CTGeofenceSettings.FETCH_CURRENT_LOCATION_PERIODIC)//byte value for Fetch Mode
                .setGeofenceMonitoringCount(100)//int value for number of Geofences CleverTap can monitor
                .setInterval(3600000)//long value for interval in milliseconds
                .setFastestInterval(1800000)//long value for fastest interval in milliseconds
                .setSmallestDisplacement(1000f)//float value for smallest Displacement in meters
                .setGeofenceNotificationResponsiveness(300000)// int value for geofence notification responsiveness in milliseconds
                .build();
        CTGeofenceAPI.getInstance(this).init(ctGeofenceSettings, clevertapDefaultInstance);

        try {
            CTGeofenceAPI.getInstance(this).triggerLocation();
        } catch (Exception e) {
            // thrown when this method is called before geofence SDK initialization
            Log.e("clevertap Exception.triggerLocation", "=" + e);
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

        Log.d("CleverTap T Default", "location Default" + location);

        Log.d("CleverTap T CT", "location CT" + cleverTapAPI.getLocation());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1001) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("CleverTap permission", "User granted permission");
                // Proceed with showing notifications
            } else {
                Log.d("CleverTap permission", "User denied permission");
                // Optionally guide user to settings
            }
        }
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
    public void onInAppButtonClick(HashMap<String, String> hashMap) {
        Log.d("CleverTap", "Button click callback" + hashMap);
    }

    @Override
    public void onPushPermissionResponse(boolean accepted) {

    }
}
