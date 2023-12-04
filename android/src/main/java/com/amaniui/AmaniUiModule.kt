package com.amaniui

import ai.amani.base.utility.AmaniVersion
import ai.amani.sdk.extentions.parcelable
import ai.amani.sdk.model.KYCResult
import ai.amani.sdk.utils.AppConstant
import ai.amani.sdk.utils.ProfileStatus
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

  private fun initSDK(args: ReadableMap, callback: Callback, launcher: ActivityResultLauncher<Intent>) {
    val activity = currentActivity as AppCompatActivity
    var birthDate: String? = null
    var expireDate: String? = null
    var documentNo: String? = null
    var lang: String? = null
    var email: String? = null
    var phone: String? = null
    var name: String? = null
    if (args.hasKey("birthDate")) {
      birthDate = args.getString("birthDate")
    }
    if (args.hasKey("expireDate")) {
      expireDate = args.getString("expireDate")
    }
    if (args.hasKey("documentNo")) {
      documentNo = args.getString("documentNo")
    }
    val geoLocation: Boolean = if (args.hasKey("geoLocation")) {
      args.getBoolean("geoLocation")
    } else {
      false
    }
    if (args.hasKey("lang")) {
      lang = args.getString("lang")
    }
    if (args.hasKey("email")) {
      email = args.getString("email")
    }
    if (args.hasKey("phone")) {
      phone = args.getString("phone")
    }
    if (args.hasKey("name")) {
      name = args.getString("name")
    }
    var amaniVersion = AmaniVersion.V2
    if (args.hasKey("apiVersion") && args.getString("apiVersion") == "v1") {
      amaniVersion = AmaniVersion.V1
    }

    AmaniSDKUI.init(
      activity = activity,
      serverURL = args.getString("server")!!,
      amaniVersion = amaniVersion
    )

    this.callback = callback
    if (email != null && phone != null && name != null) {
      AmaniSDKUI.goToKycActivity(
        activity = activity,
        resultLauncher = launcher!!,
        authToken = args.getString("token")!!,
        language = lang!!,
        idNumber = args.getString("id")!!,
        birthDate = null,
        documentNumber = null,
        expireDate = null,
        userEmail = email,
        userPhoneNumber = phone,
        userFullName = name,
        geoLocation = geoLocation,
      )
    } else if (birthDate != null && expireDate != null && documentNo != null) {
      AmaniSDKUI.goToKycActivity(
        activity = activity,
        resultLauncher = launcher!!,
        idNumber = args.getString("id")!!,
        authToken = args.getString("token")!!,
        language = lang!!,
        birthDate = "birthDate",
        expireDate = "expireDate",
        documentNumber = "documentNo",
      )

    } else {
      AmaniSDKUI.goToKycActivity(
        activity = activity,
        resultLauncher = launcher!!,
        idNumber = args.getString("id")!!,
        authToken = args.getString("token")!!
      )
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
                  kycResult.httpErrorCode == 403
                )

                callback(resultMap)
              }
            } catch (_: Exception) {}
          }
        }
      }
    })


    val intent = Intent(reactApplicationContext, AmaniActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_MULTIPLE_TASK
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
                kycResult.httpErrorCode == 403
              )
            }
          } catch (_: Exception) {}
        }
      }
    }
  }
}
