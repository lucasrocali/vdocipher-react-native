/**
 * VdoPlayerView component that can be dropped into a layout for video playback.
 */
import React, { Component } from 'react';
import PropTypes from 'prop-types';
import {requireNativeComponent, ViewPropTypes} from 'react-native';

export default class VdoPlayerView extends Component {
  constructor(props) {
    super(props);
    this.state = {
      fullscreen: true,
      orientation: 'portrait',
    }
  }

  _onInitSuccess = (event) => {
    if (this.props.onInitializationSuccess) {
      this.props.onInitializationSuccess(event.nativeEvent);
    }
  }

  _onInitFailure = (event) => {
    if (this.props.onInitializationFailure) {
      this.props.onInitializationFailure(event.nativeEvent);
    }
  }

  _onLoading = (event) => {
    if (this.props.onLoading) {
      this.props.onLoading();
    }
  }

  _onLoaded = (event) => {
    if (this.props.onLoaded) {
      this.props.onLoaded();
    }
  }

  _onLoadError = (event) => {
    if (this.props.onLoadError) {
      this.props.onLoadError(event.nativeEvent);
    }
  }

  _onPlayerStateChanged = (event) => {
    if (this.props.onPlayerStateChanged) {
      this.props.onPlayerStateChanged(event.nativeEvent);
    }
  }

  _onProgress = (event) => {
    if (this.props.onProgress) {
      this.props.onProgress(event.nativeEvent);
    }
  }

  _onBufferUpdate = (event) => {
    if (this.props.onBufferUpdate) {
      this.props.onBufferUpdate(event.nativeEvent);
    }
  }

  _onPlaybackSpeedChanged = (event) => {
    if (this.props.onPlaybackSpeedChanged) {
      this.props.onPlaybackSpeedChanged(event.nativeEvent);
    }
  }

  _onTracksChanged = (event) => {
    if (this.props.onTracksChanged) {
      this.props.onTracksChanged(event.nativeEvent);
    }
  }

  _onMediaEnded = (event) => {
    if (this.props.onMediaEnded) {
      this.props.onMediaEnded();
    }
  }

  _onError = (event) => {
    if (this.props.onError) {
      this.props.onError(event.nativeEvent);
    }
  }

  render() {
    return (
      <RCTVdoPlayerView
        onInitSuccess={this._onInitSuccess}
        onInitFailure={this._onInitFailure}
        onVdoLoading={this._onLoading}
        onVdoLoaded={this._onLoaded}
        onVdoLoadError={this._onLoadError}
        onVdoPlayerStateChanged={this._onPlayerStateChanged}
        onVdoProgress={this._onProgress}
        onVdoBufferUpdate={this._onBufferUpdate}
        onVdoPlaybackSpeedChanged={this._onPlaybackSpeedChanged}
        onVdoTracksChanged={this._onTracksChanged}
        onVdoMediaEnded={this._onMediaEnded}
        onVdoError={this._onError}
        {...this.props}
      />
    );
  }
}

VdoPlayerView.propTypes = {
  /* Native only */
  onInitSuccess: PropTypes.func,
  onInitFailure: PropTypes.func,
  onVdoLoading: PropTypes.func,
  onVdoLoaded: PropTypes.func,
  onVdoLoadError: PropTypes.func,
  onVdoPlayerStateChanged: PropTypes.func,
  onVdoProgress: PropTypes.func,
  onVdoBufferUpdate: PropTypes.func,
  onVdoPlaybackSpeedChanged: PropTypes.func,
  onVdoTracksChanged: PropTypes.func,
  onVdoMediaEnded: PropTypes.func,
  onVdoError: PropTypes.func,

  /* Wrapper component public api */
  embedInfo: PropTypes.object,
  onInitializationSuccess: PropTypes.func,
  onInitializationFailure: PropTypes.func,
  onLoading: PropTypes.func,
  onLoaded: PropTypes.func,
  onLoadError: PropTypes.func,
  onPlayerStateChanged: PropTypes.func,
  onProgress: PropTypes.func,
  onBufferUpdate: PropTypes.func,
  onPlaybackSpeedChanged: PropTypes.func,
  onTracksChanged: PropTypes.func,
  onMediaEnded: PropTypes.func,
  onError: PropTypes.func,

  /* Required */
  ...ViewPropTypes,
};

var RCTVdoPlayerView = requireNativeComponent('RCTVdoPlayerView', VdoPlayerView, {
  nativeOnly: {
    onInitSuccess: true,
    onInitFailure: true,
  }
});
