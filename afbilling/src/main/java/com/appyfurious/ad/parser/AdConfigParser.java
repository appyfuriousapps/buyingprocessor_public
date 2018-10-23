package com.appyfurious.ad.parser;

import com.appyfurious.ad.DebugConfig;
import com.appyfurious.db.Action;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.realm.RealmList;

/**
 * AdConfigParser.java
 * buyingprocessor_public
 * <p>
 * Created by o.davidovich on 17.08.2018.
 * <p>
 * Copyright © 2018 Appyfurious. All rights reserved.
 */

public class AdConfigParser {

    private static final String APPLICATION_ID = "application_id";
    private static final String BANNER_ID = "banner_id";
    private static final String INTERSTITIAL_ID = "interstitial_id";
    private static final String REWARDED_VIDEO_ID = "rewarded_video_id";
    private static final String REWARDED_VIDEO_WAITING_TIME = "rewarded_video_waiting_time";
    private static final String INTERSTITIALS_COUNT_PER_SESSION = "interstitials_count_per_session";
    private static final String INTERSTITIALS_DELAY = "interstitials_delay";

    private String mOriginal;

    private String applicationId;
    private String bannerId;
    private String interstitialId;
    private String rewardedVideoId;
    private int rewardedVideoWaitingTime;
    private int interstitialsCountPerSession;
    private int interstitialDelay;
    private RealmList<Action> actions;
    private boolean isDebug;

    public AdConfigParser(String original, boolean isDebug) {
        this.mOriginal = original;
        this.isDebug = isDebug;
        parse();
    }

    private void parse() {
        JSONObject source = null;
        try {
            source = new JSONObject(mOriginal);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (source != null) {
            try {
                String appId;
                if (isDebug) {
                    applicationId = DebugConfig.DEBUG_APP_ID;
                } else {
                    applicationId = (String) source.get(APPLICATION_ID);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                if (isDebug) {
                    bannerId = DebugConfig.DEBUG_BANNER_ID;
                } else {
                    bannerId = (String) source.get(BANNER_ID);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                if (isDebug) {
                    interstitialId = DebugConfig.DEBUG_INTERSTITIAL_ID;
                } else {
                    interstitialId = (String) source.get(INTERSTITIAL_ID);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                if (isDebug) {
                    rewardedVideoId = DebugConfig.DEBUG_REWARDED_VIDEO_ID;
                } else {
                    rewardedVideoId = (String) source.get(REWARDED_VIDEO_ID);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                rewardedVideoWaitingTime = (int) source.get(REWARDED_VIDEO_WAITING_TIME);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                interstitialsCountPerSession = (int) source
                        .get(INTERSTITIALS_COUNT_PER_SESSION);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                interstitialDelay = (int) source.get(INTERSTITIALS_DELAY);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                JSONArray jsonArray = source.getJSONArray("actions");
                actions = new RealmList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = (JSONObject) jsonArray.get(i);
                    int actionId = Integer.parseInt((String) obj.get("ID"));
                    String  actionTitle = (String) obj.get("action_title");
                    int isEnabled = (int) obj.get("is_enabled");
                    actions.add(new Action(actionId, actionTitle, isEnabled));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public String getApplicationId() {
        return applicationId;
    }

    public String getBannerId() {
        return bannerId;
    }

    public String getInterstitialId() {
        return interstitialId;
    }

    public String getRewardedVideoId() {
        return rewardedVideoId;
    }

    public int getRewardedVideoWaitingTime() {
        return rewardedVideoWaitingTime;
    }

    public int getInterstitialsCountPerSession() {
        return interstitialsCountPerSession;
    }

    public int getInterstitialDelay() {
        return interstitialDelay;
    }

    public RealmList<Action> getActions() {
        return actions;
    }

}
