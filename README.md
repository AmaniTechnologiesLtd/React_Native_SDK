# react-native-amani-ui

# Table of Content
- [Overview](#overview)
- [Basics](#basics)
    - [General Requirements](#general-requirements)
    - [Permissions](#permissions)
    - [Integration](#integration)

# Overview

The Amani Software Development kit (SDK) provides you complete steps to perform KYC.This sdk consists of 5 steps:

## 1. Upload Your Identification:  

This internally consist of 4 types of documents, you can upload any of them to get your identification verified.THese documets are
1. Turkish ID Card(New): There you can upload your new turkish ID card.
2. Turkish ID Card(Old): There you can upload your old turkish ID card.
3. Turkish Driver License: There you can upload your old turkish driver license.
4. Passport: You can also upload your passport to get verification of your identity.

## 2. Upload your selfie:

This steps includes the taking a selfie and uploading it.


## 3. Upload Your Proof of Address:

There we have 4 types of categories you can upload any of them to get your address verified.  
1. Proof of Address: you will upload simply proof of address there.  
2. ISKI: you will upload ISKI address proof there.  
3. IGDAS: There you have the option of IGDAS.  
4. CK Bogazici Elektrik: You have to upload the same here.  

## 4. Sign Digital Contract:

In this step, you will enter your information required to make digital contract.Then you will got your contract in the same step from our side.Then by reading that contract, you have to sign that and then at the end upload the same.

## 5. Upload Physical Contract:

In this step, you will download your physical contract. Then you have to upload the same contrat by filling the all the information to get your physical contract verified.

## Congratulation Screen:

After successfully uploading of all the documents you will see a congratulation screen saying you completed all the steps.We will check your documents and
increase your limit in 48 hours.

# Basics

## General Requirements
The minimum requirements for the SDK are:  
* iOS 13.0 and higher  
* react-native 0.64 or later 

### App permissions
#### For İOS Devices
Amani SDK makes use of the device Camera, Location and NFC. If you dont want to use location service please provide in init method. You will be required to have the following keys in your application's Info.plist file:

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

##### Grant accesss to NFC
Enable the Near Field Communication Tag Reading capability in the target Signing & Capabilities. 

## Integration

#### For Android Devices

##### Dependencies:
1. Add the following dependencies to your Module build.gradle file.
```groovy
implementation 'ai.amani.android:AmaniAi:1.2.60'
```
2. Enable DataBinding and add packaging options as following in the Module build.gradle by adding this line into code block of android {}:
   
```groovy
packagingOptions {
  pickFirst 'lib/x86/libc++_shared.so'
  pickFirst 'lib/x86_64/libc++_shared.so'
  pickFirst 'lib/armeabi-v7a/libc++_shared.so'
  pickFirst 'lib/arm64-v8a/libc++_shared.so'
}
dataBinding { enabled true  } 
```
3. Add the following in the Project build.gradle within in buildscript within the buildscript->repositories and buildscript->allprojects.
```groovy
    maven { url "https://jfrog.amani.ai/artifactory/amani-sdk"}
    jcenter()
```

##### ProGuard Rule Usage 

* If you are using ProGuard in your application, you just need to add this line into your ProGuard Rules!
   
```java
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

#### For IOS Devices
Since our SDK is an dynamic framework you have to update your `Podfile` for dynamic frameworks. 

To avoid build issues, set your iOS version from the podfile 

```rb
source "https://github.com/AmaniTechnologiesLtd/Mobile_SDK_Repo”
source "https://github.com/CocoaPods/Specs"
```

You most modify post install block like this.
```rb
 post_install do |installer|
    # Required for amani-sdk
    installer.generated_projects.each do |project|
      project.targets.each do |target|
        xcode_base_version = `xcodebuild -version | grep 'Xcode' | awk '{print $2}' | cut -d . -f 1`
        target.build_configurations.each do |config|
          # This block is for Xcode 15 updates
          if config.base_configuration_reference && Integer(xcode_base_version) >= 15
            xcconfig_path = config.base_configuration_reference.real_path
            xcconfig = File.read(xcconfig_path)
            xcconfig_mod = xcconfig.gsub(/DT_TOOLCHAIN_DIR/, "TOOLCHAIN_DIR")
            File.open(xcconfig_path, "w") { |file| file << xcconfig_mod }
          end
          if target.name == 'AmaniSDK'
              config.build_settings['IPHONEOS_DEPLOYMENT_TARGET'] = '13.0'
          end
          if target.name == 'AmaniUI'
              config.build_settings['IPHONEOS_DEPLOYMENT_TARGET'] = '13.0'
          end
          if target.name == 'lottie-ios'
              config.build_settings['IPHONEOS_DEPLOYMENT_TARGET'] = '13.0'
          end
        end
      end
    end
    # https://github.com/facebook/react-native/blob/main/packages/react-native/scripts/react_native_pods.rb#L197-L202
    react_native_post_install(
      installer,
      config[:reactNativePath],
      :mac_catalyst_enabled => false
    )
    __apply_Xcode_12_5_M1_post_install_workaround(installer)
  end
end
```
# Installation
Installation with yarn
```
  yarn add https://github.com/AmaniTechnologiesLtd/React_Native_SDK#feat/v3
```

## IMPORTANT AFTER INSTALLATION
Since our native SDK is a dynamic framework, if your react native version is
0.70 or higher. You must go to the ios directory and run

```sh
USE_FRAMEWORKS=dynamic NO_FLIPPER=1 pod install
```

otherwise it'll mess up the header search paths of your application and cause
more build issues and headache.

# Usage

Import `startAmaniSDKWithToken` from our package as shown below.
```js
import { startAmaniSDKWithToken } from 'amani-react-native-sdk';
```

`startAmaniSDKWithToken` method takes two parameters, first one is the data;

```typescript
// Special type to check if one key is given the rest is must be given.
type AllOrNothing<T> = T | Partial<Record<keyof T, undefined>>

export type StartAmaniSDKWithTokenParams = {
  server: string;
  id: string;
  token: string;
  geoLocation?: string;
  lang?: string;
} & AllOrNothing<{
  birthDate: string;
  expireDate: string;
  documentNo: string;
}> & AllOrNothing<{
  email: string;
  phone: string;
  name: string;
}>;
```

and the second part is callback that returning from our native sdk.
```typescript
export interface SDKActivityResult extends Record<string, any> {
  isVerificationCompleted?: boolean;
  isTokenExpired?: boolean;
  rules: Record<String, any>;
}
```

It's extended with record for the future updates.

## Example usage
In the example useCallback used for reallocating the function for every render. It memoizes the function so only changes when idNumber and customerToken params changes.

> The ID number must be the same on ID number when the customer is created. Otherwise it'll crash the app.

```typescript
import { useCallback, useState } from "react"
import { startAmaniSDKWithToken } from "amani-react-native-sdk"
// Using useCallback to get a memoized function. This isn't required but recommended.
// See react docs for more information.
const onStartButtonPressed = useCallback();
```

Later in that code...
```jsx
<Pressable onPress={onStartButtonPressed} style={styles.startButton}>
  <Text>Start KYC</Text>
</Pressable>
```

# How to acquire customer token for using this SDK
1- On the server side, you need to log in with your credentials and get a token for the next steps. This token should be used only on server-side requests not used on Web SDK links.
```bash
curl --location --request POST 'https://demo.amani.ai/api/v1/user/login/' \

- -form 'email="user@account.com"' \
- -form 'password="password"'
```
2- Get or Create a customer using the request below. If there is no customer new one is created if there is a customer already created with this ID Card Number it will be returned.

This request will return a customer token that has a short life span and is valid only for this customer. Use this token to initialize this SDK. If the ID card numbers doesn't match with the one that you use creating the customer token, the SDK will surely crash the app.

```
curl --location --request POST 'https://demo.amani.ai/api/v1/customer' \

- -header 'Authorization: TOKEN use_your_admin_token_here' \
- -form 'id_card_number="Customer_ID_Card_Number"'\ (Required)
- -form 'name="Customer Name"' \ (Optional)
- -form 'email="Customer Email"' \ (Optional)
- -form 'phone="Customer Phone"' (Optional)
```
