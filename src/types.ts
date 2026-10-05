type AllOrNothing<T> = T | Partial<Record<keyof T, undefined>>;

export type StartAmaniSDKWithTokenParams = {
  server: string;
  token: string;
  // ID card number of the customer. Optional: the native SDKs only need it to build an
  // optional customer profile (name/email/phone/idCardNumber) alongside the token; a flow
  // that already resolved an access token server-side (e.g. a QR/pid exchange) has no ID
  // card number to supply and does not need one.
  id?: string;
  geoLocation?: string;
  lang?: string;
  idVideoRecord?: boolean;
  idHologram?: boolean;
  poseEstimationVideoRecord?: boolean;
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

export interface SDKActivityResult extends Record<string, any> {
  isVerificationCompleted?: boolean;
  isTokenExpired?: boolean;
  rules: Record<string, any>;
}
