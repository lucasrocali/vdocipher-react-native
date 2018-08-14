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
    console.log('NativeControlsScreen contructor');
  }

  componentDidMount() {
    console.log('NativeControlsScreen did mount');
  }

  componentWillUnmount() {
    console.log('NativeControlsScreen will unmount');
  }

  render() {
    const embedInfo = this.props.navigation.getParam('embedInfo');

    return (
      <View style={styles.container}>
        <VdoPlayerView style={styles.player}
          embedInfo={embedInfo}
          onInitializationSuccess={() => console.log('init success')}
          onInitializationFailure={() => console.log('init failure')}
          onLoading={(args) => console.log('loading')}
          onLoaded={(args) => console.log('loaded')}
          onTracksChanged={(args) => console.log('tracks changed')}
          onMediaEnded={(args) => console.log('ended')}
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
