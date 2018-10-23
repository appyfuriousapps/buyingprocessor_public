package com.appyfurious.ad;

import android.os.CountDownTimer;
import android.support.annotation.NonNull;

import com.appyfurious.log.Logger;


/**
 * AFRewardedWaitingTimer.java
 * buyingprocessor_public
 * <p>
 * Created by o.davidovich on 23.10.2018.
 * <p>
 * Copyright © 2018 Appyfurious. All rights reserved.
 */

public class AFRewardedWaitingTimer extends CountDownTimer {

    private AFRewardedTimerListener mRewardedTimerListener;

    public AFRewardedWaitingTimer(long millisInFuture, long countDownInterval, @NonNull AFRewardedTimerListener listener) {
        super(millisInFuture, countDownInterval);
        this.mRewardedTimerListener = listener;
        start();
    }

    @Override
    public void onTick(long l) {
        Logger.INSTANCE.logAd("AFRewardedTimer is ticking... " + l + "\''s remaining.");
    }

    @Override
    public void onFinish() {
        Logger.INSTANCE.logAd("AFRewardedTimer finish!");
        if (mRewardedTimerListener != null) {
            mRewardedTimerListener.onFinish();
        }
    }



    public interface AFRewardedTimerListener {

        void onFinish();
    }

}
