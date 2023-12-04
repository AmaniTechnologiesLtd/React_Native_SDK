# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /usr/local/Cellar/android-sdk/24.3.3/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:
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