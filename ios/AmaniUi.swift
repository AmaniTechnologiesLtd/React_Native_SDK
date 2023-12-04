import AmaniSDK
import AmaniUI
import React

@objc(AmaniUi)
class AmaniUi: NSObject {
  
  var currentCallback: RCTResponseSenderBlock?
  let nativeSDK = AmaniUI.sharedInstance
  
  @objc
  func startAmaniSDKWithToken(_ params: NSDictionary, callback responseFn: @escaping RCTResponseSenderBlock) {
    let customer = CustomerRequestModel(name: params["name"] as? String ?? "", email: params["email"] as? String ?? "", phone: params["phone"] as? String ?? "", idCardNumber: params["id"] as! String)
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
    
    nativeSDK.set(
      server: params["server"] as! String,
      token: params["token"] as! String,
      customer: customer,
      language: params["lang"] as? String ?? "tr",
      nviModel: nvi,
      //      location: params["geolocation"] as? Bool ?? false,
      apiVersion: apiVersion
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
    
    let errorStrings = Error.map {
      if let message = $0.error_message {
        return $0.error_message
      }
      return nil
    }.filter { $0 != nil}
    
    if let currentCallback = currentCallback {
      currentCallback([[
        "errorType": type,
        "errors": errorStrings as Any
      ]])
    }
  }
  
  func onKYCSuccess(CustomerId: String) {
    if let currentCallback = currentCallback {
      currentCallback([
        [
          "isVerificationCompleted": true,
          "tokenExpired": false,
        ],
      ])
    }
  }
  
  func onKYCFailed(CustomerId: String, Rules: [[String: String]]?) {
    if let currentCallback = currentCallback {
      currentCallback([
        [
          "isVerificationCompleted": false,
          "tokenExpired": false,
          "rules": Rules as Any,
        ],
      ])
    }
  }
  
}
