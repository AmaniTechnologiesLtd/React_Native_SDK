import * as React from 'react';
import { StyleSheet, View, Text, Pressable, NativeModules } from 'react-native';
import { startAmaniSDKWithToken } from 'react-native-amani-ui';

export default function App() {
  React.useEffect(() => {
    console.log(NativeModules);
  }, []);

  const onStartButtonPressed = React.useCallback(() => {
    console.log('start');
    startAmaniSDKWithToken(
      {
        server: 'https://sandbox.amani.ai',
        id: '383',
        token:
          'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoxMiwiZXhwIjoxNzAxNjkxNDkwLCJ1c2VybmFtZSI6Im1vYmlsZV90ZWFtQGFtYW5pLmFpIiwiY3VzdG9tZXJfaWQiOjQ0NjcsImNvbXBhbnlfaWQiOjF9.vShAZX3xsKGUZTHPWQ55qjls_DX7mFF70V8u-b8Gi-I',
        lang: 'tr',
        idHologram: true,
        idVideoRecord: true,
        poseEstimationVideoRecord: true,
        apiVersion: 'v1',
      },
      (data) => {
        console.log(data);
        if (data.apiExceptionCode) {
          // Due to differences in the native SDKs this field always null for iOS
        }
        if (data.isTokenExpired) {
          // CUSTOMER_TOKEN is expired
        }
        if (data.isVerificationCompleted) {
          // User passed all KYC steps, if user used a back button or cancels the progess it will be false.
        }
        if (data.rules) {
          // User canceled the KYC steps by using back button etc. therefore they have missing steps.
          // You can get the list of missing steps here.
        }
      }
    );
  }, []);

  return (
    <View style={styles.container}>
      <Pressable style={styles.button} onPress={onStartButtonPressed}>
        <Text style={styles.text}>Start</Text>
      </Pressable>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  button: {
    paddingHorizontal: 4,
    paddingVertical: 6,
    backgroundColor: 'black',
  },
  text: {
    color: 'white',
  },
});
