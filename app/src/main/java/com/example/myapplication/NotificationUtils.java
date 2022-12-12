package com.example.myapplication;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class NotificationUtils {
    //Require to close notification on action button click
    public static void dismissNotification(Intent intent, Context applicationContext){
        Bundle extras = intent.getExtras();
        if (extras != null) {
            String actionId = extras.getString("actionId");
            if (actionId != null) {
                boolean autoCancel = extras.getBoolean("autoCancel", true);
                int notificationId = extras.getInt("notificationId", -1);
                if (autoCancel && notificationId > -1) {
                    NotificationManager notifyMgr =
                            (NotificationManager) applicationContext.getSystemService(Context.NOTIFICATION_SERVICE);
                    notifyMgr.cancel(notificationId);                }

            }
        }
    }
}
