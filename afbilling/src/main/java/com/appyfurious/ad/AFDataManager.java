package com.appyfurious.ad;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.arch.lifecycle.ProcessLifecycleOwner;
import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.appyfurious.ad.parser.AdConfigParser;
import com.appyfurious.db.AFAdsManagerConfiguration;
import com.appyfurious.db.AFRealmDatabase;
import com.appyfurious.db.AFSharedPreferencesManager;
import com.appyfurious.log.Logger;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import io.realm.RealmChangeListener;

/**
 * AFDataManager.java
 * buyingprocessor_public
 * <p>
 * Created by o.davidovich on 10.09.2018.
 * <p>
 * Copyright © 2018 Appyfurious. All rights reserved.
 */


public class AFDataManager implements RealmChangeListener<AFAdsManagerConfiguration>, LifecycleObserver {

    private static AFDataManager mInstance;

    private Context mApplicationContext;
    private FirebaseRemoteConfig mRemoteConfig;


    public static synchronized AFDataManager getInstance() {
        if (mInstance == null) {
            mInstance = new AFDataManager();
        }

        return mInstance;
    }

    public void initialize(Context applicationContext, int remoteConfigFileLocation) {
        this.mApplicationContext = applicationContext;
        mRemoteConfig = FirebaseRemoteConfig.getInstance();
        mRemoteConfig.setDefaults(remoteConfigFileLocation);

        AFRealmDatabase.getInstance().initialize(mApplicationContext);
        AFSharedPreferencesManager.getInstance().initialize(mApplicationContext);

        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }

    public void updateConfiguration(final boolean isDebug) {
        FirebaseRemoteConfigSettings settings =
                new FirebaseRemoteConfigSettings.Builder().setDeveloperModeEnabled(false).build();
        mRemoteConfig.setConfigSettings(settings);

        mRemoteConfig.fetch(720)
                     .addOnCompleteListener(new OnCompleteListener<Void>() {
                         @Override
                         public void onComplete(@NonNull Task<Void> task) {
                             if (task.isSuccessful()) {
                                 Toast.makeText(mApplicationContext, "Fetch Succeeded",
                                         Toast.LENGTH_SHORT).show();

                                 // After config data is successfully fetched, it must be activated before newly fetched
                                 // values are returned.
                                 mRemoteConfig.activateFetched();

                                 AdConfigParser parser = new AdConfigParser(mRemoteConfig
                                         .getString("ads_config"), isDebug);

                                 AFAdsManagerConfiguration configuration = new AFAdsManagerConfiguration(parser.getApplicationId(),
                                         parser.getBannerId(), parser.getInterstitialId(), parser.getRewardedVideoId(),
                                         parser.getInterstitialsCountPerSession(), parser.getInterstitialDelay(),
                                         parser.getActions());

                                 AFRealmDatabase.getInstance().initialize(mApplicationContext);
                                 AFRealmDatabase.getInstance().saveAd(configuration, AFDataManager.this);
                                 AFAdManager.getInstance().initialize(mApplicationContext);

                             } else {
                                 Toast.makeText(mApplicationContext, "Fetch Failed",
                                         Toast.LENGTH_SHORT).show();
                             }
                         }
                     });
    }

    @Override
    public void onChange(@NonNull AFAdsManagerConfiguration configuration) {
        Logger.Companion.logDbChange("AFRealm Configuration Changed. New config: " + configuration.toString());
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onMoveToForeground() {
        Logger.Companion.logMoveToForeground("App is going to foreground..");
        AFSharedPreferencesManager.getInstance().incrementSessionCount();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onMoveToBackground() {
        Logger.Companion.logMoveToBackground("App is going to background..");
        AFRealmDatabase.getInstance().resetInterstitialPerSession();
    }

}