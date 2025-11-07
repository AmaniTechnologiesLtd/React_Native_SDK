package com.amaniui

import ai.amani.base.utility.AmaniVersion
import ai.amani.sdk.extentions.parcelable
import ai.amani.sdk.model.DynamicFeature
import ai.amani.sdk.model.KYCResult
import ai.amani.sdk.utils.AppConstant
import ai.amani.sdk.utils.ProfileStatus
import ai.amani.sdk.ui.AmaniSDKUI
import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
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
  private var launcher: ActivityResultLauncher<Intent>? = null

  override fun getName(): String {
    return NAME
  }

  companion object {
    const val NAME = "AmaniUi"
  }

  // ✅ Helper: Dynamic feature isimlerini listeye çevirir
  private fun parseEnabledFeatures(args: ReadableMap): List<DynamicFeature> {
    val defaultFeatures = listOf(
      DynamicFeature.ID_CAPTURE,
      DynamicFeature.ID_HOLOGRAM_DETECTION,
      DynamicFeature.NFC_SCAN,
      DynamicFeature.SELFIE_AUTO,
      DynamicFeature.SELFIE_POSE_ESTIMATION
    )

    if (!args.hasKey("enabledFeatures")) return defaultFeatures

    val featuresArray = args.getArray("enabledFeatures") ?: return defaultFeatures
    val result = mutableListOf<DynamicFeature>()

    for (i in 0 until featuresArray.size()) {
      when (featuresArray.getString(i)?.uppercase()) {
        "ID_CAPTURE" -> result.add(DynamicFeature.ID_CAPTURE)
        "ID_HOLOGRAM_DETECTION" -> result.add(DynamicFeature.ID_HOLOGRAM_DETECTION)
        "NFC_SCAN" -> result.add(DynamicFeature.NFC_SCAN)
        "SELFIE_AUTO" -> result.add(DynamicFeature.SELFIE_AUTO)
        "SELFIE_POSE_ESTIMATION" -> result.add(DynamicFeature.SELFIE_POSE_ESTIMATION)
      }
    }

    return if (result.isNotEmpty()) result else defaultFeatures
  }

  private fun initSDK(args: ReadableMap, callback: Callback, launcher: ActivityResultLauncher<Intent>) {
    val activity = currentActivity as? AppCompatActivity ?: return

    var birthDate: String? = null
    var expireDate: String? = null
    var documentNo: String? = null
    var lang: String? = null
    var email: String? = null
    var phone: String? = null
    var name: String? = null

    if (args.hasKey("birthDate")) birthDate = args.getString("birthDate")
    if (args.hasKey("expireDate")) expireDate = args.getString("expireDate")
    if (args.hasKey("documentNo")) documentNo = args.getString("documentNo")
    if (args.hasKey("lang")) lang = args.getString("lang")
    if (args.hasKey("email")) email = args.getString("email")
    if (args.hasKey("phone")) phone = args.getString("phone")
    if (args.hasKey("name")) name = args.getString("name")

    val geoLocation: Boolean = if (args.hasKey("geoLocation")) args.getBoolean("geoLocation") else false

    var amaniVersion = AmaniVersion.V2
    if (args.hasKey("apiVersion") && args.getString("apiVersion") == "v1") {
      amaniVersion = AmaniVersion.V1
    }

    // ✅ Yeni configure metodu (dynamic features dahil)
    AmaniSDKUI.configure(
      applicationContext = activity.applicationContext,
      serverURL = args.getString("server")!!,
      amaniVersion = amaniVersion,
      enabledFeatures = parseEnabledFeatures(args)
    )

    this.callback = callback

    when {
      email != null && phone != null && name != null -> {
        AmaniSDKUI.goToKycActivity(
          activity = activity,
          resultLauncher = launcher,
          authToken = args.getString("token")!!,
          language = lang ?: "en",
          idNumber = args.getString("id")!!,
          userEmail = email,
          userPhoneNumber = phone,
          userFullName = name,
          geoLocation = geoLocation
        )
      }
      birthDate != null && expireDate != null && documentNo != null -> {
        AmaniSDKUI.goToKycActivity(
          activity = activity,
          resultLauncher = launcher,
          idNumber = args.getString("id")!!,
          authToken = args.getString("token")!!,
          language = lang ?: "en",
          birthDate = birthDate,
          expireDate = expireDate,
          documentNumber = documentNo
        )
      }
      else -> {
        AmaniSDKUI.goToKycActivity(
          activity = activity,
          resultLauncher = launcher,
          idNumber = args.getString("id")!!,
          authToken = args.getString("token")!!,
          language = lang ?: "en"
        )
      }
    }
  }

  @ReactMethod
  fun startAmaniSDKWithToken(args: ReadableMap, callback: Callback) {
    LifeCycleEventListener.addLifeCycleListener(object : LifeCycle {
      override fun onCreate(launcher: ActivityResultLauncher<Intent>) {
        initSDK(args = args, callback = callback, launcher = launcher)
      }

      override fun activityResult(activityResult: ActivityResult) {
        if (activityResult.resultCode == Activity.RESULT_OK) {
          val data: Intent? = activityResult.data
          data?.let {
            val kycResult: KYCResult? = it.parcelable(AppConstant.KYC_RESULT)
            try {
              if (kycResult != null) {
                val resultMap = Arguments.createMap()
                resultMap.putBoolean(
                  "isVerificationCompleted",
                  kycResult.profileStatus == ProfileStatus.APPROVED
                )
                resultMap.putBoolean(
                  "isTokenExpired",
                  kycResult.errorCode == 403
                )
                callback(resultMap)
              }
            } catch (_: Exception) {}
          }
        }
      }
    })

    val intent = Intent(reactApplicationContext, AmaniActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    reactApplicationContext.startActivity(intent)
  }

  private fun initLauncher(activity: AppCompatActivity) {
    launcher = activity.registerForActivityResult(
      ActivityResultContracts.StartActivityForResult()
    ) { result ->
      if (result.resultCode == Activity.RESULT_OK) {
        val data: Intent? = result.data
        data?.let {
          val kycResult: KYCResult? = it.parcelable(AppConstant.KYC_RESULT)
          try {
            if (kycResult != null) {
              val resultMap = Arguments.createMap()
              resultMap.putBoolean(
                "isVerificationCompleted",
                kycResult.profileStatus == ProfileStatus.APPROVED
              )
              resultMap.putBoolean(
                "isTokenExpired",
                kycResult.errorCode == 403
              )
            }
          } catch (_: Exception) {}
        }
      }
    }
  }
}
