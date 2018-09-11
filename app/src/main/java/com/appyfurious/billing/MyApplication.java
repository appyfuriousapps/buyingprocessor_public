package com.appyfurious.billing;

import android.app.Application;

import com.appyfurious.ad.AFDataManager;
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
        Logger.Companion.init(BuildConfig.DEBUG, true);

        AFDataManager.getInstance().initialize(this, R.xml.remote_config_defaults);
        AFDataManager.getInstance().updateConfiguration(BuildConfig.DEBUG);
    }

}
