package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.pushnotification.fcm.CTFcmMessageHandler;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Objects;

public class MyFcmMessageListenerService extends FirebaseMessagingService {
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Objects.requireNonNull(CleverTapAPI.getDefaultInstance(this)).pushFcmRegistrationId(token, true);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        String gifUrl = message.getData().get("gifUrl");
//        sendNotification(gifUrl);
        Bundle extras = new Bundle();
        for (Map.Entry<String, String> entry : message.getData().entrySet()) {
            extras.putString(entry.getKey(), entry.getValue());
        }
        Log.d("CleverTap", "printing extras" + extras.get("wzrk_cid"));
        if (extras.get("wzrk_sound") != null) {
//            CleverTapAPI.createNotificationChannel(this, , "abtest", "clevertap channel", 3, true, extras.get("wzrk_sound").toString());
        }
        new CTFcmMessageHandler().createNotification(getApplicationContext(), message);
        CleverTapAPI.getDefaultInstance(this).pushNotificationViewedEvent(extras);
        Log.d("notClevertap", "data in remote message" + extras);
        if (message.getNotification() != null) {
            Log.d("notClevertap", "data in remote message" + message.getNotification().getTitle());
        }

    }
}