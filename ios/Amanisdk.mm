#import <React/RCTBridgeModule.h>

@interface RCT_EXTERN_MODULE(Amanisdk, NSObject)

RCT_EXTERN_METHOD(multiply:(float)a withB:(float)b
                 withResolver:(RCTPromiseResolveBlock)resolve
                 withRejecter:(RCTPromiseRejectBlock)reject)

RCT_EXTERN_METHOD(
                  startAmaniSDKWithToken: (NSDictionary)params
                  callback: (RCTResponseSenderBlock)responseFN
                  )

+ (BOOL)requiresMainQueueSetup
{
  return YES;
}

@end
