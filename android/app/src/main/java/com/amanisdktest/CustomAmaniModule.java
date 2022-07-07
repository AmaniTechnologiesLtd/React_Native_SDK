package com.amanisdktest;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.amani_ai.base.util.Amani;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import java.util.ArrayList;

public class CustomAmaniModule extends ReactContextBaseJavaModule {
    private static ReactApplicationContext reactApplicationContext;

    CustomAmaniModule(ReactApplicationContext context){
        super(context);
        reactApplicationContext = context;
    }

    @ReactMethod
    public void showMessage(String message){
        Toast.makeText(reactApplicationContext, message , Toast.LENGTH_LONG).show();
    }

    /**
     * @param server          Server URL for first init AmaniSDK
     * @param id              ID number
     * @param email           Email as login/cred will be provided from Amani
     * @param password        Password  login/creed will be provided from Amani
     * @param geoLocation     Geo Location permission
     * @param lang            Language param "tr, eng" etc.
     */
    @ReactMethod
    public void startAmaniSDKWithEmail(String server, String id, String email, String password, Boolean geoLocation, String lang, Callback callBack){
        eventListener(callBack);
        Amani.init(reactApplicationContext,server);
        Amani.goToKycActivity(reactApplicationContext.getCurrentActivity(),id,email,password,geoLocation,lang);
    }

    /**
     * @param server          Server URL for first init AmaniSDK
     * @param id              ID number
     * @param token           Token as Amani will be provided from Amani
     * @param lang            Language param "tr, eng" etc.
     */
    @ReactMethod
    public void startAmaniSDKWithToken(String server, String id, String token, String lang, Callback callBack){
        eventListener(callBack);
        Amani.init(reactApplicationContext, server);
        Amani.goToKycActivity(reactApplicationContext.getCurrentActivity(), id, token, lang);
    }

    @NonNull
    @Override
    public String getName() {
        return "AmaniSDKModule";
    }

    private void eventListener(Callback callback) {
        AmaniSDKListener eventFire = new AmaniSDKListener();
        eventFire.setEventListener((verificationCompleted, tokenExpired, onApiException) -> {
            Log.d("TAG", "onEvent: "
                    + "\nIs verification completed?" + verificationCompleted
                    + "\nIs token expired?:" + tokenExpired
                    + "\nApi Exception (Default Value: 1000): " + onApiException);

            callback.invoke(verificationCompleted,tokenExpired,onApiException);
                }
        );
    }
}
