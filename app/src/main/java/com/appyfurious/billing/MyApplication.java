package com.appyfurious.billing;

import android.app.Application;

import com.appyfurious.ad.AFAdManager;
import com.appyfurious.ad.AFDataManager;
import com.appyfurious.db.AFAdsManagerConfiguration;
import com.appyfurious.log.Logger;

/**
 * MyApplication.java
 * buyingprocessor_public
 * <p>
 * Created by o.davidovich on 24.07.2018.
 * <p>
 * Copyright © 2018 Appyfurious. All rights reserved.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.INSTANCE.init(BuildConfig.DEBUG, true);

        AFDataManager.getInstance().initialize(this, R.xml.remote_config_defaults);

        AFAdsManagerConfiguration configuration = new AFAdsManagerConfiguration();
        configuration.setApplicationId("ca-app-pub-5995390255785257~5457813889");
        configuration.setBannerId("ca-app-pub-5995390255785257/1917066262");
        configuration.setRewardedVideoId("ca-app-pub-5995390255785257/2990474781");
        configuration.setInterstitialId("ca-app-pub-5995390255785257/5389407412");
        configuration.setInterstitialsDelay(60);
        configuration.setInterstitialsCountPerSession(1);
        configuration.setInterstitialsLastShowDate(0);

        AFAdManager.getInstance().initialize(this, configuration, null);

        AFDataManager.getInstance().updateConfiguration(BuildConfig.DEBUG);
    }

}
