package com.amanisdktest;

import android.content.Intent;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class AmaniSDKListener {

    public static EventListener listener;

    public interface EventListener {

        void onEvent(@Nullable Boolean verificationCompleted, @Nullable Boolean tokenExpired, @Nullable Integer onApiException);

    }

    public AmaniSDKListener() {
        this.listener = null;
    }

    public void setEventListener(EventListener listener) {
        this.listener = listener;
    }
}
