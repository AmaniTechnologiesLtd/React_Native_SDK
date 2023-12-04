package com.amaniui

import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher

class LifeCycleEventListener {

  companion object {
    var lifeCycle: LifeCycle? = null

    /**
     * Registering for LifeCycle, must be called before activity created
     */
    fun addLifeCycleListener(lifeCycle: LifeCycle) {
      Companion.lifeCycle = lifeCycle
    }

    /**
     * Unregistering the LifeCycle observation, must be called after activity is destroyed
     */
    fun unRegisterLifeCycle() {
      lifeCycle = null
    }
  }
}

interface LifeCycle {
  fun onCreate(launcher: ActivityResultLauncher<Intent>)

  fun activityResult(activityResult: ActivityResult)
}
