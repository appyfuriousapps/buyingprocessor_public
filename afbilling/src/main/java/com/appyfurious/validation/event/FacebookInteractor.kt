package com.appyfurious.validation.event

import android.content.Context

import com.facebook.appevents.AppEventsConstants
import com.facebook.appevents.AppEventsLogger

/**
 * FacebookInteractor.java
 * getfitandroid
 *
 *
 * Created by o.davidovich on 24.05.2018.
 *
 *
 * Copyright © 2018 Appyfurious. All rights reserved.
 */

object FacebookInteractor {

    fun logAddedToCartEvent(context: Context?) {
        context?.let {
            val logger = AppEventsLogger.newLogger(it)
            logger.logEvent(AppEventsConstants.EVENT_NAME_ADDED_TO_CART)
        }
    }

}