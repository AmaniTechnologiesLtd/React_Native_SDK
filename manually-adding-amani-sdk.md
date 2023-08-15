# Amani RN SDK manually adding react-native SDK

If you’re using a react-native version that doesn’t support automatic linking or simply you encountered some dependency-related issues you can clone our git repo and add the files manually.

## Android Side

- Add the following dependencies to your module build.gradle file.

```groovy
implementation 'ai.amani:Android.SDK.UI:1.0.0beta06'
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
  maven { url "https://jfrog.amani.ai/artifactory/amani-sdk" }
  maven { url 'https://www.jitpack.io' }
  jcenter()
```

- We require the compile version to be 33. Set it on the project's android section.

```groovy
buildscript {
    ext {
        buildToolsVersion = "31.0.0"
        minSdkVersion = 21
        compileSdkVersion = 33 // This should be 33
        targetSdkVersion = 31
    }
}
```

- You must use `tools:replace` for `android:label` and `android:name` parameters in the AndroidManifest.xml

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
  xmlns:tools="http://schemas.android.com/tools" # this line must be added
  package="com.amanisdkexample">

    <uses-permission android:name="android.permission.INTERNET" />

    <application
      android:name=".MainApplication"
      android:label="@string/app_name"
      tools:replace="android:label, android:name" # this line must be added
      android:icon="@mipmap/ic_launcher"
      android:roundIcon="@mipmap/ic_launcher_round"
      android:allowBackup="false"
      android:theme="@style/AppTheme">
```

You also need to create the activity section in the `AndroidManifest.xml` file as shown below

```xml
<application>
  <activity
      android:name=".AmaniActivity"
      android:screenOrientation="portrait"
      android:exported="true">
      <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.DEFAULT" />
      </intent-filter>
    </activity>
</application>
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

Copy `AmaniSDKModule.kt`, `AmaniSDKPackage.kt` and `LifeCycleEventListener.kt` and `ReactApp.kt` from our git repo and change the package names on these files to match your package name on android.

#### Enable Kotlin support for your project
Open the android folder in android studio right click on the java folder, create a new kotlin file. After that the android studio should ask if you want to enable kotlin for this project.

### Adding the RNAmaniSDKPackage to react-native
To use the module on android you must register the package to the react native host on the java side. You must update `MainApplication.java` file as shown below.

```java
   private final ReactNativeHost mReactNativeHost = new ReactNativeHost(this) {
    @Override
    public boolean getUseDeveloperSupport() {
      return BuildConfig.DEBUG;
    }

    @Override
    protected List<ReactPackage> getPackages() {
      @SuppressWarnings("UnnecessaryLocalVariable")
      List<ReactPackage> packages = new PackageList(this).getPackages();
      // Packages that cannot be autolinked yet can be added manually here, for example:
      // YOU MUST ADD THE LINE BELOW
      packages.add(new AmanisdkPackage());
      return packages;
    }

    @Override
    protected String getJSMainModuleName() {
      return "index";
    }
  };
```

## iOS Side

For iOS, you’ll have to do a similar process for adding our SDK.

### Update project podfile

Since our SDK is a dynamic framework you have to update your `Podfile` for dynamic frameworks.

First, you must add `use_frameworks!` directive and give our native platform sdk as a source as below.

```ruby
source "https://github.com/AmaniTechnologiesLtd/Mobile_SDK_Repo"
source "https://github.com/CocoaPods/Specs"
```

After adding the `source` directives, add our native sdk as shown below.

```ruby
pod "AmaniSDK", "~> 3.0.1"
pod "AmaniUIv1"
```

After the last end, you must add our preinstall hook.

```ruby
dynamic_frameworks = ['AmaniSDK', 'AmaniUIv1', 'OpenSSL-Universal', 'lottie-ios']
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
            config.build_settings['IPHONEOS_DEPLOYMENT_TARGET'] = '13.0'
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
lines depending on your react-native version if it doesn't exists by default.

### Copy the swift source files
If your project doens't have any swift files, you must create a new swift file so XCode creates a bridging header automatically.

For exposing required React libraries to the swift side, add the lines below to your projects bridging header.

```objective-c
#import <React/RCTBridgeModule.h>
#import <React/RCTViewManager.h>
#import <React/RCTUtils.h>
```

After creating the headers, copy `Amanisdk.swift` and `Amanisdk.mm` files to your project.
Don't forget to tick `Copy files if needed` button when you drag and drop the files to the xcode to for adding the file to the git properly.

## Add required usage strings and permissions
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

### Using with JavaScript

Here’s our `index.ts` file with typescript types removed.

```jsx
import { NativeModules, Platform } from 'react-native';

const LINKING_ERROR =
  `The package 'react-native-amanisdk' doesn't seem to be linked. Make sure: \n\n` +
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
  RNAmaniSDK.startAmaniSDKWithToken(params, callback)
}
```

You can start using our SDK by importing the `startAmaniSDKWithToken` method from the file you’ve just created.
