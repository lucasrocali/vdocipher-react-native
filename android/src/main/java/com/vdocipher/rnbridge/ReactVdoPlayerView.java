package com.vdocipher.rnbridge;

import android.util.Log;
import android.widget.FrameLayout;

import com.facebook.react.uimanager.ThemedReactContext;
import com.vdocipher.aegis.media.ErrorDescription;
import com.vdocipher.aegis.media.Track;
import com.vdocipher.aegis.player.VdoPlayer;
import com.vdocipher.aegis.player.VdoPlayer.PlayerHost;
import com.vdocipher.aegis.player.VdoPlayer.InitializationListener;
import com.vdocipher.aegis.player.VdoPlayer.PlaybackEventListener;
import com.vdocipher.aegis.player.VdoPlayer.VdoInitParams;
import com.vdocipher.aegis.player.VdoPlayerView;

public class ReactVdoPlayerView extends FrameLayout implements InitializationListener,
        PlaybackEventListener {
    private static final String TAG = "ReactVdoPlayerView";

    private final VdoPlayerView playerView;
    private final VdoPlayerControlView playerControlView;
    private final VdoEventEmitter eventEmitter;

    private VdoPlayer vdoPlayer;

    private VdoInitParams pendingInitParams;

    public ReactVdoPlayerView(ThemedReactContext context) {
        super(context);

        playerView = new VdoPlayerView(context);
        playerControlView = new VdoPlayerControlView(context);
        eventEmitter = new VdoEventEmitter(context);

        playerView.initialize(this);

        FrameLayout.LayoutParams matchParent = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        addView(playerView, matchParent);
        addView(playerControlView, matchParent);
    }

    @Override
    public void setId(int id) {
        super.setId(id);
        eventEmitter.setViewId(id);
    }

    @Override
    public void onInitializationSuccess(VdoPlayer.PlayerHost playerHost, VdoPlayer vdoPlayer, boolean restored) {
        Log.d(TAG, "init success");
        this.vdoPlayer = vdoPlayer;
        vdoPlayer.addPlaybackEventListener(this);
        playerControlView.setPlayer(vdoPlayer);
        eventEmitter.initSuccess(restored);

        // load pending params
        if (pendingInitParams != null) {
            Log.d(TAG, "load pending params");
            load(pendingInitParams);
            pendingInitParams = null;
        }
    }

    // InitializationListener impl

    @Override
    public void onInitializationFailure(PlayerHost playerHost, ErrorDescription errorDescription) {
        Log.e(TAG, "init failure: " + errorDescription.toString());
        eventEmitter.initFailure(errorDescription);
    }

    public void load(VdoInitParams params) {
        if (vdoPlayer != null && params != null) {
            vdoPlayer.load(params);
        } else {
            pendingInitParams = params;
        }
    }

    // PlaybackEventListener impl

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playerState) {
        eventEmitter.playerStateChanged(playWhenReady, playerState);
    }

    @Override
    public void onSeekTo(long timeMs) {
        eventEmitter.seekTo(timeMs);
    }

    @Override
    public void onProgress(long timeMs) {
        eventEmitter.progress(timeMs);
    }

    @Override
    public void onBufferUpdate(long bufferTimeMs) {
        eventEmitter.bufferUpdate(bufferTimeMs);
    }

    @Override
    public void onPlaybackSpeedChanged(float speed) {
        eventEmitter.speedChanged(speed);
    }

    @Override
    public void onLoading(VdoInitParams vdoInitParams) {
        eventEmitter.loading(vdoInitParams);
    }

    @Override
    public void onLoaded(VdoInitParams vdoInitParams) {
        eventEmitter.loaded(vdoInitParams, vdoPlayer.getCurrentMedia());
    }

    @Override
    public void onLoadError(VdoInitParams vdoInitParams, ErrorDescription errorDescription) {
        eventEmitter.loadError(vdoInitParams, errorDescription);
    }

    @Override
    public void onMediaEnded(VdoInitParams vdoInitParams) {
        eventEmitter.mediaEnded(vdoInitParams);
    }

    @Override
    public void onError(VdoInitParams vdoInitParams, ErrorDescription errorDescription) {
        eventEmitter.error(vdoInitParams, errorDescription);
    }

    @Override
    public void onTracksChanged(Track[] availableTracks, Track[] selectedTracks) {
        eventEmitter.tracksChanged(availableTracks, selectedTracks);
    }
}
