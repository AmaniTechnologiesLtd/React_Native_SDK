package com.amaniui

import ai.amani.base.utility.AmaniVersion
import ai.amani.sdk.DynamicFeature
import ai.amani.sdk.extentions.parcelable
import ai.amani.sdk.model.KYCResult
import ai.amani.sdk.model.UIStyle
import ai.amani.sdk.utils.AppConstant
import ai.amani.sdk.utils.ProfileStatus
import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Callback
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.ReadableMap

class AmaniUiModule(reactContext: ReactApplicationContext) :
  ReactContextBaseJavaModule(reactContext) {
  private var callback: Callback? = null
  override fun getName(): String {
    return NAME
  }

  companion object {
    const val NAME = "AmaniUi"
  }

  private fun enabledFeatures(args: ReadableMap): List<DynamicFeature> {
    if (!args.hasKey("enabledFeatures")) return DynamicFeature.allFeatures
    val array = args.getArray("enabledFeatures") ?: return DynamicFeature.allFeatures
    val features = mutableListOf<DynamicFeature>()
    for (i in 0 until array.size()) {
      runCatching { DynamicFeature.valueOf(array.getString(i)) }.getOrNull()?.let { features.add(it) }
    }
    return features
  }

  private fun initSDK(args: ReadableMap, callback: Callback, launcher: ActivityResultLauncher<Intent>) {
    val activity = getCurrentActivity() as AppCompatActivity
    val birthDate: String? = if (args.hasKey("birthDate")) args.getString("birthDate") else null
    val expireDate: String? = if (args.hasKey("expireDate")) args.getString("expireDate") else null
    val documentNo: String? = if (args.hasKey("documentNo")) args.getString("documentNo") else null
    val lang: String = if (args.hasKey("lang")) (args.getString("lang") ?: "tr") else "tr"
    val email: String? = if (args.hasKey("email")) args.getString("email") else null
    val phone: String? = if (args.hasKey("phone")) args.getString("phone") else null
    val name: String? = if (args.hasKey("name")) args.getString("name") else null
    val geoLocation: Boolean = if (args.hasKey("geoLocation")) args.getBoolean("geoLocation") else true
    val sharedSecret: String? = if (args.hasKey("sharedSecret")) args.getString("sharedSecret") else null

    var amaniVersion = AmaniVersion.V2
    if (args.hasKey("apiVersion") && args.getString("apiVersion") == "v1") {
      amaniVersion = AmaniVersion.V1
    }

    // `id` (ID card number) is optional at the JS level: a flow that already resolved an
    // access token server-side (e.g. a QR/pid exchange) has no ID card number to supply.
    // Unlike iOS (where `customer` is genuinely `CustomerRequestModel? = nil`-able),
    // AmaniSDKUI.goToKycActivity's idNumber parameter is a non-null String with no
    // default, so an absent `id` falls back to an empty string rather than null.
    val idNumber: String = (if (args.hasKey("id")) args.getString("id") else null) ?: ""

    AmaniSDKUI.configure(
      applicationContext = activity,
      serverURL = args.getString("server")!!,
      amaniVersion = amaniVersion,
      sharedSecret = sharedSecret,
      enabledFeatures = enabledFeatures(args)
    )

    // Selects the KYC flow's visual design — a separate axis from `amaniVersion` (the
    // backend API version). Defaults to V1 to match the native SDK's own default; this
    // must be set before goToKycActivity, which has no uiStyle parameter of its own.
    val uiStyle = if (args.hasKey("uiVersion") && args.getString("uiVersion") == "v2") {
      UIStyle.V2
    } else {
      UIStyle.V1
    }
    AmaniSDKUI.setUIStyle(uiStyle)

    // Each setter already no-ops on a null value, so these are safe to call unconditionally
    // with whatever JS passed (or didn't).
    AmaniSDKUI.setIdCaptureVideoRecord(if (args.hasKey("idVideoRecord")) args.getBoolean("idVideoRecord") else null)
    AmaniSDKUI.setHologramDetection(if (args.hasKey("idHologram")) args.getBoolean("idHologram") else null)
    AmaniSDKUI.setSelfieCaptureVideoRecord(if (args.hasKey("poseEstimationVideoRecord")) args.getBoolean("poseEstimationVideoRecord") else null)
    if (args.hasKey("idCaptureManualTimeout")) {
      AmaniSDKUI.setIdCaptureManualTimeOut(args.getInt("idCaptureManualTimeout"))
    }

    this.callback = callback
    AmaniSDKUI.goToKycActivity(
      activity = activity,
      resultLauncher = launcher,
      idNumber = idNumber,
      authToken = args.getString("token")!!,
      language = lang,
      geoLocation = geoLocation,
      birthDate = birthDate,
      expireDate = expireDate,
      documentNumber = documentNo,
      userEmail = email,
      userPhoneNumber = phone,
      userFullName = name,
    )
  }

  @ReactMethod
  fun startAmaniSDKWithToken(args: ReadableMap, callback: Callback) {

    LifeCycleEventListener.addLifeCycleListener(object : LifeCycle {
      override fun onCreate(launcher: ActivityResultLauncher<Intent>) {
        initSDK(args = args, callback = callback, launcher = launcher)
      }

      override fun activityResult(activityResult: ActivityResult) {
        if (activityResult.resultCode == Activity.RESULT_OK) {
          val kycResult: KYCResult? = activityResult.data?.parcelable(AppConstant.KYC_RESULT)
          val resultMap = Arguments.createMap()
          if (kycResult != null) {
            resultMap.putBoolean(
              "isVerificationCompleted",
              kycResult.profileStatus == ProfileStatus.APPROVED
            )
            resultMap.putBoolean("isTokenExpired", kycResult.errorCode == 403)
            resultMap.putString("profileStatus", kycResult.profileStatus)
            val errorCode = kycResult.errorCode
            if (errorCode != null) resultMap.putInt("errorCode", errorCode) else resultMap.putNull("errorCode")
            val errorMessage = kycResult.generalException?.message
            if (errorMessage != null) resultMap.putString("errorMessage", errorMessage) else resultMap.putNull("errorMessage")
          } else {
            resultMap.putBoolean("isVerificationCompleted", false)
            resultMap.putBoolean("isTokenExpired", false)
            resultMap.putNull("profileStatus")
            resultMap.putNull("errorCode")
            resultMap.putString("errorMessage", "No KYC result returned by the native SDK")
          }
          callback(resultMap)
        }
      }
    })

    val intent = Intent(reactApplicationContext, AmaniActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    reactApplicationContext.startActivity(intent)
  }
}
