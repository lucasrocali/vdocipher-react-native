/**
 * VdoPlayerView component that can be dropped into a layout for video playback.
 */
import React, { Component } from 'react';
import PropTypes from 'prop-types';
import {requireNativeComponent, ViewPropTypes} from 'react-native';

export default class VdoPlayerView extends Component {
  constructor(props) {
    super(props);
    this._onInitSuccess = this._onInitSuccess.bind(this);
    this._onInitFailure = this._onInitFailure.bind(this);
  }

  _onInitSuccess(event: Event) {
    if (this.props.onInitializationSuccess) {
      this.props.onInitializationSuccess();
    }
  }

  _onInitFailure(event: Event) {
    if (this.props.onInitializationFailure) {
      this.props.onInitializationFailure();
    }
  }

  render() {
    return (
      <RCTVdoPlayerView
        onInitSuccess={this._onInitSuccess}
        onInitFailure={this._onInitFailure}
        {...this.props}
      />
    );
  }
}

VdoPlayerView.propTypes = {
  /* Native only */
  onInitSuccess: PropTypes.func,
  onInitFailure: PropTypes.func,

  /* Wrapper component public api */
  onInitializationSuccess: PropTypes.func,
  onInitializationFailure: PropTypes.func,

  /* Required */
  ...ViewPropTypes,
};

var RCTVdoPlayerView = requireNativeComponent('RCTVdoPlayerView', VdoPlayerView, {
  nativeOnly: {
    onInitSuccess: true,
    onInitFailure: true,
  }
});
