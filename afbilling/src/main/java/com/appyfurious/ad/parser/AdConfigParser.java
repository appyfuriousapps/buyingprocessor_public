package com.appyfurious.ad.parser;

import com.appyfurious.ad.DebugConfig;
import com.appyfurious.afbilling.BuildConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

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
    private static final String INTERSTITIALS_COUNT_PER_SESSION = "interstitials_count_per_session";
    private static final String INTERSTITIALS_DELAY = "interstitials_delay";

    private String mOriginal;
    private ParserListener mParserListener;
    private boolean isDebug;

    public AdConfigParser(String original, ParserListener listener, boolean isDebug) {
        this.mOriginal = original;
        this.mParserListener = listener;
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
                    appId = DebugConfig.DEBUG_APP_ID;
                } else {
                    appId = (String) source.get(APPLICATION_ID);
                }
                mParserListener.applicationId(appId);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                String bannerId;
                if (isDebug) {
                    bannerId = DebugConfig.DEBUG_BANNER_ID;
                } else {
                    bannerId = (String) source.get(BANNER_ID);
                }
                mParserListener.bannerId(bannerId);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                String interstitialId;
                if (isDebug) {
                    interstitialId = DebugConfig.DEBUG_INTERSTITIAL_ID;
                } else {
                    interstitialId = (String) source.get(INTERSTITIAL_ID);
                }
                mParserListener.interstitialId(interstitialId);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                String rewardedVideoId;
                if (isDebug) {
                    rewardedVideoId = DebugConfig.DEBUG_REWARDED_VIDEO_ID;
                } else {
                    rewardedVideoId = (String) source.get(REWARDED_VIDEO_ID);
                }
                mParserListener.rewardedVideoId(rewardedVideoId);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                int interstitialsCountPerSession = (int) source
                        .get(INTERSTITIALS_COUNT_PER_SESSION);
                mParserListener.interstitialsCountPerSession(interstitialsCountPerSession);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                int interstitialDelay = (int) source.get(INTERSTITIALS_DELAY);
                mParserListener.interstitialDelay(interstitialDelay);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                JSONArray jsonArray = source.getJSONArray("actions");
                Map<String, Boolean> actions = new HashMap<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = (JSONObject) jsonArray.get(i);
                    int bool = (Integer) obj.get("is_enabled");
                    if (bool == 0 || bool == 1) {
                        actions.put((String) obj.get("action_title"), bool == 1);
                    }
                }
                mParserListener.actionsMap(actions);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    public interface ParserListener {

        void applicationId(String applicationId);

        void bannerId(String bannerId);

        void interstitialId(String interstitialId);

        void rewardedVideoId(String rewardedVideoId);

        void interstitialsCountPerSession(int interstitialsCountPerSession);

        void interstitialDelay(int interstitialDelay);

        void actionsMap(Map<String, Boolean> actions);
    }

}
