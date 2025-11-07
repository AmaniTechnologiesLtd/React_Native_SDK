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
  /**
   * Yeni: SDK'da aktif edilecek özellikleri tanımlar.
   * Örnek: ["ID_CAPTURE", "SELFIE_AUTO"]
   */
  enabledFeatures?: string[];
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
/**
 * Amani SDK'yı verilen parametrelerle başlatır.
 *
 * @param params SDK konfigürasyonu
 * @param callback SDK tamamlandığında dönecek sonuç
 */
export declare function startAmaniSDKWithToken(
  params: StartAmaniSDKWithTokenParams,
  callback: (data: SDKActivityResult) => void
): void;
export {};
//# sourceMappingURL=index.d.ts.map
