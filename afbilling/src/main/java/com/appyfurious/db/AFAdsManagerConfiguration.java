package com.appyfurious.db;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * AFAdsManagerConfiguration.java
 * buyingprocessor_public
 * <p>
 * Created by o.davidovich on 10.09.2018.
 * <p>
 * Copyright © 2018 Appyfurious. All rights reserved.
 */
public class AFAdsManagerConfiguration extends RealmObject {

    @PrimaryKey
    private int id;
    private String applicationId;
    private String bannerId;
    private String interstitialId;
    private String rewardedVideoId;
    private int interstitialsCountPerSession;
    private int interstitialsDelay;
    private RealmList<Action> actions;

    // optional
    private double interstitialsLastShowDate;
    private int currentInterstitialCountPerSession;


    public AFAdsManagerConfiguration() {
        // Default constructor for Realm
    }

    public AFAdsManagerConfiguration (String applicationId, String bannerId, String interstitialId,
                                      String rewardedVideoId, int interstitialsCountPerSession,
                                      int interstitialsDelay, RealmList<Action> actions) {
        this.id = 0;
        this.applicationId = applicationId;
        this.bannerId = bannerId;
        this.interstitialId = interstitialId;
        this.rewardedVideoId = rewardedVideoId;
        this.interstitialsCountPerSession = interstitialsCountPerSession;
        this.interstitialsDelay = interstitialsDelay;
        this.actions = actions;

        this.interstitialsLastShowDate = 0;
        this.currentInterstitialCountPerSession = 0;
    }

    public int getId() {
        return id;
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

    public int getInterstitialsCountPerSession() {
        return interstitialsCountPerSession;
    }

    public int getInterstitialsDelay() {
        return interstitialsDelay;
    }

    public RealmList<Action> getActions() {
        return actions;
    }

    public double getInterstitialsLastShowDate() {
        return interstitialsLastShowDate;
    }

    public int getCurrentInterstitialCountPerSession() {
        return currentInterstitialCountPerSession;
    }

    public Action containsAction(String actionName) {
        for (Action action : actions) {
            if (action.getActionTitle().equals(actionName)) {
                return action;
            }
        }

        return null;
    }

    public void setInterstitialsLastShowDate(double interstitialsLastShowDate) {
        this.interstitialsLastShowDate = interstitialsLastShowDate;
    }

    public void incrementCurrentInterstitialCountPerSession() {
        currentInterstitialCountPerSession += 1;
    }

    public void resetCurrentInterstitialCountPerSession() {
        currentInterstitialCountPerSession = 0;
    }

    @Override
    public String toString() {
        return "AFAdsManagerConfiguration{" +
                "id=" + id +
                ", applicationId='" + applicationId + '\'' +
                ", bannerId='" + bannerId + '\'' +
                ", interstitialId='" + interstitialId + '\'' +
                ", rewardedVideoId='" + rewardedVideoId + '\'' +
                ", interstitialsCountPerSession=" + interstitialsCountPerSession +
                ", interstitialsDelay=" + interstitialsDelay +
                ", actions=" + actions +
                ", interstitialsLastShowDate=" + interstitialsLastShowDate +
                ", currentInterstitialCountPerSession=" + currentInterstitialCountPerSession +
                '}';
    }

}