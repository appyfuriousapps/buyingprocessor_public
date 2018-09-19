package com.appyfurious.ad;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.arch.lifecycle.ProcessLifecycleOwner;
import android.content.Context;
import android.support.annotation.NonNull;

import com.appyfurious.ad.parser.AdConfigParser;
import com.appyfurious.db.AFAdsManagerConfiguration;
import com.appyfurious.db.AFRealmDatabase;
import com.appyfurious.db.AFSharedPreferencesManager;
import com.appyfurious.log.Logger;
import com.appyfurious.rating.AFRatingManager;
import com.appyfurious.rating.RatingConfigParser;
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

    public void initialize(Context applicationContext, int remoteConfigDefaultFileLocation) {
        this.mApplicationContext = applicationContext;
        mRemoteConfig = FirebaseRemoteConfig.getInstance();
        mRemoteConfig.setDefaults(remoteConfigDefaultFileLocation);

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
                                 Logger.INSTANCE.logDataManager("Fetch Succeeded");

                                 // After config data is successfully fetched, it must be activated before newly fetched
                                 // values are returned.
                                 mRemoteConfig.activateFetched();

                                 AdConfigParser parser = new AdConfigParser(mRemoteConfig // TODO check null
                                         .getString("ads_config"), isDebug);

                                 RatingConfigParser ratingParser = new RatingConfigParser(mRemoteConfig.getString("rating_config")); // TODO check null

                                 AFAdsManagerConfiguration configuration = new AFAdsManagerConfiguration(parser.getApplicationId(),
                                         parser.getBannerId(), parser.getInterstitialId(), parser.getRewardedVideoId(),
                                         parser.getInterstitialsCountPerSession(), parser.getInterstitialDelay(),
                                         parser.getActions());

                                 AFRealmDatabase.getInstance().saveAd(configuration, AFDataManager.this);
                                 AFRealmDatabase.getInstance().saveRating(ratingParser.getRatingConfigurations());
                                 AFAdManager.getInstance().updateConfiguration(mApplicationContext, configuration);
                                 AFRatingManager.getInstance().initialize();

                             } else {
                                 Logger.INSTANCE.logDataManager("Fetch Failed");
                             }
                         }
                     });
    }

    @Override
    public void onChange(@NonNull AFAdsManagerConfiguration configuration) {
        Logger.INSTANCE.logDbChange("AFRealm Configuration Changed. New config: " + configuration.toString());
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onMoveToForeground() {
        Logger.INSTANCE.logMoveToForeground("App is going to foreground..");
        AFSharedPreferencesManager.getInstance().incrementSessionCount();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onMoveToBackground() {
        Logger.INSTANCE.logMoveToBackground("App is going to background..");
        AFRealmDatabase.getInstance().resetInterstitialPerSession();
        AFRealmDatabase.getInstance().resetActionCountOnRatingConfigs();
    }

}