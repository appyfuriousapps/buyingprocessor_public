package com.appyfurious;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * AFProductIdConfiguration.java
 * buyingprocessor_public
 * <p>
 * Created by o.davidovich on 05.10.2018.
 * <p>
 * Copyright © 2018 Appyfurious. All rights reserved.
 */

public class AFProductIdConfiguration extends RealmObject {

    @PrimaryKey
    private int id;
    private String key;
    private String value;

    public AFProductIdConfiguration() {
        // Default constructor for Realm
    }

    public AFProductIdConfiguration(int id, String key, String value) {
        this.id = id;
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

}