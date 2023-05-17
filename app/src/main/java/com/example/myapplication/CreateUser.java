package com.example.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.clevertap.android.sdk.CleverTapAPI;

import com.clevertap.android.sdk.Constants;
import com.clevertap.android.sdk.InAppNotificationListener;
import com.clevertap.android.sdk.inapp.CTInAppNotification;
import com.clevertap.android.sdk.inapp.CTLocalInApp;
import com.clevertap.android.sdk.inapp.InAppListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CreateUser extends AppCompatActivity  {
    EditText userpropkey,userpropvalue,namest1,emailst1,numbst1,idst1;
    CheckBox pushc,smsc,emailc,whatsappc,Promotional,Transactional;
    public int keyn = 0;
    public EditText ed[] = new EditText[20];
    ArrayList<String> newList	=	new	ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        JSONObject jsonObject = CTLocalInApp.builder()
                .setInAppType(CTLocalInApp.InAppType.HALF_INTERSTITIAL)
                .setTitleText("Get Notified")
                .setMessageText("Please enable notifications on your device to use Push Notifications.")
                .followDeviceOrientation(true)
                .setPositiveBtnText("Allow")
                .setNegativeBtnText("Cancel")
                .setBackgroundColor(Constants.WHITE)
                .setBtnBorderColor(Constants.BLUE)
                .setTitleTextColor(Constants.BLUE)
                .setMessageTextColor(Constants.BLACK)
                .setBtnTextColor(Constants.WHITE)
                .setImageUrl("https://picsum.photos/id/237/64/64.jpg")
                .setBtnBackgroundColor(Constants.BLUE)
                .build();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
        CleverTapAPI.getDefaultInstance(getApplicationContext()).promptPushPrimer(jsonObject);
            }
        }, 0);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        CleverTapAPI clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(getApplicationContext());
        CleverTapAPI.setDebugLevel(CleverTapAPI.LogLevel.VERBOSE);
        userpropkey = findViewById(R.id.upet1);
        userpropvalue = findViewById(R.id.upet2);
        namest1 = findViewById(R.id.nameet);
        emailst1 = findViewById(R.id.emailet);
        numbst1 = findViewById(R.id.numbet);
        idst1 = findViewById(R.id.idet);
        pushc = findViewById(R.id.pushc);
        smsc = findViewById(R.id.smsc);
        emailc = findViewById(R.id.emailc);
        whatsappc = findViewById(R.id.whatsappc);
        Promotional = findViewById(R.id.Promotional);
        Transactional = findViewById(R.id.Transactional);
        pushc.setChecked(true);
        smsc.setChecked(true);
        emailc.setChecked(true);
        whatsappc.setChecked(true);
        Promotional.setChecked(true);
        Transactional.setChecked(true);
        HashMap<String, Object> userprop = new HashMap<String, Object>();
        findViewById(R.id.userpropv).setOnClickListener(v -> {
            LinearLayout ll = (LinearLayout) findViewById(R.id.ll1);
            ed[keyn] = new EditText(CreateUser.this);
            ed[keyn].setId(keyn);
            ed[keyn].setHint("Enter Custom User Property Value");
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            ed[keyn].setLayoutParams(p);
            ll.addView(ed[keyn]);
            keyn++;
        });
        //create user function
        findViewById(R.id.cubt).setOnClickListener(v -> {
            Log.d("CreatFunct", "CreatFunct");
            String namest = namest1.getText().toString();
            String emailst = emailst1.getText().toString();
            String numbst = numbst1.getText().toString();
            String idst = idst1.getText().toString();
            HashMap<String, Object> profileUpdate = new HashMap<String, Object>();
            profileUpdate.put("Name", namest);
            profileUpdate.put("Identity", idst);
            profileUpdate.put("Email", emailst);
            profileUpdate.put("Phone", numbst);

            profileUpdate.put("MSG-push", !pushc.isChecked());
            profileUpdate.put("MSG-sms", smsc.isChecked());
            profileUpdate.put("MSG-email", emailc.isChecked());
            profileUpdate.put("MSG-whatsapp", whatsappc.isChecked());

            ArrayList<String> channelsub = new ArrayList<>();
            if (Promotional.isChecked()) {
                channelsub.add("Promotional");
            }
            if (Transactional.isChecked()) {
                channelsub.add("Transactional");
            }

            profileUpdate.put("push channel", channelsub);

            Log.d("clevertap","onuserloginpayload"+profileUpdate);
            clevertapDefaultInstance.onUserLogin(profileUpdate);
            Toast.makeText(getApplicationContext(), "OnUserLogin Called", Toast.LENGTH_SHORT).show();
//
//            Intent i = new Intent(CreateUser.this, loadingscreentest.class);
//            CreateUser.this.startActivity(i);

        });
        clevertapDefaultInstance.setOffline(!pushc.isChecked());
        //Get Upload user
        findViewById(R.id.userpropbt).setOnClickListener(v -> {
            String upkey = userpropkey.getText().toString();
            newList.add(userpropvalue.getText().toString());
            for (int i = 0; i < keyn; i++) {
                newList.add(ed[i].getText().toString());
            }
            userprop.put(upkey, newList);
            clevertapDefaultInstance.pushProfile(userprop);
            Toast.makeText(getApplicationContext(), "Push Profile  Called", Toast.LENGTH_SHORT).show();
        });
    }
    @Override
    protected void onNewIntent(final Intent intent) {
        super.onNewIntent(intent);


        /**
         * On Android 12, clear notification on CTA click when Activity is already running in activity backstack
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

            NotificationUtils.dismissNotification(intent, getApplicationContext());
        }
    }


}