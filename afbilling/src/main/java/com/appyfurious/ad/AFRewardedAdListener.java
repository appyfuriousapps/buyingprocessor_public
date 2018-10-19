package com.appyfurious.ad;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.appyfurious.afbilling.R;
import com.appyfurious.log.Logger;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import org.jetbrains.annotations.NotNull;

/**
 * AFRewardedAdListener.java
 * buyingprocessor_public
 * <p>
 * Created by o.davidovich on 20.08.2018.
 * <p>
 * Copyright © 2018 Appyfurious. All rights reserved.
 */

public class AFRewardedAdListener implements RewardedVideoAdListener {

    private Context mContext;
    private RewardedCallback mRewardedCallback;
    private String mErrorMessage;
    private boolean isUserLeftAppForAd;

    public AFRewardedAdListener(@NonNull Context context, @NotNull RewardedCallback callback) {
        mContext = context;
        mRewardedCallback = callback;
    }

    public AFRewardedAdListener(Context context, @NonNull RewardedCallback callback, @Nullable String errorMessage) {
        mContext = context;
        mRewardedCallback = callback;
        mErrorMessage = errorMessage;
    }

    @Override
    public void onRewardedVideoAdLoaded() {
        hideRewardedLoadingProgress();
        AFAdManager.getInstance().requestRewardedVideoAd();
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
        hideRewardedLoadingProgress();
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
    }

    public void onAppMovedToForegroundAfterAd() {
        if (isUserLeftAppForAd) {
            mRewardedCallback.onRewardedVideoAdClosed();
            isUserLeftAppForAd = false;
        }
    }

    public void showRewardedLoadingProgress() {
        Toast.makeText(mContext, "Show progress...", Toast.LENGTH_SHORT).show();
    }

    public void hideRewardedLoadingProgress() {
        Toast.makeText(mContext, "Hide progress...", Toast.LENGTH_SHORT).show();
    }

}
