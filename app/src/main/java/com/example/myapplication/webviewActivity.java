package com.example.myapplication;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.clevertap.android.sdk.CTWebInterface;
import com.clevertap.android.sdk.CleverTapAPI;

public class webviewActivity extends AppCompatActivity {
    EditText url;
    String geturl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        WebView mywebview = (WebView) findViewById(R.id.webview);
//        CleverTapInstanceConfig config =  CleverTapInstanceConfig.createInstance(this,"86K-6R8-W66Z","TEST-b26-36b");
//        CleverTapAPI cleverTapAPI = CleverTapAPI.instanceWithConfig(this,config);
        mywebview.addJavascriptInterface(new CTWebInterface(CleverTapAPI.getDefaultInstance(this)), "CleverTap");
        mywebview.getSettings().setJavaScriptEnabled(true);

        mywebview.loadUrl("https://abeezerwebtest.000webhostapp.com/");

        mywebview.getSettings().setDomStorageEnabled(true);

        mywebview.setWebViewClient(new WebViewClient());
        url = findViewById(R.id.url);
        findViewById(R.id.openurl).setOnClickListener(view -> {
            geturl = url.getText().toString();
            mywebview.loadUrl(geturl);
        });
    }
}