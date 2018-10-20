package com.appyfurious.ad;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.ProgressBar;

/**
 * RewardedLoadingProgressDefault.java
 * buyingprocessor_public
 * <p>
 * Created by o.davidovich on 20.10.2018.
 * <p>
 * Copyright © 2018 Appyfurious. All rights reserved.
 */

public class RewardedLoadingProgressDefault implements RewardedLoadingProgressListener {

    private Context mContext;
    private ViewGroup mRootView;
    private ProgressBar mProgressBar;

    public RewardedLoadingProgressDefault(Context context) {
        mContext = context;
        mProgressBar = new ProgressBar(mContext, null, android.R.attr.progressBarStyleHorizontal);
        mProgressBar.setIndeterminate(true);
    }

    @Override
    public void showRewardedLoadingProgress() {
        if (mContext instanceof AppCompatActivity) {
            AppCompatActivity rewardedActivity = (AppCompatActivity) mContext;
            mRootView = rewardedActivity.findViewById(android.R.id.content);
            mRootView.addView(mProgressBar);
        }

    }

    @Override
    public void hideRewardedLoadingProgress() {
        mRootView.removeView(mProgressBar);
    }

}