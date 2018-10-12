package com.appyfurious.rating;

import com.appyfurious.ad.AFDataManager;
import com.appyfurious.analytics.Events;
import com.appyfurious.db.AFRatingConfiguration;
import com.appyfurious.db.AFRealmDatabase;
import com.appyfurious.db.AFSharedPreferencesManager;
import com.appyfurious.log.Logger;

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

    /**
     *  Rating events name
     */

    private static final String EVENT_NAME_RATING_CLICK = "Rating_click";
    private static final String EVENT_NAME_RATING_SHOW = "Rating_impression";

    /**
     *  Rating params
     */

    private static final String SESSION = "Session";
    private static final String ACTION_COUNT = "Action_count";
    private static final String CLICK = "Click";


    private static AFRatingManager mInstance;
    private AFRatingConfiguration mCurrentRatingConfiguration;


    public static AFRatingManager getInstance() {
        if (mInstance == null) {
            mInstance = new AFRatingManager();
        }

        return mInstance;
    }

    public void initialize() {}

    public AFRatingConfiguration requestRating(String actionTitle) {

        RealmResults<AFRatingConfiguration> ratingConfigs = AFRealmDatabase.getInstance()
                                                                           .getRatingConfigurations(actionTitle);
        int currentSessionCount = 0;
        if (ratingConfigs != null) {
            for (AFRatingConfiguration conf : ratingConfigs) {
                currentSessionCount = AFRealmDatabase.getInstance().incrementRatingActionCount(conf);
            }
        }
        
        mCurrentRatingConfiguration = AFRealmDatabase.getInstance().getActionEqualRatingConfigs(actionTitle,
                AFSharedPreferencesManager.getInstance().getSessionCount(), currentSessionCount,
                true, false);
        
        if (mCurrentRatingConfiguration != null) {
            onRatingShow();
            return mCurrentRatingConfiguration;
        } else {
            return null;
        }
    }

    private void onRatingShow() {
        Events.INSTANCE.logEventUniversal(AFDataManager.getInstance().getApplicationContext(),
                EVENT_NAME_RATING_SHOW,
                new Events.Param(SESSION, String.valueOf(AFDataManager.getInstance().getSessionCount())),
                new Events.Param(ACTION_COUNT, String.valueOf(mCurrentRatingConfiguration.getActionCount())));
    }

    public void onRatingClick(RatingAnswerType type) {
        if (type == RatingAnswerType.YES) {
            Events.INSTANCE.logEventUniversal(AFDataManager.getInstance().getApplicationContext(),
                    EVENT_NAME_RATING_CLICK,
                    new Events.Param(SESSION, String.valueOf(AFDataManager.getInstance().getSessionCount())),
                    new Events.Param(ACTION_COUNT, String.valueOf(mCurrentRatingConfiguration.getActionCount())),
                    new Events.Param(CLICK, RatingAnswerType.YES.name()));
        } else if (type == RatingAnswerType.NO) {
            Events.INSTANCE.logEventUniversal(AFDataManager.getInstance().getApplicationContext(),
                    EVENT_NAME_RATING_CLICK,
                    new Events.Param(SESSION, String.valueOf(AFDataManager.getInstance().getSessionCount())),
                    new Events.Param(ACTION_COUNT, String.valueOf(mCurrentRatingConfiguration.getActionCount())),
                    new Events.Param(CLICK, RatingAnswerType.NO.name()));
        } else {
            Logger.INSTANCE.logRating("Unknown Rating Answer type");
        }
    }

}