
# vdocipher-rn-bridge


## Note:

1. This is a _work in progress_. Please contact us at info@vdocipher.com  before
   using it in your application
2. IOS is not supported. Only Android support available. IOS support will be
   added in future.
3. Currently, it is not possible to get any events or callbacks from Android
   player activity. The video will open in a new _native_ activity outside of
   the react native setup. Back button will take the user from the player
   activity to home activity.

React native library for integrating vdocipher android sdk into your react native app.

## Try the demo app

To run the example react-native app included in this repo, clone this repo to your
development machine, and run the example app:

`$ mkdir vdocipher-react-native && cd vdocipher-react-native`

`$ git clone https://github.com/VdoCipher/vdocipher-react-native.git .`

`$ npm install && cd example && npm install && npm run android`

## Getting started

`$ npm install vdocipher-rn-bridge --save`

For installation you can choose either automatic or manual installation:

### Mostly automatic installation

`$ react-native link vdocipher-rn-bridge`

### Manual installation


#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `vdocipher-rn-bridge` and add `VdocipherRnBridge.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libVdocipherRnBridge.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.vdocipher.rnbridge.VdocipherRnBridgePackage;` to the imports at the top of the file
  - Add `new VdocipherRnBridgePackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':vdocipher-rn-bridge'
  	project(':vdocipher-rn-bridge').projectDir = new File(rootProject.projectDir, 	'../node_modules/vdocipher-rn-bridge/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':vdocipher-rn-bridge')
  	```

### Installation troubleshooting
If you encounter a build error for android project like _'Cannot find com.vdocipher.aegis:vdocipher-android:X.X.X',_
you may need to add the following maven repository to your **android/build.gradle** under **allprojects -> repositories**:

```
maven {
    url "https://github.com/VdoCipher/maven-repo/raw/master/repo"
}
```

## Usage

### Launch a video in a fullscreen player

```javascript
import { startVideoScreen } from 'vdocipher-rn-bridge';

startVideoScreen({embedInfo: {otp: 'some-otp', playbackInfo: 'some-playbackInfo'}});
```

### Embed a video in your react native layout

```javascript
import { VdoPlayerView } from 'vdocipher-rn-bridge';

const embedInfo = {otp: 'some-otp', playbackInfo: 'some-playbackInfo'};

// in JSX

<VdoPlayerView
  style={{height: 200, width: '100%'}}
  embedInfo={embedInfo}
/>
```

### VdoPlayerView props

* [embedInfo](#embedinfo)
* [showNativeControls](#shownativecontrols)
* [playWhenReady](#playwhenready)

### VdoPlayerView event props

* [onInitializationSuccess](#oninitializationsuccess)
* [onInitializationFailure](#oninitializationfailure)
* [onLoading](#onloading)
* [onLoaded](#onloaded)
* [onLoadError](#onloadError)
* [onPlayerStateChanged](#onplayerstatechanged)
* [onProgress](#onprogress)
* [onBufferUpdate](#onbufferUpdate)
* [onPlaybackSpeedChanged](#onplaybackspeedchanged)
* [onTracksChanged](#ontrackschanged)
* [onMediaEnded](#onmediaended)
* [onError](#onerror)

### Props

#### embedInfo

Property | Type | Description
--- | --- | ---
otp | string | a valid otp
playbackInfo | string | a valid playbackInfo

#### showNativeControls

Controls whether natively embedded player controls are shown.
* **true (default)** - show native controls
* **false** - hide native controls

#### playWhenReady

Controls whether playback will progress if the player is in ready state.
* **true (default)** - play when ready
* **false** - pause playback

### Event props

#### onInitializationSuccess
Callback function invoked when a native `VdoPlayer` is created.

Typically, no handling is required when this is called. The internal player will automatically load the `embedInfo` provided in props.

#### onInitializationFailure
Callback function invoked when a `VdoPlayer` creation failed due to some reason. Playback will not happen.

The error details are provided in the payload under `errorDescription`.

Payload:

Property | Type | Description
--- | --- | ---
errorDescription | object | error details

`errorDescription`:

Property | Type | Description
--- | --- | ---
errorCode | number | an integer identifying the error
errorMsg | string | short message description of the error
httpStatusCode | number | http status code if relevant, -1 otherwise

#### onLoading
Callback function invoked when a new video starts loading.

#### onLoaded
Callback function invoked when a new video has successfully loaded. Playback can begin now.

#### onLoadError
Callback function invoked when a video failed to load due to some reason.

The error details are provided in the payload under `errorDescription`.

Payload:

Property | Type | Description
--- | --- | ---
errorDescription | object | error details
embedInfo | object | embedInfo for which the error occurred

`errorDescription`:

Property | Type | Description
--- | --- | ---
errorCode | number | an integer identifying the error
errorMsg | string | short message description of the error
httpStatusCode | number | http status code if relevant, -1 otherwise

#### onPlayerStateChanged
Callback function invoked when player state changes.

Payload:

Property | Type | Description
--- | --- | ---
playerState | string | one of 'idle', 'buffering', 'ready' or 'ended'
playWhenReady | boolean | whether playback will progress if playerState is 'ready'

#### onProgress
Callback function invoked to provided playback time updates.

Payload:

Property | Type | Description
--- | --- | ---
currentTime | number | current playback time in milliseconds

#### onBufferUpdate
Callback function invoked to provide buffered time updates.

Payload:

Property | Type | Description
--- | --- | ---
bufferTime | number | current buffered time in milliseconds

#### onPlaybackSpeedChanged
Callback function invoked when playback speed changes.

Payload:

Property | Type | Description
--- | --- | ---
playbackSpeed | number | current playback speed

#### onTracksChanged
Callback function invoked when available or selected track changes.

Payload:

Property | Type | Description
--- | --- | ---
availableTracks | object[] | array of available `track` objects
selectedTracks | object[] | array of currently selected `track` objects

`track`:

Property | Type | Description
--- | --- | ---
id | number | an integer identifying the track
type | string | one of 'audio', 'video', 'captions', 'combined' or 'unknown'
language | string | optional: language of track if relevant
bitrate | number | optional: bitrate in bps if relevant
width | number | optional: width resolution if relevant
height | number | optional: height resolution if relevant otherwise

#### onMediaEnded
Callback function invoked when a video reached end of playback.

#### onError
Callback function invoked when video playback is interrupted due to an error.

The error details are provided in the payload under `errorDescription`.

Payload:

Property | Type | Description
--- | --- | ---
errorDescription | object | error details
embedInfo | object | embedInfo for which the error occurred

`errorDescription`:

Property | Type | Description
--- | --- | ---
errorCode | number | an integer identifying the error
errorMsg | string | short message description of the error
httpStatusCode | number | http status code if relevant, -1 otherwise
