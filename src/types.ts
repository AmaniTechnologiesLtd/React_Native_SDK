type AllOrNothing<T> = T | Partial<Record<keyof T, undefined>>;

export type StartAmaniSDKWithTokenParams = {
  server: string;
  id: string;
  token: string;
  geoLocation?: string;
  lang?: string;
  idVideoRecord?: boolean;
  idHologram?: boolean;
  poseEstimationVideoRecord?: boolean;
  apiVersion?: 'v1' | 'v2';
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
