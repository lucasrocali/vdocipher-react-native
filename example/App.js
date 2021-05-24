/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow
 */

import React, { Component } from 'react';
import {
  Platform,
  StyleSheet,
  Text,
  Button,
  View
} from 'react-native';
import 'react-native-gesture-handler';
import { createAppContainer } from 'react-navigation';
import { createStackNavigator } from 'react-navigation-stack';
import { startVideoScreen } from 'vdocipher-rn-bridge';
import NativeControlsScreen from './NativeControlsScreen';
import JSControlsScreen from './JSControlsScreen';
import DownloadsScreen from './DownloadsScreen';

type Props = {};
class HomeScreen extends Component<Props> {
  constructor(props) {
    super(props);
    console.log('HomeScreen contructor');
    this.state = {};
  }

  componentDidMount() {
    console.log('HomeScreen did mount');
    fetch("https://dev.vdocipher.com/api/site/homepage_video")
      .then(res => res.json())
      .then(resp => this.setState({otp: resp.otp, playbackInfo: resp.playbackInfo}));
  }

  componentWillUnmount() {
    console.log('HomeScreen will unmount');
  }

  render() {
    var ready = this.state.otp != null;
    const { otp, playbackInfo } = this.state;
    const embedInfo = { otp, playbackInfo };
    return (
      <View style={styles.container}>
        <Text style={styles.welcome}>
          Welcome to VdoCipher react-native integration!
        </Text>
        <View style={styles.buttonContainer}>
          <Button
            disabled={!ready}
            title={ready ? "Start video in native fullscreen" : "Loading..."}
            onPress={
              () => startVideoScreen({embedInfo})
            }
          />
        </View>
        <View style={styles.buttonContainer}>
          <Button
            disabled={!ready}
            title={ready ? "Start video with embedded native controls" : "Loading..."}
            onPress={
              () => this.props.navigation.navigate('NativeControls', {embedInfo})
            }
          />
        </View>
        <View style={styles.buttonContainer}>
          <Button
            disabled={!ready}
            title={ready ? "Start video with JS controls" : "Loading..."}
            onPress={
              () => this.props.navigation.navigate('JSControls', {embedInfo})
            }
          />
        </View>
        <View style={styles.buttonContainer}>
          <Button
            title='Downloads'
            onPress={
              () => this.props.navigation.navigate('Downloads')
            }
          />
        </View>
      </View>
    );
  }
}

const RootStack = createStackNavigator(
  {
    Home: {
      screen: HomeScreen
    },
    NativeControls: {
      screen: NativeControlsScreen
    },
    JSControls: {
      screen: JSControlsScreen
    },
    Downloads: {
      screen: DownloadsScreen
    },
  },
  {
     initialRouteName: 'Home',
     headerMode: 'none',
  }
);

export default createAppContainer(RootStack);

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  },
  welcome: {
    fontSize: 20,
    textAlign: 'center',
    margin: 20,
  },
  buttonContainer: {
    margin: 20,
  },
});
