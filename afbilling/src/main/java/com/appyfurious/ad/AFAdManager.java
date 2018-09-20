package com.appyfurious.ad;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.appyfurious.afbilling.R;
import com.appyfurious.db.AFAdsManagerConfiguration;
import com.appyfurious.db.AFRealmDatabase;
import com.appyfurious.db.Action;
import com.appyfurious.log.Logger;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardedVideoAd;
//import com.mopub.common.MoPub;
//import com.mopub.common.SdkConfiguration;
import com.vungle.mediation.VungleAdapter;
import com.vungle.mediation.VungleExtrasBuilder;
import com.vungle.mediation.VungleInterstitialAdapter;

import io.realm.RealmChangeListener;

/**
 * AFAdManager.java
 * buyingprocessor_public
 * <p>
 * Created by o.davidovich on 11.09.2018.
 * <p>
 * Copyright © 2018 Appyfurious. All rights reserved.
 */

public class AFAdManager implements AdDownloadingCallback, RealmChangeListener<AFAdsManagerConfiguration> {


    private static AFAdManager mInstance;

    private Context applicationContext;

    private LinearLayout mAdContainer;
    private AdView mAdView;

    private boolean isAdSuccessfullyDownloaded;
    private boolean isPremium;

    private InterstitialAd mInterstitialAd;

    private RewardedVideoAd mRewardedVideoAd;

    private AFAdsManagerConfiguration mAFAdsManagerConfiguration;

    private boolean isBannerAdVisible;
    private AppCompatActivity bannerActivity;

    private AdRequest adRequest;


    public static synchronized AFAdManager getInstance() {
        if (mInstance == null) {
            mInstance = new AFAdManager();
        }

        return mInstance;
    }

    public void initialize(Context applicationContext, AFAdsManagerConfiguration configuration, String[] vungleExtras) {
        this.applicationContext = applicationContext;

        mAFAdsManagerConfiguration = AFRealmDatabase.getInstance().getAdsConfiguration();
        if (mAFAdsManagerConfiguration == null) {
            this.mAFAdsManagerConfiguration = configuration;
            AFRealmDatabase.getInstance().saveAd(configuration, null);
        }

        MobileAds.initialize(applicationContext, mAFAdsManagerConfiguration.getApplicationId());

        initMediation(vungleExtras);

        initBanner(applicationContext, mAFAdsManagerConfiguration.getBannerId());
        initInterstitialId(applicationContext, mAFAdsManagerConfiguration.getInterstitialId());
    }

    public void updateConfiguration(Context applicationContext, AFAdsManagerConfiguration configuration) {
        this.applicationContext = applicationContext;
        this.mAFAdsManagerConfiguration = configuration;

        AFRealmDatabase.getInstance().saveAd(configuration, null);

        MobileAds.initialize(applicationContext, mAFAdsManagerConfiguration.getApplicationId());

        initBanner(applicationContext, mAFAdsManagerConfiguration.getBannerId());
        initInterstitialId(applicationContext, mAFAdsManagerConfiguration.getInterstitialId());
    }

    @Override
    public void onAdLoadSuccess() {
        isAdSuccessfullyDownloaded = true;
        if (!isBannerAdVisible) {
            initBannerContainer(bannerActivity);
        }
    }

    @Override
    public void onAdLoadFailure() {
        isAdSuccessfullyDownloaded = false;
    }

    public void initBannerContainer(AppCompatActivity activity) {
        this.bannerActivity = activity;
        mAdContainer = activity.findViewById(R.id.ad_container);
        if (mAdContainer != null) {
            if (!isPremium) {
                if (isAdSuccessfullyDownloaded) {
                    mAdContainer.setVisibility(View.VISIBLE);
                    loadAd(mAdContainer);
                    isBannerAdVisible = true;
                } else {
                    mAdContainer.setVisibility(View.GONE);
                    isBannerAdVisible = false;
                }
            } else {
                mAdContainer.setVisibility(View.GONE);
                isBannerAdVisible = false;
            }
        } else {
            isBannerAdVisible = false;
        }
    }

    public void loadAd(ViewGroup adContainer) {

        if (mAdView.getParent() != null) {
            ViewGroup tempVg = (ViewGroup) mAdView.getParent();
            tempVg.removeView(mAdView);
        }

        adContainer.addView(mAdView);
    }

    public void initBanner(Context applicationContext, String bannerId) {
        mAdView = new AdView(applicationContext);
        mAdView.setAdSize(AdSize.SMART_BANNER);
        mAdView.setAdUnitId(bannerId);

        //AdRequest adRequest = new AdRequest.Builder().build();

        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AppyAdListener(this));
    }

    public void initInterstitialId(Context applicationContext, String interstitialId) {
        loadInterstitialAd(applicationContext, interstitialId);
    }

    public void loadInterstitialAd(Context applicationContext, String interstitialId) {
        mInterstitialAd = new InterstitialAd(applicationContext);
        mInterstitialAd.setAdUnitId(interstitialId);
        // AdRequest adRequest = new AdRequest.Builder().build();

        mInterstitialAd.loadAd(adRequest);
    }

    public void requestInterstitial(String action) {
        mAFAdsManagerConfiguration = AFRealmDatabase.getInstance().getAdsConfiguration();
        long unixTime = (System.currentTimeMillis() - (long) mAFAdsManagerConfiguration
                .getInterstitialsLastShowDate()) / 1000;
        if (unixTime > mAFAdsManagerConfiguration.getInterstitialsDelay()) {
            if (mAFAdsManagerConfiguration.getInterstitialsCountPerSession() >
                    mAFAdsManagerConfiguration.getCurrentInterstitialCountPerSession()) {

                Action act = mAFAdsManagerConfiguration.containsAction(action);
                if (act != null) {
                    boolean isActionEnabled = act.isEnabled();
                    if (isActionEnabled) {
                        if (mInterstitialAd != null) {
                            if (mInterstitialAd.isLoaded()) {
                                mInterstitialAd.show();
                                AFRealmDatabase.getInstance().setInterstitialsLastShowDate(System
                                        .currentTimeMillis());
                                AFRealmDatabase.getInstance()
                                               .incrementCurrentInterstitialCountPerSession();
                            }

                            loadInterstitialAd(applicationContext, mAFAdsManagerConfiguration
                                    .getInterstitialId());
                        }
                    }
                }
            } else {
                Logger.INSTANCE.logAd("Interstitial aborted by interstitial count per session");
            }
        } else {
            Logger.INSTANCE.logAd("Interstitial aborted by interstitial delay");
        }
    }

    public void loadRewardedVideoAd(AppCompatActivity context, RewardedCallback callback, Button button) {
        mRewardedVideoAd = MobileAds
                .getRewardedVideoAdInstance(context); // Context must always be the cast of Activity
        mRewardedVideoAd.setRewardedVideoAdListener(new AppyRewardedAdListener(callback, button));
        mRewardedVideoAd.loadAd(mAFAdsManagerConfiguration.getRewardedVideoId(),
                adRequest);
    }

    public void requestRewardedVideoAd() {
        if (mRewardedVideoAd != null) {
            if (mRewardedVideoAd.isLoaded()) {
                mRewardedVideoAd.show();
            }
        }
    }

    @Override
    public void onChange(@NonNull AFAdsManagerConfiguration configuration) {
        this.mAFAdsManagerConfiguration = configuration;
        Logger.INSTANCE.logDbChange("Ads Config Changed: " + configuration.toString());
    }

    private void initMediation(String[] vungleExtras) {
//        SdkConfiguration sdkConfiguration =
//                new SdkConfiguration.Builder("MOPUB_AD_UNIT_ID").build(); // TODO add the key
//
//        MoPub.initializeSdk(applicationContext, sdkConfiguration, null);

        Bundle bundleVungle = new VungleExtrasBuilder(vungleExtras).build();

        adRequest = new AdRequest.Builder()
                .addNetworkExtrasBundle(VungleAdapter.class, bundleVungle)
                .addNetworkExtrasBundle(VungleInterstitialAdapter.class, bundleVungle)
                .build();
    }

}