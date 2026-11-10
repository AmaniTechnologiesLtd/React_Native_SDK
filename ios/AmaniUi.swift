import AmaniSDK
import AmaniUI
import CoreLocation
import React

@objc(AmaniUi)
class AmaniUi: NSObject {

  var currentCallback: RCTResponseSenderBlock?
  let nativeSDK = AmaniUI.sharedInstance

  @objc
  func startAmaniSDKWithToken(_ params: NSDictionary, callback responseFn: @escaping RCTResponseSenderBlock) {
    nativeSDK.setDelegate(delegate: self)

    nativeSDK.set(
      server: params["server"] as! String,
      token: params["token"] as! String,
      sharedSecret: params["sharedSecret"] as? String,
      customer: customer(from: params),
      language: params["lang"] as? String ?? "tr",
      nviModel: nviModel(from: params),
      country: params["country"] as? String,
      location: location(from: params),
      apiVersion: apiVersion(from: params),
      uiVersion: uiVersion(from: params)
    )

    applyCaptureConfig(from: params)
    currentCallback = responseFn
    presentSDK()
  }

  @objc
  func startAmaniSDKWithCredentials(_ params: NSDictionary, callback responseFn: @escaping RCTResponseSenderBlock) {
    nativeSDK.setDelegate(delegate: self)

    // Unlike the token flow, the native username/password `set(...)` overload requires a
    // non-optional `customer`, so this always builds one even if `id` wasn't supplied.
    let customer = CustomerRequestModel(
      name: params["name"] as? String,
      email: params["email"] as? String,
      phone: params["phone"] as? String,
      idCardNumber: params["id"] as? String ?? ""
    )

    nativeSDK.set(
      server: params["server"] as! String,
      userName: params["userName"] as! String,
      password: params["password"] as! String,
      sharedSecret: params["sharedSecret"] as? String,
      customer: customer,
      language: params["lang"] as? String ?? "tr",
      nviModel: nviModel(from: params),
      country: params["country"] as? String,
      location: location(from: params),
      apiVersion: apiVersion(from: params),
      uiVersion: uiVersion(from: params)
    )

    applyCaptureConfig(from: params)
    currentCallback = responseFn
    presentSDK()
  }

  // `id` (ID card number) is optional: a flow that already resolved an access token
  // server-side (e.g. a QR/pid exchange) has no ID card number to supply, and the
  // native SDK's `set(...)` takes `customer` as `CustomerRequestModel? = nil`.
  private func customer(from params: NSDictionary) -> CustomerRequestModel? {
    guard let id = params["id"] as? String, !id.isEmpty else { return nil }
    return CustomerRequestModel(name: params["name"] as? String ?? "", email: params["email"] as? String ?? "", phone: params["phone"] as? String ?? "", idCardNumber: id)
  }

  private func nviModel(from params: NSDictionary) -> NviModel? {
    guard let documentNo = params["documentNo"] as? String,
          let birthDate = params["birthDate"] as? String,
          let expireDate = params["expireDate"] as? String
    else { return nil }
    return NviModel(documentNo: documentNo, dateOfBirth: birthDate, dateOfExpire: expireDate)
  }

  private func location(from params: NSDictionary) -> CLLocation? {
    guard let coords = params["location"] as? NSDictionary,
          let latitude = coords["latitude"] as? Double,
          let longitude = coords["longitude"] as? Double
    else { return nil }
    return CLLocation(latitude: latitude, longitude: longitude)
  }

  private func apiVersion(from params: NSDictionary) -> AmaniSDK.ApiVersions {
    (params["apiVersion"] as? String) == "v1" ? .v1 : .v2
  }

  // Selects the KYC flow's visual design — a separate axis from `apiVersion` (the
  // backend API version). Defaults to `.v1` to match the native SDK's own default.
  private func uiVersion(from params: NSDictionary) -> UIVersion {
    (params["uiVersion"] as? String) == "v2" ? .v2 : .v1
  }

  private func applyCaptureConfig(from params: NSDictionary) {
    nativeSDK.setIdVideoRecord(enable: params["idVideoRecord"] as? Bool ?? false)
    nativeSDK.setIdHologramDetection(enable: params["idHologram"] as? Bool ?? false)
    nativeSDK.setPoseEstimationRecord(enable: params["poseEstimationVideoRecord"] as? Bool ?? false)
    if let clientSideMrz = params["clientSideMrz"] as? Bool {
      nativeSDK.setClientSideMrz(enabled: clientSideMrz)
    }
  }

  private func presentSDK() {
    DispatchQueue.main.async {
      if let currentlyPresentedVC = RCTPresentedViewController() {
        self.nativeSDK.showSDK(on: currentlyPresentedVC) { _, _ in
          // no-op
        }
      }
    }
  }

}

extension AmaniUi: AmaniUIDelegate {
  func onError(type: String, Error: [AmaniSDK.AmaniError]) {
    let errorStrings = Error.compactMap { $0.error_message }
    
    if let callback = currentCallback {
      currentCallback = nil
      callback([[
        "errorType": type,
        "errors": errorStrings
      ]])
    }
  }
  
  func onKYCSuccess(CustomerId: String) {
    if let callback = currentCallback {
      currentCallback = nil
      callback([[
        "isVerificationCompleted": true,
        "tokenExpired": false
      ]])
    }
  }
  
  func onKYCFailed(CustomerId: String, Rules: [[String: String]]?) {
    if let callback = currentCallback {
      currentCallback = nil
      callback([[
        "isVerificationCompleted": false,
        "tokenExpired": false,
        "rules": Rules as Any
      ]])
    }
  }
}
