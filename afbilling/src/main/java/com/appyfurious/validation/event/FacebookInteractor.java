package com.appyfurious.validation.event;

import android.content.Context;

import com.facebook.appevents.AppEventsConstants;
import com.facebook.appevents.AppEventsLogger;

/**
 * FacebookInteractor.java
 * getfitandroid
 * <p>
 * Created by o.davidovich on 24.05.2018.
 * <p>
 * Copyright © 2018 Appyfurious. All rights reserved.
 */

public class FacebookInteractor {

    public static void logAddedToCartEvent(Context context) {
        AppEventsLogger logger = AppEventsLogger.newLogger(context);
        logger.logEvent(AppEventsConstants.EVENT_NAME_ADDED_TO_CART);
    }

}