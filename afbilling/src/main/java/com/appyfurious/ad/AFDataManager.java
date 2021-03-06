package com.appyfurious.ad;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.arch.lifecycle.ProcessLifecycleOwner;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.appsee.Appsee;
import com.appyfurious.AFProductIdConfiguration;
import com.appyfurious.ad.parser.AdConfigParser;
import com.appyfurious.ad.parser.ProductIdsConfigParser;
import com.appyfurious.afbilling.AFStoreManager;
import com.appyfurious.db.AFAdsManagerConfiguration;
import com.appyfurious.db.AFRealmDatabase;
import com.appyfurious.db.AFSharedPreferencesManager;
import com.appyfurious.log.Logger;
import com.appyfurious.network.manager.AFNetworkManager;
import com.appyfurious.rating.AFRatingManager;
import com.appyfurious.rating.RatingConfigParser;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmChangeListener;
import io.realm.RealmList;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

/**
 * AFDataManager.java
 * buyingprocessor_public
 * <p>
 * Created by o.davidovich on 10.09.2018.
 * <p>
 * Copyright © 2018 Appyfurious. All rights reserved.
 */


public class AFDataManager {

    private static AFDataManager mInstance;

    private Context mApplicationContext;
    private FirebaseRemoteConfig mRemoteConfig;

    private boolean isDebug;


    public static synchronized AFDataManager getInstance() {
        if (mInstance == null) {
            mInstance = new AFDataManager();
        }

        return mInstance;
    }

    public void initialize(Context applicationContext, int remoteConfigDefaultFileLocation, boolean isDebug) {
        this.mApplicationContext = applicationContext;
        this.isDebug = isDebug;
        mRemoteConfig = FirebaseRemoteConfig.getInstance();
        mRemoteConfig.setDefaults(remoteConfigDefaultFileLocation);

        AFRealmDatabase.getInstance().initialize();
        AFSharedPreferencesManager.getInstance().initialize(mApplicationContext);

        ProcessLifecycleOwner.get().getLifecycle().addObserver(observer);
    }

    public void updateConfiguration() {
        FirebaseRemoteConfigSettings settings =
                new FirebaseRemoteConfigSettings.Builder().setDeveloperModeEnabled(false).build();
        mRemoteConfig.setConfigSettings(settings);

        mRemoteConfig.fetch(720)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Logger.INSTANCE.logDataManager("Fetch Succeeded");

                        // After config data is successfully fetched, it must be activated before newly fetched
                        // values are returned.
                        mRemoteConfig.activateFetched();

                        AdConfigParser parser = new AdConfigParser(mRemoteConfig // TODO check null
                                .getString("ads_config"), isDebug);

                        RatingConfigParser ratingParser = new RatingConfigParser(mRemoteConfig.getString("rating_config")); // TODO check null

                        if (!TextUtils.isEmpty(mRemoteConfig.getString("product_ids_config"))) {
                            ProductIdsConfigParser productIdsParser = new ProductIdsConfigParser(mRemoteConfig
                                    .getString("product_ids_config"));
                            AFRealmDatabase.getInstance().saveProductIds(productIdsParser
                                    .getProductIdConfigurations(), afProductIdConfigurations -> {
                                Logger.INSTANCE.logRatingIdConfigChanged("Product Id Config changed. New config: " + afProductIdConfigurations
                                        .toString());
                                List<String> productIds = new ArrayList<>();
                                for (AFProductIdConfiguration conf : afProductIdConfigurations) {
                                    if (!productIds.contains(conf.getValue())) {
                                        productIds.add(conf.getValue());
                                    }
                                }

                                AFStoreManager.INSTANCE.updateProducts(productIds);
                            });
                        }

                        AFAdsManagerConfiguration configuration = new AFAdsManagerConfiguration(parser
                                .getApplicationId(),
                                parser.getBannerId(), parser.getInterstitialId(), parser
                                .getRewardedVideoId(),
                                parser.getRewardedVideoWaitingTime(),
                                parser.getInterstitialsCountPerSession(), parser
                                .getInterstitialDelay(),
                                parser.getActions());

                        AFRealmDatabase.getInstance().saveAd(configuration, onChange);
                        AFRealmDatabase.getInstance()
                                .saveRating(ratingParser.getRatingConfigurations());
                        AFAdManager.getInstance().updateConfiguration(mApplicationContext, configuration);

                        AFRatingManager.getInstance().initialize();

                        if (!TextUtils.isEmpty(mRemoteConfig.getString("is_AppSee_enabled"))) {
                            try {
                                int isAppSeeEnabled = Integer.parseInt(mRemoteConfig.getString("is_AppSee_enabled"));
                                if (isAppSeeEnabled == 1) {
                                    AFSharedPreferencesManager.getInstance().putAppseeEnabled(true);
                                } else if (isAppSeeEnabled == 0) {
                                    AFSharedPreferencesManager.getInstance().putAppseeEnabled(false);
                                } else {
                                    Logger.INSTANCE.logAppSee("Unable to resolve Appsee config. Unknown value: " + isAppSeeEnabled);
                                }
                            } catch (Exception e) {
                                Logger.INSTANCE.logAppSee("Unable to resolve Appsee config. Caused by: " + e.getMessage());
                            }
                        }
                    } else {
                        Logger.INSTANCE.logDataManager("Fetch Failed");
                    }
                });
    }

    public Context getApplicationContext() {
        return mApplicationContext;
    }

    public String getProductIdForAction(String action) {
        return AFRealmDatabase.getInstance().getProductIdForAction(action);
    }

    public void setDefaultProductIdsConfiguration(RealmList<AFProductIdConfiguration> defConfigs) {
        AFRealmDatabase.getInstance().saveProductIds(defConfigs, afProductIdConfigurations -> {
            Logger.INSTANCE.logRatingIdConfigChanged("Product Id Default Config: " + defConfigs.toString());
            List<String> productIds = new ArrayList<>();
            for (AFProductIdConfiguration conf : afProductIdConfigurations) {
                if (!productIds.contains(conf.getValue())) {
                    productIds.add(conf.getValue());
                }
            }

            AFStoreManager.INSTANCE.updateProducts(productIds);
        });
    }

    public int getSessionCount() {
        return AFSharedPreferencesManager.getInstance().getSessionCount();
    }

    public void entryPoint() {
        boolean isAFAppseeEnabled = AFSharedPreferencesManager.getInstance().isAFAppseeEnabled();
        if (isAFAppseeEnabled) {
            Appsee.start();
            Logger.INSTANCE.logAppSee("Initializing Appsee for this app...");
        } else {
            Logger.INSTANCE.logAppSee("AppSee disabled by Remote Config.");
        }
    }

    private RealmChangeListener<AFAdsManagerConfiguration> onChange = configuration ->
            Logger.INSTANCE.logDbChange("AFRealm Configuration Changed. New config: " + configuration.toString());

    private Function2<? super Context, ? super Boolean, Unit> networkListener = (Function2<Context, Boolean, Unit>) (context, isOnline) -> {
        if (isOnline) {
            updateConfiguration();
        }
        Logger.INSTANCE.notify("networkListener isOnline: " + isOnline);
        return Unit.INSTANCE;
    };

    private LifecycleObserver observer = new LifecycleObserver() {
        @OnLifecycleEvent(Lifecycle.Event.ON_START)
        public void onMoveToForeground() {
            Logger.INSTANCE.logMoveToForeground("App is going to foreground..");
            AFSharedPreferencesManager.getInstance().incrementSessionCount();
            updateConfiguration();
            AFAdManager.getInstance().onMoveToForeground();
            AFNetworkManager.INSTANCE.addListener(networkListener);
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        public void onMoveToBackground() {
            Logger.INSTANCE.logMoveToBackground("App is going to background..");
            AFRealmDatabase.getInstance().resetInterstitialPerSession();
            AFRealmDatabase.getInstance().resetActionCountOnRatingConfigs();
            AFNetworkManager.INSTANCE.removeListener(networkListener);
        }
    };
}