import React, { useCallback, useState } from 'react'; 
import { StyleSheet, View, Text, TextInput, Pressable, Image, DrawerLayoutAndroid } from 'react-native';
import { startAmaniSDKWithToken } from 'amani-react-native-sdk';

export default function App() {
  const [idNumber, setIdNumber] = useState("")
  const onStartButtonPressed = useCallback(() => {
    startAmaniSDKWithToken({ server: "https://amaniserver.amani.ai", id: idNumber, token: "customerToken", lang: "tr"}, (data) => {
      console.log(data)
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
    })
  }, [idNumber])

  return <View style={styles.container}>
    <Image source={require("./assets/amani_logo.jpeg")} />
    <TextInput placeholder='TCN ID Number' onEndEditing={(e) => setIdNumber(e.nativeEvent.text)} style={[styles.idInput, styles.borders, styles.margin]} />
    <Pressable onPress={onStartButtonPressed} style={[styles.startButton, styles.borders, styles.margin]}>
      <Text style={[styles.textColor]}>START KYC</Text>
    </Pressable>
  </View>
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    padding: 20,
  },
  idInput: {
    width: '100%',
    height: 40,
    paddingHorizontal: 8,
  },
  startButton: {
    width: '100%',
    height: 40,
    justifyContent: 'center',
    alignItems: 'center',
  },
  borders: {
    borderWidth: 1,
    borderColor: '#CCC',
    borderRadius: 16,
  },
  textColor: {
    color: 'black',
  },
  margin: {
    marginTop: 16,
  }
});
