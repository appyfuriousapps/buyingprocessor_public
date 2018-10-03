package com.appyfurious.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * AFNetworkManager.java
 * buyingprocessor_public
 * <p>
 * Created by o.davidovich on 03.10.2018.
 * <p>
 * Copyright © 2018 Appyfurious. All rights reserved.
 */


public class AFNetworkManager {

    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
