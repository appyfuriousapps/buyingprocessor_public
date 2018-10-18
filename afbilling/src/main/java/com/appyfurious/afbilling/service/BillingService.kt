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
        private set

    private val listenersConnected = arrayListOf<(IInAppBillingService?) -> Unit>()

    private fun addConnectedListener(listener: (IInAppBillingService?) -> Unit) {
        listenersConnected.add(listener)
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        isConnected = true
        inAppBillingService = IInAppBillingService.Stub.asInterface(service)
        listenersConnected.map { it(inAppBillingService) }
        listenersConnected.removeAll(listenersConnected)
        Logger.notify("onServiceConnected!")
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        isConnected = false
        inAppBillingService = null
        Logger.notify("onServiceDisconnected!")
    }

    init {
        bind(context)
    }

    fun connected(body: (IInAppBillingService?) -> Unit) {
        if (isConnected) {
            Logger.notify("BillingService connected isConnected success")
            body(inAppBillingService)
        } else {
            addConnectedListener { service ->
                Logger.notify("BillingService connected addConnectedListener success")
                body(inAppBillingService)
            }
        }
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
        context.unbindService(this)
    }

    fun getStatus(body: ((BillingResponseType) -> Unit)? = null) {
        if (isConnected) {
            body?.invoke(BillingResponseType.SUCCESS)
        } else {
            body?.invoke(BillingResponseType.NOT_AUTH)
        }
    }

    enum class BillingResponseType {
        SUCCESS, NOT_AUTH, NOT_INTERNET, NOT_PRODUCT
    }
}