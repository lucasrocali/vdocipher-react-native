package com.vdocipher.rnbridge;

import android.util.Log;

import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.vdocipher.aegis.player.VdoPlayer.VdoInitParams;

import java.util.Map;

import javax.annotation.Nullable;

public class ReactVdoPlayerViewManager extends ViewGroupManager<ReactVdoPlayerView> {
    private static final String TAG = "ReactVdoPlayerViewMngr";

    private static final String REACT_CLASS = "RCTVdoPlayerView";

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    public ReactVdoPlayerView createViewInstance(ThemedReactContext context) {
        ReactVdoPlayerView playerView = new ReactVdoPlayerView(context);
        Log.d(TAG, "created " + playerView.toString());
        return playerView;
    }

    @Nullable
    @Override
    public Map<String, Object> getExportedCustomDirectEventTypeConstants() {
        MapBuilder.Builder<String, Object> builder = MapBuilder.builder();
        for (String event : VdoEventEmitter.EVENTS) {
            builder.put(event, MapBuilder.of("registrationName", event));
        }
        return builder.build();
    }

    @Override
    public void onDropViewInstance(ReactVdoPlayerView view) {
        Log.d(TAG, "dropped " + view.toString());
        view.stopPlayback();
    }

    @ReactProp(name = "embedInfo")
    public void setEmbedInfo(ReactVdoPlayerView vdoPlayerView, @Nullable ReadableMap embedInfo) {
        if (embedInfo != null) {
            vdoPlayerView.load(
                    new VdoInitParams.Builder()
                    .setOtp(embedInfo.getString("otp"))
                    .setPlaybackInfo(embedInfo.getString("playbackInfo"))
                    //.setPreferredCaptionsLanguage(embedInfo.getString("lang??"))
                    .build()
            );
        }
    }
}
