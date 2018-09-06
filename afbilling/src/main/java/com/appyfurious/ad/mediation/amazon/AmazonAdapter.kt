package com.appyfurious.ad.mediation.amazon

import android.content.Context
import android.os.Bundle
import com.amazon.device.ads.AdLayout
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.mediation.*

class AmazonAdapter : MediationBannerAdapter,
        MediationInterstitialAdapter {

    lateinit var adView: AdLayout

    fun init(context: Context) {
        adView = AdLayout(context, com.amazon.device.ads.AdSize.SIZE_320x50)
    }

    override fun onResume() {
    }

    override fun onPause() {
    }

    override fun onDestroy() {

    }

    override fun getBannerView() = adView

    override fun requestBannerAd(context: Context?, p1: MediationBannerListener?, p2: Bundle?, p3: AdSize?, p4: MediationAdRequest?, p5: Bundle?) {
    }

    override fun requestInterstitialAd(p0: Context?, p1: MediationInterstitialListener?, p2: Bundle?, p3: MediationAdRequest?, p4: Bundle?) {
    }

    override fun showInterstitial() {

    }
}