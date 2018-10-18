package com.appyfurious.network.manager

import android.app.Application
import android.arch.lifecycle.*
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.appyfurious.log.Logger

object AFNetworkManager {

    private lateinit var application: Application
    private lateinit var networkReceiver: NetworkChangeReceiver
    private var isInit = false
    private val listeners = arrayListOf<(Context, Boolean) -> Unit>()
    private var lastNetworkStatus: Boolean? = null

    fun addListener(listener: (Context, Boolean) -> Unit) {
        listeners.add(listener)
    }

    fun removeListener(listener: (Context, Boolean) -> Unit) {
        listeners.remove(listener)
    }

    fun init(application: Application) {
        if (!isInit) {
            this.application = application
            ProcessLifecycleOwner.get().lifecycle.addObserver(listener)
        }
        Logger.notify("success init AFNetworkManager")
    }

    fun isOnline(context: Context) = (context.getSystemService(Context.CONNECTIVITY_SERVICE)
            as ConnectivityManager).activeNetworkInfo?.isConnected == true

    private val connected = { context: Context ->
        val newStatus = isOnline(context)
        if (lastNetworkStatus != newStatus) {
            lastNetworkStatus = newStatus
            listeners.map { it(context, newStatus) }
            Logger.notify("new network status! $newStatus")
        }
        Unit
    }

    private val listener = object : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_START)
        fun onMoveToForeground() {
            Logger.notify("AFNetworkManager onMoveToForeground")
            networkReceiver = NetworkChangeReceiver.register(application)
            networkReceiver.listener = connected
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        fun onMoveToBackground() {
            Logger.notify("AFNetworkManager onMoveToBackground")
            networkReceiver.listener = null
            NetworkChangeReceiver.unregister(application, networkReceiver)
        }
    }
}
