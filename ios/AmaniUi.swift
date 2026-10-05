import AmaniSDK
import AmaniUI
import React

@objc(AmaniUi)
class AmaniUi: NSObject {
  
  var currentCallback: RCTResponseSenderBlock?
  let nativeSDK = AmaniUI.sharedInstance
  
  @objc
  func startAmaniSDKWithToken(_ params: NSDictionary, callback responseFn: @escaping RCTResponseSenderBlock) {
    // `id` (ID card number) is optional: a flow that already resolved an access token
    // server-side (e.g. a QR/pid exchange) has no ID card number to supply, and the
    // native SDK's `set(...)` takes `customer` as `CustomerRequestModel? = nil`.
    var customer: CustomerRequestModel?
    if let id = params["id"] as? String, !id.isEmpty {
      customer = CustomerRequestModel(name: params["name"] as? String ?? "", email: params["email"] as? String ?? "", phone: params["phone"] as? String ?? "", idCardNumber: id)
    }
    var nvi: NviModel?
    if params["birthDate"] != nil && params["expireDate"] != nil && params["documentNo"] != nil {
      nvi = NviModel(documentNo: params["documentNo"] as! String, dateOfBirth: params["birthDate"] as! String, dateOfExpire: params["expireDate"] as! String)
    }
    nativeSDK.setDelegate(delegate: self)
    
    var apiVersion: AmaniSDK.ApiVersions = .v2
    let apiParam = params["apiVersion"] as? String
    if apiParam != nil && apiParam == "v1" {
      apiVersion = .v1
    }

    // Selects the KYC flow's visual design — a separate axis from `apiVersion` (the
    // backend API version). Defaults to `.v1` to match the native SDK's own default.
    var uiVersion: UIVersion = .v1
    if params["uiVersion"] as? String == "v2" {
      uiVersion = .v2
    }

    nativeSDK.set(
      server: params["server"] as! String,
      token: params["token"] as! String,
      customer: customer,
      language: params["lang"] as? String ?? "tr",
      nviModel: nvi,
      //      location: params["geolocation"] as? Bool ?? false,
      apiVersion: apiVersion,
      uiVersion: uiVersion
    )
    
    nativeSDK.setIdVideoRecord(enable: params["idVideoRecord"] as? Bool ?? false)
    nativeSDK.setIdHologramDetection(enable: params["idHologram"] as? Bool ?? false)
    nativeSDK.setPoseEstimationRecord(enable: params["poseEstimationVideoRecord"] as? Bool ?? false)
    
    currentCallback = responseFn
    
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
