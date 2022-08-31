import Amani

@objc(RNAmaniSDK)
class RNAmaniSDK: NSObject {
  
  var currentCallback: RCTResponseSenderBlock?
  let nativeSDK = AmaniSDK.sharedInstance
  
  @objc
  func startAmaniSDKWithToken(_ params: NSDictionary, callback responseFn: @escaping RCTResponseSenderBlock) -> Void {
    let customer = CustomerRequestModel(name: params["name"] as? String ?? "", email: params["email"] as? String ?? "", phone: params["phone"] as? String ?? "", idCardNumber: params["id"] as! String)
    var nvi: NviModel? = nil
    if (params["birthDate"] != nil && params["expireDate"] != nil && params["documentNo"] != nil) {
      nvi = NviModel(documentNo: params["documentNo"] as! String, dateOfBirth: params["birthDate"] as! String, dateOfExpire: params["expireDate"] as! String)
    }
    nativeSDK.setDelegate(delegate: self)
    nativeSDK.set(server: params["server"] as! String, token: params["token"] as! String, customer: customer, nvi: nvi, sharedSecret: params["sharedSecret"] as? String ?? nil, useGeoLocation: params["geolocation"] as? Bool ?? false, language: params["lang"] as? String ?? "tr")
    currentCallback = responseFn
   
    if let currentlyPresentedVC = RCTPresentedViewController() {
      DispatchQueue.main.async {
        self.nativeSDK.showSDK(overParent: currentlyPresentedVC)
      }
    }
  }
  
  @objc
  func startAmaniSDKWithCredentials(_ params: NSDictionary, callback responseFn: @escaping RCTResponseSenderBlock) -> Void {
    let customer = CustomerRequestModel(name: params["name"] as? String ?? "", email: params["email"] as? String ?? "", phone: params["phone"] as? String ?? "", idCardNumber: params["id"] as! String)
    var nvi: NviModel? = nil
    if (params["birthDate"] != nil && params["expireDate"] != nil && params["documentNo"] != nil) {
      nvi = NviModel(documentNo: params["documentNo"] as! String, dateOfBirth: params["birthDate"] as! String, dateOfExpire: params["expireDate"] as! String)
    }
    nativeSDK.setDelegate(delegate: self)
    nativeSDK.set(server: params["server"] as! String, userName: params["loginEmail"] as! String, password: params["password"] as! String, customer: customer, nvi: nvi, sharedSecret: params["sharedSecret"] as? String ?? nil, useGeoLocation: params["geolocation"] as? Bool ?? false, language: params["lang"] as? String ?? "tr")
    currentCallback = responseFn
    
   
    if let currentlyPresentedVC = RCTPresentedViewController() {
      DispatchQueue.main.async {
        self.nativeSDK.showSDK(overParent: currentlyPresentedVC)
      }
    }
  }
 
}

extension RNAmaniSDK: AmaniSDKDelegate {
  
   func onKYCSuccess(CustomerId: Int) {
    if let currentCallback = currentCallback {
      currentCallback([
        [
          "isVerificationCompleted": true,
          "tokenExpired": false,
        ]
      ])
    }
  }
  
  func onKYCFailed(CustomerId: Int, Rules: [[String : String]]?) {
    if let currentCallback = currentCallback {
      currentCallback([
        [
          "isVerificationCompleted": false,
          "tokenExpired": false,
          "rules": Rules as Any
        ]
      ])
    }
  }
  
  func onTokenExpired() {
    if let currentCallback = currentCallback {
      currentCallback([
        [
          "isVerificationCompleted": false,
          "tokenExpired": true
        ]
      ])
    }
  }
  
  func onNoInternetConnection() {
    if let currentCallback = currentCallback {
      currentCallback([
        [
          "isVerificationCompleted": false,
          "tokenExpired": false,
        ]
      ])
    }
  }
 
  
  func onEvent(name: String, Parameters: [String]?, type: String) {
  }
  
}
