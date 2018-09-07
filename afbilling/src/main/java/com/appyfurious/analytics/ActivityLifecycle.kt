package com.appyfurious.analytics

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.appyfurious.log.Logger
import java.util.*

@Deprecated("")
class ActivityLifecycle(application: Application) : Application.ActivityLifecycleCallbacks {

    private val screenNames = Stack<String?>()

    init {
        application.unregisterActivityLifecycleCallbacks(this)
        application.registerActivityLifecycleCallbacks(this)
    }

    fun getTwoScreenName(): Result {
        val screenName = try {
            screenNames.pop()
        } catch (ex: Exception) {
            null
        }
        val callScreenName = try {
            screenNames.peek()
        } catch (ex: Exception) {
            null
        }
        screenName?.let {
            screenNames.push(it)
        }
        Logger.notify("getTwoScreenName Result screenName: $screenName, callScreenName: $callScreenName")
        return Result(screenName ?: "screenName null", callScreenName ?: "callScreenName null")
    }

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
        screenNames.push(activity?.javaClass?.simpleName)
        Logger.notify("onActivityCreated simpleName ${activity?.javaClass?.simpleName}")
    }

    override fun onActivityStarted(activity: Activity?) {
    }

    override fun onActivityResumed(activity: Activity?) {
    }

    override fun onActivityPaused(activity: Activity?) {
    }

    override fun onActivityStopped(activity: Activity?) {
    }

    override fun onActivityDestroyed(activity: Activity?) {
        screenNames.pop()
        Logger.notify("onActivityDestroyed simpleName ${activity?.javaClass?.simpleName}")
    }

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
    }

    class Result(val screenName: String, val callScreenName: String)
}