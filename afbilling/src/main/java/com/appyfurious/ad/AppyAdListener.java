package com.appyfurious.ad;

import com.google.android.gms.ads.AdListener;

/**
 * AppyAdListener.java
 * buyingprocessor_public
 * <p>
 * Created by o.davidovich on 24.07.2018.
 * <p>
 * Copyright © 2018 Appyfurious. All rights reserved.
 */

public class AppyAdListener extends AdListener {

    private AdDownloadingCallback mCallback;

    public AppyAdListener(AdDownloadingCallback mCallback) {
        this.mCallback = mCallback;
    }

    @Override
    public void onAdFailedToLoad(int i) {
        if (mCallback != null) {
            mCallback.onAdLoadFailure();
        }
    }

    @Override
    public void onAdLoaded() {
        if (mCallback != null) {
            mCallback.onAdLoadSuccess();
        }
    }

}
