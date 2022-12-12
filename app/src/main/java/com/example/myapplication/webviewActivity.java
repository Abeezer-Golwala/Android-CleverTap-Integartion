package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;

import com.clevertap.android.sdk.CTWebInterface;
import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.CleverTapInstanceConfig;

public class webviewActivity extends AppCompatActivity {
    EditText url;
    String geturl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        WebView mywebview = (WebView) findViewById(R.id.webview);
        CleverTapInstanceConfig config =  CleverTapInstanceConfig.createInstance(this,"86K-6R8-W66Z","TEST-b26-36b");
        CleverTapAPI cleverTapAPI = CleverTapAPI.instanceWithConfig(this,config);
        mywebview.addJavascriptInterface(new CTWebInterface(cleverTapAPI),"CleverTap");
        mywebview.loadUrl("https://abeezerwebtest.000webhostapp.com/");
        mywebview.getSettings().setJavaScriptEnabled(true);
        mywebview.getSettings().setDomStorageEnabled(true);

        mywebview.setWebViewClient(new WebViewClient());
        url = findViewById(R.id.url);
        findViewById(R.id.openurl).setOnClickListener(view -> {
            geturl = url.getText().toString();
            mywebview.loadUrl(geturl);
        });
    }

}