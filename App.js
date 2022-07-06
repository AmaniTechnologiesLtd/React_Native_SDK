import { useCallback, useState } from 'react';
import React from 'react';
import {
  View,
  Pressable,
  TextInput,
  Text,
  Image,
} from 'react-native';

import { NativeModules } from'react-native';
const AmaniSDKModule = NativeModules.AmaniSDKModule;

const App = () => {
  const [idNumber, setIdNumber] = useState("")

  const onStartButtonPressed = useCallback(() => {
    AmaniSDKModule.startAmaniSDKWithToken("server", idNumber, "token", "tr", (callBack) => {
      // 0. element of callBack returns boolean value of tokenExpired 
      if (Object.values(callBack)[1]) AmaniSDKModule.showMessage("Token is expired");
      // 1. element of callBack returns boolean value of verificationCompleted 
      if (Object.values(callBack)[0]) AmaniSDKModule.showMessage("Verification is completed");  
      else CustomModule.showMessage("Verification is NOT completed");  
      // 2. element of callBack returns Integer value of an ApiExcetion if exist.  
      if (Object.values(callBack)[2] != null) AmaniSDKModule.showMessage("Api Exception" + Object.values(callBack)[2]); 
    });
  }, [idNumber])

  return <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center', padding: 20, marginBottom: 16 }}>
    <Image source={require("./amani_logo.jpeg")} style={{ marginBottom: 16 }}/>
    <TextInput placeholder='TCN Number' style={{ borderWidth: 1, borderRadius: 16, paddingHorizontal: 8, borderColor: '#ccc', width: '100%', height: 40}} onEndEditing={(e) => setIdNumber(e.nativeEvent.text)} />
    <Pressable onPress={onStartButtonPressed} style={{ height: 40, marginTop: 16, width: '100%', justifyContent: 'center', borderWidth: 1, borderColor: '#ccc', alignItems: 'center', borderRadius: 16, }}>
      <Text style={{ color: 'black' }}>START KYC</Text>
    </Pressable>
  </View>

};


export default App;
