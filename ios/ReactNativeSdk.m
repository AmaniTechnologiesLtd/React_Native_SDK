#import <React/RCTBridgeModule.h>

@interface RCT_EXTERN_MODULE(RNAmaniSDK, NSObject)

RCT_EXTERN_METHOD(
                  startAmaniSDKWithToken: (NSDictionary)params
                    callback: (RCTResponseSenderBlock)responseFN
)

+ (BOOL)requiresMainQueueSetup
{
  return YES;
}

@end
