package com.example.myapplication;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.clevertap.android.sdk.CleverTapAPI;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class CreateUser extends AppCompatActivity {
    public int keyn = 0;
    public EditText[] ed = new EditText[20];
    EditText userpropkey, userpropvalue, namest1, emailst1, numbst1, idst1;
    CheckBox pushc, smsc, emailc, whatsappc, Promotional, Transactional;
    ArrayList<String> newList = new ArrayList<>();
    Date selectedDob = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_create_user);

        CleverTapAPI clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(getApplicationContext());
        CleverTapAPI.setDebugLevel(CleverTapAPI.LogLevel.VERBOSE);
        CleverTapAPI.createNotificationChannel(getApplicationContext(), "Promotional", "abtest", "test ab", 5, true);
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
        HashMap<String, Object> userprop = new HashMap<>();


        TextView dobLabel = findViewById(R.id.label_dob);
        EditText dobEditText = findViewById(R.id.dob);
        Calendar defaultCalendar = Calendar.getInstance();
        defaultCalendar.set(2000, Calendar.JANUARY, 1);
        selectedDob = defaultCalendar.getTime(); // default value
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        dobEditText.setText(sdf.format(selectedDob)); // show default


// DOB Picker Logic
        dobEditText.setOnClickListener(v -> {
            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select your date of birth")
                    .setSelection(selectedDob.getTime())
                    .build();

            datePicker.addOnPositiveButtonClickListener(selection -> {
                selectedDob = new Date(selection);
                dobEditText.setText(sdf.format(selectedDob));
                dobLabel.setText("DOB Selected"); // ✅ update label after selection
            });

            datePicker.show(getSupportFragmentManager(), "DOB_PICKER");
        });


        findViewById(R.id.userpropv).setOnClickListener(v -> {
            LinearLayout ll = findViewById(R.id.ll1);
            ed[keyn] = new EditText(CreateUser.this);
            ed[keyn].setId(keyn);
            ed[keyn].setHint("Enter Custom User Property Value");
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            ed[keyn].setLayoutParams(p);
            ll.addView(ed[keyn]);
            keyn++;
        });

        Toast.makeText(getApplicationContext(), "Create User", Toast.LENGTH_SHORT).show();
        //create user function
        findViewById(R.id.cubt).setOnClickListener(v -> {
            Log.d("CreateFunc", "CreateFunc");
            String namest = namest1.getText().toString();
            String emailst = emailst1.getText().toString();
            String numbst = numbst1.getText().toString();
            String idst = idst1.getText().toString();
            HashMap<String, Object> profileUpdate = new HashMap<>();
            profileUpdate.put("Identity", idst);
            profileUpdate.put("Name", namest);

            profileUpdate.put("Email", emailst);
            profileUpdate.put("Phone", numbst);
            if (selectedDob != null) {
                Log.d("DOB_SELECTED", "Selected Date object: " + selectedDob.toString());
                profileUpdate.put("DOB", selectedDob);
                // 👉 You can pass selectedDob directly to API, convert to timestamp, etc.
            } else {
                Log.d("DOB_SELECTED", "No DOB selected");
                // 👉 Send nothing or null
            }


            profileUpdate.put("MSG-push", pushc.isChecked());
            profileUpdate.put("MSG-sms", smsc.isChecked());
            profileUpdate.put("MSG-email", emailc.isChecked());
            profileUpdate.put("MSG-whatsapp", whatsappc.isChecked());

            ArrayList<String> channelsub = new ArrayList<>();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (Promotional.isChecked()) {
                    channelsub.add("Promotional");
                    CleverTapAPI.createNotificationChannel(getApplicationContext(), "Promotional", "Promotional channel", "Promotional channel ab", 5, true, "glass.wav");

                } else {
                    CleverTapAPI.deleteNotificationChannel(this, "Promotional");

                }
                if (Transactional.isChecked()) {
                    channelsub.add("Transactional");
                    CleverTapAPI.createNotificationChannel(getApplicationContext(), "Transactional", "Transactional", "Transactional channel ab", 5, true, "glass.wav");

                } else {
                    CleverTapAPI.deleteNotificationChannel(getApplicationContext(), "Transactional");

                }
            }
            profileUpdate.put("push channel", channelsub);

            Log.d("clevertap", "onuserloginpayload" + profileUpdate);
            clevertapDefaultInstance.onUserLogin(profileUpdate);

            Toast.makeText(getApplicationContext(), "OnUserLogin Called", Toast.LENGTH_SHORT).show();

        });
//        clevertapDefaultInstance.setOffline(!pushc.isChecked());
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            CleverTapAPI.getDefaultInstance(getApplicationContext()).pushNotificationClickedEvent(intent.getExtras());
            NotificationUtils.dismissNotification(intent, getApplicationContext());
        }
    }


}