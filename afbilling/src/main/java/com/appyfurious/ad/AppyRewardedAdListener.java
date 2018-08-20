package com.appyfurious.ad;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
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

    public AppyRewardedAdListener(@NotNull RewardedCallback<? extends AppCompatActivity> callback, Button button) {
        mRewardedCallback = callback;
        mButton = button;
        mButton.setVisibility(View.GONE);
    }

    @Override
    public void onRewardedVideoAdLoaded() {
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

}
