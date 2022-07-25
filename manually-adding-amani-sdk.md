# Amani RN SDK manually adding react-native SDK

If you’re using a react-native version that doesn’t support automatic linking or simply you encountered some dependency-related issues you can clone our git repo and add the files manually.

Before doing the steps below, 

## Android Side

- Add the following dependencies to your module build.gradle file.

```groovy
implementation 'ai.amani.android:AmaniAi:1.2.51'
```

- Enable DataBinding and add packaging options as following in the Module build.gradle by adding this line into code block of android {}:

```groovy
packagingOptions {
  pickFirst 'lib/x86/libc++_shared.so'
  pickFirst 'lib/x86_64/libc++_shared.so'
  pickFirst 'lib/armeabi-v7a/libc++_shared.so'
  pickFirst 'lib/arm64-v8a/libc++_shared.so'
}
dataBinding { enabled true }
```

- Add the following in the Project build.gradle within in buildscript within the buildscript->repositories and buildscript->allprojects.

```groovy
    maven { url "https://jfrog.amani.ai/artifactory/amani-sdk"}
    jcenter()
```

### **ProGuard Rule Usage**

- If you are using ProGuard in your application, you just need to add this line into your ProGuard Rules!

```
-keep class com.amani_ml** {*;}
-dontwarn com.amani.ml**
-keep class datamanager.** {*;}
-dontwarn datamanager.**
-keep class networkmanager.** {*;}
-dontwarn networkmanager.**
-keep class com.amani_ai.jniLibrary.CroppedResult { *; }

-keep class org.jmrtd.** { *; }
-keep class net.sf.scuba.** {*;}
-keep class org.bouncycastle.** {*;}
-keep class org.spongycastle.** {*;}
-keep class org.ejbca.** {*;}

-dontwarn org.ejbca.**
-dontwarn org.bouncycastle.**
-dontwarn org.spongycastle.**
-dontwarn org.jmrtd.**
-dontwarn net.sf.scuba.**

-keep class org.tensorflow.lite**{ *; }
-dontwarn org.tensorflow.lite.**
-keep class org.tensorflow.lite.support**{ *; }
-dontwarn org.tensorflow.lite.support**
```

### Merging the contents of the android folder

Copy `RNAmaniSDKListener.java`, `RNAmaniSDKModule.java` and `RNAmaniSDKPackage.java` from our git repo and change the package names on these files to match your package name on android.

## iOS Side

For iOS, you’ll have to do a similar process for adding our SDK.

### Update project podfile

Since our SDK is a dynamic framework you have to update your `Podfile` for dynamic frameworks.

First, you must add `use_frameworks!` directive and give our native platform sdk as a source as below.

```ruby
source "https://github.com/AmaniTechnologiesLtd/Public-IOS-SDK.git"
source "https://github.com/CocoaPods/Specs"
```

After adding the `source` directives, add our native sdk as shown below.

```ruby
pod "Amani"
```

After the last end, you must add our preinstall hook.

```ruby
dynamic_frameworks = ['Amani','IQKeyboardManagerSwift','lottie-ios']
pre_install do |installer|
  installer.pod_targets.each do |pod|
    if !dynamic_frameworks.include?(pod.name)
      puts "Overriding the static_framework? method for #{pod.name}"
      def pod.static_framework?;
        true
      end
      def pod.build_type;
        Pod::BuildType.static_library
      end
    end
  end
end
```

On the post-install hook, modify as shown below.

```ruby
post_install do |installer|
    installer.pods_project.build_configurations.each do |config|
      #if you have intel Mac you need to comment out the following line
        #config.build_settings["EXCLUDED_ARCHS[sdk=iphonesimulator*]"] = "arm64"
        installer.pods_project.targets.each do |target|
          target.build_configurations.each do |config|
            config.build_settings['BUILD_LIBRARY_FOR_DISTRIBUTION'] = 'YES'
            config.build_settings.delete 'IPHONEOS_DEPLOYMENT_TARGET'
          end
        end
      end
    react_native_post_install(installer)
    __apply_Xcode_12_5_M1_post_install_workaround(installer)
  end
```

You might have to delete
```ruby
react_native_post_install(installer)
__apply_Xcode_12_5_M1_post_install_workaround(installer)
```
lines depending on your react-native version.

### Copy the swift source files
If your project doens't have any swift files, you must create a new swift file so XCode creates a bridging header automatically.

For exposing required React libraries to the swift side, add the lines below to your projects bridging header.

```objective-c
#import <React/RCTBridgeModule.h>
#import <React/RCTViewManager.h>
#import <React/RCTUtils.h>
```

After creating the headers, copy `RNAmaniSDK.swift` and `RNAmaniSDK.m` files to your project.
Don't forget to tick `Copy files if needed` button when you drag and drop the files to the xcode.

## Add required usage strings
```xml
<key>com.apple.developer.nfc.readersession.iso7816.select-identifiers</key>
	<array>
		<string>A0000002471001</string>
	</array>
	<key>NFCReaderUsageDescription</key>
	<string>This application requires access to NFC to  scan IDs.</string>
	<key>NSLocationWhenInUseUsageDescription</key>
	<string>This application requires access to your location to upload the document.</string>
	<key>NSLocationUsageDescription</key>
	<string>This application requires access to your location to upload the document.</string>
	<key>NSLocationAlwaysUsageDescription</key>
	<string>This application requires access to your location to upload the document.</string>
	<key>NSLocationAlwaysAndWhenInUseUsageDescription</key>
	<string>This application requires access to your location to upload the document.</string>
	<key>NSCameraUsageDescription</key>
	<string>This application requires access to your camera for scanning and uploading the document.</string>
```
**Note**: All keys will be required for app submission.

## Copying the typescript (or javascript) file.

If you’re using TypeScript (which we strongly suggest) create a new folder on your project and copy the `src/index.ts` file along with `src/types.ts` from our repository. After that, you can start to use our react-native SDK.

## Using with JavaScript

Here’s our `index.ts` file with typescript types removed.

```jsx
import { NativeModules, Platform } from 'react-native';

const LINKING_ERROR =
  `The package 'RNAmaniSDK' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo managed workflow\n';

const RNAmaniSDK = NativeModules.RNAmaniSDK ? NativeModules.RNAmaniSDK: new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

export function startAmaniSDKWithToken(params, callback) {
  if (!params.server) {
    throw new TypeError("'server is missing or null.'")    
  }
  if (!params.token) {
    throw new TypeError("'token' is missing or null.")
  }
  if (!params.id) {
    throw new TypeError("'id' is missing or null.")
  }
  // override api url
  params.server = Platform.OS === 'android' ? `${params.server}/api/v1/` : params.server
  RNAmaniSDK.startAmaniSDKWithToken(params, callback)
}
```

You can start using our SDK by importing the `startAmaniSDKWithToken` method from the file you’ve just created.