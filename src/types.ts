export interface StartAmaniSDKWithTokenParams {
  server: string;
  id: string;
  token: string;
  birthDate?: string;
  expireDate?: string;
  documentNo?: string;
  geoLocation?: string;
  lang?: string;
  email?: string;
  phone?: string;
  name?: string;
}

export interface SDKActivityResult extends Record<string, any> {
  isVerificationCompleted?: boolean;
  isTokenExpired?: boolean;
  apiExceptionCode?: number;
}