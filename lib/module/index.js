import { NativeModules, Platform } from 'react-native';
const LINKING_ERROR =
  `The package 'react-native-amani-ui' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({
    ios: "- You have run 'pod install'\n",
    default: '',
  }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

// Native module tanımı
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

// --------------------
// 🔹 Type Definitions
// --------------------

// --------------------
// 🔹 API Export
// --------------------

/**
 * Amani SDK'yı verilen parametrelerle başlatır.
 *
 * @param params SDK konfigürasyonu
 * @param callback SDK tamamlandığında dönecek sonuç
 */
export function startAmaniSDKWithToken(params, callback) {
  console.log('[AmaniUI] Starting SDK with params:', params);

  // Temel zorunlu alanlar
  if (!params.server) {
    throw new TypeError("'server' is missing or null.");
  }
  if (!params.token) {
    throw new TypeError("'token' is missing or null.");
  }
  if (!params.id) {
    throw new TypeError("'id' is missing or null.");
  }

  // enabledFeatures eğer undefined ise, native taraf boş değer alır
  // Android tarafında bu durumda tüm özellikler aktif olur.
  Amanisdk.startAmaniSDKWithToken(params, callback);
}

// --------------------
// 🔹 Kullanım Örnekleri
// --------------------

/*
// Tüm feature'lar aktif (varsayılan)
startAmaniSDKWithToken({
  server: 'https://your-server.amani.ai',
  token: 'TOKEN',
  id: 'USER_ID'
}, cb);

// Sadece ID_CAPTURE aktif
startAmaniSDKWithToken({
  server: 'https://your-server.amani.ai',
  token: 'TOKEN',
  id: 'USER_ID',
  enabledFeatures: ['ID_CAPTURE']
}, cb);

// ID + SELFIE aktif
startAmaniSDKWithToken({
  server: 'https://your-server.amani.ai',
  token: 'TOKEN',
  id: 'USER_ID',
  enabledFeatures: ['ID_CAPTURE', 'SELFIE_AUTO']
}, cb);
*/
//# sourceMappingURL=index.js.map
