import { NativeModules, Platform } from 'react-native';
import type { SDKActivityResult, StartAmaniSDKWithTokenParams } from './types';

const LINKING_ERROR =
  `The package 'react-native-amani-ui' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

const Amanisdk = NativeModules.AmaniUi
  ? NativeModules.AmaniUi
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

export function startAmaniSDKWithToken(
  params: StartAmaniSDKWithTokenParams,
  callback: (data: SDKActivityResult) => void
) {
  console.log(Amanisdk);
  // check for whatever is not optional.
  if (!params.server) {
    throw new TypeError("'server is missing or null.'");
  }
  if (!params.token) {
    throw new TypeError("'token' is missing or null.");
  }
  if (!params.id) {
    throw new TypeError("'id' is missing or null.");
  }
  // override api url
  Amanisdk.startAmaniSDKWithToken(params, callback);
}
