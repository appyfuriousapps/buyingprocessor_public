package com.appyfurious.db;

import android.support.annotation.NonNull;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmResults;

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


    public void initialize() {
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("AFManager.realm")
                .modules(new LibraryModule())
                .build();

        Realm.deleteRealm(config);
        realm = Realm.getInstance(config);
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
        if (config != null && listener != null) {
            config.addChangeListener(listener);
        }
    }

    public void saveRating(final RealmList<AFRatingConfiguration> ratingConfigurations) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                for (AFRatingConfiguration configuration : ratingConfigurations) {
                    realm.copyToRealmOrUpdate(configuration);
                }
            }
        });
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

    public RealmResults<AFRatingConfiguration> getRatingConfigurations(String actionTitle) {
        RealmResults<AFRatingConfiguration> configurations = realm
                .where(AFRatingConfiguration.class)
                .equalTo("actionTitle", actionTitle)
                .equalTo("isCompleted", false).findAll();
        return configurations;
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

    public void resetActionCountOnRatingConfigs() {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                RealmResults<AFRatingConfiguration> configs = realm
                        .where(AFRatingConfiguration.class).findAll();
                if (configs != null) {
                    for (AFRatingConfiguration conf : configs) {
                        conf.setCurrentActionCount(0);
                    }
                }
            }
        });
    }

    public int incrementRatingActionCount(final AFRatingConfiguration configuration) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                configuration.setCurrentActionCount(configuration.getCurrentActionCount() + 1);
                realm.copyToRealmOrUpdate(configuration);
            }
        });

        return configuration.getCurrentActionCount();
    }

    public AFRatingConfiguration getActionEqualRatingConfigs(final String actionTitle, final int sessionCount,
                                                             final int actionCount, final boolean isEnabled,
                                                             final boolean isCompleted) {

        final AFRatingConfiguration[] afRatingConfiguration = new AFRatingConfiguration[1];
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                RealmResults<AFRatingConfiguration> configs = realm
                        .where(AFRatingConfiguration.class)
                        .equalTo("actionTitle", actionTitle)
                        .equalTo("sessionCount", sessionCount)
                        .equalTo("actionCount", actionCount)
                        .equalTo("isEnabled", isEnabled ? 1 : 0)
                        .equalTo("isCompleted", isCompleted).findAll();


                for (AFRatingConfiguration conf : configs) {
                    if (conf.isActionCountEqualCurrentActionCount()) {
                        conf.setCompleted(true);
                        conf.setCurrentActionCount(0);
                        realm.copyToRealmOrUpdate(conf);
                        afRatingConfiguration[0] = conf;
                        return;

                    }
                }
            }
        });

        return afRatingConfiguration[0];
    }

}