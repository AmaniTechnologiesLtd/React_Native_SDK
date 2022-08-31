import { NativeModules, Platform } from 'react-native';
import type { SDKActivityResult, StartAmaniSDKWithCredentialParams, StartAmaniSDKWithTokenParams } from './types';

const LINKING_ERROR =
  `The package 'amani-react-native-sdk' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo managed workflow\n';

const RNAmaniSDK = NativeModules.RNAmaniSDK ? NativeModules.RNAmaniSDK: new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

export function startAmaniSDKWithToken(params: StartAmaniSDKWithTokenParams, callback: (data: SDKActivityResult) => void) {
  // check for whatever is not optional.
  if (!params.server) {
    throw new TypeError("'server is missing or null.'")    
  }
  if (!params.token) {
    throw new TypeError("'token' is missing or null.")
  }
  if (!params.id) {
    throw new TypeError("'id' is missing or null.")
  }
  // override api url
  params.server = Platform.OS === 'android' ? `${params.server}/api/v1/` : params.server
  RNAmaniSDK.startAmaniSDKWithToken(params, callback)
}

export function startAmaniSDKWithCredentials(params: StartAmaniSDKWithCredentialParams, callback: (data: SDKActivityResult) => void) {
  if (!__DEV__) {
    throw new Error("You can't use `startAmaniSDKWithCredentials` function on production version of your app.")
  }
  // check for whatever is not optional.
  if (!params.server) {
    throw new TypeError("'server is missing or null.'")    
  }
  if (!params.loginEmail) {
    throw new TypeError("'loginEmail' is missing or null.")
  }
  if (!params.password) {
    throw new TypeError("'password' is missing or null.")
  }
  if (!params.id) {
    throw new TypeError("'id' is missing or null.")
  }
  // override api url
  params.server = Platform.OS === 'android' ? `${params.server}/api/v1/` : params.server
  RNAmaniSDK.startAmaniSDKWithCredentials(params, callback)
}


