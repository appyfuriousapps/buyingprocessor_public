package com.appyfurious.billing;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.appyfurious.ad.AFAdManager;

/**
 * MyActivity.java
 * buyingprocessor_public
 * <p>
 * Created by o.davidovich on 24.07.2018.
 * <p>
 * Copyright © 2018 Appyfurious. All rights reserved.
 */

public class MyActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);
        AFAdManager.getInstance().requestInterstitial("EnterInActivity1");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        AFAdManager.getInstance().requestInterstitial("ExitFromActivity2");
    }

    public void onClickButton(View view) {
        Intent i = new Intent(this, My2Activity.class);
        startActivity(i);
    }

}
