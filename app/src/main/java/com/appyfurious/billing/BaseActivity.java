package com.appyfurious.billing;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.appyfurious.ad.AFAdManager;

/**
 * BaseActivity.java
 * buyingprocessor_public
 * <p>
 * Created by o.davidovich on 24.07.2018.
 * <p>
 * Copyright © 2018 Appyfurious. All rights reserved.
 */

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AFAdManager.getInstance().initBannerContainer(this);
    }

}
