package com.appyfurious.rating;

import com.appyfurious.db.AFRatingConfiguration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.realm.RealmList;

/**
 * RatingConfigParser.java
 * buyingprocessor_public
 * <p>
 * Created by o.davidovich on 12.09.2018.
 * <p>
 * Copyright © 2018 Appyfurious. All rights reserved.
 */


public class RatingConfigParser {

    public static final String ID = "ID";
    public static final String ACTION_TITLE = "action_title";
    public static final String SESSION_COUNT = "session_count";
    public static final String ACTION_COUNT = "action_count";
    public static final String ACTION_URL = "action_url";
    public static final String IS_ENABLED = "is_enabled";

    public String mOriginal;

    public int id;
    public String actionTitle;
    public int sessionCount;
    public int actionCount;
    public String actionUrl;
    public int isEnabled;

    private RealmList<AFRatingConfiguration> mRatingConfigurations;

    public RatingConfigParser(String original) {
        this.mOriginal = original;
        parse();
    }

    private void parse() {
        JSONArray source = null;
        try {
            source = new JSONArray(mOriginal);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (source != null) {
            for (int i = 0; i < source.length(); i++) {
                JSONObject ratingObj = null;
                try {
                    ratingObj = (JSONObject) source.get(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (ratingObj != null) {
                    try {
                        id = Integer.parseInt((String) ratingObj.get(ID));
                        actionTitle = (String) ratingObj.get(ACTION_TITLE);
                        sessionCount = (int) ratingObj.get(SESSION_COUNT);
                        actionCount = (int) ratingObj.get(ACTION_COUNT);
                        actionUrl = (String) ratingObj.get(ACTION_URL);
                        isEnabled = (int) ratingObj.get(IS_ENABLED);

                        if (mRatingConfigurations == null) {
                            mRatingConfigurations = new RealmList<>();
                        }

                        mRatingConfigurations.add(new AFRatingConfiguration(id, actionTitle,
                                sessionCount, actionCount, actionUrl, isEnabled));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
    }

    public RealmList<AFRatingConfiguration> getRatingConfigurations() {
        return mRatingConfigurations;
    }

}