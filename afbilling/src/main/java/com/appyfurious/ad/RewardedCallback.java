package com.appyfurious.ad;

/**
 * RewardedCallback.java
 * buyingprocessor_public
 * <p>
 * Created by o.davidovich on 20.08.2018.
 * <p>
 * Copyright © 2018 Appyfurious. All rights reserved.
 */

public interface RewardedCallback {

    void onRewardedVideoAdLoaded();

    void onRewardUser();

    /**
     *
     *  This method fires in 2 cases:
     *  1) When the user closed the rewarded video ad.
     *  2) When the user go to the ad and then, go back to the app.
     *
     */

    void onRewardedVideoAdClosed();

}
