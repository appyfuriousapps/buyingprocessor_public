package com.smaato.soma.mediation.adapters.MoPub;

import com.mopub.mobileads.CustomEventInterstitial;
import com.mopub.mobileads.MoPubErrorCode;
import com.smaato.soma.CrashReportTemplate;
import com.smaato.soma.video.VASTAdListener;
import com.smaato.soma.video.Video;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import java.util.Map;

public class SomaMopubVideoAdapter extends CustomEventInterstitial implements VASTAdListener {

    public static final String PUBLISHER_ID = "publisherId";
    public static final String AD_SPACE_ID = "adSpaceId";

    private Video video;
    private CustomEventInterstitialListener customEventInterstitialListener = null;
    private Handler mHandler;

    @Override
    protected void loadInterstitial(final Context context,
                                    final CustomEventInterstitialListener customEventInterstitialListener,
                                    Map<String, Object> localExtras,
                                    final Map<String, String> serverExtras) {
        // Enable to get full logs
        //Debugger.setDebugMode(Debugger.Level_3);

        this.customEventInterstitialListener = customEventInterstitialListener;
        mHandler = new Handler(Looper.getMainLooper());

        new CrashReportTemplate<Void>() {
            @Override
            public Void process() throws Exception {
                video = new Video(context);
                video.setVastAdListener(SomaMopubVideoAdapter.this);

                int publisherId = Integer.parseInt(serverExtras.get(PUBLISHER_ID));
                int adSpaceId = Integer.parseInt(serverExtras.get(AD_SPACE_ID));
                video.getAdSettings().setPublisherId(publisherId);
                video.getAdSettings().setAdspaceId(adSpaceId);

                // Set user targeting parameters
                // video.getUserSettings().setAge(35);
                // video.getUserSettings().setUserGender(UserSettings.Gender.MALE);
                // video.getUserSettings().setLatitude(37.331689);
                // video.getUserSettings().setLongitude(-122.030731);
                // video.getUserSettings().setKeywordList("Android,California");

                video.asyncLoadNewBanner();

                return null;
            }
        }.execute();
    }

    @Override
    protected void showInterstitial() {
        new CrashReportTemplate<Void>() {
            @Override
            public Void process() throws Exception {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (video.isVideoPlayable()) {
                            video.show();
                        }
                    }
                });
                return null;
            }
        }.execute();
    }

    @Override
    protected void onInvalidate() {
        if (video != null) {
            video.destroy();
            video = null;
        }
    }

    @Override
    public void onReadyToShow() {
        new CrashReportTemplate<Void>() {
            @Override
            public Void process() throws Exception {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        customEventInterstitialListener.onInterstitialLoaded();
                    }
                });
                return null;
            }
        }.execute();
    }

    @Override
    public void onWillShow() {
        new CrashReportTemplate<Void>() {
            @Override
            public Void process() throws Exception {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        customEventInterstitialListener.onInterstitialShown();
                    }
                });
                return null;
            }
        }.execute();
    }

    @Override
    public void onWillOpenLandingPage() {
        new CrashReportTemplate<Void>() {
            @Override
            public Void process() throws Exception {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        customEventInterstitialListener.onInterstitialClicked();
                    }
                });
                return null;
            }
        }.execute();
    }

    @Override
    public void onWillClose() {
        new CrashReportTemplate<Void>() {
            @Override
            public Void process() throws Exception {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        customEventInterstitialListener.onInterstitialDismissed();
                    }
                });
                return null;
            }
        }.execute();
    }

    @Override
    public void onFailedToLoadAd() {
        new CrashReportTemplate<Void>() {
            @Override
            public Void process() throws Exception {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        customEventInterstitialListener.onInterstitialFailed(MoPubErrorCode.NO_FILL);
                    }
                });
                return null;
            }
        }.execute();
    }
}