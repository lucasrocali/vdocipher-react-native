# Offline downloads
## Overview

The VdoCipher react-native sdk also offers capability to download videos to local storage for offline playback on android devices running Lollipop and above (api level 20+).

It includes apis to :
* fetch available download options for a video in your dashboard
* download media assets to local storage
* track download progress
* manage downloads (query or delete downloads)

We'll explore a typical download workflow in this document. The [example app on github](https://github.com/VdoCipher/vdocipher-react-native/blob/master/example/DownloadsScreen.js) provides code examples for a typical use case.

## Get the available options for a media

A video in your VdoCipher dashboard may be encoded in multiple bitrates (for adaptive streaming) or has multiple audio tracks for different languages. Hence, there are some options regarding what exactly needs to be downloaded. For offline playback, adaptive doesn't make sense, and also the user typically has a preferred language. So, you must specify exactly one video track and exactly one audio track for download.

The first step is to get the available options for the video. We'll use the `getDownloadOptions` function for this purpose. We'll need a playbackInfo and otp or signature corresponding to the video to fetch the options.

```
import { VdoDownload } from 'vdocipher-rn-bridge';

// assuming we have otp and playbackInfo
VdoDownload.getDownloadOptions({otp, playbackInfo})
      .then( ({downloadOptions, enqueue}) => {
        console.log('Got download options', downloadOptions);
      });
```

The two parameters in the response to note are `downloadOptions` and `enqueue`.

`downloadOptions` is an object that contains a `mediaInfo` object (with general details of the media, such as title, description as set in your VdoCipher dashboard, etc.) and an array of `track` objects corresponding to the available audio and video track options. Each `track` object in the array corresponds to a audio or video track (specified by its `type` property) and contains relevant information such as bitrate, resolution, language, etc.

`downloadOption`:

Property | Type | Description
--- | --- | ---
mediaId | string | string identifying the video in your dashboard
mediaInfo | object | object containing description of the video
availableTracks | object[] | array of `track` objects available to download

where `mediaInfo` is:

Property | Type | Description
--- | --- | ---
mediaId | string | string identifying the video in your dashboard
title | string | video title as set in your dashboard
description | string | video description as set in your dashboard
duration | number | video duration in milliseconds

and `track` is:

Property | Type | Description
--- | --- | ---
id | number | an integer identifying the track
type | string | one of 'audio', 'video', 'captions', 'combined' or 'unknown'
language | string | optional: language of track if relevant
bitrate | number | optional: bitrate in bps if relevant
width | number | optional: width resolution if relevant
height | number | optional: height resolution if relevant otherwise

`enqueue` is a function that you call after you have decided the track selections to start the download. It takes an object as argument which has a `selections` property equal to an array containing the indices of selected tracks from the `downloadOptions.availableTracks` array.

Once we've obtained the available options, the next step is to make a selection of which tracks to download.

## Select the tracks to download, and enqueue request

As mentioned earlier in this document, we need to select exactly one audio track and one video track.

Once we have the download options, we can make selections automatically based on user preferences etc. (e.g. select the highest quality/bitrate) or present the options to the user to choose.

Once a selection has been made (automatically or by the user), call the `enqueue` function with the selections.

```
// selections must include exactly one audio track (`type` === 'audio') and one video track (`type` === 'video')
// track indices are the index of the track in the `availableTracks` array in received `downloadOptions`

const selections = getSelection(downloadOptions.availableTracks);
enqueue({selections})
  .then(() => console.log('enqueue success'));
```

This will add the request to the download queue and start download when all requests enqueued before have completed.

## Monitoring download progress

We can monitor the progress of the download queue by registering a event listener.

```
// register listeners for download events; recommended to do this in componentDidMount()

this.state.unregister.push(
  VdoDownload.addEventListener('onQueued',
  (mediaId, status) => console.log('queued', mediaId))
);
this.state.unregister.push(
  VdoDownload.addEventListener('onChanged',
  (mediaId, status) => console.log('changed', mediaId, downloadStatus.downloadPercent + '%'))
);
this.state.unregister.push(
  VdoDownload.addEventListener('onCompleted',
  (mediaId, status) => console.log('completed', mediaId))
);
this.state.unregister.push(
  VdoDownload.addEventListener('onFailed',
  (mediaId, status) => console.warn('failed', mediaId, downloadStatus.reason))
);
this.state.unregister.push(
  VdoDownload.addEventListener('onDeleted',
  (mediaId) => console.log('deleted', mediaId))
);

// unregister listeners in componentWillUnmount()
this.state.unregister.forEach((fn) => fn());
```

## Query for downloads

The sdk allows querying for all downloads managed by it or only specific downloads specified by filters.

Queries provide status of all downloads that are queued or downloading or completed.

```
// query for all downloads
VdoDownload.query()
  .then(statusArray => {
    console.log('query results', statusArray);
  });

// filtered query
var queryFilters = {
  mediaId: ['abc', 'xyz'],
  status: ['pending', 'failed']
};

VdoDownload.query(queryFilters)
  .then(statusArray => {
    console.log('filtered query results', statusArray);
  });
```

A query result is provided as an array of `downloadStatus` objects which provides information such as the mediaInfo, status, any errors if they occured while downloading, etc.

`downloadStatus`:

Property | Type | Description
--- | --- | ---
mediaInfo | object | object containing description of the video
status | string | one of 'pending', 'downloading', 'paused', 'completed, or 'failed'
reason | int | if `status` is 'failed', this identifies an error reason
reasonDescription | string | error reason description
totalSizeBytes | int | total download size estimate in bytes
bytesDownloaded | int | estimated downloaded bytes
downloadPercent | int | download progress in percent
lastModifiedTimestamp | number | last status change timestamp

## Delete a download

To delete a offline download, use the `remove()` method. This will cancel the download if it is still downloading or pending and remove any downloaded media files. You will also receive a `onDeleted` event if you have an event listener registered for the event.

```
// Specify an array of mediaId's to delete
VdoDownload.remove([mediaId]);
```
