package com.example.myapplication;

import android.content.Context;
import android.webkit.JavascriptInterface;

import com.clevertap.android.sdk.CleverTapAPI;

public class SDKBridge {
    CleverTapAPI clevertapDefaultInstance;

    private Context context;

    // Constructor to accept the context
    public void WebAppInterface(Context context) {
        this.context = context;
    }

    @JavascriptInterface
    public String callSDKMethod() {
        clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(context);
        // Call the SDK method and get the result asynchronously
        return clevertapDefaultInstance.getAllDisplayUnits().toString();
    }
}
