
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
