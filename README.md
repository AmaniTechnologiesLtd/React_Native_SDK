# Amani React Native SDK #

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
* iOS 11.0 and higher  


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
implementation 'ai.amani.android:AmaniAi:1.2.51'
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

First you must add `use_frameworks!` directive and our native platform sdk as a pod.

```rb
use_frameworks!
pod 'Amani', :git => 'https://github.com/AmaniTechnologiesLtd/Public-IOS-SDK.git'
```
After the last end, you must add our preinstall hook.
```rb
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

On post install hook, modify as shown below.

```rb
post_install do |installer|
    installer.pods_project.build_configurations.each do |config|
      #if you have intel Mac you need to comment out following line 
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

# Installation
Installation with yarn
```
  yarn add https://github.com/AmaniTechnologiesLtd/React_Native_SDK
```

# Usage
`startAmaniSDKWithToken` method takes two parameters, first one is the data;

```typescript
export interface StartAmaniSDKWithTokenParams {
  server: string;
  id: string;
  token: string;
  geoLocation?: boolean;
  lang?: string;
  sharedSecter?: string;
  // nvi data
  birthDate?: string;
  expireDate?: string;
  documentNo?: string;

  email?: string;
  phone?: string;
  name?: string;
}
```

and the second part is callback that returning from our native sdk.
```typescript
export interface SDKActivityResult extends Record<string, any> {
  isVerificationCompleted?: boolean;
  isTokenExpired?: boolean;
  apiExceptionCode?: number;
}
```

It's extended with record for the future updates.

