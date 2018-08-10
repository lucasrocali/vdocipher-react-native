package com.vdocipher.rnbridge;

import android.util.Log;
import android.widget.FrameLayout;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.vdocipher.aegis.media.ErrorDescription;
import com.vdocipher.aegis.player.VdoPlayer;
import com.vdocipher.aegis.player.VdoPlayerView;

public class ReactVdoPlayerView extends FrameLayout implements VdoPlayer.InitializationListener {
    private static final String TAG = "ReactVdoPlayerView";

    private final VdoPlayerView playerView;
    private final VdoPlayerControlView playerControlView;

    public ReactVdoPlayerView(ThemedReactContext context) {
        super(context);

        playerView = new VdoPlayerView(context);
        playerControlView = new VdoPlayerControlView(context);

        playerView.initialize(this);

        FrameLayout.LayoutParams matchParent = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        addView(playerView, matchParent);
        addView(playerControlView, matchParent);
    }

    @Override
    public void onInitializationSuccess(VdoPlayer.PlayerHost playerHost, VdoPlayer vdoPlayer, boolean restored) {
        Log.d(TAG, "init success");
        WritableMap event = Arguments.createMap();
        event.putBoolean("restored", restored);
        ReactContext reactContext = (ReactContext)getContext();
        reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
                getId(),
                "onInitSuccess",
                event);
    }

    @Override
    public void onInitializationFailure(VdoPlayer.PlayerHost playerHost, ErrorDescription errorDescription) {
        Log.e(TAG, "init failure: " + errorDescription.toString());
        WritableMap event = Arguments.createMap();
        event.putInt("errorCode", errorDescription.errorCode);
        ReactContext reactContext = (ReactContext)getContext();
        reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
                getId(),
                "onInitFailure",
                event);
    }
}
