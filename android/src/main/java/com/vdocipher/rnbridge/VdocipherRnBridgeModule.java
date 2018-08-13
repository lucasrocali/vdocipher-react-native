package com.vdocipher.rnbridge;

import android.content.Intent;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReadableMap;

import org.json.JSONException;
import org.json.JSONObject;

public class VdocipherRnBridgeModule extends ReactContextBaseJavaModule {

  private final ReactApplicationContext reactContext;

  public VdocipherRnBridgeModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @Override
  public String getName() {
    return "VdocipherRnBridge";
  }

  @ReactMethod
  public void hello() {
    android.widget.Toast.makeText(getReactApplicationContext(), "Hello", android.widget.Toast.LENGTH_SHORT).show();
  }

  // todo error event handling
  @ReactMethod
  public void startVideoScreen(ReadableMap embedParams) {
    ReadableMap embedInfo = embedParams.getMap("embedInfo");
    String otp = embedInfo.getString("otp");
    String playbackInfo = embedInfo.getString("playbackInfo");
    android.util.Log.i("params", "[" + otp + ", " + playbackInfo + "]");
    ReactApplicationContext context = getReactApplicationContext();
    Intent intent = VdoPlayerActivity.getStartIntent(context, otp, playbackInfo);
    context.startActivity(intent);
  }
}