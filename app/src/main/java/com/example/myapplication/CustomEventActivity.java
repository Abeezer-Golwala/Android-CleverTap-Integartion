package com.example.myapplication;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.clevertap.android.sdk.CleverTapAPI;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class CustomEventActivity extends AppCompatActivity {

    int keyn = 0;
    EditText[] edKeys = new EditText[20];
    EditText[] edValues = new EditText[20];
    Spinner[] typeSpinners = new Spinner[20];
    Spinner[] booleanSpinners = new Spinner[20];
    String[] selectedTypes = new String[20];

    EditText eventname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_event);

        CleverTapAPI clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(getApplicationContext());
        CleverTapAPI.setDebugLevel(CleverTapAPI.LogLevel.VERBOSE);

        HashMap<String, Object> eventProps = new HashMap<>();
        eventname = findViewById(R.id.eventName);

        findViewById(R.id.addprop).setOnClickListener(v -> {
            LinearLayout ll = findViewById(R.id.ll1);

            edKeys[keyn] = new EditText(this);
            edKeys[keyn].setHint("Enter Event Property Key");
            ll.addView(edKeys[keyn]);

            edValues[keyn] = new EditText(this);
            edValues[keyn].setHint("Value");
            edValues[keyn].setInputType(InputType.TYPE_CLASS_TEXT);
            ll.addView(edValues[keyn]);

            booleanSpinners[keyn] = new Spinner(this);
            ArrayAdapter<String> boolAdapter = new ArrayAdapter<>(
                    this, android.R.layout.simple_spinner_item, new String[]{"true", "false"});
            boolAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            booleanSpinners[keyn].setAdapter(boolAdapter);
            booleanSpinners[keyn].setVisibility(View.GONE);
            ll.addView(booleanSpinners[keyn]);

            typeSpinners[keyn] = new Spinner(this);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_spinner_item,
                    new String[]{"String", "Float", "Boolean", "DateTime"}
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            typeSpinners[keyn].setAdapter(adapter);
            ll.addView(typeSpinners[keyn]);

            int currentIndex = keyn;

            typeSpinners[keyn].setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selected = parent.getItemAtPosition(position).toString();
                    selectedTypes[currentIndex] = selected;

                    EditText valueField = edValues[currentIndex];
                    Spinner boolSpinner = booleanSpinners[currentIndex];

                    valueField.setText("");
                    valueField.setInputType(InputType.TYPE_CLASS_TEXT);
                    valueField.setFocusable(true);
                    valueField.setFocusableInTouchMode(true);
                    valueField.setVisibility(View.VISIBLE);
                    boolSpinner.setVisibility(View.GONE);
                    valueField.setOnClickListener(null);

                    switch (selected) {
                        case "Float":
                            valueField.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                            valueField.setText("0");
                            break;

                        case "Boolean":
                            valueField.setVisibility(View.GONE);
                            boolSpinner.setVisibility(View.VISIBLE);
                            boolSpinner.setSelection(0); // default true
                            break;

                        case "DateTime":
                            valueField.setFocusable(false);
                            valueField.setClickable(true);

                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                            Calendar calendar = Calendar.getInstance();
                            valueField.setText(sdf.format(calendar.getTime()));

                            valueField.setOnClickListener(v -> {
                                DatePickerDialog datePickerDialog = new DatePickerDialog(
                                        CustomEventActivity.this,
                                        (view1, year, month, dayOfMonth) -> {
                                            TimePickerDialog timePickerDialog = new TimePickerDialog(
                                                    CustomEventActivity.this,
                                                    (view2, hourOfDay, minute) -> {
                                                        calendar.set(year, month, dayOfMonth, hourOfDay, minute);
                                                        valueField.setText(sdf.format(calendar.getTime()));
                                                    },
                                                    calendar.get(Calendar.HOUR_OF_DAY),
                                                    calendar.get(Calendar.MINUTE),
                                                    true
                                            );
                                            timePickerDialog.show();
                                        },
                                        calendar.get(Calendar.YEAR),
                                        calendar.get(Calendar.MONTH),
                                        calendar.get(Calendar.DAY_OF_MONTH)
                                );
                                datePickerDialog.show();
                            });
                            break;

                        default:
                            valueField.setInputType(InputType.TYPE_CLASS_TEXT);
                            valueField.setText("");
                            break;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            selectedTypes[keyn] = "String";
            keyn++;
        });

        findViewById(R.id.uploadev).setOnClickListener(v -> {
            String evn = eventname.getText().toString().trim();
            if (evn.isEmpty()) {
                Toast.makeText(this, "Please enter event name", Toast.LENGTH_SHORT).show();
                return;
            }

            for (int i = 0; i < keyn; i++) {
                String key = edKeys[i].getText().toString().trim();
                String type = selectedTypes[i];

                if (key.isEmpty()) continue;

                Object value;
                try {
                    switch (type) {
                        case "Float":
                            String floatStr = edValues[i].getText().toString();
                            value = floatStr.isEmpty() ? 0f : Float.parseFloat(floatStr);
                            break;
                        case "Boolean":
                            value = booleanSpinners[i].getSelectedItem().equals("true");
                            break;
                        case "DateTime":
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                            String dateStr = edValues[i].getText().toString();
                            Date date = sdf.parse(dateStr);
                            value = date != null ? date : new Date();
                            break;
                        case "String":
                        default:
                            String str = edValues[i].getText().toString();
                            value = str.isEmpty() ? null : str;
                            break;
                    }
                    eventProps.put(key, value);
                } catch (ParseException | NumberFormatException e) {
                    Log.e("EventError", "Invalid input for key: " + key, e);
                }
            }

            Log.d("EventProps", eventProps.toString());
            clevertapDefaultInstance.pushEvent(evn, eventProps);
            eventProps.clear();
            Toast.makeText(this, "Event Pushed", Toast.LENGTH_SHORT).show();
        });
    }
}