package com.appyfurious.db.migration;

import android.support.annotation.NonNull;

import com.appyfurious.log.Logger;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;


/**
 * AFRealmMigrations.java
 * buyingprocessor_public
 * <p>
 * Created by o.davidovich on 23.10.2018.
 * <p>
 * Copyright © 2018 Appyfurious. All rights reserved.
 */


public class AFRealmMigrations implements RealmMigration {

    @Override
    public void migrate(@NonNull DynamicRealm realm, long oldVersion, long newVersion) {
        Logger.INSTANCE.logDb("Old Version: " + oldVersion + "; New version: " + newVersion);

        final RealmSchema schema = realm.getSchema();

        if (oldVersion == 1) {
            final RealmObjectSchema afAdConfig = schema.get("AFAdsManagerConfiguration");
            if (afAdConfig != null) {
                afAdConfig.addField("rewardedVideoWaitingTime", int.class);
            }
        }
    }

}
