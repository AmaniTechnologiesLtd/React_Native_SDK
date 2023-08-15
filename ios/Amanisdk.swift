import AmaniSDK
import AmaniUIv1

@objc(Amanisdk)
class Amanisdk: NSObject {

  @objc(multiply:withB:withResolver:withRejecter:)
  func multiply(a: Float, b: Float, resolve:RCTPromiseResolveBlock,reject:RCTPromiseRejectBlock) -> Void {
    resolve(a*b)
  }
  
  var currentCallback: RCTResponseSenderBlock?
  let nativeSDK = AmaniUIv1.sharedInstance
  
  @objc
  func startAmaniSDKWithToken(_ params: NSDictionary, callback responseFn: @escaping RCTResponseSenderBlock) {
    let customer = CustomerRequestModel(name: params["name"] as? String ?? "", email: params["email"] as? String ?? "", phone: params["phone"] as? String ?? "", idCardNumber: params["id"] as! String)
    var nvi: NviModel?
    if params["birthDate"] != nil && params["expireDate"] != nil && params["documentNo"] != nil {
      nvi = NviModel(documentNo: params["documentNo"] as! String, dateOfBirth: params["birthDate"] as! String, dateOfExpire: params["expireDate"] as! String)
    }
    nativeSDK.setDelegate(delegate: self)
    //    nativeSDK.set(server: params["server"] as! String, token: params["token"] as! String, customer: customer, nviData: nvi, sharedSecret: params["sharedSecret"] as? String ?? nil, useGeoLocation: params["geolocation"] as? Bool ?? false, language: params["lang"] as? String ?? "tr")
    
    nativeSDK.set(
      server: params["server"] as! String,
      token: params["token"] as! String,
      customer: customer,
      useGeoLocation: params["geolocation"] as? Bool ?? false,
      language: params["lang"] as? String ?? "tr",
      nviModel: nvi,
      apiVersion: .v2
    )
    
    currentCallback = responseFn
    
    DispatchQueue.main.async {
      if let currentlyPresentedVC = RCTPresentedViewController() {
        self.nativeSDK.showSDK(on: currentlyPresentedVC) { _, _ in
          // no-op
        }
      }
    }
  }
  
  //  @objc
  //  func startAmaniSDKWithCredentials(_ params: NSDictionary, callback responseFn: @escaping RCTResponseSenderBlock) {
  //    let customer = CustomerRequestModel(name: params["name"] as? String ?? "", email: params["email"] as? String ?? "", phone: params["phone"] as? String ?? "", idCardNumber: params["id"] as! String)
  //    var nvi: NviModel?
  //    if params["birthDate"] != nil && params["expireDate"] != nil && params["documentNo"] != nil {
  //      nvi = NviModel(documentNo: params["documentNo"] as! String, dateOfBirth: params["birthDate"] as! String, dateOfExpire: params["expireDate"] as! String)
  //    }
  //    nativeSDK.setDelegate(delegate: self)
  //    nativeSDK.set(server: params["server"] as! String, userName: params["loginEmail"] as! String, password: params["password"] as! String, customer: customer, nvi: nvi, sharedSecret: params["sharedSecret"] as? String ?? nil, useGeoLocation: params["geolocation"] as? Bool ?? false, language: params["lang"] as? String ?? "tr")
  //    currentCallback = responseFn
  //
  //    if let currentlyPresentedVC = RCTPresentedViewController() {
  //      DispatchQueue.main.async {
  //        self.nativeSDK.showSDK(overParent: currentlyPresentedVC)
  //      }
  //    }
  //  }
}

extension Amanisdk: AmaniUIDelegate {
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
