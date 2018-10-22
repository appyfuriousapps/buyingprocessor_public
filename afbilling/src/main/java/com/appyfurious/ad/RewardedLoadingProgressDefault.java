package com.appyfurious.ad;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.appyfurious.afbilling.R;

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
    private FullScreenDialog mRewardedLoadingDialog;

    public RewardedLoadingProgressDefault(Context context) {
        mContext = context;
    }

    @Override
    public void showRewardedLoadingProgress() {
        mRewardedLoadingDialog = new FullScreenDialog();
        FragmentTransaction ft = ((AppCompatActivity) mContext).getSupportFragmentManager().beginTransaction();
        mRewardedLoadingDialog.show(ft, "");
    }

    @Override
    public void hideRewardedLoadingProgress() {
        mRewardedLoadingDialog.dismiss();
    }

}