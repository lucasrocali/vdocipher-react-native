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
import com.vdocipher.aegis.player.VdoPlayer;
import com.vdocipher.aegis.player.VdoPlayer.VdoInitParams;

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
    private static final String EVENT_PLAYBACK_SPEED_CHANGED = "onVdoSpeedChanged";
    private static final String EVENT_TRACKS_CHANGED = "onVdoTracksChanged";
    private static final String EVENT_MEDIA_ENDED = "onVdoMediaEnded";
    private static final String EVENT_ERROR = "onVdoError";

    static final String[] EVENTS = {
            EVENT_INIT_SUCCESS, EVENT_INIT_FAILURE,
            EVENT_LOADING, EVENT_LOADED, EVENT_LOAD_ERROR,
            EVENT_PLAYER_STATE_CHANGED,
            EVENT_PROGRESS, EVENT_BUFFER_UPDATE,
            EVENT_PLAYBACK_SPEED_CHANGED, EVENT_TRACKS_CHANGED,
            EVENT_MEDIA_ENDED,
            EVENT_ERROR
    };

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({EVENT_INIT_SUCCESS, EVENT_INIT_FAILURE,
            EVENT_LOADING, EVENT_LOADED, EVENT_LOAD_ERROR,
            EVENT_PLAYER_STATE_CHANGED,
            EVENT_PROGRESS, EVENT_BUFFER_UPDATE,
            EVENT_PLAYBACK_SPEED_CHANGED, EVENT_TRACKS_CHANGED,
            EVENT_MEDIA_ENDED,
            EVENT_ERROR})
    @interface VdoEvent {}

    private static final String EVENT_PROP_RESTORED = "restored";

    private static final String EVENT_PROP_MEDIA_INFO = "mediaInfo";
    private static final String EVENT_PROP_MEDIA_INFO_ID = "mediaId";
    private static final String EVENT_PROP_MEDIA_INFO_TYPE = "type";
    private static final String EVENT_PROP_MEDIA_INFO_TITLE = "title";
    private static final String EVENT_PROP_MEDIA_INFO_DESCRIPTION = "description";
    private static final String EVENT_PROP_MEDIA_INFO_DURATION = "duration";

    private static final String EVENT_PROP_TRACK_ID = "id";
    private static final String EVENT_PROP_TRACK_TYPE = "type";
    private static final String EVENT_PROP_TRACK_LANGUAGE = "language";
    private static final String EVENT_PROP_TRACK_BITRATE = "bitrate";
    private static final String EVENT_PROP_TRACK_WIDTH = "width";
    private static final String EVENT_PROP_TRACK_HEIGHT = "height";

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
    private static final String EVENT_PROP_ERROR_CODE = "errorCode";
    private static final String EVENT_PROP_ERROR_MSG = "errorMsg";
    private static final String EVENT_PROP_ERROR_HTTP_CODE = "httpStatusCode";

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
        WritableMap mInfo = Arguments.createMap();
        mInfo.putString(EVENT_PROP_MEDIA_INFO_ID, mediaInfo.mediaId);
        mInfo.putInt(EVENT_PROP_MEDIA_INFO_TYPE, mediaInfo.type);
        mInfo.putString(EVENT_PROP_MEDIA_INFO_TITLE, mediaInfo.title);
        mInfo.putString(EVENT_PROP_MEDIA_INFO_DESCRIPTION, mediaInfo.description);
        mInfo.putInt(EVENT_PROP_MEDIA_INFO_DURATION, (int)mediaInfo.duration);

        WritableMap event = Arguments.createMap();
        event.putMap(EVENT_PROP_MEDIA_INFO, mInfo);
        receiveEvent(EVENT_LOADED, event);
    }

    void loadError(VdoInitParams vdoInitParams, ErrorDescription errorDescription) {
        WritableMap errDes = makeErrorDescriptionMap(errorDescription);

        WritableMap event = Arguments.createMap();
        event.putMap(EVENT_PROP_ERROR_DESCRIPTION, errDes);
        receiveEvent(EVENT_INIT_FAILURE, event);
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

    private static WritableMap makeErrorDescriptionMap(ErrorDescription errorDescription) {
        WritableMap errDes = Arguments.createMap();
        errDes.putInt(EVENT_PROP_ERROR_CODE, errorDescription.errorCode);
        errDes.putString(EVENT_PROP_ERROR_MSG, errorDescription.errorMsg);
        errDes.putInt(EVENT_PROP_ERROR_HTTP_CODE, errorDescription.httpStatusCode);
        return errDes;
    }

    private static WritableArray makeTrackMapArray(Track[] tracks) {
        WritableArray trackArray = Arguments.createArray();
        for (Track track: tracks) {
            trackArray.pushMap(makeTrackMap(track));
        }
        return trackArray;
    }

    private static WritableMap makeTrackMap(Track track) {
        WritableMap errDes = Arguments.createMap();
        errDes.putInt(EVENT_PROP_TRACK_ID, track.id);
        errDes.putString(EVENT_PROP_TRACK_TYPE, trackType(track.type));
        errDes.putString(EVENT_PROP_TRACK_LANGUAGE, track.language);
        errDes.putInt(EVENT_PROP_TRACK_BITRATE, track.bitrate);
        errDes.putInt(EVENT_PROP_TRACK_WIDTH, track.width);
        errDes.putInt(EVENT_PROP_TRACK_HEIGHT, track.height);
        return errDes;
    }

    private static String trackType(int type) {
        switch (type) {
            case Track.TYPE_AUDIO:
                return "audio";
            case Track.TYPE_VIDEO:
                return "video";
            case Track.TYPE_CAPTIONS:
                return "captions";
            case Track.TYPE_COMBINED:
                return "combined";
            default:
                return "unknown";
        }
    }

    private static String stateName(int playerState) {
        switch (playerState) {
            case VdoPlayer.STATE_IDLE:
                return "idle";
            case VdoPlayer.STATE_BUFFERING:
                return "buffering";
            case VdoPlayer.STATE_READY:
                return "ready";
            default:
                return "ended";
        }
    }

    private void receiveEvent(@VdoEvent String type, WritableMap event) {
        eventEmitter.receiveEvent(viewId, type, event);
    }
}
