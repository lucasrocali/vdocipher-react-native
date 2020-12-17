/**
 * The native VdoCipher player module exposes a function 'startVideoScreen'
 * which takes the following parameters:
 *
 * 1. String params = json_stringify({
 *                                     embedInfo: {
 *                                        otp: string,
 *                                        playbackInfo: string
 *                                     }
 *                                  })
 */
import { NativeModules } from 'react-native';
import VdoPlayerView from './VdoPlayerView';
import VdoDownload from './downloads';

const { VdocipherRnBridge } = NativeModules;

const startVideoScreen = (params) => {
  VdocipherRnBridge.startVideoScreen(params);
}

export {
  startVideoScreen,
  VdoPlayerView,
  VdoDownload
}
