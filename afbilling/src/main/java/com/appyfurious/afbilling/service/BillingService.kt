package com.appyfurious.afbilling.service

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.android.vending.billing.IInAppBillingService
import com.appyfurious.log.Logger

class BillingService(private val connected: (IInAppBillingService) -> Unit) : ServiceConnection {

    lateinit var inAppBillingService: IInAppBillingService
    var isAuth = false
    var isConnected = false

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        isConnected = true
        inAppBillingService = IInAppBillingService.Stub.asInterface(service)
        connected(inAppBillingService)
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        isConnected = false
    }

    fun bind(context: Context, error: () -> Unit) {
        Logger.notify("onResume connected")
        try {
            val serviceIntent = Intent("com.android.vending.billing.InAppBillingService.BIND")
            serviceIntent.`package` = "com.android.vending"
            context.bindService(serviceIntent, this, Context.BIND_AUTO_CREATE)
        } catch (ex: Exception) {
            (context as? Activity)?.runOnUiThread {
                error()
            }
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
        if (isConnected && isAuth) {
            body?.invoke(BillingResponseType.SUCCESS)
        } else
            if (!isConnected)
                body?.invoke(BillingResponseType.NOT_CONNECTED)
            else
                if (!isAuth)
                    body?.invoke(BillingResponseType.NOT_AUTH)
    }

    enum class BillingResponseType {
        SUCCESS, NOT_CONNECTED, NOT_AUTH
    }
}