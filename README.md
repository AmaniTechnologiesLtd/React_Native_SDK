# Amani React Native SDK Documentation
This documentation shows how to bridge our native SDK’s to react native.
# Setting up & Installation

To get you started, we’ll need some changes on the default react native boilerplate in the Android folder.

Don't replace the blocks below, merge them with what you have

```bash
android {
		// For SDK versions 1.2.40 and later you must add this block.
    packagingOptions {
        pickFirst 'lib/x86/libc++_shared.so'
        pickFirst 'lib/x86_64/libc++_shared.so'
        pickFirst 'lib/armeabi-v7a/libc++_shared.so'
        pickFirst 'lib/arm64-v8a/libc++_shared.so'
    }

		dataBinding { enabled true  }
}
```

```bash
dependencies {
// ...
		implementation 'ai.amani.android:AmaniAi:1.2.41'
// ...
}
```

On your main `build.gradle` under the `android` folder add the following code to download our native SDK. 

```jsx
maven { url "https://jfrog.amani.ai/artifactory/amani-sdk" }
jcenter()
```

After adding the required sections copy AmaniSDKListener.java, CustomAmaniModule.java, and CustomAmaniPackage.java files to main/java/com/yourpackagename folder. Right after copying the implementation files, you must call them in your MainActivity.java and MainApplication.java as shown below.

As Java imports by packages, you must change the package name on the first lines of these three files to your applications package name. That information can be found from the first line of your `MainActivity.java` or `MainApplication.java` files.

```java
import com.amani_ai.base.Utiltiy.AppConstants;
import com.amani_ai.base.callback.EventFire;
import com.amani_ai.base.callback.EventName;
import com.amani_ai.base.callback.EventType;
import com.facebook.react.ReactActivity;

import java.util.Objects;
import android.content.Intent;
```

```java
@Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (AmaniSDKListener.listener != null) {
      boolean verificationCompleted = Objects.requireNonNull(data).getBooleanExtra(AppConstants.ON_SUCCESS,false);
      boolean tokenExpired = Objects.requireNonNull(data).getBooleanExtra(AppConstants.TOKEN_EXPIRED,false);
      Integer apiException = Objects.requireNonNull(data).getIntExtra(AppConstants.ON_API_EXCEPTION,1000);
      AmaniSDKListener.listener.onEvent(verificationCompleted, tokenExpired, apiException);
    }
  }
```

```java
private final ReactNativeHost mReactNativeHost =
      new ReactNativeHost(this) {
        @Override
        public boolean getUseDeveloperSupport() {
          return BuildConfig.DEBUG;
        }

        @Override
        protected List<ReactPackage> getPackages() {
          @SuppressWarnings("UnnecessaryLocalVariable")
          List<ReactPackage> packages = new PackageList(this).getPackages();
					// Add this line
          packages.add(new CustomAmaniPackage()); // Adding Custom Amani Package here
          return packages;
        }

        @Override
        protected String getJSMainModuleName() {
          return "index";
        }
      };
```

Lastly, you have to remove this piece of code if it exists on your `MainActivity.java` file.

```java
**/**
   * Returns the instance of the {@link ReactActivityDelegate}. There the RootView is created and
   * you can specify the renderer you wish to use - the new renderer (Fabric) or the old renderer
   * (Paper).
   */
  @Override
  protected ReactActivityDelegate createReactActivityDelegate() {
    return new MainActivityDelegate(this, getMainComponentName());
  }**
```

Now you’re ready to use our SDK with React Native on Android. Congratulations!

# Usage

Import AmaniSDKModule as shown below, this way you can start to use our SDK.

```jsx
import { NativeModules } from'react-native';
const AmaniSDKModule = NativeModules.AmaniSDKModule;
```

You can start the SDK as shown below.

### How to acquire token

On the server-side, you need to log in with your credentials and get a token for the next steps. This token should be used only on server-side requests not used on SDK itself.

```bash
curl --location --request POST 'https://tr.amani.ai/api/v1/user/login/' \
--form 'email="user@account.com"' \
--form 'password="password"'

```

Get or Create a customer using the request below. If there is no customer new one is created if there is a customer already created with this ID Card Number it will be returned.

This request will return a customer token that has a short life span and is valid only for this customer. Use this token to call `AmaniSDKModule.startAmaniSDKWithToken` function.

```bash
curl --location --request POST 'https://tr.amani.ai/api/v1/customer' \
--header 'Authorization: TOKEN use_your_token_here' \
--form 'id_card_number="Customer_ID_Card_Number"'\ (Required)
--form 'name="Customer Name"' \ (Optional)
--form 'email="Customer Email"' \ (Optional)
--form 'phone="Customer Phone"' (Optional)
****
```

```jsx
AmaniSDKModule.startAmaniSDKWithToken("server", "TCN ID", "token", "tr", (callBack)=>{

    // 0. element of callBack returns boolean value of tokenExpired 
    if (Object.values(callBack)[1]) AmaniSDKModule.showMessage("Token is expired");

    // 1. element of callBack returns boolean value of verificationCompleted 
    if (Object.values(callBack)[0]) AmaniSDKModule.showMessage("Verification is completed");  
    else CustomModule.showMessage("Verification is NOT completed");  
    
    // 2. element of callBack returns Integer value of an ApiExcetion if exist.  
    if (Object.values(callBack)[2] != null) AmaniSDKModule.showMessage("Api Exception" + Object.values(callBack)[2]); 
    
  });
```

| Param Number | Param Name | Param Type |
| --- | --- | --- |
| 1 | server | string |
| 2 | TCN ID | string |
| 3 | token | string |
| 6 | lang | string |
| 7 | callback function | (callbackData) ⇒ void |