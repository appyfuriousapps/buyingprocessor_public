package com.appyfurious.ad;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.appyfurious.afbilling.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

/**
 * AdManager.java
 * buyingprocessor_public
 * <p>
 * Created by o.davidovich on 24.07.2018.
 * <p>
 * Copyright © 2018 Appyfurious. All rights reserved.
 */

public class AdManager implements AdDownloadingCallback {

    private AdView mAdView;
    private LinearLayout mAdContainer;
    private boolean isAdSuccessfullyDownloaded;

    public void initBannerAd(Context context, String bannerAdKey) {
        mAdView = new AdView(context);
        mAdView.setAdSize(AdSize.BANNER);
        mAdView.setAdUnitId(bannerAdKey);

        AdRequest adRequest = new AdRequest.Builder().build();

        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AppyAdListener(this));
    }

    public void initBannerContainer(AppCompatActivity activity) {
        mAdContainer =  activity.findViewById(R.id.ad_container);
        if (mAdContainer != null) {
            if (isAdSuccessfullyDownloaded) {
                mAdContainer.setVisibility(View.VISIBLE);
                loadAd(mAdContainer);
            } else {
                mAdContainer.setVisibility(View.GONE);
            }
        }
    }

    public void loadAd(ViewGroup adContainer) {

        if (mAdView.getParent() != null) {
            ViewGroup tempVg = (ViewGroup) mAdView.getParent();
            tempVg.removeView(mAdView);
        }

        adContainer.addView(mAdView);
    }

    @Override
    public void onAdLoadSuccess() {
        isAdSuccessfullyDownloaded = true;
    }

    @Override
    public void onAdLoadFailure() {
        isAdSuccessfullyDownloaded = false;
    }

    public boolean isAdSuccessfullyDownloaded() {
        return isAdSuccessfullyDownloaded;
    }

}
