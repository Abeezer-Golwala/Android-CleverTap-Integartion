package com.example.myapplication;

import android.os.Bundle;

import androidx.annotation.NonNull;

import com.clevertap.android.sdk.pushnotification.fcm.CTFcmMessageHandler;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFcmMessageListenerService extends FirebaseMessagingService {
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
    }


    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        Bundle extras = new Bundle();
        for (Map.Entry<String, String> entry : message.getData().entrySet()) {
            extras.putString(entry.getKey(), entry.getValue());
        }

        new CTFcmMessageHandler().createNotification(getApplicationContext(), message);
    }
}