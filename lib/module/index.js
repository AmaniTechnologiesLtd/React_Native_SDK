import { NativeModules, Platform } from 'react-native';
const LINKING_ERROR = `The package 'react-native-amanisdk' doesn't seem to be linked. Make sure: \n\n` + Platform.select({
  ios: "- You have run 'pod install'\n",
  default: ''
}) + '- You rebuilt the app after installing the package\n' + '- You are not using Expo Go\n';
const Amanisdk = NativeModules.Amanisdk ? NativeModules.Amanisdk : new Proxy({}, {
  get() {
    throw new Error(LINKING_ERROR);
  }
});
export function multiply(a, b) {
  return Amanisdk.multiply(a, b);
}
export function startAmaniSDKWithToken(params, callback) {
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
//# sourceMappingURL=index.js.map