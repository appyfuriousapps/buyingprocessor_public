package com.appyfurious.db;

import android.content.Context;
import android.content.SharedPreferences;

import com.appyfurious.log.Logger;

/**
 * AFSharedPreferencesManager.java
 * buyingprocessor_public
 * <p>
 * Created by o.davidovich on 11.09.2018.
 * <p>
 * Copyright © 2018 Appyfurious. All rights reserved.
 */

public class AFSharedPreferencesManager {

    private static final String AF_PREFERENCES_SESSION_COUNT_KEY = "af_session_count";
    private static final String AF_PREFERENCES_APPSEE_ENABLED = "af_appsee_enabled";

    private static AFSharedPreferencesManager mSharedPreferencesManager;
    private SharedPreferences mSharedPreferences;


    public void initialize(Context applicationContext) {
        mSharedPreferences = applicationContext.getSharedPreferences("AFPreferences", Context.MODE_PRIVATE);
    }

    public static AFSharedPreferencesManager getInstance() {
        if (mSharedPreferencesManager == null) {
            mSharedPreferencesManager = new AFSharedPreferencesManager();
        }

        return mSharedPreferencesManager;
    }

    public void incrementSessionCount() {
        int sessionCount = mSharedPreferences.getInt(AF_PREFERENCES_SESSION_COUNT_KEY, 0);
        mSharedPreferences.edit().putInt(AF_PREFERENCES_SESSION_COUNT_KEY, sessionCount + 1)
                          .apply();
        Logger.INSTANCE.logSharedPreferences("Session count = " + getSessionCount());
    }

    public int getSessionCount() {
        return mSharedPreferences.getInt(AF_PREFERENCES_SESSION_COUNT_KEY, 0);
    }

    public void putAppseeEnabled(boolean isEnabled) {
        mSharedPreferences.edit().putBoolean(AF_PREFERENCES_APPSEE_ENABLED, isEnabled).apply();
        Logger.INSTANCE.logSharedPreferences("AFAppsee enabled: " + isEnabled);
    }

    public boolean isAFAppseeEnabled() {
        return mSharedPreferences.getBoolean(AF_PREFERENCES_APPSEE_ENABLED, true);
    }

}