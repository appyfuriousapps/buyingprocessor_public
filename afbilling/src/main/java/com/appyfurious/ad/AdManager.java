package com.appyfurious.ad;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.appyfurious.ad.parser.AdConfigParser;
import com.appyfurious.afbilling.R;
import com.appyfurious.log.Logger;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.Map;

/**
 * AdManager.java
 * buyingprocessor_public
 * <p>
 * Created by o.davidovich on 24.07.2018.
 * <p>
 * Copyright © 2018 Appyfurious. All rights reserved.
 */

public class AdManager implements AdDownloadingCallback, AdConfigParser.ParserListener {

    /**
     * Banner block
     */

    private LinearLayout mAdContainer;
    private AdView mAdView;

    /**
     * Interstitials block
     */

    private InterstitialAd mInterstitialAd;
    private String mInterstitialKey;
    private int mInterstitialsCountPerSession;
    private int mInterstitialDelay;

    /**
     * Rewarded Video block
     */

    private RewardedVideoAd mRewardedVideoAd;
    private String mRewardedVideoAdKey;

    /**
     * Actions block
     */

    private Map<String, Boolean> mActions;


    private boolean isAdSuccessfullyDownloaded;
    private boolean isPremium;

    private Context mApplicationContext;

    private long mAdValue;
    private int mCurrentCountPerSession;


    public AdManager(Context context) {
        this.mApplicationContext = context;
        initAdManager();
    }

    private void initAdManager() {
        final FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);

        mFirebaseRemoteConfig.fetch(720)
                             .addOnCompleteListener(new OnCompleteListener<Void>() {
                                 @Override
                                 public void onComplete(@NonNull Task<Void> task) {
                                     if (task.isSuccessful()) {
                                         Toast.makeText(mApplicationContext, "Fetch Succeeded",
                                                 Toast.LENGTH_SHORT).show();

                                         // After config data is successfully fetched, it must be activated before newly fetched
                                         // values are returned.
                                         mFirebaseRemoteConfig.activateFetched();
                                         new AdConfigParser(mFirebaseRemoteConfig
                                                 .getString("ads_config_android"), AdManager.this);
                                     } else {
                                         Toast.makeText(mApplicationContext, "Fetch Failed",
                                                 Toast.LENGTH_SHORT).show();
                                     }
                                 }
                             });
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

    @Override
    public void applicationId(String applicationId) {
        MobileAds.initialize(mApplicationContext, applicationId);
    }

    @Override
    public void bannerId(String bannerId) {
        mAdView = new AdView(mApplicationContext);
        mAdView.setAdSize(AdSize.BANNER);
        mAdView.setAdUnitId(bannerId);

        AdRequest adRequest = new AdRequest.Builder().build();

        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AppyAdListener(this));
    }

    @Override
    public void interstitialId(String interstitialId) {
        this.mInterstitialKey = interstitialId;
        loadInterstitialAd();
    }

    public void loadInterstitialAd() {
        mInterstitialAd = new InterstitialAd(mApplicationContext);
        mInterstitialAd.setAdUnitId(mInterstitialKey);
        AdRequest adRequest = new AdRequest.Builder().build();

        mInterstitialAd.loadAd(adRequest);
    }

    public void requestInterstitial(String action) {
        long unixTime = (System.currentTimeMillis() - mAdValue) / 1000;
        if (unixTime > mInterstitialDelay) {
            if (mInterstitialsCountPerSession > mCurrentCountPerSession) {
                if (mActions.containsKey(action)) {
                    boolean isActionEnabled = mActions.get(action);
                    if (isActionEnabled) {
                        if (mInterstitialAd != null) {
                            if (mInterstitialAd.isLoaded()) {
                                mInterstitialAd.show();
                                mAdValue = System.currentTimeMillis();
                                mCurrentCountPerSession++;
                            }

                            loadInterstitialAd();
                        }
                    }
                }
            } else {
                Logger.Companion.logAd("Interstitial aborted by interstitial count per session");
            }
        } else {
            Logger.Companion.logAd("Interstitial aborted by interstitials delay");
        }
    }

    @Override
    public void interstitialsCountPerSession(int interstitialsCountPerSession) {
        mInterstitialsCountPerSession = interstitialsCountPerSession;
    }

    @Override
    public void interstitialDelay(int interstitialDelay) {
        mInterstitialDelay = interstitialDelay;
    }

    @Override
    public void actionsMap(Map<String, Boolean> actions) {
        mActions = actions;
    }

    @Override
    public void rewardedVideoId(String rewardedVideoId) {
        this.mRewardedVideoAdKey = rewardedVideoId;
    }

    public void loadRewardedVideoAd(AppCompatActivity context, RewardedCallback callback, Button button) {
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(context); // Context must always be the cast of Activity
        mRewardedVideoAd.setRewardedVideoAdListener(new AppyRewardedAdListener(callback, button));
        mRewardedVideoAd.loadAd(mRewardedVideoAdKey,
                new AdRequest.Builder().build());
    }

    public void requestRewardedVideoAd() {
        if (mRewardedVideoAd != null) {
            if (mRewardedVideoAd.isLoaded()) {
                mRewardedVideoAd.show();
            }
        }
    }

}
