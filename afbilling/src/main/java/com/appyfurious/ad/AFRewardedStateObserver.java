package com.appyfurious.ad;

import android.content.Context;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.appyfurious.afbilling.R;
import com.appyfurious.log.Logger;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import org.jetbrains.annotations.NotNull;

/**
 * AFRewardedStateObserver.java
 * buyingprocessor_public
 * <p>
 * Created by o.davidovich on 20.08.2018.
 * <p>
 * Copyright © 2018 Appyfurious. All rights reserved.
 */

public class AFRewardedStateObserver implements RewardedVideoAdListener, AFRewardedWaitingTimer.AFRewardedTimerListener {

    private Context mContext;
    private RewardedCallback mRewardedCallback;
    private RewardedLoadingProgressListener mRewardedLoadingListener;
    private CountDownTimer mRewardedWaitingTimer;
    private String mErrorMessage;
    private boolean isUserLeftAppForAd;

    public AFRewardedStateObserver(@NonNull Context context, @NotNull RewardedCallback callback,
                                   int rewardedWaitingTime) {
        mContext = context;
        mRewardedCallback = callback;
        mRewardedLoadingListener = new RewardedLoadingProgressDefault(mContext);
        mRewardedWaitingTimer = new AFRewardedWaitingTimer(rewardedWaitingTime * 1000, 1000, this);
    }

    public AFRewardedStateObserver(Context context, @NonNull RewardedCallback callback, int rewardedWaitingTime,
                                   @Nullable String errorMessage) {
        mContext = context;
        mRewardedCallback = callback;
        mErrorMessage = errorMessage;
        mRewardedLoadingListener = new RewardedLoadingProgressDefault(mContext);
        mRewardedWaitingTimer = new AFRewardedWaitingTimer(rewardedWaitingTime * 1000, 1000, this);
    }

    public AFRewardedStateObserver(Context context, @NonNull RewardedCallback callback, int rewardedWaitingTime,
                                   @Nullable String errorMessage,
                                   @Nullable RewardedLoadingProgressListener progressListener) {
        mContext = context;
        mRewardedCallback = callback;
        mErrorMessage = errorMessage;
        mRewardedLoadingListener = progressListener;
        mRewardedWaitingTimer = new AFRewardedWaitingTimer(rewardedWaitingTime * 1000, 1000, this);
    }

    @Override
    public void onRewardedVideoAdLoaded() {
        mRewardedLoadingListener.hideRewardedLoadingProgress();

        if (mRewardedWaitingTimer != null) {
            mRewardedWaitingTimer.cancel();
            Logger.INSTANCE.logAd("AFRewardedTimer aborted.");
        }

        AFAdManager.getInstance().requestRewardedVideoAd();
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
        mRewardedLoadingListener.hideRewardedLoadingProgress();

        if (mRewardedWaitingTimer != null) {
            mRewardedWaitingTimer.cancel();
            Logger.INSTANCE.logAd("AFRewardedTimer aborted.");
        }

        Logger.INSTANCE.logAd("Rewarded loading failed. Error code: " + i);
        Toast.makeText(mContext, mErrorMessage == null ?
                mContext.getString(R.string.rewarded_video_error) : mErrorMessage, Toast.LENGTH_SHORT)
             .show();
    }

    @Override
    public void onRewardedVideoAdOpened() {}

    @Override
    public void onRewardedVideoStarted() {}

    @Override
    public void onRewardedVideoAdLeftApplication() {
        isUserLeftAppForAd = true;
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        mRewardedCallback.onRewardUser();
    }

    @Override
    public void onRewardedVideoCompleted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {
        if (!isUserLeftAppForAd) {
            mRewardedCallback.onRewardedVideoAdClosed();
            isUserLeftAppForAd = false;
        }

        if (mRewardedWaitingTimer != null) {
            mRewardedWaitingTimer.cancel();
            Logger.INSTANCE.logAd("AFRewardedTimer aborted.");
        }
    }

    public void onAppMovedToForegroundAfterAd() {
        if (isUserLeftAppForAd) {
            mRewardedCallback.onRewardedVideoAdClosed();
            isUserLeftAppForAd = false;
        }
    }

    public void startLoading() {
       mRewardedLoadingListener.showRewardedLoadingProgress();
    }

    @Override
    public void onFinish() {
        mRewardedCallback.onRewardUser();
        mRewardedCallback.onRewardedVideoAdClosed();
    }
}
