package com.appyfurious.analytics

import android.app.Application
import android.content.pm.PackageManager
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.appyfurious.log.Logger
//import com.crashlytics.android.Crashlytics
import com.facebook.appevents.AppEventsLogger
//import io.fabric.sdk.android.Fabric

object Analytics {

    private const val FABRIC_API_KEY_NAME = "io.fabric.ApiKey"
    private const val FACEBOOK_SDK_APP_ID = "com.facebook.sdk.ApplicationId"

    var isInitAppsflyer = false
        private set

    var isInitFacebook = false
        private set

    var isInitFabric = false
        private set

    fun init(application: Application, appsflyerDevKey: String?) {
        Logger.notify("start Analytics init")
        val metaData = application.packageManager.getApplicationInfo(application.packageName,
                PackageManager.GET_META_DATA).metaData
        val fabricApiKeyValue = metaData.getString(FABRIC_API_KEY_NAME)
        val facebookSdkAppId = metaData.getString(FACEBOOK_SDK_APP_ID)

        Logger.notify("fabricApiKeyValue: $fabricApiKeyValue")
        isInitFabric = fabricApiKeyValue != null && fabricApiKeyValue != ""
        if (isInitFabric) {
       //     Fabric.with(application, Crashlytics())
        }

        Logger.notify("facebookSdkAppId: $facebookSdkAppId")
        isInitFacebook = facebookSdkAppId != null && facebookSdkAppId != ""
        if (isInitFacebook) {
            AppEventsLogger.activateApp(application)
        }

        Logger.notify("appsflyerDevKey: $appsflyerDevKey")
        isInitAppsflyer = appsflyerDevKey != null && appsflyerDevKey != ""
        if (isInitAppsflyer) {
            AppsFlyerLib.getInstance().init(appsflyerDevKey, listener, application)
            AppsFlyerLib.getInstance().startTracking(application)
        }

        Logger.notify("finish Analytics init")
    }

    private val listener = object : AppsFlyerConversionListener {
        override fun onAppOpenAttribution(p0: MutableMap<String, String>?) {
        }

        override fun onAttributionFailure(p0: String?) {
        }

        override fun onInstallConversionDataLoaded(p0: MutableMap<String, String>?) {
        }

        override fun onInstallConversionFailure(p0: String?) {
        }

    }
}