package com.example.myapplication;


import android.app.Application;
import android.util.Log;

import com.clevertap.android.pushtemplates.PushTemplateNotificationHandler;
import com.clevertap.android.pushtemplates.TemplateRenderer;
import com.clevertap.android.sdk.ActivityLifecycleCallback;
import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.interfaces.NotificationHandler;
import com.clevertap.android.sdk.pushnotification.CTPushNotificationListener;
import com.clevertap.android.sdk.pushnotification.PushConstants;

import java.util.HashMap;
public class GlobalApplication extends Application implements CTPushNotificationListener {

    @Override
    public void onCreate() {
        ActivityLifecycleCallback.register(this);
        super.onCreate();
        CleverTapAPI.setNotificationHandler((NotificationHandler)new PushTemplateNotificationHandler());
        CleverTapAPI.createNotificationChannel(getApplicationContext(),"abtest","abtest","test ab",3,true);
        CleverTapAPI.getDefaultInstance(this).setCTPushNotificationListener(this);
        TemplateRenderer.setDebugLevel(3);
        Log.d("payload","Here");
        System.out.println("testctt");
    }
    @Override
    public void onNotificationClickedPayloadReceived(HashMap<String, Object> payload) {
        System.out.println("testctt"+payload.toString());
    }
}
