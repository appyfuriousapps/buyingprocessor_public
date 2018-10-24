package com.appyfurious.ad;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.appyfurious.afbilling.AFStoreManager;
import com.appyfurious.db.AFAdsManagerConfiguration;
import com.appyfurious.db.AFRealmDatabase;
import com.appyfurious.db.Action;
import com.appyfurious.log.Logger;
import com.appyfurious.network.manager.AFNetworkManager;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.mopub.common.MoPub;
import com.mopub.common.SdkConfiguration;
import com.vungle.mediation.VungleAdapter;
import com.vungle.mediation.VungleExtrasBuilder;
import com.vungle.mediation.VungleInterstitialAdapter;

import org.jetbrains.annotations.NotNull;

import io.realm.RealmChangeListener;

//import com.mopub.common.MoPub;
//import com.mopub.common.SdkConfiguration;

/**
 * AFAdManager.java
 * buyingprocessor_public
 * <p>
 * Created by o.davidovich on 11.09.2018.
 * <p>
 * Copyright © 2018 Appyfurious. All rights reserved.
 */

public class AFAdManager implements AdDownloadingCallback, RealmChangeListener<AFAdsManagerConfiguration>,
        LifecycleObserver {

    private static AFAdManager mInstance;

    private Context applicationContext;
    private Context mRewardedVideoContext;

    private ViewGroup mRootView;
    private ViewGroup mAdContainer;
    private AdView mAdView;

    private boolean isAdSuccessfullyDownloaded;

    private InterstitialAd mInterstitialAd;

    private RewardedVideoAd mRewardedVideoAd;
    private AFRewardedStateObserver mRewardedObserver;

    private AFAdsManagerConfiguration mAFAdsManagerConfiguration;

    private boolean isBannerAdVisible;
    private AppCompatActivity bannerActivity;

    private AdRequest adRequest;
    private ViewGroup mRootViewMarginToNull;

    private Boolean isPremium;


    public static synchronized AFAdManager getInstance() {
        if (mInstance == null) {
            mInstance = new AFAdManager();
        }

        return mInstance;
    }

    public void initialize(Context applicationContext, AFAdsManagerConfiguration configuration,
                           String[] vungleExtras) {
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

        AFStoreManager.INSTANCE.isSubsData().observeForever(isSubs -> {
            if (isPremium != isSubs) {
                isPremium = isSubs;
                if (bannerActivity != null && mAdView != null) {
                    initBannerContainer(bannerActivity);
                }
            }
        });
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
        if (activity != null) {
            if (activity instanceof BannerAdActivity) {

                mRootView = activity.findViewById(android.R.id.content);

                for (int i = 0; i < mRootView.getChildCount(); i++) {
                    View v = mRootView.getChildAt(i);
                    if (v != null) {
                        if (v instanceof ViewGroup) {
                            mAdContainer = (ViewGroup) v;
                            break;
                        }
                    }
                }

                if (mAdContainer != null) {
                    Boolean isPremium = AFStoreManager.INSTANCE.isSubsData().getValue();
                    if (isPremium != null && !isPremium) {
                        if (isAdSuccessfullyDownloaded) {
                            loadAd(mRootView, mAdContainer);
                            mAdView.setVisibility(View.VISIBLE);
                            isBannerAdVisible = true;
                        } else {
                            mAdView.setVisibility(View.GONE);
                            isBannerAdVisible = false;
                            setRootViewMarginToNull(mAdContainer);
                            if (AFNetworkManager.INSTANCE.isOnline(applicationContext)) {
                                updateConfiguration(applicationContext, mAFAdsManagerConfiguration);
                            }
                        }
                    } else {
                        mAdView.setVisibility(View.GONE);
                        isBannerAdVisible = false;
                        setRootViewMarginToNull(mAdContainer);
                    }
                } else {
                    isBannerAdVisible = false;
                }
            } else {
                Logger.INSTANCE
                        .logAd("Ad is not visible. Activity is not instance of BannerAdActivity");
            }

        }
    }

    public void loadAd(ViewGroup rootView, ViewGroup adContainer) {
        if (mAdView.getParent() != null) {
            ViewGroup tempVg = (ViewGroup) mAdView.getParent();
            tempVg.removeView(mAdView);
        }

        if (adContainer.getLayoutParams() instanceof FrameLayout.LayoutParams) {
            FrameLayout.LayoutParams flp = (FrameLayout.LayoutParams) adContainer.getLayoutParams();

            flp.bottomMargin = mAdView.getHeight();

            adContainer.setLayoutParams(flp);
        }

        rootView.addView(mAdView);

        ViewGroup.LayoutParams lp = mAdView.getLayoutParams();

        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        if (lp instanceof LinearLayout.LayoutParams) {
            LinearLayout.LayoutParams llp = ((LinearLayout.LayoutParams) lp);
            llp.gravity = Gravity.BOTTOM;

        } else if (lp instanceof FrameLayout.LayoutParams) {
            FrameLayout.LayoutParams flp = ((FrameLayout.LayoutParams) lp);
            flp.gravity = Gravity.BOTTOM;

        } else if (lp instanceof RelativeLayout.LayoutParams) {
            RelativeLayout.LayoutParams rlp = ((RelativeLayout.LayoutParams) lp);
            rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

        } else if (lp instanceof GridLayout.LayoutParams) {
            GridLayout.LayoutParams glp = ((GridLayout.LayoutParams) lp);
            glp.setGravity(Gravity.BOTTOM);

        } else if (lp instanceof ConstraintLayout.LayoutParams) {
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone((ConstraintLayout) adContainer);
            constraintSet.connect(mAdView.getId(), ConstraintSet.BOTTOM, adContainer.getId(),
                    ConstraintSet.BOTTOM, 0);
            constraintSet.connect(mAdView.getId(), ConstraintSet.LEFT, adContainer.getId(),
                    ConstraintSet.LEFT, 0);
            constraintSet.connect(mAdView.getId(), ConstraintSet.RIGHT, adContainer.getId(),
                    ConstraintSet.RIGHT, 0);
            constraintSet.applyTo((ConstraintLayout) adContainer);
        } else if (lp instanceof CoordinatorLayout.LayoutParams) {
            CoordinatorLayout.LayoutParams clp = ((CoordinatorLayout.LayoutParams) lp);
            clp.gravity = Gravity.BOTTOM;
        }

        mAdView.setLayoutParams(lp);
    }

    public void initBanner(Context applicationContext, String bannerId) {
        mAdView = new AdView(applicationContext);
        mAdView.getViewTreeObserver()
               .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                   @SuppressLint("NewApi")
                   @Override
                   public void onGlobalLayout() {
                       initBannerContainer(bannerActivity);
                       mAdView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                   }
               });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mAdView.setId(View.generateViewId());
        }
        mAdView.setAdSize(AdSize.SMART_BANNER);
        mAdView.setAdUnitId(bannerId);

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
        requestInterstitial(action, null);
    }

    public void requestInterstitial(String action, InterstitialCallback callback) {

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
                                if (callback != null) {
                                    mInterstitialAd.setAdListener(new AdListener() {
                                        @Override
                                        public void onAdClosed() {
                                            callback.onInterstitialClosed();
                                        }

                                        @Override
                                        public void onAdOpened() {
                                            callback.onInterstitialOpened();
                                        }
                                    });
                                }
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

    public void loadRewardedVideoAd(@NotNull AppCompatActivity context, RewardedCallback callback, String errorMessage) {
        mRewardedVideoContext = context;
        mRewardedVideoAd = MobileAds
                .getRewardedVideoAdInstance(context); // Context must always be the cast of Activity

        mRewardedObserver = new AFRewardedStateObserver(context, callback,
                mAFAdsManagerConfiguration.getRewardedVideoWaitingTime(),
                errorMessage);

        mRewardedVideoAd.setRewardedVideoAdListener(mRewardedObserver);
        mRewardedVideoAd.loadAd(mAFAdsManagerConfiguration.getRewardedVideoId(),
                new AdRequest.Builder().build());

        mRewardedObserver.startLoading();

        Lifecycle lc = context.getLifecycle();
        lc.addObserver(this);
    }

    public void remoteRewardedListener() {
        mRewardedObserver = null;
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
        SdkConfiguration sdkConfiguration =
                new SdkConfiguration.Builder("MOPUB_AD_UNIT_ID").build(); // TODO add the key

        MoPub.initializeSdk(applicationContext, sdkConfiguration, null);

        Bundle bundleVungle = new VungleExtrasBuilder(vungleExtras).build();

        adRequest = new AdRequest.Builder()
                .addNetworkExtrasBundle(VungleAdapter.class, bundleVungle)
                .addNetworkExtrasBundle(VungleInterstitialAdapter.class, bundleVungle)
                .addTestDevice("5C9C9DBD05D907E8397E575B2D3C8516")
                .build();
    }

    private void setRootViewMarginToNull(ViewGroup adContainer) {
        if (mAdView.getParent() != null) {
            ViewGroup tempVg = (ViewGroup) mAdView.getParent();
            tempVg.removeView(mAdView);
        }

        if (adContainer.getLayoutParams() instanceof FrameLayout.LayoutParams) {
            FrameLayout.LayoutParams flp = (FrameLayout.LayoutParams) adContainer.getLayoutParams();

            if (flp.bottomMargin != 0) {
                flp.bottomMargin = 0;
            }

            adContainer.setLayoutParams(flp);
        }
    }

    public void onMoveToForeground() {
        if (mRewardedObserver != null) {
            mRewardedObserver.onAppMovedToForegroundAfterAd();
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    void onResume() {
        if (mRewardedVideoAd != null) {
            mRewardedVideoAd.resume(mRewardedVideoContext);
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    void onPause() {
        if (mRewardedVideoAd != null) {
            mRewardedVideoAd.pause(mRewardedVideoContext);
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    void onDestroy() {
        if (mRewardedVideoAd != null) {
            mRewardedVideoAd.destroy(mRewardedVideoContext);
        }
    }

}