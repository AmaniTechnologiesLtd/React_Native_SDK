"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.multiply = multiply;
exports.startAmaniSDKWithToken = startAmaniSDKWithToken;
var _reactNative = require("react-native");
const LINKING_ERROR = `The package 'react-native-amanisdk' doesn't seem to be linked. Make sure: \n\n` + _reactNative.Platform.select({
  ios: "- You have run 'pod install'\n",
  default: ''
}) + '- You rebuilt the app after installing the package\n' + '- You are not using Expo Go\n';
const Amanisdk = _reactNative.NativeModules.Amanisdk ? _reactNative.NativeModules.Amanisdk : new Proxy({}, {
  get() {
    throw new Error(LINKING_ERROR);
  }
});
function multiply(a, b) {
  return Amanisdk.multiply(a, b);
}
function startAmaniSDKWithToken(params, callback) {
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