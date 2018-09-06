package com.appyfurious.ad;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.amazon.device.ads.AdRegistration;
import com.appyfurious.afbilling.R;
import com.google.ads.mediation.facebook.FacebookAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.mopub.common.MoPub;
import com.mopub.common.SdkConfiguration;
import com.mopub.mobileads.dfp.adapters.MoPubAdapter;
import com.vungle.mediation.VungleAdapter;
import com.vungle.mediation.VungleExtrasBuilder;
import com.vungle.mediation.VungleInterstitialAdapter;

/**
 * AdManager.java
 * buyingprocessor_public
 * <p>
 * Created by o.davidovich on 24.07.2018.
 * <p>
 * Copyright © 2018 Appyfurious. All rights reserved.
 */

public class AdManager implements AdDownloadingCallback {

    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    private LinearLayout mAdContainer;
    private boolean isAdSuccessfullyDownloaded;
    private boolean isPremium;

    private Context mApplicationContext;
    private String mInterstitialKey;

    private AdRequest adRequest;

    public void initMediation(Context context, String adUnitId) {
        SdkConfiguration sdkConfiguration = new SdkConfiguration.Builder(adUnitId).build();
        MoPub.initializeSdk(context, sdkConfiguration, null);

        Bundle bundleMoPub = new MoPubAdapter.BundleBuilder().build();
        Bundle bundleFacebook = new FacebookAdapter.FacebookExtrasBundleBuilder().build();
        Bundle bundleVungle = new VungleExtrasBuilder(new String[]{"ACTIVITY_NAME_1", "ACTIVITY_NAME_2"}).build();

        AdRegistration.setAppKey("AMAZON_KEY");

        adRequest = new AdRequest.Builder()
                //unity init in gradle
                //smaato init in gradle
                .addNetworkExtrasBundle(MoPubAdapter.class, bundleMoPub)//MoPub init
                .addNetworkExtrasBundle(FacebookAdapter.class, bundleFacebook)//Facebook init
                .addNetworkExtrasBundle(VungleAdapter.class, bundleVungle)//Vungle init
                .addNetworkExtrasBundle(VungleInterstitialAdapter.class, bundleVungle)//Vungle Interstitial init
                .build();
    }

    public void initBannerAd(Context context, String bannerAdKey) {
        mAdView = new AdView(context);
        mAdView.setAdSize(AdSize.BANNER);
        mAdView.setAdUnitId(bannerAdKey);

        adRequest = new AdRequest.Builder().build();//TODO AD

        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AppyAdListener(this));
    }

    public void initBannerContainer(AppCompatActivity activity) {
        mAdContainer = activity.findViewById(R.id.ad_container);
        if (mAdContainer != null) {
            if (!isPremium) {
                if (isAdSuccessfullyDownloaded) {
                    mAdContainer.setVisibility(View.VISIBLE);
                    loadAd(mAdContainer);
                } else {
                    mAdContainer.setVisibility(View.GONE);
                }
            } else {
                mAdContainer.setVisibility(View.GONE);
            }
        }
    }

    public void loadAd(ViewGroup adContainer) {

        if (mAdView.getParent() != null) {
            ViewGroup tempVg = (ViewGroup) mAdView.getParent();
            tempVg.removeView(mAdView);
        }

        adContainer.addView(mAdView);
    }

    public void initInterstitialAd(Context context, String interstitialAdKey) {
        this.mApplicationContext = context;
        this.mInterstitialKey = interstitialAdKey;
        loadInterstitialAd();
    }

    public void loadInterstitialAd() {
        mInterstitialAd = new InterstitialAd(mApplicationContext);
        mInterstitialAd.setAdUnitId(mInterstitialKey);
        adRequest = new AdRequest.Builder().build();//TODO AD

        mInterstitialAd.loadAd(adRequest);
    }

    public void showInterstitial() {
        if (!isPremium) {
            if (mInterstitialAd != null) {
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                }

                loadInterstitialAd();
            }
        }
    }

    @Override
    public void onAdLoadSuccess() {
        isAdSuccessfullyDownloaded = true;
    }

    @Override
    public void onAdLoadFailure() {
        isAdSuccessfullyDownloaded = false;
    }

    public void setPremium(boolean premium) {
        isPremium = premium;
    }

}
