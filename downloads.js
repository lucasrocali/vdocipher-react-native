/*
 * Downloads api:
 * 1. Fetch available download options for a video in your dashboard
 * 2. Download media assets to local storage
 * 3. Monitor download progress
 * 4. Manage downloads (query or delete downloads)
 */

import { NativeModules } from 'react-native';
import addEventListener from './downloadMonitor';

const { VdoDownload } = NativeModules;

const getDownloadOptions = (optionParams) => {

  return new Promise(function(resolve, reject) {
    VdoDownload.getDownloadOptions(
      optionParams,
      (errorDescription) => {
        reject(errorDescription);
      },
      (nativeId, downloadOptions) => {
        resolve({
          downloadOptions,
          enqueue: getEnqueueFn(nativeId)
        });
      }
    );
  });
};

const getEnqueueFn = (nativeId) => (downloadOptions) => {

  return new Promise(function(resolve, reject) {
    const requestOptions = {
      selections: downloadOptions.selections
    };

    VdoDownload.enqueueDownload(
      nativeId,
      requestOptions,
      (exception, msg) => {
        reject({exception, msg});
      },
      () => {
        // Download enqueued successfully
        resolve();
      }
    );
  });
};

const query = (queryFilters) => {

  return new Promise(function(resolve, reject) {
    VdoDownload.query(
      queryFilters,
      (exception, msg) => {
        reject({exception, msg});
      },
      (downloadStatusArray) => {
        // Query completed
        resolve(downloadStatusArray);
      }
    );
  });
};

const remove = (mediaIds) => {

  return new Promise(function(resolve, reject) {
    VdoDownload.remove(
      mediaIds,
      (exception, msg) => {
        reject({exception, msg});
      },
      () => {
        // Remove request posted
        resolve();
      }
    );
  });
};

module.exports = {
  getDownloadOptions,
  query,
  remove,
  addEventListener
};


// Example usage -- enqueue download ----------------------

// var optionParams = {otp, playbackInfo};
// var {downloadOptions, enqueue} = await getDownloadOptions(optionParams);
// var {mediaInfo, availableTracks} = downloadOptions;
// var selectedTracks = await getSelection(availableTracks);
// enqueue(selectedTracks);


// Example usage -- monitor downloads -----------------------

// var queryFilters = {
//   mediaId: [],
//   status: []
// };
//
// query(queryFilters)
//   .then(statusList => {
//     console.log(statusList);
//     invalidateUI(statusList);
//   });
//
// addEventListener('onQueued',
//   (mediaId, downloadStatus) => {
//     updateUI(mediaId, downloadStatus);
//   });
//
// addEventListener('onChanged',
//   (mediaId, downloadStatus) => {
//     updateUI(mediaId, downloadStatus);
//   });
//
// addEventListener('onCompleted',
//   (mediaId, downloadStatus) => {
//     updateUI(mediaId, downloadStatus);
//   });
//
// addEventListener('onFailed',
//   (mediaId, downloadStatus) => {
//     updateUI(mediaId);
//   });
//
// addEventListener('onDeleted',
//   (mediaId) => {
//     updateUI(mediaId);
//   });
