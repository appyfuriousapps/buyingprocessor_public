package com.appyfurious.db;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
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
    private int rewardedVideoWaitingTime;
    private int interstitialsCountPerSession;
    private int interstitialsDelay;
    private RealmList<Action> actions;

    // custom
    private double interstitialsLastShowDate;
    private int currentInterstitialCountPerSession;


    public AFAdsManagerConfiguration() {
        // Default constructor for Realm
    }

    public AFAdsManagerConfiguration (String applicationId, String bannerId, String interstitialId,
                                      String rewardedVideoId, int rewardedVideoWaitingTime,
                                      int interstitialsCountPerSession,
                                      int interstitialsDelay, RealmList<Action> actions) {
        this.id = 0;
        this.applicationId = applicationId;
        this.bannerId = bannerId;
        this.interstitialId = interstitialId;
        this.rewardedVideoId = rewardedVideoId;
        this.rewardedVideoWaitingTime = rewardedVideoWaitingTime;
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
        if (actions != null) {
            for (Action action : actions) {
                if (action.getActionTitle().equals(actionName)) {
                    return action;
                }
            }
        }

        return null;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public void setBannerId(String bannerId) {
        this.bannerId = bannerId;
    }

    public void setInterstitialId(String interstitialId) {
        this.interstitialId = interstitialId;
    }

    public void setRewardedVideoId(String rewardedVideoId) {
        this.rewardedVideoId = rewardedVideoId;
    }

    public void setInterstitialsCountPerSession(int interstitialsCountPerSession) {
        this.interstitialsCountPerSession = interstitialsCountPerSession;
    }

    public void setInterstitialsDelay(int interstitialsDelay) {
        this.interstitialsDelay = interstitialsDelay;
    }

    public void setInterstitialsLastShowDate(double interstitialsLastShowDate) {
        this.interstitialsLastShowDate = interstitialsLastShowDate;
    }

    public int getRewardedVideoWaitingTime() {
        return rewardedVideoWaitingTime;
    }

    public void setRewardedVideoWaitingTime(int rewardedVideoWaitingTime) {
        this.rewardedVideoWaitingTime = rewardedVideoWaitingTime;
    }

    public void setActions(RealmList<Action> actions) {
        this.actions = actions;
    }

    public void setActions(RealmResults<Action> actions) {
        if (actions != null && !actions.isEmpty()) {
            this.actions = new RealmList<>();
            this.actions.addAll(actions);
        }
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