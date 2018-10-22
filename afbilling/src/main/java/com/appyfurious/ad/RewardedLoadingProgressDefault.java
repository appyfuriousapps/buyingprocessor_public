package com.appyfurious.ad;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.CoordinatorLayout;
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
    private ViewGroup mRootView;
    private ProgressBar mProgressBar;

    public RewardedLoadingProgressDefault(Context context) {
        mContext = context;
        mProgressBar = new ProgressBar(mContext, null, R.style.RewardedProgressStyle);
        mProgressBar.setIndeterminate(true);
    }

    @Override
    public void showRewardedLoadingProgress() {
        if (mContext instanceof AppCompatActivity) {
            AppCompatActivity rewardedActivity = (AppCompatActivity) mContext;
            mRootView = rewardedActivity.findViewById(android.R.id.content);

            ViewGroup.LayoutParams lp = mRootView.getLayoutParams();
            lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            lp.height = 6;
            mRootView.addView(mProgressBar);

            if (lp instanceof LinearLayout.LayoutParams) {
                LinearLayout.LayoutParams llp = ((LinearLayout.LayoutParams) lp);
                llp.gravity = Gravity.TOP;

            } else if (lp instanceof FrameLayout.LayoutParams) {
                FrameLayout.LayoutParams flp = ((FrameLayout.LayoutParams) lp);
                flp.gravity = Gravity.TOP;

            } else if (lp instanceof RelativeLayout.LayoutParams) {
                RelativeLayout.LayoutParams rlp = ((RelativeLayout.LayoutParams) lp);
                rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP);

            } else if (lp instanceof GridLayout.LayoutParams) {
                GridLayout.LayoutParams glp = ((GridLayout.LayoutParams) lp);
                glp.setGravity(Gravity.TOP);

            } else if (lp instanceof ConstraintLayout.LayoutParams) {
//                ConstraintSet constraintSet = new ConstraintSet();
//                constraintSet.clone((ConstraintLayout) adContainer);
//                constraintSet.connect(mAdView.getId(), ConstraintSet.BOTTOM, adContainer.getId(),
//                        ConstraintSet.BOTTOM, 0);
//                constraintSet.connect(mAdView.getId(), ConstraintSet.LEFT, adContainer.getId(),
//                        ConstraintSet.LEFT, 0);
//                constraintSet.connect(mAdView.getId(), ConstraintSet.RIGHT, adContainer.getId(),
//                        ConstraintSet.RIGHT, 0);
//                constraintSet.applyTo((ConstraintLayout) adContainer);
            } else if (lp instanceof CoordinatorLayout.LayoutParams) {
                CoordinatorLayout.LayoutParams clp = ((CoordinatorLayout.LayoutParams) lp);
                clp.gravity = Gravity.TOP;
            }

            mProgressBar.setLayoutParams(lp);
        }

    }

    @Override
    public void hideRewardedLoadingProgress() {
        mRootView.removeView(mProgressBar);
    }

}