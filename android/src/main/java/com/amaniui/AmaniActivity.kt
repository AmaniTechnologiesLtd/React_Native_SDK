package com.amaniui

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.facebook.react.ReactActivity

/**
 * @Author: @zekiamani
 * @Date: 8.08.2023
 */

/**
 * This activity is a DummyActivity for creation of the activity launcher
 */
class AmaniActivity: ReactActivity() {

 private var launcher: ActivityResultLauncher<Intent> = this.registerForActivityResult(
  ActivityResultContracts.StartActivityForResult()
  ) { result ->

   /**
    * Returning the activity result to AmaniSDKModule
    */
   LifeCycleEventListener.lifeCycle!!.activityResult(result)

   /**
    * Finishing the dummy activity to return main page
    * Finishing the Dummy activity must be called after activity result is received
    * If you call the finish before activity result is received, result will never be received
    */
   this.finish()

   /**
    * Unregistering the LifeCycle observation
    */
   LifeCycleEventListener.unRegisterLifeCycle()
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    /**
     * Returning the launcher just before activity created
     */
    LifeCycleEventListener.lifeCycle!!.onCreate( launcher)
  }
}
