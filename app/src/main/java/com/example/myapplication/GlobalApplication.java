package com.example.myapplication;



import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import com.clevertap.android.pushtemplates.TemplateRenderer;
import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.pushtemplates.PushTemplateNotificationHandler;
import com.clevertap.android.sdk.Constants;
import com.clevertap.android.sdk.inapp.CTLocalInApp;
import com.clevertap.android.sdk.interfaces.NotificationHandler;
import com.clevertap.android.sdk.ActivityLifecycleCallback;

import com.clevertap.android.sdk.pushnotification.CTPushNotificationListener;
import com.clevertap.android.sdk.pushnotification.PushConstants;
import com.clevertap.android.sdk.pushnotification.amp.CTBackgroundJobService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONObject;

import java.util.HashMap;
public class GlobalApplication extends Application implements CTPushNotificationListener {

    @Override
    public void onCreate() {
        ActivityLifecycleCallback.register(this);

        CleverTapAPI.setNotificationHandler((NotificationHandler)new PushTemplateNotificationHandler());
        super.onCreate();
        CleverTapAPI.createNotificationChannel(getApplicationContext(),"abtest","abtest","test ab",3,true);

        CleverTapAPI.getDefaultInstance(this).setCTPushNotificationListener(this);



        TemplateRenderer.setDebugLevel(3);
        CleverTapAPI.setDebugLevel(3);
        CleverTapAPI.getDefaultInstance(this).enableDeviceNetworkInfoReporting(true);

    }
    @Override
    public void onNotificationClickedPayloadReceived(HashMap<String, Object> payload) {
        Log.d("CleverTap", "onNotificationClickedPayloadReceived: "+payload);
    }
}
