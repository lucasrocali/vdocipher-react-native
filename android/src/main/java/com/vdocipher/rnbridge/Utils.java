package com.vdocipher.rnbridge;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.vdocipher.aegis.media.ErrorDescription;
import com.vdocipher.aegis.media.MediaInfo;
import com.vdocipher.aegis.media.Track;
import com.vdocipher.aegis.offline.DownloadOptions;
import com.vdocipher.aegis.offline.DownloadStatus;
import com.vdocipher.aegis.offline.VdoDownloadManager;
import com.vdocipher.aegis.player.VdoPlayer;

import java.util.List;

/**
 * Utility class.
 */

public class Utils {
    private static final String EVENT_PROP_MEDIA_INFO = "mediaInfo";
    private static final String EVENT_PROP_MEDIA_INFO_ID = "mediaId";
    private static final String EVENT_PROP_MEDIA_INFO_TYPE = "type";
    private static final String EVENT_PROP_MEDIA_INFO_TITLE = "title";
    private static final String EVENT_PROP_MEDIA_INFO_DESCRIPTION = "description";
    private static final String EVENT_PROP_MEDIA_INFO_DURATION = "duration";

    private static final String EVENT_PROP_AVAILABLE_TRACKS = "availableTracks";

    private static final String EVENT_PROP_TRACK_ID = "id";
    private static final String EVENT_PROP_TRACK_TYPE = "type";
    private static final String EVENT_PROP_TRACK_LANGUAGE = "language";
    private static final String EVENT_PROP_TRACK_BITRATE = "bitrate";
    private static final String EVENT_PROP_TRACK_WIDTH = "width";
    private static final String EVENT_PROP_TRACK_HEIGHT = "height";

    private static final String EVENT_PROP_DOWNLOAD_STATUS_MEDIA_INFO = "mediaInfo";
    private static final String EVENT_PROP_DOWNLOAD_STATUS_LOCAL_STORAGE_FOLDER = "localStorageFolder";
    private static final String EVENT_PROP_DOWNLOAD_STATUS_STATUS = "status";
    private static final String EVENT_PROP_DOWNLOAD_STATUS_REASON = "reason";
    private static final String EVENT_PROP_DOWNLOAD_STATUS_REASON_DESCRIPTION = "reasonDescription";
    private static final String EVENT_PROP_DOWNLOAD_STATUS_TOTAL_SIZE_BYTES = "totalSizeBytes";
    private static final String EVENT_PROP_DOWNLOAD_STATUS_BYTES_DOWNLOADED = "bytesDownloaded";
    private static final String EVENT_PROP_DOWNLOAD_STATUS_DOWNLOAD_PERCENT = "downloadPercent";
    private static final String EVENT_PROP_DOWNLOAD_STATUS_POSTER = "poster";
    private static final String EVENT_PROP_DOWNLOAD_STATUS_LAST_MODIFIED_TIMESTAMP = "lastModifiedTimestamp";

    private static final String EVENT_PROP_ERROR_CODE = "errorCode";
    private static final String EVENT_PROP_ERROR_MSG = "errorMsg";
    private static final String EVENT_PROP_ERROR_HTTP_CODE = "httpStatusCode";

    public static String trackType(int type) {
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

    public static String stateName(int playerState) {
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

    public static WritableMap makeErrorDescriptionMap(ErrorDescription errorDescription) {
        WritableMap errDes = Arguments.createMap();
        errDes.putInt(EVENT_PROP_ERROR_CODE, errorDescription.errorCode);
        errDes.putString(EVENT_PROP_ERROR_MSG, errorDescription.errorMsg);
        errDes.putInt(EVENT_PROP_ERROR_HTTP_CODE, errorDescription.httpStatusCode);
        return errDes;
    }

    public static WritableMap makeMediaInfoMap(MediaInfo mediaInfo) {
        WritableMap mediaInfoMap = Arguments.createMap();
        mediaInfoMap.putString(EVENT_PROP_MEDIA_INFO_ID, mediaInfo.mediaId);
        mediaInfoMap.putString(EVENT_PROP_MEDIA_INFO_TYPE, mediaInfoTypeName(mediaInfo.type));
        mediaInfoMap.putString(EVENT_PROP_MEDIA_INFO_TITLE, mediaInfo.title);
        mediaInfoMap.putString(EVENT_PROP_MEDIA_INFO_DESCRIPTION, mediaInfo.description);
        mediaInfoMap.putInt(EVENT_PROP_MEDIA_INFO_DURATION, (int)mediaInfo.duration);
        return mediaInfoMap;
    }

    public static WritableArray makeTrackMapArray(Track[] tracks) {
        WritableArray trackArray = Arguments.createArray();
        for (Track track: tracks) {
            trackArray.pushMap(makeTrackMap(track));
        }
        return trackArray;
    }

    public static WritableMap makeTrackMap(Track track) {
        WritableMap trackMap = Arguments.createMap();
        trackMap.putInt(EVENT_PROP_TRACK_ID, track.id);
        trackMap.putString(EVENT_PROP_TRACK_TYPE, trackType(track.type));
        trackMap.putString(EVENT_PROP_TRACK_LANGUAGE, track.language);
        trackMap.putInt(EVENT_PROP_TRACK_BITRATE, track.bitrate);
        trackMap.putInt(EVENT_PROP_TRACK_WIDTH, track.width);
        trackMap.putInt(EVENT_PROP_TRACK_HEIGHT, track.height);
        return trackMap;
    }

    public static WritableMap makeDownloadOptionsMap(DownloadOptions options) {
        WritableMap optionsMap = Arguments.createMap();
        optionsMap.putString(EVENT_PROP_MEDIA_INFO_ID, options.mediaId);
        optionsMap.putMap(EVENT_PROP_MEDIA_INFO, makeMediaInfoMap(options.mediaInfo));
        optionsMap.putArray(EVENT_PROP_AVAILABLE_TRACKS, makeTrackMapArray(options.availableTracks));
        return optionsMap;
    }

    public static WritableMap makeDownloadStatusMap(DownloadStatus status) {
        WritableMap statusMap = Arguments.createMap();
        statusMap.putMap(EVENT_PROP_DOWNLOAD_STATUS_MEDIA_INFO, makeMediaInfoMap(status.mediaInfo));
        statusMap.putString(EVENT_PROP_DOWNLOAD_STATUS_LOCAL_STORAGE_FOLDER, status.localStorageFolder);
        statusMap.putString(EVENT_PROP_DOWNLOAD_STATUS_STATUS, downloadStatusNameFromInt(status.status));
        statusMap.putInt(EVENT_PROP_DOWNLOAD_STATUS_REASON, status.reason);
        statusMap.putString(EVENT_PROP_DOWNLOAD_STATUS_REASON_DESCRIPTION, status.reasonDescription);
        statusMap.putDouble(EVENT_PROP_DOWNLOAD_STATUS_TOTAL_SIZE_BYTES, status.totalSizeBytes);
        statusMap.putDouble(EVENT_PROP_DOWNLOAD_STATUS_BYTES_DOWNLOADED, status.bytesDownloaded);
        statusMap.putInt(EVENT_PROP_DOWNLOAD_STATUS_DOWNLOAD_PERCENT, status.downloadPercent);
        statusMap.putString(EVENT_PROP_DOWNLOAD_STATUS_POSTER, status.poster);
        statusMap.putDouble(EVENT_PROP_DOWNLOAD_STATUS_LAST_MODIFIED_TIMESTAMP, status.lastModifiedTimestamp);

        return statusMap;
    }

    public static WritableArray makeDownloadStatusMapArray(List<DownloadStatus> statusList) {
        WritableArray statusArray = Arguments.createArray();
        for (DownloadStatus status: statusList) {
            statusArray.pushMap(makeDownloadStatusMap(status));
        }
        return statusArray;
    }

    public static String mediaInfoTypeName(int mediaInfoType) {
        switch (mediaInfoType) {
            case MediaInfo.TYPE_STREAMING:
                return "streaming";
            case MediaInfo.TYPE_OFFLINE:
                return "offline";
            case MediaInfo.TYPE_INFO:
                return "info";
            default:
                throw new IllegalArgumentException("Unknown MediaInfo type");
        }
    }

    public static int downloadStatusIntFromName(String statusName) {
        switch (statusName) {
            case "pending":
                return VdoDownloadManager.STATUS_PENDING;
            case "downloading":
                return VdoDownloadManager.STATUS_DOWNLOADING;
            case "paused":
                return VdoDownloadManager.STATUS_PAUSED;
            case "completed":
                return VdoDownloadManager.STATUS_COMPLETED;
            case "failed":
                return VdoDownloadManager.STATUS_FAILED;
            default:
                throw new IllegalArgumentException("Unknown status");
        }
    }

    public static String downloadStatusNameFromInt(int status) {
        switch (status) {
            case VdoDownloadManager.STATUS_PENDING:
                return "pending";
            case VdoDownloadManager.STATUS_DOWNLOADING:
                return "downloading";
            case VdoDownloadManager.STATUS_PAUSED:
                return "paused";
            case VdoDownloadManager.STATUS_COMPLETED:
                return "completed";
            case VdoDownloadManager.STATUS_FAILED:
                return "failed";
            default:
                throw new IllegalArgumentException("Unknown status");
        }
    }

    public static String digitalClockTime(int timeInMilliSeconds) {
        int totalSeconds = timeInMilliSeconds/1000;
        int hours = totalSeconds / (60 * 60);
        int minutes = (totalSeconds - hours * 60 * 60) / 60;
        int seconds = (totalSeconds - hours * 60 * 60 - minutes * 60);

        String timeThumb = "";
        if (hours > 0) {
            if (hours < 10) {
                timeThumb += "0" + hours + ":";
            } else {
                timeThumb += hours + ":";
            }
        }
        if (minutes > 0) {
            if (minutes < 10) {
                timeThumb += "0" + minutes + ":";
            } else {
                timeThumb += minutes + ":";
            }
        } else {
            timeThumb += "00" + ":";
        }
        if (seconds < 10) {
            timeThumb += "0" + seconds;
        } else {
            timeThumb += seconds;
        }
        return timeThumb;
    }

    /**
     * @return index of number in provided array closest to the provided number
     */
    public static int getClosestFloatIndex(float[] refArray, float comp) {
        float distance = Math.abs(refArray[0] - comp);
        int index = 0;
        for (int i = 1; i < refArray.length; i++) {
            float currDistance = Math.abs(refArray[i] - comp);
            if (currDistance < distance) {
                index = i;
                distance = currDistance;
            }
        }
        return index;
    }

    public static String playbackStateString(boolean playWhenReady, int playbackState) {
        String stateName;
        switch (playbackState) {
            case VdoPlayer.STATE_IDLE:
                stateName = "STATE_IDLE";
                break;
            case VdoPlayer.STATE_READY:
                stateName = "STATE_READY";
                break;
            case VdoPlayer.STATE_BUFFERING:
                stateName = "STATE_BUFFERING";
                break;
            case VdoPlayer.STATE_ENDED:
                stateName = "STATE_ENDED";
                break;
            default:
                stateName = "STATE_UNKNOWN";
        }
        return "playWhenReady " + (playWhenReady ? "true" : "false") + ", " + stateName;
    }
}