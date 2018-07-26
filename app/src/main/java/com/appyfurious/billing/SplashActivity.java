package com.appyfurious.billing;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;

/**
 * SplashActivity.java
 * buyingprocessor_public
 * <p>
 * Created by o.davidovich on 25.07.2018.
 * <p>
 * Copyright © 2018 Appyfurious. All rights reserved.
 */

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getApplicationAdManager().setPremium(false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashActivity.this, MyActivity.class);
                startActivity(i);
            }
        }, 500);
    }
}
