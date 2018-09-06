package com.smaato.soma.mediation.adapters.AdMob;

import com.google.android.gms.ads.mediation.MediationAdRequest;
import com.google.android.gms.ads.mediation.customevent.CustomEventInterstitial;
import com.google.android.gms.ads.mediation.customevent.CustomEventInterstitialListener;

import com.smaato.soma.video.VASTAdListener;
import com.smaato.soma.video.Video;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

/**
 * @author David Debre on Mar 28, 2018.
 */
public class VideoAdMobMediationAdapter implements CustomEventInterstitial, VASTAdListener {

    private Video video;
    private CustomEventInterstitialListener interstitialListener;
    private Handler mainHandler;

    @Override
    public void requestInterstitialAd(Context context,
            CustomEventInterstitialListener listener, String serverParameter,
            MediationAdRequest mediationAdRequest, Bundle customEventExtras) {
        interstitialListener = listener;
        mainHandler = new Handler(Looper.getMainLooper());

        String[] serverParams = serverParameter.split("&");
        String publisherId = serverParams[0].split("=")[1];
        String adSpaceId = serverParams[1].split("=")[1];

        video = new Video(context);
        video.setVastAdListener(this);
        video.getAdSettings().setPublisherId(Long.parseLong(publisherId));
        video.getAdSettings().setAdspaceId(Long.parseLong(adSpaceId));
        video.asyncLoadNewBanner();
    }

    @Override
    public void showInterstitial() {
        video.show();
    }

    @Override
    public void onDestroy() {
        if (video != null) {
            video.destroy();
        }
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }

    // Vast video listener
    @Override
    public void onReadyToShow() {
        interstitialListener.onAdLoaded();
    }

    @Override
    public void onWillShow() {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                interstitialListener.onAdOpened();
            }
        });
    }

    @Override
    public void onWillOpenLandingPage() {
        interstitialListener.onAdClicked();
    }

    @Override
    public void onWillClose() {
        interstitialListener.onAdClosed();
    }

    @Override
    public void onFailedToLoadAd() {
        interstitialListener.onAdFailedToLoad(0);
    }
}
