import Amani

@objc(RNAmaniSDK)
class RNAmaniSDK: NSObject, AmaniSDKDelegate {
  
  var currentCallback: RCTResponseSenderBlock?
  
  func onKYCSuccess(CustomerId: Int) {
    if let currentCallback = currentCallback {
      currentCallback([
        ["isVerificationCompleted": true]
      ])
    }
  }
  
  func onKYCFailed(CustomerId: Int, Rules: [[String : String]]?) {
    if let currentCallback = currentCallback {
      currentCallback([
        [
          "isVerificationCompleted": false,
          "customerID": CustomerId,
          "rules": Rules as Any,
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
          "problemName": "You're disconnected from the internet"
        ]
      ])
    }
  }
  
  func onEvent(name: String, Parameters: [String]?, type: String) {
//    if let currentCallback = currentCallback {
//      currentCallback([
//        [
//          "nativeEvent": [
//            "name": name,
//            "parameters": Parameters as Any,
//            "type": type,
//          ],
//        ]
//      ])
//    }
  }
  
  let nativeSDK = AmaniSDK.sharedInstance
  
  @objc
  func startAmaniSDKWithToken(_ params: NSDictionary, callback responseFn: @escaping RCTResponseSenderBlock) -> Void {
    let customer = CustomerRequestModel(name: params["name"] as? String ?? "", email: params["email"] as? String ?? "", phone: params["phone"] as? String ?? "", idCardNumber: params["id"] as! String)
    var nvi: NviModel? = nil
    if (params["birthDate"] != nil && params["expireDate"] != nil && params["documentNo"] != nil) {
      nvi = NviModel(documentNo: params["documentNo"] as! String, dateOfBirth: params["birthDate"] as! String, dateOfExpire: params["expireDate"] as! String)
    }
    nativeSDK.setDelegate(delegate: self)
//    nativeSDK.set(token: params["token"] as! String, customer: customer)
    nativeSDK.set(server: params["server"] as! String, token: params["token"] as! String, customer: customer, nvi: nvi, sharedSecret: params["sharedSecret"] as? String ?? nil, useGeoLocation: params["geolocation"] as? Bool ?? false, language: params["lang"] as? String ?? "tr")
    currentCallback = responseFn
   
    if let currentlyPresentedVC = RCTPresentedViewController() {
      DispatchQueue.main.async {
        self.nativeSDK.showSDK(overParent: currentlyPresentedVC)
      }
    }
  }
 
  
  
}
