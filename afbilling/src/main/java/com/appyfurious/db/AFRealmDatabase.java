package com.appyfurious.db;

import android.content.Context;
import android.support.annotation.NonNull;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmConfiguration;

/**
 * AFRealmDatabase.java
 * buyingprocessor_public
 * <p>
 * Created by o.davidovich on 10.09.2018.
 * <p>
 * Copyright © 2018 Appyfurious. All rights reserved.
 */


public class AFRealmDatabase {


    private static AFRealmDatabase mInstance;
    private Realm realm;


    public void initialize(Context applicationContext) {
        Realm.init(applicationContext);
        RealmConfiguration config = new RealmConfiguration.Builder().name("AFManager.realm")
                                                                    .build();
        Realm.setDefaultConfiguration(config);
        realm = Realm.getDefaultInstance();
    }

    public static AFRealmDatabase getInstance() {
        if (mInstance == null) {
            mInstance = new AFRealmDatabase();
        }

        return mInstance;
    }

    private AFRealmDatabase() { }

    public void saveAd(final AFAdsManagerConfiguration configuration,
                       RealmChangeListener<AFAdsManagerConfiguration> listener) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                realm.copyToRealmOrUpdate(configuration);
            }
        });

        AFAdsManagerConfiguration config = realm.where(AFAdsManagerConfiguration.class).findFirst();
        if (config != null) {
            config.addChangeListener(listener);
        }
    }

    public void setInterstitialsLastShowDate(final long lastShowDate) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                AFAdsManagerConfiguration configuration = realm
                        .where(AFAdsManagerConfiguration.class).findFirst();
                if (configuration != null) {
                    configuration.setInterstitialsLastShowDate(lastShowDate);
                    realm.insertOrUpdate(configuration);
                }
            }
        });
    }

    public void incrementCurrentInterstitialCountPerSession() {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                AFAdsManagerConfiguration configuration = realm
                        .where(AFAdsManagerConfiguration.class).findFirst();
                if (configuration != null) {
                    configuration.incrementCurrentInterstitialCountPerSession();
                    realm.insertOrUpdate(configuration);
                }
            }
        });
    }

    public AFAdsManagerConfiguration getAdsConfiguration() {
        AFAdsManagerConfiguration config = realm.where(AFAdsManagerConfiguration.class).findFirst();
        return config;
    }

    public void resetInterstitialPerSession() {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                AFAdsManagerConfiguration configuration = realm
                        .where(AFAdsManagerConfiguration.class).findFirst();
                if (configuration != null) {
                    configuration.resetCurrentInterstitialCountPerSession();
                    realm.insertOrUpdate(configuration);
                }
            }
        });
    }

}