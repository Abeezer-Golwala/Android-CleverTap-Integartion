package com.example.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.ContentResolver;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.clevertap.android.geofence.CTGeofenceAPI;
import com.clevertap.android.geofence.CTGeofenceSettings;
import com.clevertap.android.geofence.Logger;
import com.clevertap.android.geofence.interfaces.CTGeofenceEventsListener;
import com.clevertap.android.sdk.CTInboxListener;
import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.InAppNotificationButtonListener;
import com.clevertap.android.sdk.InboxMessageListener;
import com.clevertap.android.sdk.PushPermissionResponseListener;
import com.clevertap.android.sdk.displayunits.DisplayUnitListener;
import com.clevertap.android.sdk.displayunits.model.CleverTapDisplayUnit;
import com.clevertap.android.sdk.displayunits.model.CleverTapDisplayUnitContent;
import com.clevertap.android.sdk.inapp.CTLocalInApp;
import com.clevertap.android.sdk.inbox.CTInboxMessage;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements CTInboxListener, DisplayUnitListener, LocationListener, InboxMessageListener, CTGeofenceEventsListener, InAppNotificationButtonListener, PushPermissionResponseListener {

    private static final String TAG = "CleverTap";
    private static final int keepTestIcon = R.drawable.testicon;
    protected LocationManager locationManager;
    ViewPager2 viewPager;
    ArrayList<SliderData> sliderDataArrayList = new ArrayList<>();
    CleverTapAPI clevertapDefaultInstance;
    ViewPager2Adapter viewPager2Adapter;
    Button btnShowInbox;
    Button btnWebview;
    Button btnCustomEvent;
    Button btnCreateProfile;
    private boolean isInboxInitialized = false; // To ensure inbox is shown only after loaded

    private String getUriStringForDrawable(int resourceId) {
        return ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                + getResources().getResourcePackageName(resourceId)
                + '/' + getResources().getResourceTypeName(resourceId)
                + '/' + getResources().getResourceEntryName(resourceId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Request POST_NOTIFICATIONS on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
        final Handler handler = new Handler();

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


        // Initialize CleverTap SDK
        clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(getApplicationContext());
        if (clevertapDefaultInstance != null) {
            clevertapDefaultInstance.setDisplayUnitListener(this);
            clevertapDefaultInstance.setCTNotificationInboxListener(this);
            clevertapDefaultInstance.setCTInboxMessageListener(this);
            clevertapDefaultInstance.initializeInbox();
            if (clevertapDefaultInstance != null) {
                //Set the Notification Inbox Listener
                clevertapDefaultInstance.setCTNotificationInboxListener(this);
                clevertapDefaultInstance.initializeInbox();
                clevertapDefaultInstance.pushEvent("App_launch1");
                clevertapDefaultInstance.pushEvent("App_launch2");
                clevertapDefaultInstance.pushEvent("App_launch3");
            }
            clevertapDefaultInstance.setInAppNotificationButtonListener(this);


            CleverTapAPI.createNotificationChannel(
                    getApplicationContext(),
                    "abtest",
                    "Your Channel Name",
                    "Your Channel Description",
                    NotificationManager.IMPORTANCE_HIGH, // >= O
                    true // Show badge
            );


            // Optionally trigger a first fetch of display units
            clevertapDefaultInstance.getAllDisplayUnits();
        } else {
            Log.e(TAG, "CleverTap instance is NULL.");
        }

        // ViewPager/Slider Section (optional, for your banners/carousel)
        viewPager = findViewById(R.id.slider);
        sliderDataArrayList.add(new SliderData(getUriStringForDrawable(R.drawable.default_slider_1), null, null));
        sliderDataArrayList.add(new SliderData(getUriStringForDrawable(R.drawable.default_slider_2), null, null));
        sliderDataArrayList.add(new SliderData(getUriStringForDrawable(R.drawable.default_slider_3), null, null));
        viewPager2Adapter = new ViewPager2Adapter(this, sliderDataArrayList);
        viewPager.setAdapter(viewPager2Adapter);

        // Button Setup
        btnShowInbox = findViewById(R.id.appinbox);
        btnWebview = findViewById(R.id.webview);
        btnCustomEvent = findViewById(R.id.getmsg);
        findViewById(R.id.createuser).setOnClickListener(view -> MainActivity.this.startActivity(new Intent(MainActivity.this, CreateUser.class)));
        findViewById(R.id.pushnotification).setOnClickListener(v -> clevertapDefaultInstance.pushEvent("AbeezerPushEvent"));
        findViewById(R.id.inappnotif).setOnClickListener(v -> {


            clevertapDefaultInstance.pushEvent("abeezerinapnotif");

            Log.d("CleverTap", "product_1" + clevertapDefaultInstance.getProperty("testt"));
        });
        findViewById(R.id.pushev).setOnClickListener(v -> MainActivity.this.startActivity(new Intent(MainActivity.this, CustomEventActivity.class)));

        if (btnShowInbox != null) {
            btnShowInbox.setOnClickListener(v -> {
                if (clevertapDefaultInstance != null && isInboxInitialized) {
                    clevertapDefaultInstance.showAppInbox(); // Will open the CleverTap Inbox UI
                } else {
                    Toast.makeText(MainActivity.this, "Inbox not ready yet!", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (btnWebview != null) {
            btnWebview.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, webviewActivity.class); // Replace with your webview activity
                startActivity(intent);
            });
        }

        if (btnCustomEvent != null) {
            btnCustomEvent.setOnClickListener(v -> {
                if (clevertapDefaultInstance != null) {
                    clevertapDefaultInstance.pushEvent("Abeezergetmsg");

                    Toast.makeText(MainActivity.this, "Inbox Message Triggerd", Toast.LENGTH_SHORT).show();
                }
            });
        }
        clevertapDefaultInstance.enableDeviceNetworkInfoReporting(true);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 1);
        }


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
        clevertapDefaultInstance.parseVariables();
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

    // After activity already running, handle new intents
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "onNewIntent called");
        if (clevertapDefaultInstance != null) {
            Bundle extras = intent.getExtras();
            if (extras != null && CleverTapAPI.getNotificationInfo(extras) != null
                    && CleverTapAPI.getNotificationInfo(extras).fromCleverTap) {
                Log.d(TAG, "onNewIntent: Received CleverTap Push Notification payload");
                clevertapDefaultInstance.pushNotificationClickedEvent(extras);

                super.onNewIntent(intent);

                Log.d("Clevertap", "get_extras_foreground " + intent.getExtras().getString("wzrk_Accid"));
                NotificationUtils.dismissNotification(intent, this);


            } else {
                Log.d(TAG, "onNewIntent: Received normal intent. Extras: " + (extras != null ? extras.toString() : "null"));
            }
        }
    }

    // CleverTap Inbox and Display Units Listeners

    @Override
    public void onDisplayUnitsLoaded(final ArrayList<CleverTapDisplayUnit> units) {
        Log.d(TAG, "onDisplayUnitsLoaded called, units size: " + (units != null ? units.size() : "null"));
        if (units == null || units.isEmpty()) {
            return;
        }
        final ArrayList<SliderData> newSliderItems = new ArrayList<>();
        for (CleverTapDisplayUnit unit : units) {
            if (unit != null && unit.getContents() != null) {
                if (clevertapDefaultInstance != null) {
                    clevertapDefaultInstance.pushDisplayUnitViewedEventForID(unit.getUnitID());
                }
                for (CleverTapDisplayUnitContent content : unit.getContents()) {
                    if (content != null && content.getMedia() != null && !content.getMedia().isEmpty()) {
                        String imageUrl = content.getMedia();
                        String targetUrl = content.getActionUrl();
                        String wzrkId = unit.getUnitID();
                        newSliderItems.add(new SliderData(imageUrl, targetUrl, wzrkId));
                    }
                }
            }
        }
        runOnUiThread(() -> {
            if (viewPager2Adapter != null && !newSliderItems.isEmpty()) {
                viewPager2Adapter.updateData(newSliderItems);
            }
        });
    }

    // Called when Inbox is loaded and ready
    @Override
    public void inboxDidInitialize() {
        Log.d(TAG, "Inbox Initialized");
        isInboxInitialized = true;
        // Optionally, update badge/count
        if (btnShowInbox != null && clevertapDefaultInstance != null) {
            int count = clevertapDefaultInstance.getInboxMessageUnreadCount();
            btnShowInbox.setText("Inbox (" + count + ")");
        }
    }

    // Update inbox badge/count on message update
    @Override
    public void inboxMessagesDidUpdate() {
        Log.d(TAG, "Inbox Messages Updated");
        if (btnShowInbox != null && clevertapDefaultInstance != null) {
            int count = clevertapDefaultInstance.getInboxMessageUnreadCount();
            btnShowInbox.setText("Inbox (" + count + ")");
        }
    }

    // Optional: React to inbox item tap
    @Override
    public void onInboxItemClicked(CTInboxMessage message, int contentPageIndex, int buttonIndex) {
        Log.d(TAG, "Inbox Message Clicked: " + message.getMessageId() + " at page " + contentPageIndex + " button " + buttonIndex);
        // You can open a dedicated screen or handle action URL
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


    // Optional: log user response to system notification permission pop-up
    @Override
    public void onPushPermissionResponse(boolean accepted) {
        Log.i(TAG, "Push Permission Response by User: " + (accepted ? "ACCEPTED" : "DENIED"));
    }

    // If you use ActivityCompat.requestPermissions, implement this!
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101 && permissions.length > 0 && permissions[0].equals(Manifest.permission.POST_NOTIFICATIONS)) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Notification permission granted!");
            } else {
                Log.d(TAG, "Notification permission denied.");
            }
        }
    }

    @Override
    public void onInAppButtonClick(HashMap<String, String> hashMap) {
        Log.d("CleverTap", "Button click callback" + hashMap);
    }
}
