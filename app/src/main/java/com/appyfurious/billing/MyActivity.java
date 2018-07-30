package com.appyfurious.billing;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;

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
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getApplicationAdManager().showInterstitial();
    }

    public void onClickButton(View view) {
        Intent i = new Intent(this, My2Activity.class);
        startActivity(i);
    }

}
