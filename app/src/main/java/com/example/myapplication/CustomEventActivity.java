package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.clevertap.android.sdk.CleverTapAPI;

import java.util.HashMap;

public class CustomEventActivity extends AppCompatActivity {
    EditText eventname;
    public int keyn = 0;
    public EditText ed[] = new EditText[20];
    public EditText ed1[] = new EditText[20];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_event);
        CleverTapAPI clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(getApplicationContext());
        CleverTapAPI.setDebugLevel(CleverTapAPI.LogLevel.VERBOSE);
        HashMap<String, Object> eventpropl = new HashMap<String, Object>();
        eventname = findViewById(R.id.eventName);
        findViewById(R.id.addprop).setOnClickListener(v->{
            LinearLayout ll = (LinearLayout)findViewById(R.id.ll1);
            ed[keyn] = new EditText(CustomEventActivity.this);
            ed[keyn].setId(keyn);
            ed[keyn].setHint("Enter Event Property Key");
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            ed[keyn].setLayoutParams(p);
            ll.addView(ed[keyn]);
            ed1[keyn] = new EditText(CustomEventActivity.this);
            ed1[keyn].setId(keyn);
            ed1[keyn].setHint("value");
            ed1[keyn].setLayoutParams(p);
            ll.addView(ed1[keyn]);
            keyn++;
        });
        findViewById(R.id.uploadev).setOnClickListener(v->{
            String evn = eventname.getText().toString();
            for(int i = 0;i<keyn;i++){

                eventpropl.put(ed[i].getText().toString(),ed1[i].getText().toString());
                Log.d("Eveprop",""+eventpropl);
            }
            Log.d("Mapd","mapd"+eventpropl);
            clevertapDefaultInstance.pushEvent(evn, eventpropl);
            eventpropl.clear();
            Toast.makeText(getApplicationContext(),"Event Pushed", Toast.LENGTH_SHORT).show();

        });
    }
}