package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.clevertap.android.sdk.pushnotification.fcm.CTFcmMessageHandler;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFcmMessageListenerService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        Bundle extras = new Bundle();
        for (Map.Entry<String, String> entry : message.getData().entrySet()) {
            extras.putString(entry.getKey(), entry.getValue());
        }
        Log.d("CleverTap", "printing extras" + extras.get("wzrk_cid"));
        if (extras.get("wzrk_sound") != null) {
//            CleverTapAPI.createNotificationChannel(this, , "abtest", "clevertap channel", 3, true, extras.get("wzrk_sound").toString());
        }


//        CleverTapAPI.processPushNotification(getApplicationContext(), extras);
//        CleverTapAPI.getDefaultInstance(this).pushNotificationViewedEvent(extras);
//        Handler handler = new Handler(Looper.getMainLooper());
//        GlobalScope.launch (Dispatchers.IO) {
//            CTFcmMessageHandler().createNotification(AppContext.getInstance(), remoteMessage)
//        }
//        handler.postDelayed(() -> {
        new CTFcmMessageHandler().createNotification(getApplicationContext(), message);
        Log.d("notClevertap", "data in remote message" + extras);
        if (message.getNotification() != null) {
            Log.d("notClevertap", "data in remote message" + message.getNotification().getTitle());
        }
//        }, 3000);

//        handler.postDelayed(() -> {
//
//            CleverTapAPI.processPushNotification(getApplicationContext(), extras);
//            CleverTapAPI.getDefaultInstance(this).pushNotificationViewedEvent(extras);
//        }, 3000);
    }


}