import React, { useCallback, useState } from 'react'; 
import { StyleSheet, View, Text, TextInput, Pressable, Image } from 'react-native';
import { startAmaniSDKWithToken } from 'amani-react-native-sdk';

export default function App() {
  const [idNumber, setIdNumber] = useState("")
  const onStartButtonPressed = useCallback(() => {
    startAmaniSDKWithToken({ server: "server", id: idNumber, token: "token", lang: "tr"}, (data) => {
      console.log(data);
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
