package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.clevertap.android.sdk.CleverTapAPI;

import java.util.ArrayList;
import java.util.HashMap;

public class CreateUser extends AppCompatActivity {
    EditText userpropkey,userpropvalue,namest1,emailst1,numbst1,idst1;
    CheckBox c1,c2,c3,c4;
    public int keyn = 0;
    public EditText ed[] = new EditText[20];
    String[] stringArray1;
    ArrayList<String> newList	=	new	ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        c1 = findViewById(R.id.pushc);
        c2 = findViewById(R.id.smsc);
        c3 = findViewById(R.id.emailc);
        c4 = findViewById(R.id.whatsappc);
        c1.setChecked(true);
        c2.setChecked(true);
        c3.setChecked(true);
        c4.setChecked(true);
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
            profileUpdate.put("Name", namest);    // String
            profileUpdate.put("Identity", idst);      // String or number
            profileUpdate.put("Email", emailst); // Email address of the user
            profileUpdate.put("Phone", numbst);
            profileUpdate.put("MSG-push", c1.isChecked());
            profileUpdate.put("MSG-sms", c2.isChecked());
            profileUpdate.put("MSG-email", c3.isChecked());
            profileUpdate.put("MSG-whatsapp", c4.isChecked());
            clevertapDefaultInstance.onUserLogin(profileUpdate);
//            clevertapDefaultInstance.pushProfile(profileUpdate);
            Toast.makeText(getApplicationContext(), "OnUserLogin Called", Toast.LENGTH_SHORT).show();

        });
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