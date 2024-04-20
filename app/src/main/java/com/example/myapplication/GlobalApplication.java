package com.example.myapplication;


import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.clevertap.android.pushtemplates.PushTemplateNotificationHandler;
import com.clevertap.android.pushtemplates.TemplateRenderer;
import com.clevertap.android.sdk.ActivityLifecycleCallback;
import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.pushnotification.CTPushNotificationListener;

import java.util.HashMap;

public class GlobalApplication extends Application implements CTPushNotificationListener, Application.ActivityLifecycleCallbacks {
    @Override
    public void onCreate() {

        ActivityLifecycleCallback.register(this);
        registerActivityLifecycleCallbacks(this);
        CleverTapAPI.setNotificationHandler(new PushTemplateNotificationHandler());
        super.onCreate();
        CleverTapAPI.createNotificationChannel(getApplicationContext(), "abtest", "abtest", "test ab", 5, true);
        CleverTapAPI.getDefaultInstance(this).setCTPushNotificationListener(this);
        TemplateRenderer.setDebugLevel(3);
        CleverTapAPI.setDebugLevel(3);
        CleverTapAPI.getDefaultInstance(this).enableDeviceNetworkInfoReporting(true);

    }

    @Override
    public void onNotificationClickedPayloadReceived(HashMap<String, Object> payload) {
        Log.d("CleverTap", "onNotificationClickedPayloadReceived: " + payload);
    }


    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            NotificationUtils.dismissNotification(activity.getIntent(), getApplicationContext());
        }
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }
}
