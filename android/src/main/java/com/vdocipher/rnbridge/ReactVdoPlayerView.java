package com.vdocipher.rnbridge;

import android.app.Activity;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.uimanager.ThemedReactContext;
import com.vdocipher.aegis.media.ErrorDescription;
import com.vdocipher.aegis.media.Track;
import com.vdocipher.aegis.player.VdoPlayer;
import com.vdocipher.aegis.player.VdoPlayer.PlayerHost;
import com.vdocipher.aegis.player.VdoPlayer.InitializationListener;
import com.vdocipher.aegis.player.VdoPlayer.PlaybackEventListener;
import com.vdocipher.aegis.player.VdoPlayer.VdoInitParams;
import com.vdocipher.aegis.player.VdoPlayerView;
import com.vdocipher.aegis.player.a.g;

public class ReactVdoPlayerView extends FrameLayout implements InitializationListener,
        PlaybackEventListener, LifecycleEventListener, VdoPlayerControlView.FullscreenActionListener {
    private static final String TAG = "ReactVdoPlayerView";

    private final ThemedReactContext themedReactContext;
    private final VdoPlayerView playerView;
    private final VdoPlayerControlView playerControlView;
    private final VdoEventEmitter eventEmitter;

    private VdoPlayer vdoPlayer;

    // from props
    private boolean showNativeControls = true;
    private boolean playWhenReady = true;
    private float pendingPlaybackSpeed = 0;
    private boolean fullscreen = false;

    private VdoInitParams pendingInitParams;
    private boolean stopped = false;
    private Object playbackState = null;

    public ReactVdoPlayerView(ThemedReactContext context) {
        super(context);
        context.addLifecycleEventListener(this);
        themedReactContext = context;

        playerView = new VdoPlayerView(context);
        playerControlView = new VdoPlayerControlView(context);
        playerControlView.setFullscreenActionListener(this);
        eventEmitter = new VdoEventEmitter(context);

        playerView.initialize(this);

        FrameLayout.LayoutParams matchParent = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        addView(playerView, matchParent);
        addView(playerControlView, matchParent);

        setKeepScreenOn(true);
    }

    /**
     * Clean up all resources, references to this instance which may prevent it from being GC'ed.
     */
    public void cleanUp() {
        stopPlayback();
        themedReactContext.removeLifecycleEventListener(this);
    }

    /**
     * Frees resources used by player. Does not lose player instance.
     */
    public void stopPlayback() {
        stopped = true;
        playbackState = playerView.getLastPlaybackState();
        playerView.packUp();

        setKeepScreenOn(false);
        if (fullscreen) {
            setFullscreen(false);
        }
    }

    public void restorePlayback() {
        if (stopped && playbackState != null) {
            playerView.restore((g)playbackState);
            setKeepScreenOn(true);
        }
        stopped = false;
    }

    public void showNativeControls(boolean showNativeControls) {
        this.showNativeControls = showNativeControls;
        playerControlView.setVisibility(showNativeControls ? VISIBLE : GONE);
    }

    public boolean setFullscreen(boolean enterFullscreen) {
        if (fullscreen == enterFullscreen) {
            return false;
        }

        Activity activity = themedReactContext.getCurrentActivity();
        if (activity == null) {
            return false;
        }
        View decorView = activity.getWindow().getDecorView();
        int visibility;

        if (enterFullscreen) {
            if (Build.VERSION.SDK_INT >= 19) {
                visibility = SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | SYSTEM_UI_FLAG_FULLSCREEN;
            } else {
                visibility = SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | SYSTEM_UI_FLAG_FULLSCREEN;
            }
            decorView.setSystemUiVisibility(visibility);
        } else {
            visibility = SYSTEM_UI_FLAG_VISIBLE;
            decorView.setSystemUiVisibility(visibility);
        }

        fullscreen = enterFullscreen;
        if (fullscreen) {
            eventEmitter.enterFullscreen();
        } else {
            eventEmitter.exitFullscreen();
        }
        return true;
    }

    public void getPlaybackProperties() {
        if (vdoPlayer != null) {
            Object tp = vdoPlayer.getPlaybackProperty("totalPlayed");
            long totalPlayed = (tp instanceof Long) ? (long)tp : 0L;
            Object tc = vdoPlayer.getPlaybackProperty("totalCovered");
            long totalCovered = (tc instanceof Long) ? (long)tc : 0L;
            eventEmitter.playbackProperties(totalPlayed, totalCovered);
        }
    }

    public void setPlayWhenReady(boolean playWhenReady) {
        this.playWhenReady = playWhenReady;
        if (vdoPlayer != null) {
            vdoPlayer.setPlayWhenReady(playWhenReady);
        }
    }

    public void setPlaybackSpeed(float playbackSpeed) {
        this.pendingPlaybackSpeed = playbackSpeed;
        if (vdoPlayer != null) {
            vdoPlayer.setPlaybackSpeed(playbackSpeed);
        }
    }

    public void seekTo(int targetMs) {
        if (vdoPlayer != null) {
            vdoPlayer.seekTo(targetMs);
        }
    }

    @Override
    public void setId(int id) {
        super.setId(id);
        eventEmitter.setViewId(id);
    }

    @Override
    protected void onAttachedToWindow() {
        Log.d(TAG, "onAttachedToWindow");
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        Log.d(TAG, "onDetachedFromWindow");
        super.onDetachedFromWindow();
    }

    // FullscreenActionListener impl

    @Override
    public boolean onFullscreenAction(boolean enterFullscreen) {
        return setFullscreen(enterFullscreen);
    }


    // LifecycleEventListener impl

    @Override
    public void onHostResume() {
        Log.d(TAG, "onHostResume");
        restorePlayback();
        playerControlView.setFullscreenState(fullscreen);
    }

    @Override
    public void onHostPause() {
        Log.d(TAG, "onHostPause");
        stopPlayback();
    }

    @Override
    public void onHostDestroy() {
        Log.d(TAG, "onHostDestroy");
        // this will probably never be called if the view is not on the first route into app;
        // already unregistered as a lifecycle listener
    }


    // InitializationListener impl

    @Override
    public void onInitializationSuccess(VdoPlayer.PlayerHost playerHost, VdoPlayer vdoPlayer, boolean restored) {
        Log.d(TAG, "init success");
        this.vdoPlayer = vdoPlayer;
        vdoPlayer.addPlaybackEventListener(this);
        playerControlView.setPlayer(vdoPlayer);
        eventEmitter.initSuccess(restored);

        // don't continue to playback if stopped
        if (stopped) return;

        // load pending params
        if (pendingInitParams != null) {
            Log.d(TAG, "load pending params");
            load(pendingInitParams);
            pendingInitParams = null;
        }
    }

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
        vdoPlayer.setPlayWhenReady(playWhenReady);
        if (pendingPlaybackSpeed > 0) {
            vdoPlayer.setPlaybackSpeed(pendingPlaybackSpeed);
            pendingPlaybackSpeed = 0;
        }
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
