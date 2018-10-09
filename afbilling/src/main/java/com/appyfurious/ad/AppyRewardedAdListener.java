package com.appyfurious.ad;

import android.support.annotation.Nullable;
import android.widget.Button;

import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import org.jetbrains.annotations.NotNull;

/**
 * AppyRewardedAdListener.java
 * buyingprocessor_public
 * <p>
 * Created by o.davidovich on 20.08.2018.
 * <p>
 * Copyright © 2018 Appyfurious. All rights reserved.
 */

public class AppyRewardedAdListener implements RewardedVideoAdListener {

    private RewardedCallback mRewardedCallback;
    private Button mButton;

    public AppyRewardedAdListener(@NotNull RewardedCallback callback, @Nullable Button button) {
        mRewardedCallback = callback;
        mButton = button;
        if (mButton != null) {
            mButton.setEnabled(false);
        }
    }

    @Override
    public void onRewardedVideoAdLoaded() {
        if (mButton != null) {
            mButton.setEnabled(true);
        }
        mRewardedCallback.onRewardedVideoAdLoaded();
    }

    @Override
    public void onRewardedVideoAdOpened() {}

    @Override
    public void onRewardedVideoStarted() {}

    @Override
    public void onRewardedVideoAdClosed() {
        mRewardedCallback.onRewardedVideoAdClosed();
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        mRewardedCallback.onRewardUser();
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {}

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {}

    @Override
    public void onRewardedVideoCompleted() {}

}
