package com.reactnativeamanisdk;
import com.facebook.react.bridge.WritableMap;

public class RNAmaniSDKListener {

  public static EventListener listener;

  public interface EventListener {
    void onEvent(WritableMap callbackData);
  }

  public RNAmaniSDKListener() {
    this.listener = null;
  }

  public void setEventListener(EventListener listener) {
    this.listener = listener;
  }

}
