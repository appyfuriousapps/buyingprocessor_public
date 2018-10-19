package com.appyfurious.network.manager

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import com.appyfurious.log.Logger


class NetworkChangeReceiver : BroadcastReceiver() {

    companion object {
        fun register(context: Context): NetworkChangeReceiver {
            val receiver = NetworkChangeReceiver()
            val filter = IntentFilter()
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
            context.registerReceiver(receiver, filter)
            return receiver
        }

        fun unregister(context: Context, receiver: NetworkChangeReceiver?) {
            context.unregisterReceiver(receiver)
        }
    }

    var listener: ((Context) -> Unit)? = null

    override fun onReceive(context: Context, intent: Intent) {
        listener?.invoke(context)
    }
}