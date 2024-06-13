package com.example.myapplication;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

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

    private void sendNotification(String gifUrl) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("abtest", "GIF Notifications", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        // Create custom notification layout
        RemoteViews notificationLayout = new RemoteViews(getPackageName(), R.layout.notification_layout);

        // Load GIF using Glide
//        Glide.with(this)
//                .asGif()
//                .load(gifUrl)
//                .into(new CustomTarget<Drawable>() {
//                    @Override
//                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
//                        // Set the GIF drawable to ImageView in custom layout
//                        notificationLayout.setImageViewBitmap(resource);
//
//                        // Build the notification
//                        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(MyFcmMessageListenerService.this, "abtest")
//                                .setSmallIcon(R.drawable.testicon)
//                                .setContentTitle("GIF Notification")
//                                .setContentText("You've received a new GIF")
//                                .setCustomBigContentView(notificationLayout)
//                                .setContentIntent(pendingIntent)
//                                .setAutoCancel(true);
//
//                        // Show the notification
//                        notificationManager.notify(0, notificationBuilder.build());
//                    }
//
//                    @Override
//                    public void onLoadCleared(@Nullable Drawable placeholder) {
//
//                    }
//                });
    }

}