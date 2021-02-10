import React, { Component } from 'react';
import {
  StyleSheet,
  Text,
  View,
  Dimensions,
} from 'react-native';
import { VdoPlayerView } from 'vdocipher-rn-bridge';

export default class NativeControlsScreen extends Component {
  constructor(props) {
    super(props);
    console.log('NativeControlsScreen contructor');
    this.state = {
      fullscreen: false,
    };
  }

  componentDidMount() {
    console.log('NativeControlsScreen did mount');
  }

  componentWillUnmount() {
    console.log('NativeControlsScreen will unmount');
  }

  _handleFullscreenChange = (enterFullscreen) => {
    if (enterFullscreen) {
      console.log('enter fullscreen');
    } else {
      console.log('exit fullscreen');
    }
    this.setState({fullscreen: enterFullscreen});
  }

  render() {
    const embedInfo = this.props.navigation.getParam('embedInfo');
    const playerStyle = this.state.fullscreen ? styles.playerFullscreen : styles.playerNormal;

    return (
      <View style={styles.container}>
        <VdoPlayerView style={playerStyle}
          embedInfo={embedInfo}
          onInitializationSuccess={() => console.log('init success')}
          onInitializationFailure={(error) => console.log('init failure', error)}
          onLoading={(args) => console.log('loading')}
          onLoaded={(args) => console.log('loaded')}
          onLoadError={({errorDescription}) => console.log('load error', errorDescription)}
          onError={({errorDescription}) => console.log('error', errorDescription)}
          onTracksChanged={(args) => console.log('tracks changed')}
          onPlaybackSpeedChanged={(speed) => console.log('speed changed to', speed)}
          onMediaEnded={(args) => console.log('ended')}
          onEnterFullscreen={() => this._handleFullscreenChange(true)}
          onExitFullscreen={() => this._handleFullscreenChange(false)}
        />
        {!this.state.fullscreen &&
          <Text style={styles.description}>
            The ui controls for the player are embedded inside the native view
          </Text>
        }
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  },
  playerNormal: {
    height: 200,
    width: '100%',
  },
  playerFullscreen: {
    height: '100%',
    width: '100%',
  },
  description: {
    fontSize: 20,
    textAlign: 'center',
    margin: 10,
  },
});
