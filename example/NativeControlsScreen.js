import React, { Component } from 'react';
import {
  StyleSheet,
  Text,
  View
} from 'react-native';
import { VdoPlayerView } from 'vdocipher-rn-bridge';

export default class NativeControlsScreen extends Component {
  constructor(props) {
    super(props);
    this.state = {};
  }

  componentDidMount() {
    fetch("https://dev.vdocipher.com/api/site/homepage_video")
      .then(res => res.json())
      .then(resp => this.setState({otp: resp.otp, playbackInfo: resp.playbackInfo}));
  }

  render() {
    var ready = this.state.otp != null;
    const { otp, playbackInfo } = this.state;
    return (
      <View style={styles.container}>
        <VdoPlayerView style={styles.player}
          onInitializationSuccess={() => console.log('init success')}
          onInitializationFailure={() => console.log('init failure')}
        />
        <Text style={styles.description}>
          The ui controls for the player are embedded inside the native view
        </Text>
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
  player: {
    height: 200,
    width: '100%',
  },
  description: {
    fontSize: 20,
    textAlign: 'center',
    margin: 10,
  },
});
