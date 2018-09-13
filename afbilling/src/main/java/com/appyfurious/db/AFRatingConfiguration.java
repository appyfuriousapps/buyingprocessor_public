package com.appyfurious.db;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * AFRatingConfiguration.java
 * buyingprocessor_public
 * <p>
 * Created by o.davidovich on 12.09.2018.
 * <p>
 * Copyright © 2018 Appyfurious. All rights reserved.
 */
public class AFRatingConfiguration extends RealmObject {

    @PrimaryKey
    private int id;
    private String actionTitle;
    private int sessionCount;
    private int actionCount;
    private String actionUrl;
    private int isEnabled;

    // custom
    private int currentActionCount;
    private boolean isCompleted;


    public AFRatingConfiguration() {
        // Empty constructor for Realm
    }

    public AFRatingConfiguration(int id, String actionTitle, int sessionCount, int actionCount,
                                  String actionUrl, int isEnabled) {
        this.id = id;
        this.actionTitle = actionTitle;
        this.sessionCount = sessionCount;
        this.actionCount = actionCount;
        this.actionUrl = actionUrl;
        this.isEnabled = isEnabled;
        this.currentActionCount = 0;
        this.isCompleted = false;
    }

    public int getId() {
        return id;
    }

    public String getActionTitle() {
        return actionTitle;
    }

    public int getSessionCount() {
        return sessionCount;
    }

    public int getActionCount() {
        return actionCount;
    }

    public String getActionUrl() {
        return actionUrl;
    }

    public int getIsEnabled() {
        return isEnabled;
    }

    public int getCurrentActionCount() {
        return currentActionCount;
    }

    public void setCurrentActionCount(int currentActionCount) {
        this.currentActionCount = currentActionCount;
    }

    public boolean isActionCountEqualCurrentActionCount() {
        return this.actionCount == this.currentActionCount;
    }

    public void setCompleted(boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

}