package com.appyfurious.afbilling.utils

import android.content.Context
import com.appyfurious.log.Logger
import com.appyfurious.afbilling.validation.utils.AdvertisingIdClient

class Adverting(context: Context, private val success: (String, Boolean?) -> Unit) : AdvertisingIdClient.Listener {

    init {
        AdvertisingIdClient.getAdvertisingId(context, this)
    }

    private val default = "advertingId"

    override fun onAdvertisingIdClientFinish(adInfo: AdvertisingIdClient.AdInfo?) {
        Logger.notify("onAdvertisingIdClientFinish ${adInfo?.id}")
        success(adInfo?.id ?: default, adInfo?.isLimitAdTrackingEnabled)
    }

    override fun onAdvertisingIdClientFail(exception: Exception) {
        Logger.notify("onAdvertisingIdClientFail advertingId")
        success(default, true)
    }
}
