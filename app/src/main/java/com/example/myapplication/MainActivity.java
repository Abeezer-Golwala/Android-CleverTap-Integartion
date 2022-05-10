package com.example.myapplication;

import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.clevertap.android.sdk.CTInboxListener;
import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.displayunits.DisplayUnitListener;
import com.clevertap.android.sdk.displayunits.model.CleverTapDisplayUnit;
import com.clevertap.android.sdk.displayunits.model.CleverTapDisplayUnitContent;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements CTInboxListener,DisplayUnitListener {
    Button createu,pushpbt,appinbox,getmsg,inappnotif,nativedisp;
    CardView c;
    TextView  text1,titlem,msg;

    private FirebaseAnalytics mFirebaseAnalytics ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CleverTapAPI clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(getApplicationContext());
        CleverTapAPI.setDebugLevel(CleverTapAPI.LogLevel.INFO);
        CleverTapAPI cleverTapDefaultInstance = CleverTapAPI.getDefaultInstance(this);
        CleverTapAPI.setDebugLevel(3);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(MainActivity.this);
        if (cleverTapDefaultInstance != null) {
            //Set the Notification Inbox Listener
            cleverTapDefaultInstance.setCTNotificationInboxListener(this);
            //Initialize the inbox and wait for callbacks on overridden methods
            cleverTapDefaultInstance.initializeInbox();
        }
        CleverTapAPI.getDefaultInstance(this).setDisplayUnitListener(this);
        CleverTapAPI.createNotificationChannel(getApplicationContext(),"abtest","abtest","test ab",NotificationManager.IMPORTANCE_MAX,true);
        createu = findViewById(R.id.createuser);
        pushpbt = findViewById(R.id.pushnotification);
        text1 = findViewById(R.id.textv);
        appinbox = findViewById(R.id.appinbox);
        getmsg = findViewById(R.id.getmsg);
        inappnotif = findViewById(R.id.inappnotif);
        nativedisp = findViewById(R.id.nativedisp);
        c=findViewById(R.id.c1);
        titlem = findViewById(R.id.titlem);
        msg = findViewById(R.id.msg);
        appinbox.setOnClickListener(v->{
            cleverTapDefaultInstance.showAppInbox();
        });
        getmsg.setOnClickListener(v -> { clevertapDefaultInstance.pushEvent("Abeezergetmsg"); });
        pushpbt.setOnClickListener(v -> { clevertapDefaultInstance.pushEvent("AbeezerPushEvent"); });
        inappnotif.setOnClickListener(v->{clevertapDefaultInstance.pushEvent("abeezerinapnotif");});
        nativedisp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clevertapDefaultInstance.pushEvent("abeezernativedisp");
                Bundle params = new Bundle();
                params.putString("image_name", "name");
                params.putString("full_text", "text");
                mFirebaseAnalytics.logEvent("share_image", params);
            }
        });
        findViewById(R.id.pushev).setOnClickListener(v->{
            Intent i = new Intent(MainActivity.this,CustomEventActivity.class);
            MainActivity.this.startActivity(i);
        });
        createu.setOnClickListener(view -> {
            Intent i = new Intent(MainActivity.this,CreateUser.class);
            MainActivity.this.startActivity(i);
            // each of the below mentioned fields are optional
//            HashMap<String, Object> profileUpdate = new HashMap<String, Object>();
//            profileUpdate.put("Name", "Test Test");    // String
//            profileUpdate.put("Identity", 6102);      // String or number
//            profileUpdate.put("Email", " abeezer@android2.com "); // Email address of the user
//            profileUpdate.put("Phone", "+918800000000");   // Phone (with the country code, starting with +)
//            profileUpdate.put("Gender", "M");             // Can be either M or F
//            profileUpdate.put("DOB", new Date());         // Date of Birth. Set the Date object to the appropriate value first
//            // optional fields. controls whether the user will be sent email, push etc.
//
//            profileUpdate.put("MSG-email", true);        // Disable email notifications
//            profileUpdate.put("MSG-push", true);          // Enable push notifications
//            profileUpdate.put("MSG-sms", true);          // Disable SMS notifications
//            profileUpdate.put("MSG-whatsapp", true);      // Enable WhatsApp notifications
//            ArrayList<String> stuff = new ArrayList<String>();
//            stuff.add("bag");
//            stuff.add("shoes");
//            profileUpdate.put("MyStuff", stuff);                        //ArrayList of Strings
//            String[] otherStuff = {"Jeans","Perfume"};
//            profileUpdate.put("MyStuff", otherStuff);                   //String Array
//            clevertapDefaultInstance.onUserLogin(profileUpdate);
//            clevertapDefaultInstance.pushEvent("Product viewed");
//            text1.setText("User Created");
        });
    }

    @Override
    public void onDisplayUnitsLoaded(ArrayList<CleverTapDisplayUnit> units) {
        for (int i = 0; i <units.size() ; i++) {
            CleverTapDisplayUnit unit = units.get(i);
            prepareDisplayView(unit);
        }
    }

    private void prepareDisplayView(CleverTapDisplayUnit unit) {
        for (CleverTapDisplayUnitContent i:unit.getContents()) {
            titlem.setText(i.getTitle());
            msg.setText(i.getMessage());
            //Notification Viewed Event
            CleverTapAPI.getDefaultInstance(this).pushDisplayUnitViewedEventForID(unit.getUnitID());

            //Notification Clicked Event
            c.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    CleverTapAPI.getDefaultInstance(getApplicationContext()).pushDisplayUnitClickedEventForID(unit.getUnitID());

                }
            });
        }
    }

    @Override
    public void inboxDidInitialize() {
    }

    @Override
    public void inboxMessagesDidUpdate() {

    }

}

