import { NativeEventEmitter, NativeModules } from 'react-native';

const eventEmitter = new NativeEventEmitter(NativeModules.VdoDownload);

const nativeEvents = ['onQueued', 'onChanged', 'onCompleted', 'onFailed', 'onDeleted'];

nativeEvents.forEach((name) => {
  eventEmitter.addListener(name, (event) => {
    _emitEvent(name, event.mediaId, event.downloadStatus);
  });
});

var _eventListeners = {};

var _emitEvent = (eventName, mediaId, downloadStatus) => {
  if (!_eventListeners[eventName]) {
    return;
  }
  _eventListeners[eventName].forEach((fn) => fn(mediaId, downloadStatus));
};

var addEventListener = (eventName, fn) => {
  const list = _eventListeners[eventName] = _eventListeners[eventName] || [];
  list.push(fn);
  return () => list.splice(list.findIndex((f) => f === fn), 1);
};

export default addEventListener;
