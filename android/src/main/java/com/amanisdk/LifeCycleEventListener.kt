package com.amanisdk

import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity

class LifeCycleEventListener {

  companion object {
    var lifeCycle: LifeCycle? = null

    /**
     * Registering for LifeCycle, must be called before activity created
     */
    fun addLifeCycleListener(lifeCycle: LifeCycle) {
      this.lifeCycle = lifeCycle
    }

    /**
     * Unregistering the LifeCycle observation, must be called after activity is destroyed
     */
    fun unRegisterLifeCycle() {
      this.lifeCycle = null
    }
  }
}

interface LifeCycle {
  fun onCreate(launcher: ActivityResultLauncher<Intent>)

  fun activityResult(activityResult: ActivityResult)
}
