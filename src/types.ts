type AllOrNothing<T> = T | Partial<Record<keyof T, undefined>>;

// Dynamic Core SDK features that can be selectively enabled on Android (`DynamicFeature` in
// the native Core SDK). Defaults to all features when omitted. iOS has no equivalent concept
// and ignores this field.
export type AmaniDynamicFeature =
  | 'ID_CAPTURE'
  | 'ID_HOLOGRAM_DETECTION'
  | 'SELFIE_POSE_ESTIMATION'
  | 'SELFIE_AUTO'
  | 'NFC_SCAN';

type CommonSDKParams = {
  // Extra security layer forwarded to the native SDK's init call. Optional on both platforms.
  sharedSecret?: string;
  // ISO country code forwarded to the native SDK. iOS only today — Android's native UI SDK
  // has no corresponding parameter.
  country?: string;
  // Device location to attach to every upload. iOS only: the native SDK expects the host app
  // to already have an up-to-date location and forwards exactly what's passed here. Android
  // has no equivalent — its own `geoLocation` field below is a different concept (a flag that
  // tells the native SDK to collect location itself).
  location?: { latitude: number; longitude: number };
  // Whether the native Android SDK should collect geolocation itself. Android only; defaults
  // to true (matching the native default) when omitted. No iOS equivalent — see `location`.
  geoLocation?: boolean;
  lang?: string;
  idVideoRecord?: boolean;
  idHologram?: boolean;
  poseEstimationVideoRecord?: boolean;
  // Android only: caps how long the manual-capture fallback waits before activating, in
  // seconds, after an auto-capture failure. No iOS equivalent.
  idCaptureManualTimeout?: number;
  // iOS only: enables on-device (client-side) MRZ reading. No Android UI equivalent.
  clientSideMrz?: boolean;
  // Android only: Core SDK dynamic features to enable. No iOS equivalent.
  enabledFeatures?: AmaniDynamicFeature[];
  apiVersion?: 'v1' | 'v2';
  // Selects the KYC flow's visual design (home screen layout, step container, button
  // styling, etc.) — a separate axis from `apiVersion` (which selects the backend API
  // version). Defaults to 'v1' on both native SDKs if omitted.
  uiVersion?: 'v1' | 'v2';
} & AllOrNothing<{
  birthDate: string;
  expireDate: string;
  documentNo: string;
}> &
  AllOrNothing<{
    email: string;
    phone: string;
    name: string;
  }>;

export type StartAmaniSDKWithTokenParams = CommonSDKParams & {
  server: string;
  token: string;
  // ID card number of the customer. Optional: the native SDKs only need it to build an
  // optional customer profile (name/email/phone/idCardNumber) alongside the token; a flow
  // that already resolved an access token server-side (e.g. a QR/pid exchange) has no ID
  // card number to supply and does not need one.
  id?: string;
};

// iOS only: the native UI SDK is the only one of the two with a username/password login
// overload — the native Android UI SDK's `goToKycActivity` has no equivalent, so calling this
// on Android throws instead of silently doing nothing.
export type StartAmaniSDKWithCredentialsParams = CommonSDKParams & {
  server: string;
  userName: string;
  password: string;
  // ID card number of the customer. Optional, same as `id` on the token flow.
  id?: string;
};

export interface SDKActivityResult extends Record<string, any> {
  isVerificationCompleted?: boolean;
  isTokenExpired?: boolean;
  // Android only today: the native SDK's raw profile status string (e.g. "APPROVED",
  // "INCOMPLETE"). Null on iOS and on Android if no result was returned at all.
  profileStatus?: string | null;
  // Android only today: the native SDK's raw error code, when the session ended in an error.
  errorCode?: number | null;
  // Android only today: a human-readable message for the error, when available.
  errorMessage?: string | null;
  rules: Record<string, any>;
}
