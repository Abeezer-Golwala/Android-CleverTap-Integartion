package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;

import com.clevertap.android.sdk.AnalyticsManager;
import com.clevertap.android.sdk.BaseCallbackManager;
import com.clevertap.android.sdk.CleverTapInstanceConfig;
import com.clevertap.android.sdk.ControllerManager;
import com.clevertap.android.sdk.CoreMetaData;
import com.clevertap.android.sdk.DeviceInfo;
import com.clevertap.android.sdk.inapp.InAppController;
import com.clevertap.android.sdk.inapp.InAppQueue;
import com.clevertap.android.sdk.inapp.evaluation.EvaluationManager;
import com.clevertap.android.sdk.inapp.images.InAppResourceProvider;
import com.clevertap.android.sdk.task.MainLooperHandler;

@SuppressLint("RestrictedApi")
public class test extends InAppController {
    public test(Context context, CleverTapInstanceConfig config, MainLooperHandler mainLooperHandler, ControllerManager controllerManager, BaseCallbackManager callbackManager, AnalyticsManager analyticsManager, CoreMetaData coreMetaData, DeviceInfo deviceInfo, InAppQueue inAppQueue, EvaluationManager evaluationManager, InAppResourceProvider resourceProvider) {
        super(context, config, mainLooperHandler, controllerManager, callbackManager, analyticsManager, coreMetaData, deviceInfo, inAppQueue, evaluationManager, resourceProvider);
    }
}
