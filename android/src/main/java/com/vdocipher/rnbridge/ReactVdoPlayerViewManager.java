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
        view.cleanUp();
    }

    @ReactProp(name = "embedInfo")
    public void setEmbedInfo(ReactVdoPlayerView vdoPlayerView, @Nullable ReadableMap embedInfo) {
        if (embedInfo != null) {
            boolean offline = embedInfo.hasKey("offline") && embedInfo.getBoolean("offline");
            final VdoInitParams initParams;

            if (offline) {
                String mediaId = embedInfo.hasKey("mediaId") ? embedInfo.getString("mediaId") : null;
                initParams = VdoInitParams.createParamsForOffline(mediaId);
            } else {
                String otp = embedInfo.hasKey("otp") ? embedInfo.getString("otp") : null;
                String playbackInfo = embedInfo.hasKey("playbackInfo") ? embedInfo.getString("playbackInfo") : null;
                initParams = new VdoInitParams.Builder()
                                .setOtp(otp)
                                .setPlaybackInfo(playbackInfo)
                                //.setPreferredCaptionsLanguage(embedInfo.getString("lang??"))
                                .build();
            }
            vdoPlayerView.load(initParams);
        }
    }

    @ReactProp(name = "showNativeControls", defaultBoolean = true)
    public void setShowNativeControls(ReactVdoPlayerView vdoPlayerView, boolean showNativeControls) {
        vdoPlayerView.showNativeControls(showNativeControls);
    }

    @ReactProp(name = "playWhenReady", defaultBoolean = true)
    public void setPlayWhenReady(ReactVdoPlayerView vdoPlayerView, boolean playWhenReady) {
        vdoPlayerView.setPlayWhenReady(playWhenReady);
    }

    @ReactProp(name = "playbackSpeed", defaultFloat = 1f)
    public void setPlaybackSpeed(ReactVdoPlayerView vdoPlayerView, float playbackSpeed) {
        vdoPlayerView.setPlaybackSpeed(playbackSpeed);
    }

    @ReactProp(name = "seek")
    public void setSeek(ReactVdoPlayerView vdoPlayerView, final int targetMs) {
        vdoPlayerView.seekTo(targetMs);
    }

    @ReactProp(name = "fullscreen", defaultBoolean = false)
    public void setFullscreen(ReactVdoPlayerView vdoPlayerView, boolean fullscreen) {
        vdoPlayerView.setFullscreen(fullscreen);
    }
}
