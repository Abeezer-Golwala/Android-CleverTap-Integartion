package com.example.myapplication;

import com.clevertap.android.pushtemplates.PushTemplateNotificationHandler;
import com.clevertap.android.sdk.ActivityLifecycleCallback;
import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.interfaces.NotificationHandler;

@SuppressWarnings({"unused"})
public class PushTemplateHandler extends android.app.Application {

    @Override
    public void onCreate() {
        CleverTapAPI.setNotificationHandler((NotificationHandler)new PushTemplateNotificationHandler());
        ActivityLifecycleCallback.register(this);
        super.onCreate();
    }
}
