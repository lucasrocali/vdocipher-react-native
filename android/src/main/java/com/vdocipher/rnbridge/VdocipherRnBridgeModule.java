package com.vdocipher.rnbridge;

import android.content.Intent;
import android.app.Activity;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.vdocipher.aegis.player.VdoPlayer.VdoInitParams;

import static com.vdocipher.rnbridge.Utils.getTechOverride;

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
    Activity currentActivity = getCurrentActivity();
    if (currentActivity == null) {
      android.util.Log.e("VdoRnBridgeModule", "Current Activity context could not be obtained.");
    } else {
      boolean offline = embedInfo.hasKey("offline") && embedInfo.getBoolean("offline");
      final VdoInitParams vdoParams;

      if (offline) {
        String mediaId = embedInfo.hasKey("mediaId") ? embedInfo.getString("mediaId") : null;
        vdoParams = VdoInitParams.createParamsForOffline(mediaId);
      } else {
        String otp = embedInfo.hasKey("otp") ? embedInfo.getString("otp") : null;
        String playbackInfo = embedInfo.hasKey("playbackInfo") ? embedInfo.getString("playbackInfo") : null;
        boolean forceLowestBitrate = embedInfo.hasKey("forceLowestBitrate")
                && embedInfo.getBoolean("forceLowestBitrate");
        boolean forceHighestSupportedBitrate = embedInfo.hasKey("forceHighestSupportedBitrate")
                && embedInfo.getBoolean("forceHighestSupportedBitrate");
        int maxVideoBitrateKbps = embedInfo.hasKey("maxVideoBitrateKbps") ?
                embedInfo.getInt("maxVideoBitrateKbps") : Integer.MAX_VALUE;
        int bufferingGoalMs = embedInfo.hasKey("bufferingGoalMs") ?
                embedInfo.getInt("bufferingGoalMs") : 0;
        String[] overrides = getTechOverride(embedInfo);

        VdoInitParams.Builder builder = new VdoInitParams.Builder()
                .setOtp(otp)
                .setPlaybackInfo(playbackInfo)
                .setForceLowestBitrate(forceLowestBitrate)
                .setForceHighestSupportedBitrate(forceHighestSupportedBitrate)
                .setMaxVideoBitrateKbps(maxVideoBitrateKbps)
                .setPreferredCaptionsLanguage("en");
        if (bufferingGoalMs > 0) {
          builder.setBufferingGoalMs(bufferingGoalMs);
        }
        if (overrides != null) {
          builder.setTechOverride(overrides);
        }
        vdoParams = builder.build();
      }

      Intent intent = VdoPlayerActivity.getStartIntent(currentActivity, vdoParams);
      currentActivity.startActivity(intent);
    }
  }
}
