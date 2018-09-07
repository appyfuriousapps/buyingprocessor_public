package com.appyfurious.analytics

import android.app.Application
import com.appyfurious.log.Logger

@Deprecated("")
class ScreenManager private constructor(application: Application) {

    companion object {
        private lateinit var screenManager: ScreenManager
        fun init(application: Application) {
            screenManager = ScreenManager(application)
            Logger.notify("ScreenManager init")
        }

        fun getInstance() = screenManager
    }

    private val activityLifecycle = ActivityLifecycle(application)

    fun getTwoScreenName() = activityLifecycle.getTwoScreenName()
}