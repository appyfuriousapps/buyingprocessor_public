package com.appyfurious.ad;

import android.content.Context;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

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
    private AFRewardedLoadingDialogFragment mRewardedLoadingDialog;

    public RewardedLoadingProgressDefault(Context context) {
        mContext = context;
    }

    @Override
    public void showRewardedLoadingProgress() {
        mRewardedLoadingDialog = new AFRewardedLoadingDialogFragment();
        FragmentTransaction ft = ((AppCompatActivity) mContext).getSupportFragmentManager().beginTransaction();
        mRewardedLoadingDialog.show(ft, "");
    }

    @Override
    public void hideRewardedLoadingProgress() {
        mRewardedLoadingDialog.dismiss();
    }

}