package com.appyfurious.rating;

import com.appyfurious.db.AFRatingConfiguration;
import com.appyfurious.db.AFRealmDatabase;
import com.appyfurious.db.AFSharedPreferencesManager;

import io.realm.RealmResults;

/**
 * AFRatingManager.java
 * buyingprocessor_public
 * <p>
 * Created by o.davidovich on 13.09.2018.
 * <p>
 * Copyright © 2018 Appyfurious. All rights reserved.
 */

public class AFRatingManager {

    private static AFRatingManager mInstance;


    public static AFRatingManager getInstance() {
        if (mInstance == null) {
            mInstance = new AFRatingManager();
        }

        return mInstance;
    }

    public void initialize() {

    }

    public AFRatingConfiguration requestRating(String actionTitle) {

        RealmResults<AFRatingConfiguration> ratingConfigs = AFRealmDatabase.getInstance()
                                                                           .getRatingConfigurations(actionTitle);
        int currentSessionCount = 0;
        if (ratingConfigs != null) {
            for (AFRatingConfiguration conf : ratingConfigs) {
                currentSessionCount = AFRealmDatabase.getInstance().incrementRatingActionCount(conf);
            }
        }

        return AFRealmDatabase.getInstance().getActionEqualRatingConfigs(actionTitle,
                AFSharedPreferencesManager.getInstance().getSessionCount(), currentSessionCount,
                true, false);
    }

}