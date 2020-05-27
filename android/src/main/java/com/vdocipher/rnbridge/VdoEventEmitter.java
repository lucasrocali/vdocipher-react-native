package com.vdocipher.rnbridge;

import androidx.annotation.StringDef;
import android.view.View;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.vdocipher.aegis.media.ErrorDescription;
import com.vdocipher.aegis.media.MediaInfo;
import com.vdocipher.aegis.media.Track;
import com.vdocipher.aegis.player.VdoPlayer.VdoInitParams;
import static com.vdocipher.rnbridge.Utils.*;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

class VdoEventEmitter {
    private static final String EVENT_INIT_SUCCESS = "onInitSuccess";
    private static final String EVENT_INIT_FAILURE = "onInitFailure";
    private static final String EVENT_LOADING = "onVdoLoading";
    private static final String EVENT_LOADED = "onVdoLoaded";
    private static final String EVENT_LOAD_ERROR = "onVdoLoadError";
    private static final String EVENT_PLAYER_STATE_CHANGED = "onVdoPlayerStateChanged";
    private static final String EVENT_PROGRESS = "onVdoProgress";
    private static final String EVENT_BUFFER_UPDATE = "onVdoBufferUpdate";
    private static final String EVENT_PLAYBACK_SPEED_CHANGED = "onVdoPlaybackSpeedChanged";
    private static final String EVENT_TRACKS_CHANGED = "onVdoTracksChanged";
    private static final String EVENT_MEDIA_ENDED = "onVdoMediaEnded";
    private static final String EVENT_ERROR = "onVdoError";
    private static final String EVENT_PLAYBACK_PROPERTIES = "onVdoPlaybackProperties";
    private static final String EVENT_ENTER_FULLSCREEN = "onVdoEnterFullscreen";
    private static final String EVENT_EXIT_FULLSCREEN = "onVdoExitFullscreen";

    static final String[] EVENTS = {
            EVENT_INIT_SUCCESS, EVENT_INIT_FAILURE,
            EVENT_LOADING, EVENT_LOADED, EVENT_LOAD_ERROR,
            EVENT_PLAYER_STATE_CHANGED,
            EVENT_PROGRESS, EVENT_BUFFER_UPDATE,
            EVENT_PLAYBACK_SPEED_CHANGED, EVENT_TRACKS_CHANGED,
            EVENT_MEDIA_ENDED,
            EVENT_ERROR,
            EVENT_PLAYBACK_PROPERTIES,
            EVENT_ENTER_FULLSCREEN, EVENT_EXIT_FULLSCREEN
    };

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({EVENT_INIT_SUCCESS, EVENT_INIT_FAILURE,
            EVENT_LOADING, EVENT_LOADED, EVENT_LOAD_ERROR,
            EVENT_PLAYER_STATE_CHANGED,
            EVENT_PROGRESS, EVENT_BUFFER_UPDATE,
            EVENT_PLAYBACK_SPEED_CHANGED, EVENT_TRACKS_CHANGED,
            EVENT_MEDIA_ENDED,
            EVENT_ERROR,
            EVENT_PLAYBACK_PROPERTIES,
            EVENT_ENTER_FULLSCREEN, EVENT_EXIT_FULLSCREEN})
    @interface VdoEvent {}

    private static final String EVENT_PROP_RESTORED = "restored";

    private static final String EVENT_PROP_MEDIA_INFO = "mediaInfo";

    private static final String EVENT_PROP_CURRENT_TIME = "currentTime";
    private static final String EVENT_PROP_BUFFER_TIME = "bufferTime";
    private static final String EVENT_PROP_SEEK_TIME = "seekTime";
    private static final String EVENT_PROP_ORIENTATION = "orientation";
    private static final String EVENT_PROP_AVAILABLE_TRACKS = "availableTracks";
    private static final String EVENT_PROP_SELECTED_TRACKS = "selectedTracks";
    private static final String EVENT_PROP_PLAY_WHEN_READY = "playWhenReady";
    private static final String EVENT_PROP_PLAYER_STATE = "playerState";
    private static final String EVENT_PROP_PLAYBACK_SPEED = "playbackSpeed";

    private static final String EVENT_PROP_ERROR_DESCRIPTION = "errorDescription";

    private final RCTEventEmitter eventEmitter;
    private int viewId = View.NO_ID;

    VdoEventEmitter(ReactContext context) {
        eventEmitter = context.getJSModule(RCTEventEmitter.class);
    }

    void setViewId(int viewId) {
        this.viewId = viewId;
    }

    void initSuccess(boolean restored) {
        WritableMap event = Arguments.createMap();
        event.putBoolean(EVENT_PROP_RESTORED, restored);
        receiveEvent(EVENT_INIT_SUCCESS, event);
    }

    void initFailure(ErrorDescription errorDescription) {
        WritableMap errDes = makeErrorDescriptionMap(errorDescription);

        WritableMap event = Arguments.createMap();
        event.putMap(EVENT_PROP_ERROR_DESCRIPTION, errDes);
        receiveEvent(EVENT_INIT_FAILURE, event);
    }

    void loading(VdoInitParams vdoInitParams) {
        receiveEvent(EVENT_LOADING, null);
    }

    void loaded(VdoInitParams vdoInitParams, MediaInfo mediaInfo) {
        WritableMap mInfo = makeMediaInfoMap(mediaInfo);

        WritableMap event = Arguments.createMap();
        event.putMap(EVENT_PROP_MEDIA_INFO, mInfo);
        receiveEvent(EVENT_LOADED, event);
    }

    void loadError(VdoInitParams vdoInitParams, ErrorDescription errorDescription) {
        WritableMap errDes = makeErrorDescriptionMap(errorDescription);

        WritableMap event = Arguments.createMap();
        event.putMap(EVENT_PROP_ERROR_DESCRIPTION, errDes);
        receiveEvent(EVENT_LOAD_ERROR, event);
    }

    void playerStateChanged(boolean playWhenReady, int playerState) {
        WritableMap event = Arguments.createMap();
        event.putBoolean(EVENT_PROP_PLAY_WHEN_READY, playWhenReady);
        event.putString(EVENT_PROP_PLAYER_STATE, stateName(playerState));
        receiveEvent(EVENT_PLAYER_STATE_CHANGED, event);
    }

    void seekTo(long timeMs) {}

    void progress(long timeMs) {
        WritableMap event = Arguments.createMap();
        event.putInt(EVENT_PROP_CURRENT_TIME, (int)timeMs);
        receiveEvent(EVENT_PROGRESS, event);
    }

    void bufferUpdate(long bufferTimeMs) {
        WritableMap event = Arguments.createMap();
        event.putInt(EVENT_PROP_BUFFER_TIME, (int)bufferTimeMs);
        receiveEvent(EVENT_BUFFER_UPDATE, event);
    }

    void speedChanged(float speed) {
        WritableMap event = Arguments.createMap();
        event.putDouble(EVENT_PROP_PLAYBACK_SPEED, (double)speed);
        receiveEvent(EVENT_PLAYBACK_SPEED_CHANGED, event);
    }

    void tracksChanged(Track[] availableTracks, Track[] selectedTracks) {
        WritableArray available = makeTrackMapArray(availableTracks);
        WritableArray selected = makeTrackMapArray(selectedTracks);

        WritableMap event = Arguments.createMap();
        event.putArray(EVENT_PROP_AVAILABLE_TRACKS, available);
        event.putArray(EVENT_PROP_SELECTED_TRACKS, selected);
        receiveEvent(EVENT_TRACKS_CHANGED, event);
    }

    void mediaEnded(VdoInitParams vdoInitParams) {
        receiveEvent(EVENT_MEDIA_ENDED, null);
    }

    void error(VdoInitParams vdoInitParams, ErrorDescription errorDescription) {
        WritableMap errDes = makeErrorDescriptionMap(errorDescription);

        WritableMap event = Arguments.createMap();
        event.putMap(EVENT_PROP_ERROR_DESCRIPTION, errDes);
        receiveEvent(EVENT_ERROR, event);
    }

    void enterFullscreen() {
        receiveEvent(EVENT_ENTER_FULLSCREEN, null);
    }

    void exitFullscreen() {
        receiveEvent(EVENT_EXIT_FULLSCREEN, null);
    }

    void playbackProperties(long totalPlayed, long totalCovered) {
        WritableMap event = Arguments.createMap();
        event.putInt("totalPlayed", (int)totalPlayed);
        event.putInt("totalCovered", (int)totalCovered);
        receiveEvent(EVENT_PLAYBACK_PROPERTIES, event);
    }

    private void receiveEvent(@VdoEvent String type, WritableMap event) {
        eventEmitter.receiveEvent(viewId, type, event);
    }
}
