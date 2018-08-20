package com.appyfurious.billing;

import android.app.Application;
import android.os.Build;
import android.widget.Toast;

import com.appyfurious.ad.AdManager;
import com.appyfurious.ad.AdManagerApplication;

/**
 * MyApplication.java
 * buyingprocessor_public
 * <p>
 * Created by o.davidovich on 24.07.2018.
 * <p>
 * Copyright © 2018 Appyfurious. All rights reserved.
 */

public class MyApplication extends Application implements AdManagerApplication {

    private AdManager adManager;

    @Override
    public void onCreate() {
        super.onCreate();

        adManager = new AdManager(this);
    }

    @Override
    public AdManager getAdManager() {
        return adManager;
    }

}
