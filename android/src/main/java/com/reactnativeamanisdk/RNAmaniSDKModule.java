package com.reactnativeamanisdk;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;

import com.amani_ai.base.Utiltiy.AppConstants;
import com.amani_ai.base.util.Amani;
import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.module.annotations.ReactModule;

import java.util.Objects;

@ReactModule(name = RNAmaniSDKModule.NAME)
public class RNAmaniSDKModule extends ReactContextBaseJavaModule implements ActivityEventListener {
    public static final String NAME = "RNAmaniSDK";
    private static ReactApplicationContext rnAppContext;

    public RNAmaniSDKModule(ReactApplicationContext reactContext) {
        super(reactContext);
        rnAppContext = reactContext;
        reactContext.addActivityEventListener(this);
    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
      if (RNAmaniSDKListener.listener != null) {
        WritableMap resultMap = Arguments.createMap();
        // Maybe expand upon this.
        resultMap.putBoolean("isVerificationCompleted", Objects.requireNonNull(data).getBooleanExtra(AppConstants.ON_SUCCESS, false));
        resultMap.putBoolean("isTokenExpired", Objects.requireNonNull(data).getBooleanExtra(AppConstants.TOKEN_EXPIRED, false));
        resultMap.putInt("apiExceptionCode", Objects.requireNonNull(data).getIntExtra(AppConstants.ON_API_EXCEPTION, 1000));
        RNAmaniSDKListener.listener.onEvent(resultMap);
      }
    }

    @Override
    public void onNewIntent(Intent intent) {}

    @Override
    @NonNull
    public String getName() {
        return NAME;
    }

    @ReactMethod
    public void startAmaniSDKWithToken(ReadableMap args, Callback callback) {
      String birthDate = null;
      String expireDate = null;
      String documentNo = null;
      Boolean geoLocation = false;
      String lang = null;
      String email = null;
      String phone = null;
      String name = null;

      if (args.hasKey("birthDate")) {
        birthDate = args.getString("birthDate");
      }
      if (args.hasKey("expireDate")) {
        expireDate = args.getString("expireDate");
      }
      if (args.hasKey("documentNo")) {
        documentNo = args.getString("documentNo");
      }
      if (args.hasKey("geoLocation")) {
        geoLocation = args.getBoolean("geoLocation");
      } else {
        geoLocation = false;
      }
      if (args.hasKey("lang")) {
        lang = args.getString("lang");
      }
      if (args.hasKey("email")) {
        email = args.getString("email");
      }
      if (args.hasKey("phone")) {
        phone = args.getString("phone");
      }
      if (args.hasKey("name")) {
        name = args.getString("name");
      }


      Amani.init(rnAppContext, args.getString("server"));

      eventListener(callback);
      if (email != null && phone != null && name != null) {
        Amani.goToKycActivity(rnAppContext.getCurrentActivity(), args.getString("id"), args.getString("token"), birthDate, expireDate, documentNo, geoLocation, lang, email, phone, name);
      } else if (birthDate != null && expireDate != null && documentNo != null) {
        Amani.goToKycActivity(rnAppContext.getCurrentActivity(), args.getString("id"), args.getString("token"), birthDate, expireDate, documentNo, lang);
      } else {
        Amani.goToKycActivity(rnAppContext.getCurrentActivity(), args.getString("id"), args.getString("token"), lang);
      }
    }

    @ReactMethod
    public void startAmaniSDKWithCredentials(ReadableMap args, Callback callback) {
      String birthDate = null;
      String expireDate = null;
      String documentNo = null;
      Boolean geoLocation = false;
      String lang = null;
      String email = null;
      String phone = null;
      String name = null;

      if (args.hasKey("birthDate")) {
        birthDate = args.getString("birthDate");
      }
      if (args.hasKey("expireDate")) {
        expireDate = args.getString("expireDate");
      }
      if (args.hasKey("documentNo")) {
        documentNo = args.getString("documentNo");
      }
      if (args.hasKey("geoLocation")) {
        geoLocation = args.getBoolean("geoLocation");
      } else {
        geoLocation = false;
      }
      if (args.hasKey("lang")) {
        lang = args.getString("lang");
      }
      if (args.hasKey("email")) {
        email = args.getString("email");
      }
      if (args.hasKey("phone")) {
        phone = args.getString("phone");
      }
      if (args.hasKey("name")) {
        name = args.getString("name");
      }


      Amani.init(rnAppContext, args.getString("server"));

      eventListener(callback);
      if (email != null && phone != null && name != null) {
        Amani.goToKycActivity(rnAppContext.getCurrentActivity(), args.getString("id"), args.getString("loginEmail"), args.getString("password"), birthDate, expireDate, documentNo, geoLocation, lang, email, phone, name);
      } else if (birthDate != null && expireDate != null && documentNo != null) {
        Amani.goToKycActivity(rnAppContext.getCurrentActivity(), args.getString("id"), args.getString("loginEmail"), args.getString("password"), birthDate, expireDate, documentNo, lang);
      } else {
        Amani.goToKycActivity(rnAppContext.getCurrentActivity(), args.getString("id"), args.getString("loginEmail"), args.getString("password"), lang);
      }
    }

    private void eventListener(Callback callback) {
      RNAmaniSDKListener eventFire = new RNAmaniSDKListener();
      eventFire.setEventListener((callbackData -> {
        // Possible debug log point.
        callback.invoke(callbackData);
      }));
    }

}
