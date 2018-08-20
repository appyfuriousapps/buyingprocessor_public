package com.appyfurious.ad;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import java.util.Collection;
import java.util.List;

/**
 * RewardedCallback.java
 * buyingprocessor_public
 * <p>
 * Created by o.davidovich on 20.08.2018.
 * <p>
 * Copyright © 2018 Appyfurious. All rights reserved.
 */
public interface RewardedCallback<T> {

    void onRewardedVideoAdLoaded();

    void onRewardUser();

    void onRewardedVideoAdClosed();

}
