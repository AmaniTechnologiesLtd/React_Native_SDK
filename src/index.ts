import { NativeModules, Platform } from 'react-native';
import type { SDKActivityResult, StartAmaniSDKWithTokenParams } from './types';

const LINKING_ERROR =
  `The package 'RNAmaniSDK' doesn't seem to be linked. Make sure: \n\n` +
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
