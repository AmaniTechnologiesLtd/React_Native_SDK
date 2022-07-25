type AllOrNothing<T> = T | Partial<Record<keyof T, undefined>>

export type StartAmaniSDKWithTokenParams = {
  server: string;
  id: string;
  token: string;
  geoLocation?: string;
  lang?: string;
} & AllOrNothing<{
  birthDate: string;
  expireDate: string;
  documentNo: string;
}> & AllOrNothing<{
  email: string;
  phone: string;
  name: string;
}>;

export type StartAmaniSDKWithCredentialParams = {
  server: string;
  id: string;
  loginEmail: string;
  password: string;
  geoLocation?: string;
  lang?: string;
} & AllOrNothing<{
  birthDate: string;
  expireDate: string;
  documentNo: string;
}> & AllOrNothing<{
  email: string;
  phone: string;
  name: string;
}>;

export interface SDKActivityResult extends Record<string, any> {
  isVerificationCompleted?: boolean;
  isTokenExpired?: boolean;
  apiExceptionCode?: number;
}