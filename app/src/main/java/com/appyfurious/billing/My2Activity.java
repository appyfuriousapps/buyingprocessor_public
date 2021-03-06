package com.appyfurious.billing;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.appyfurious.ad.AFAdManager;
import com.appyfurious.ad.RewardedCallback;
import com.appyfurious.db.AFRatingConfiguration;
import com.appyfurious.rating.AFRatingManager;

/**
 * My2Activity.java
 * buyingprocessor_public
 * <p>
 * Created by o.davidovich on 25.07.2018.
 * <p>
 * Copyright © 2018 Appyfurious. All rights reserved.
 */

public class My2Activity extends BaseActivity implements View.OnClickListener, RewardedCallback {

    private Button mButton;
    private TextView mTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example_1);

        mButton = findViewById(R.id.button3);
        mTextView = findViewById(R.id.textView);
        mButton.setOnClickListener(this);
        AFAdManager.getInstance().loadRewardedVideoAd(this, this, "");

        AFRatingConfiguration conf = AFRatingManager.getInstance().requestRating("Enter in Activity 2");
        if (conf != null) {
            Toast.makeText(this, "Rating Show", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AFRatingConfiguration configuration = AFRatingManager.getInstance().requestRating("Exit from Activity 2");
        if (configuration != null) {
            Toast.makeText(this, "Rating Show", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View view) {
        AFAdManager.getInstance().requestRewardedVideoAd();
    }

    @Override
    public void onRewardedVideoAdLoaded() {
        if (mButton != null) {
            mButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRewardUser() {
        mTextView.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
    }

    @Override
    public void onRewardedVideoAdClosed() {

    }
}
