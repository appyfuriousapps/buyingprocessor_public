package com.smaato.soma.mediation.adapters.MoPub;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.mopub.mobileads.CustomEventInterstitial;
import com.mopub.mobileads.MoPubErrorCode;
import com.smaato.soma.CrashReportTemplate;
import com.smaato.soma.debug.DebugCategory;
import com.smaato.soma.debug.Debugger;
import com.smaato.soma.debug.LogMessage;
import com.smaato.soma.interstitial.Interstitial;
import com.smaato.soma.interstitial.InterstitialAdListener;

import java.util.Map;

/**
 * Example of MoPub Smaato Interstitial mediation adapter.
 *
 * @author Chouaieb Nabil
 *         Updated to support latest MoPub SDK 4.7.0
 *         Common for all Publishers
 */
public class SomaMopubAdapterInterstitial extends CustomEventInterstitial implements InterstitialAdListener {

    private static String TAG = "###SmaatoInt";

    private Interstitial interstitial;
    private CustomEventInterstitialListener customEventInterstitialListener = null;
    private Handler mHandler;

    @Override
    protected void loadInterstitial(Context context,
                                    final CustomEventInterstitialListener customEventInterstitialListener, Map<String, Object> localExtras,
                                    final Map<String, String> serverExtras) {
        // Enable to get full logs
        //Debugger.setDebugMode(Debugger.Level_3);

        mHandler = new Handler(Looper.getMainLooper());
        this.customEventInterstitialListener = customEventInterstitialListener;
        if (interstitial == null) {
            interstitial = new Interstitial((Activity) context);
            interstitial.setInterstitialAdListener(this);
        }

        new CrashReportTemplate<Void>() {
            @Override
            public Void process() throws Exception {

                int publisherId = Integer.parseInt((String) serverExtras.get("publisherId"));
                int adSpaceId = Integer.parseInt((String) serverExtras.get("adSpaceId"));
                interstitial.getAdSettings().setPublisherId(publisherId);
                interstitial.getAdSettings().setAdspaceId(adSpaceId);

                //printDebugLogs("loading SmaatoInter",DebugCategory.ERROR);

                /* *******************************
                 * Set user targeting parameters
                 ******************************* */
                /*
                  Set user age
                 */
                // interstitial.getUserSettings().setAge(35);

                /*
                  Set user gender. Possible values:
                    UserSettings.Gender.FEMALE
                    UserSettings.Gender.MALE
                    UserSettings.Gender.UNSET
                 */
                // interstitial.getUserSettings().setUserGender(UserSettings.Gender.MALE);

                /*
                  Set user location automatically
                 */
                // interstitial.setLocationUpdateEnabled(true);

                /*
                  Set user location manually (lat/long)
                 */
                 // interstitial.getUserSettings().setLatitude(37.331689);
                 // interstitial.getUserSettings().setLongitude(-122.030731);

                /*
                  Set custom keywords as a string of comma-separated tags
                 */
                // interstitial.getUserSettings().setKeywordList("Android,California");

                interstitial.asyncLoadNewBanner();

                return null;
            }
        }.execute();

    }

    @Override
    protected void onInvalidate() {
        if(interstitial != null) {
            interstitial.destroy();
            interstitial = null;
        }
    }

    @Override
    protected void showInterstitial() {
        //interstitial.show();

        new CrashReportTemplate<Void>() {
            @Override
            public Void process() throws Exception {

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (interstitial.isInterstitialReady()) {
                            //printDebugLogs("isInterstitialReady = true",DebugCategory.ERROR);
                            interstitial.show();
                        }
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
                        //printDebugLogs("onFailedToLoadAd = true",DebugCategory.ERROR);
                        customEventInterstitialListener.onInterstitialFailed(MoPubErrorCode.NO_FILL);
                    }
                });

                return null;
            }
        }.execute();


    }

    @Override
    public void onReadyToShow() {

        new CrashReportTemplate<Void>() {
            @Override
            public Void process() throws Exception {

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        //printDebugLogs("onReadyToShow ",DebugCategory.ERROR);
                        customEventInterstitialListener.onInterstitialLoaded();

                        // comment below line, if you would like to show SmaatoAd, only when calling  mMoPubInterstitial.show()
                        //showInterstitial();
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
                        //printDebugLogs("onWillClose ",DebugCategory.ERROR);
                        customEventInterstitialListener.onInterstitialDismissed();
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
                        //printDebugLogs("onWillOpenLandingPage ",DebugCategory.ERROR);
                        customEventInterstitialListener.onInterstitialClicked();
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
                        printDebugLogs("onWillShow ", DebugCategory.ERROR);
                        customEventInterstitialListener.onInterstitialShown();
                    }
                });

                return null;
            }
        }.execute();

    }

    public void printDebugLogs(String str, DebugCategory debugCategory) {
        Debugger.showLog(new LogMessage(TAG,
                str,
                Debugger.Level_1,
                debugCategory));
    }

}