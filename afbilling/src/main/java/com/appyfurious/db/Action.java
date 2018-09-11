package com.appyfurious.db;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Action.java
 * buyingprocessor_public
 * <p>
 * Created by o.davidovich on 11.09.2018.
 * <p>
 * Copyright © 2018 Appyfurious. All rights reserved.
 */


public class Action extends RealmObject {

    @PrimaryKey
    private int id;
    private String actionTitle;
    private int isEnabled;

    public Action() {
        // Default constructor for Realm
    }

    public Action(int id, String actionTitle, int isEnabled) {
        this.id = id;
        this.actionTitle = actionTitle;
        this.isEnabled = isEnabled;
    }

    public int getId() {
        return id;
    }

    public String getActionTitle() {
        return actionTitle;
    }

    public boolean isEnabled() {
        return isEnabled != 0;
    }

    @Override
    public String toString() {
        return "Action{" + "id=" + id +
                ", actionTitle='" + actionTitle + '\'' +
                ", isEnabled=" + isEnabled +
                '}';
    }

}