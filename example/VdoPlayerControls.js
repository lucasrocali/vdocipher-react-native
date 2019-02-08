import React, { Component } from 'react';
import {
  StyleSheet,
  Text,
  View,
  TouchableWithoutFeedback
} from 'react-native';
import { VdoPlayerView } from 'vdocipher-rn-bridge';
import ProgressBar from 'react-native-progress/Bar';
import Icon from 'react-native-vector-icons/FontAwesome';

function digitalTime(time) {
  return ~~(time / 60) + ":" + (time % 60 < 10 ? "0" : "") + time % 60;
}

export default class VdoPlayerControls extends Component {

  constructor(props) {
    super(props);
    this.state = {
      error: false,
      init: false,
      loaded: false,
      playWhenReady: false,
      buffering: false,
      ended: false,
      duration: 0,
      position: 0,
      speed: 1
    };
  }

  _onInitSuccess = () => {
    this.setState({
      init: true
    });
  }

  _onInitFailure = (error) => {
    this.setState({
      init: false,
      error: error.errorDescription
    });
  }

  _onLoading = () => {
    this.setState({
      loaded: false
    });
  }

  _onLoaded = metaData => {
    this.setState({
      loaded: true,
      duration: metaData.mediaInfo.duration / 1000
    });
  }

  _onTracksChanged = () => {
    // todo
  }

  _onProgress = (progress) => {
    this.setState({
      position: progress.currentTime / 1000
    });
  }

  _onMediaEnded = () => {
    this.setState({
      playWhenReady: false,
      ended: true
    });
  }

  _onPlayButtonTouch = () => {
    if (this.state.progress >= this.state.duration) {
      // todo
    }
    this.setState(state => {
      return {
        playWhenReady: !state.playWhenReady
      };
    });
  }

  _onProgressTouch = event => {
    if (this._seekbarWidth) {
      var positionX = event.nativeEvent.locationX;
      var targetSeconds = Math.floor((positionX / this._seekbarWidth) * this.state.duration);
      this._player.seek(targetSeconds * 1000);
    }
  }

  render() {
    var progressFraction = this.state.duration === 0 ? 0 : this.state.position / this.state.duration;
    var showPlayIcon = this.state.ended || !this.state.playWhenReady;

    return (
      <View style={styles.player.container}>
        <View>
          <VdoPlayerView ref={player => this._player = player}
            style={styles.player.video}
            {...this.props}
            playWhenReady={this.state.playWhenReady}
            showNativeControls={false}
            onInitializationSuccess={this._onInitSuccess}
            onInitializationFailure={this._onInitFailure}
            onLoading={this._onLoading}
            onLoaded={this._onLoaded}
            onTracksChanged={this._onTracksChanged}
            onProgress={this._onProgress}
            onMediaEnded={this._onMediaEnded}
          />
          <View style={styles.controls.container}>
            <TouchableWithoutFeedback onPress={this._onPlayButtonTouch}>
              <Icon name = {showPlayIcon ? "play" : "pause"}
                size={30}
                color="#FFF"
              />
            </TouchableWithoutFeedback>
            <Text
              style={styles.controls.position}>
              {digitalTime(Math.floor(this.state.position))}
            </Text>
            <TouchableWithoutFeedback style={styles.controls.progressBarContainer}
              onPress={this._onProgressTouch}
              onLayout={event => this._seekbarWidth = event.nativeEvent.layout.width}>
              <View style={styles.controls.progressBarContainer}>
                <ProgressBar style={styles.controls.progressBar}
                  progress={progressFraction}
                  color="#FFF"
                  unfilledColor="rgba(255, 255, 255, 0.4)"
                  borderColor="#FFF"
                  width={null}
                  height={20}
                />
              </View>
            </TouchableWithoutFeedback>
            <Text
              style={styles.controls.duration}>
              {digitalTime(Math.floor(this.state.duration))}
            </Text>
          </View>
        </View>
      </View>
    );
  }
}

const styles = {
  player: StyleSheet.create({
    container: {
      backgroundColor: '#000',
      alignSelf: 'stretch',
    },
    video: {
      overflow: 'hidden',
      position: 'absolute',
      top: 0,
      right: 0,
      bottom: 0,
      left: 0,
    },
  }),
  controls: StyleSheet.create({
    container: {
      backgroundColor: "rgba(0, 0, 0, 0.4)",
      height: 48,
      left: 0,
      bottom: 0,
      right: 0,
      position: 'absolute',
      flexDirection: 'row',
      alignItems: 'center',
      justifyContent: 'space-between',
      paddingHorizontal: 16,
    },
    position: {
      backgroundColor: 'transparent',
      color: '#FFF',
      fontSize: 14,
      textAlign: 'right',
      paddingHorizontal: 12,
    },
    duration: {
      backgroundColor: 'transparent',
      color: '#FFF',
      fontSize: 14,
      textAlign: 'right',
      paddingLeft: 12,
    },
    progressBarContainer: {
      flexDirection: 'row',
      flex: 1,
      alignItems: 'center',
    },
    progressBar: {
      width:'100%',
    }
  })
};
