package com.appyfurious.ad;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.appyfurious.afbilling.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

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
    private RewardedVideoAd mRewardedVideoAd;
    private LinearLayout mAdContainer;
    private boolean isAdSuccessfullyDownloaded;
    private boolean isPremium;

    private Context mApplicationContext;
    private String mInterstitialKey;
    private String mRewardedVideoAdKey;

    public AdManager(Context context) {
        this.mApplicationContext = context;
    }

    public void initMobileAds(String appAdKey) {
        MobileAds.initialize(mApplicationContext, appAdKey);
    }

    public void initBannerAd(String bannerAdKey) {
        mAdView = new AdView(mApplicationContext);
        mAdView.setAdSize(AdSize.BANNER);
        mAdView.setAdUnitId(bannerAdKey);

        AdRequest adRequest = new AdRequest.Builder().build();

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

    public void initInterstitialAd(String interstitialAdKey) {
        this.mInterstitialKey = interstitialAdKey;
        loadInterstitialAd();
    }

    public void loadInterstitialAd() {
        mInterstitialAd = new InterstitialAd(mApplicationContext);
        mInterstitialAd.setAdUnitId(mInterstitialKey);
        AdRequest adRequest = new AdRequest.Builder().build();

        mInterstitialAd.loadAd(adRequest);
    }

    public void showInterstitial() {
        if (mInterstitialAd != null) {
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }

            loadInterstitialAd();
        }
    }

    public void initRewardedVideoAd(String rewardedVideoAdKey) {
        this.mRewardedVideoAdKey = rewardedVideoAdKey;
    }

    public void loadRewardedVideoAd(AppCompatActivity context, RewardedVideoAdListener listener) {
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(context);
        mRewardedVideoAd.setRewardedVideoAdListener(listener);
        mRewardedVideoAd.loadAd(mRewardedVideoAdKey,
                new AdRequest.Builder().build());
    }

    public void showRewardedVideoAd() {
        if (mRewardedVideoAd != null) {
            if (mRewardedVideoAd.isLoaded()) {
                mRewardedVideoAd.show();
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
