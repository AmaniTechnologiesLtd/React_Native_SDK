package com.amanisdktest;

import com.amani_ai.base.Utiltiy.AppConstants;
import com.amani_ai.base.callback.EventFire;
import com.amani_ai.base.callback.EventName;
import com.amani_ai.base.callback.EventType;

import com.facebook.react.ReactActivity;
import com.facebook.react.ReactActivityDelegate;
import com.facebook.react.ReactRootView;

import java.util.Objects;

import android.content.Intent;

public class MainActivity extends ReactActivity {

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (AmaniSDKListener.listener != null) {
      boolean verificationCompleted = Objects.requireNonNull(data).getBooleanExtra(AppConstants.ON_SUCCESS,false);
      boolean tokenExpired = Objects.requireNonNull(data).getBooleanExtra(AppConstants.TOKEN_EXPIRED,false);
      Integer apiException = Objects.requireNonNull(data).getIntExtra(AppConstants.ON_API_EXCEPTION,1000);
      AmaniSDKListener.listener.onEvent(verificationCompleted, tokenExpired, apiException);
    }
  }

  /**
   * Returns the name of the main component registered from JavaScript. This is used to schedule
   * rendering of the component.
   */
  @Override
  protected String getMainComponentName() {
    return "amanisdktest";
  }

  public static class MainActivityDelegate extends ReactActivityDelegate {
    public MainActivityDelegate(ReactActivity activity, String mainComponentName) {
      super(activity, mainComponentName);
    }

    @Override
    protected ReactRootView createRootView() {
      ReactRootView reactRootView = new ReactRootView(getContext());
      // If you opted-in for the New Architecture, we enable the Fabric Renderer.
      reactRootView.setIsFabric(BuildConfig.IS_NEW_ARCHITECTURE_ENABLED);
      return reactRootView;
    }

    @Override
    protected boolean isConcurrentRootEnabled() {
      // If you opted-in for the New Architecture, we enable Concurrent Root (i.e. React 18).
      // More on this on https://reactjs.org/blog/2022/03/29/react-v18.html
      return BuildConfig.IS_NEW_ARCHITECTURE_ENABLED;
    }
  }
}
