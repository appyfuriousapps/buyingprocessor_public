package com.appyfurious.afbilling.service

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.android.vending.billing.IInAppBillingService
import com.appyfurious.log.Logger

class BillingService(context: Context) : ServiceConnection {

    var inAppBillingService: IInAppBillingService? = null
        private set
    var isConnected = false

    private val listenersConnected = arrayListOf<(IInAppBillingService?) -> Unit>()

    fun addConnectedListener(listener: (IInAppBillingService?) -> Unit) {
        listenersConnected.add(listener)
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        isConnected = true
        inAppBillingService = IInAppBillingService.Stub.asInterface(service)
        listenersConnected.map { it(inAppBillingService) }
        listenersConnected.removeAll(listenersConnected)
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        isConnected = false
        inAppBillingService = null
    }

    init {
        bind(context)
    }

    fun bind(context: Context) {
        Logger.notify("onResume connected")
        try {
            val serviceIntent = Intent("com.android.vending.billing.InAppBillingService.BIND")
            serviceIntent.`package` = "com.android.vending"
            context.bindService(serviceIntent, this, Context.BIND_AUTO_CREATE)
        } catch (ex: Exception) {
            Logger.exception("init")
        }
    }

    fun unbind(context: Context) {
        Logger.notify("onPause disconnected")
        if (isConnected)
            context.unbindService(this)
        isConnected = false
    }

    fun getStatus(body: ((BillingResponseType) -> Unit)? = null) {
        if (isConnected) {
            body?.invoke(BillingResponseType.SUCCESS)
        } else {
            body?.invoke(BillingResponseType.NOT_CONNECTED)
        }
    }

    enum class BillingResponseType {
        SUCCESS, NOT_CONNECTED
    }
}