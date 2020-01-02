package com.vdocipher.rnbridge;

import android.content.Context;
import android.util.Log;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.vdocipher.aegis.media.ErrorCodes;
import com.vdocipher.aegis.media.ErrorDescription;
import com.vdocipher.aegis.offline.DownloadOptions;
import com.vdocipher.aegis.offline.DownloadRequest;
import com.vdocipher.aegis.offline.DownloadSelections;
import com.vdocipher.aegis.offline.OptionsDownloader;
import com.vdocipher.aegis.offline.VdoDownloadManager;

import java.io.File;
import java.util.HashMap;

import javax.annotation.Nonnull;

import static com.vdocipher.rnbridge.Utils.*;

/**
 * Exposes apis to:
 * <p>
 * <li>Fetch available download options for a video in your VdoCipher dashboard
 * <li>Download media assets to local storage
 * <li>Track download progress
 * <li>Manage downloads (query or delete downloads)
 */

public class VdoDownloadModule extends ReactContextBaseJavaModule {
    private static final String TAG = "VdoDownloadModule";

    private final HashMap<String, DownloadOptions> downloadOptionsStore;
    private static final String DEFAULT_DOWNLOAD_DIRECTORY_NAME = "offlineVdos";

    public VdoDownloadModule(ReactApplicationContext reactContext) {
        super(reactContext);
        downloadOptionsStore = new HashMap<>();
    }

    @Override
    public String getName() {
        return "VdoDownload";
    }

    @ReactMethod
    public void getDownloadOptions(ReadableMap optionParams,
                                   Callback errorCallback,
                                   Callback successCallback) {
        String otp = optionParams.getString("otp");
        String playbackInfo = optionParams.getString("playbackInfo");

        OptionsDownloader optionsDownloader = new OptionsDownloader();

        try {
            optionsDownloader.downloadOptionsWithOtp(otp, playbackInfo, new OptionsDownloader.Callback() {
                @Override
                public void onOptionsReceived(DownloadOptions options) {
                    Log.i(TAG, "onOptionsReceived");
                    downloadOptionsStore.put(playbackInfo, options);
                    successCallback.invoke(
                            playbackInfo, /* nativeId */
                            makeDownloadOptionsMap(options)
                    );
                }

                @Override
                public void onOptionsNotReceived(ErrorDescription errorDescription) {
                    Log.e(TAG, "onOptionsNotReceived: " + errorDescription.toString());
                    errorCallback.invoke(
                            makeErrorDescriptionMap(errorDescription)
                    );
                }
            });
        } catch (RuntimeException e) {
            Log.e(TAG, Log.getStackTraceString(e));
            ErrorDescription errDesc = new ErrorDescription(
                    ErrorCodes.INVALID_PLAYBACK_INFO, "Invalid playbackInfo", -1);
            errorCallback.invoke(
                    makeErrorDescriptionMap(errDesc)
            );
        }
    }

    @ReactMethod
    public void enqueueDownload(String nativeId,
                                ReadableMap requestOptions,
                                Callback errorCallback,
                                Callback successCallback) {
        enqueueDownload(nativeId, requestOptions, new EnqueueFailureCallback(errorCallback), successCallback);
    }

    public void query(ReadableMap queryFilters,
                      Callback errorCallback,
                      Callback successCallback) {
        try {
            // Read query filters
            ReadableArray mediaIdFilters = queryFilters.getArray("mediaId");
            ReadableArray statusFilters = queryFilters.getArray("status");

            // Build query
            VdoDownloadManager.Query query = new VdoDownloadManager.Query();

            if (mediaIdFilters != null && mediaIdFilters.size() > 0) {
                String[] mediaIds = new String[mediaIdFilters.size()];
                for (int i = 0; i < mediaIdFilters.size(); i++) {
                    mediaIds[i] = mediaIdFilters.getString(i);
                }
                query.setFilterByMediaId(mediaIds);
            }

            if (statusFilters != null && statusFilters.size() > 0) {
                int[] statuses = new int[statusFilters.size()];
                for (int i = 0; i < statusFilters.size(); i++) {
                    statuses[i] = downloadStatusIntFromName(statusFilters.getString(i));
                }
                query.setFilterByStatus(statuses);
            }

            VdoDownloadManager vdoDownloadManager = VdoDownloadManager.getInstance(getReactApplicationContext());
            vdoDownloadManager.query(query, statusList -> {
                Log.i(TAG, statusList.size() + " results found");
                successCallback.invoke(
                        makeDownloadStatusMapArray(statusList)
                );
            });
        } catch (RuntimeException e) {
            Log.e(TAG, "error running query: " + Log.getStackTraceString(e));
            errorCallback.invoke(e.getClass().getName(), e.getMessage());
        }
    }

    private void enqueueDownload(String nativeId,
                                ReadableMap requestOptions,
                                EnqueueFailureCallback errorCallback,
                                Callback successCallback) {
        try {
            // Ensure downloadOptions is available
            // TODO remove used from store/cache
            DownloadOptions downloadOptions = downloadOptionsStore.get(nativeId);
            if (downloadOptions == null) {
                Log.e(TAG, "download options could not be obtained");
                errorCallback.invoke(null, "download options could not be obtained");
                return;
            }

            // Read selected tracks
            ReadableArray selectionsArray = requestOptions.getArray("selections");
            int[] selectedIndices = new int[selectionsArray.size()];
            for (int i = 0; i < selectionsArray.size(); i++) {
                selectedIndices[i] = selectionsArray.getInt(i);
            }

            // Obtain download location
            String downloadLocation;
            try {
                downloadLocation = getDownloadLocation(getReactApplicationContext());
            } catch (RuntimeException e) {
                Log.e(TAG, "download location could not be obtained: " + Log.getStackTraceString(e));
                errorCallback.invoke(e.getClass().getName(), e.getMessage());
                return;
            }
            Log.i(TAG, "will save media to " + downloadLocation);

            DownloadSelections downloadSelections = new DownloadSelections(downloadOptions, selectedIndices);

            // Build a DownloadRequest
            DownloadRequest request = new DownloadRequest.Builder(downloadSelections, downloadLocation).build();

            VdoDownloadManager vdoDownloadManager = VdoDownloadManager.getInstance(getReactApplicationContext());

            // Enqueue request to VdoDownloadManager for download
            try {
                vdoDownloadManager.enqueue(request);
                successCallback.invoke();
            } catch (IllegalArgumentException | IllegalStateException e) {
                Log.e(TAG, "enqueue failed: " + Log.getStackTraceString(e));
                errorCallback.invoke(e.getClass().getName(), e.getMessage());
            }
        } catch (RuntimeException e) {
            Log.e(TAG, "error enqueuing download request: " + Log.getStackTraceString(e));
            errorCallback.invoke(e.getClass().getName(), e.getMessage());
        }
    }

    private String getDownloadLocation(@Nonnull Context context) {
        String downloadLocation = context.getExternalFilesDir(null).getPath() + File.separator + DEFAULT_DOWNLOAD_DIRECTORY_NAME;

        // Ensure download directory is created
        File dlLocation = new File(downloadLocation);
        if (!(dlLocation.exists() && dlLocation.isDirectory())) {
            // Directory not created yet; let's create it
            if (!dlLocation.mkdir()) {
                throw new RuntimeException("failed to create storage directory");
            }
        }
        return downloadLocation;
    }

    /**
     * Helper class to enforce enqueue failure callback arguments.
     */
    private static class EnqueueFailureCallback {
        private final Callback errorCallback;

        EnqueueFailureCallback(Callback errorCallback) {
            this.errorCallback = errorCallback;
        }

        // TODO user message argument?
        void invoke(String exception, String message) {
            errorCallback.invoke(exception, message);
        }
    }
}
