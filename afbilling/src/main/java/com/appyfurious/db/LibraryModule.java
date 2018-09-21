package com.appyfurious.db;

import io.realm.annotations.RealmModule;

/**
 * LibraryModule.java
 * afbillingandroid
 * <p>
 * Created by o.davidovich on 21.09.2018.
 * <p>
 * Copyright © 2018 Appyfurious. All rights reserved.
 */

@RealmModule(library = true, classes = {Action.class, AFAdsManagerConfiguration.class, AFAdsManagerConfiguration.class})
public class LibraryModule {
}
