import * as React from 'react';
import { useCallback } from 'react';

import { StyleSheet, View, Text, Pressable } from 'react-native';
import { startAmaniSDKWithToken } from 'react-native-amanisdk';

export default function App() {
  const onStartButtonPressed = useCallback(() => {
    startAmaniSDKWithToken(
      {
        server: 'https://dev.amani.ai',
        id: '4564564567',
        token:'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0b2tlbl90eXBlIjoiYWNjZXNzIiwiZXhwIjoxNjkyMTc3MjQyLCJpYXQiOjE2OTE1NzI0NDIsImp0aSI6Ijc3NTRiNjEyZTM3MzRiZGM4OTRmNzljNzIwOWRmYjIwIiwidXNlcl9pZCI6ImNhNjNmN2FiLTI4MDctNDVkZC1iOGMyLWRlZTRlNDZjZTdhYiIsImNvbXBhbnlfaWQiOiI0ODViNjY1ZS04YjA2LTRmN2MtYjVjOS00ZGFhNjkzYjg3OGQiLCJwcm9maWxlX2lkIjoiZjQwOTViMDEtY2FkOC00ZTRkLWE5MTgtMzFlMDRjNTJjZjM0IiwiYXBpX3VzZXIiOmZhbHNlfQ.riQFAx66T_JDrJD1yqfUwW7xR7P5V61D7npkaMePESs',
        lang: 'tr',
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
