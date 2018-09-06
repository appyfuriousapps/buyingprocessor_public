package com.smaato.soma.mediation.adapters.MoPub;

import android.content.Context;

import com.mopub.common.DataKeys;
import com.mopub.mobileads.CustomEventBanner;
import com.mopub.mobileads.MoPubErrorCode;
import com.smaato.soma.AdDimension;
import com.smaato.soma.AdDimensionHelper;
import com.smaato.soma.AdDownloaderInterface;
import com.smaato.soma.AdListenerInterface;
import com.smaato.soma.BannerStateListener;
import com.smaato.soma.BannerView;
import com.smaato.soma.BaseView;
import com.smaato.soma.CrashReportTemplate;
import com.smaato.soma.ReceivedBannerInterface;
import com.smaato.soma.bannerutilities.constant.BannerStatus;
import com.smaato.soma.debug.DebugCategory;
import com.smaato.soma.debug.Debugger;
import com.smaato.soma.debug.LogMessage;

import java.util.Map;

/**
 * Example of MoPub Smaato Banner mediation adapter.
 * <p>
 * Updated with Smaato v 5.1.2 version & Mopub v 4.12.0
 *
 * @author Palani Soundararajan
 *         <p>
 *         singleton pattern removed due to mInvalidated flag in CustomEventBannerAdapter
 *         Mopubs onBannerClicked event handled.
 */

public class SomaMopubAdapter extends CustomEventBanner {

    private BannerView mBanner;

    private static String TAG = "###SomaMopubAdapter";

    /*
    * (non-Javadoc)
    * @see
    * com.mopub.mobileads.CustomEventBanner#loadBanner(android.content.Context,
    * com.mopub.mobileads.CustomEventBanner.CustomEventBannerListener,
    * java.util.Map, java.util.Map) */
    @Override
    public void loadBanner(Context context,
                           final CustomEventBannerListener customEventBannerListener, Map<String, Object> localExtras,
                           Map<String, String> serverExtras) {
        try {

            // uncomment to enable debug logs
            // Debugger.setDebugMode(Debugger.Level_3);
            if (mBanner == null) {
                mBanner = new BannerView(context);

                mBanner.addAdListener(new AdListenerInterface() {
                    @Override
                    public void onReceiveAd(AdDownloaderInterface arg0,
                                            final ReceivedBannerInterface arg1) {

                        new CrashReportTemplate<Void>() {
                            @Override
                            public Void process() throws Exception {

                                if (arg1.getStatus() == BannerStatus.ERROR) {
                                    printDebugLogs("NO_FILL", DebugCategory.DEBUG);
                                    customEventBannerListener.onBannerFailed(MoPubErrorCode.NO_FILL);
                                } else {
                                    printDebugLogs("Ad available", DebugCategory.DEBUG);
                                    customEventBannerListener.onBannerLoaded(mBanner);
                                }

                                return null;
                            }
                        }.execute();

                    }
                });

                mBanner.setBannerStateListener(new BannerStateListener() {
                    @Override
                    public void onWillOpenLandingPage(BaseView arg0) {
                        new CrashReportTemplate<Void>() {
                            @Override
                            public Void process() throws Exception {
                                printDebugLogs("Banner Clicked", DebugCategory.DEBUG);
                                customEventBannerListener.onBannerClicked();
                                return null;
                            }
                        }.execute();

                    }

                    @Override
                    public void onWillCloseLandingPage(BaseView arg0) {
                        new CrashReportTemplate<Void>() {
                            @Override
                            public Void process() throws Exception {
                                mBanner.asyncLoadNewBanner();
                                printDebugLogs("Banner closed", DebugCategory.DEBUG);
                                return null;
                            }
                        }.execute();
                    }
                });
            }

            int publisherId = Integer.parseInt(serverExtras.get("publisherId"));
            int adSpaceId = Integer.parseInt(serverExtras.get("adSpaceId"));
            mBanner.getAdSettings().setPublisherId(publisherId);
            mBanner.getAdSettings().setAdspaceId(adSpaceId);

            int adHeight = (int) localExtras.get(DataKeys.AD_HEIGHT);
            int adWidth = (int) localExtras.get(DataKeys.AD_WIDTH);

            // support for default, medium rectangle, leaderboard, skyscraper and wide skyscraper banner format
            AdDimension adDimension = AdDimensionHelper.getAdDimensionForValues(adHeight, adWidth);

            if (adDimension != null) {
                mBanner.getAdSettings().setAdDimension(adDimension);
                mBanner.getAdSettings().setDimensionStrict(true);
            }

            /* *******************************
             * Set user targeting parameters
             ******************************* */
            /*
              Set user age
             */
            // mBanner.getUserSettings().setAge(35);

            /*
              Set user gender. Possible values:
                UserSettings.Gender.FEMALE
                UserSettings.Gender.MALE
                UserSettings.Gender.UNSET
             */
            // mBanner.getUserSettings().setUserGender(UserSettings.Gender.MALE);

            /*
              Set user location automatically
             */
            // mBanner.setLocationUpdateEnabled(true);

            /*
              Set user location manually (lat/long)
             */
            // mBanner.getUserSettings().setLatitude(37.331689);
            // mBanner.getUserSettings().setLongitude(-122.030731);

            /*
              Set custom keywords as a string of comma-separated tags
             */
            // mBanner.getUserSettings().setKeywordList("Android,California");

            mBanner.asyncLoadNewBanner();
        } catch (RuntimeException e) {
            e.printStackTrace();
            printDebugLogs("Failed to load banner", DebugCategory.ERROR);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

/*
* (non-Javadoc)
* @see com.mopub.mobileads.CustomEventBanner#onInvalidate() */

    @Override
    public void onInvalidate() {
        // clear memory
        if (mBanner != null) {
            mBanner.destroy();
            mBanner = null;
        }

    }

    public void printDebugLogs(String str, DebugCategory debugCategory) {
        Debugger.showLog(new LogMessage(TAG,
                str,
                Debugger.Level_1,
                debugCategory));
    }

}
