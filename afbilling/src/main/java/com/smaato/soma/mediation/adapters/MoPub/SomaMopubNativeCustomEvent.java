package com.smaato.soma.mediation.adapters.MoPub;

import android.content.Context;
import android.view.View;

import com.mopub.nativeads.CustomEventNative;
import com.mopub.nativeads.ImpressionTracker;
import com.mopub.nativeads.NativeClickHandler;
import com.mopub.nativeads.NativeErrorCode;
import com.mopub.nativeads.NativeImageHelper;
import com.mopub.nativeads.StaticNativeAd;
import com.smaato.soma.CrashReportTemplate;
import com.smaato.soma.ErrorCode;
import com.smaato.soma.debug.DebugCategory;
import com.smaato.soma.debug.Debugger;
import com.smaato.soma.debug.LogMessage;
import com.smaato.soma.internal.nativead.BannerNativeAd;
import com.smaato.soma.nativead.MediationNativeAdListener;
import com.smaato.soma.nativead.NativeAd;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.mopub.nativeads.NativeImageHelper.preCacheImages;

/**
 * Tested with Smaato SDK version 5.0.7 and above
 * MoPub SDK Ver 4.7.0
 * Created by palani on 04/07/16.
 */
public class SomaMopubNativeCustomEvent extends CustomEventNative {

    private static String TAG = "###SomaMopubNativeAdapter";

    @Override
    protected void loadNativeAd(Context context, CustomEventNativeListener customEventNativeListener, Map<String, Object> localExtras, Map<String, String> serverExtras) {

        try {

            long publisherId = Long.parseLong(serverExtras.get("publisherId"));
            long adSpaceId = Long.parseLong(serverExtras.get("adSpaceId"));


            if (isInputValid(publisherId, adSpaceId)) {
                final SmaatoForwardingNativeAd smaatoForwardingNativeAd =
                        new SmaatoForwardingNativeAd(context,
                                publisherId, adSpaceId, customEventNativeListener, new ImpressionTracker(context), new NativeClickHandler(context));
                smaatoForwardingNativeAd.loadAd();
            } else {
                customEventNativeListener.onNativeAdFailed(NativeErrorCode.NATIVE_ADAPTER_CONFIGURATION_ERROR);
                return;
            }

        } catch (Exception e) {
            Debugger.showLog(new LogMessage(TAG,
                    "Exception in Adapter Configuration. Please check inputs",
                    Debugger.Level_1,
                    DebugCategory.DEBUG));
            customEventNativeListener.onNativeAdFailed(NativeErrorCode.NATIVE_ADAPTER_CONFIGURATION_ERROR);
            return;
        }

    }

    private boolean isInputValid(long pub, long adspace) {

        if (pub > -1 && adspace > -1) {
            return true;
        }

        return false;
    }


    static class SmaatoForwardingNativeAd extends StaticNativeAd implements MediationNativeAdListener {

        private final Context mContext;
        private CustomEventNativeListener mCustomEventNativeListener;
        private NativeAd mNativeAd;

        private ImpressionTracker mImpressionTracker;
        private NativeClickHandler mNativeClickHandler;

        SmaatoForwardingNativeAd(final Context context,
                                 final long publisherId, final long adSpaceID,
                                 final CustomEventNativeListener customEventNativeListener,
                                 final ImpressionTracker impressionTracker,
                                 final NativeClickHandler nativeClickHandler) {
            mContext = context.getApplicationContext();
            mNativeAd = new NativeAd(context.getApplicationContext());
            mNativeAd.getAdSettings().setPublisherId(publisherId);
            mNativeAd.getAdSettings().setAdspaceId(adSpaceID);


            /* *******************************
             * Set user targeting parameters
             ******************************* */
            /*
              Set user age
             */
            // mNativeAd.getUserSettings().setAge(35);

            /*
              Set user gender. Possible values:
                UserSettings.Gender.FEMALE
                UserSettings.Gender.MALE
                UserSettings.Gender.UNSET
             */
            // mNativeAd.getUserSettings().setUserGender(UserSettings.Gender.MALE);

            /*
              Set user location (lat/long)
             */
            // mNativeAd.getUserSettings().setLatitude(37.331689);
            // mNativeAd.getUserSettings().setLongitude(-122.030731);

            /*
              Set custom keywords as a string of comma-separated tags
             */
            // mNativeAd.getUserSettings().setKeywordList("Android,California");

            mCustomEventNativeListener = customEventNativeListener;
            mImpressionTracker = impressionTracker;
            mNativeClickHandler = nativeClickHandler;
        }

        void loadAd() {
            new CrashReportTemplate<Void>() {
                @Override
                public Void process() throws Exception {

                    mNativeAd.loadMediationNativeAd(SmaatoForwardingNativeAd.this);
                    return null;
                }
            }.execute();
        }

        @Override
        public void onAdClicked() {
            new CrashReportTemplate<Void>() {
                @Override
                public Void process() throws Exception {
                    notifyAdClicked();
                    return null;
                }
            }.execute();
        }

        @Override
        public void onLoggingImpression() {
            new CrashReportTemplate<Void>() {
                @Override
                public Void process() throws Exception {
                    notifyAdImpressed();
                    return null;
                }
            }.execute();
        }

        @Override
        public void prepare(final View view) {
            new CrashReportTemplate<Void>() {
                @Override
                public Void process() throws Exception {
                    mImpressionTracker.addView(view, SmaatoForwardingNativeAd.this);
                    mNativeAd.registerViewForInteraction(view);
                    mNativeClickHandler.setOnClickListener(view, SmaatoForwardingNativeAd.this);

                    return null;
                }
            }.execute();

        }

        @Override
        public void clear(final View view) {

            new CrashReportTemplate<Void>() {
                @Override
                public Void process() throws Exception {
                    mNativeAd.unRegisterView(view);
                    mImpressionTracker.removeView(view);
                    mNativeClickHandler.clearOnClickListener(view);
                    return null;
                }
            }.execute();
        }

        @Override
        public void destroy() {
            new CrashReportTemplate<Void>() {
                @Override
                public Void process() throws Exception {
                    mImpressionTracker.destroy();
                    // TODO remove any listeners added
                    //mNativeAd.setAdListener();

                    mNativeAd.destroy();
                    return null;
                }
            }.execute();
        }

        @Override
        public void recordImpression(final View view) {
            try {
                notifyAdImpressed();
                mNativeAd.fireViewedImpression(view);
            } catch (Exception m) {

                Debugger.showLog(new LogMessage(TAG,
                        "Exception in Adapter Configuration. Please check inputs" + m.getMessage(),
                        Debugger.Level_1,
                        DebugCategory.DEBUG));
            }
        }

        @Override
        public void handleClick(final View view) {
            new CrashReportTemplate<Void>() {
                @Override
                public Void process() throws Exception {
                    notifyAdClicked();
                    mNativeClickHandler.openClickDestinationUrl(getClickDestinationUrl(), view);
                    mNativeAd.recordClickImpression(view);

                    Debugger.showLog(new LogMessage(TAG,
                            "Smaato Native Ad clicked",
                            Debugger.Level_1,
                            DebugCategory.DEBUG));

                    return null;
                }
            }.execute();
        }

        @Override
        public void onAdLoaded(final BannerNativeAd nativeAd) {

            new CrashReportTemplate<Void>() {
                @Override
                public Void process() throws Exception {

                    setTitle(nativeAd.getTitle());
                    setText(nativeAd.getText());
                    setMainImageUrl(nativeAd.getMainImageUrl());
                    setIconImageUrl(nativeAd.getIconImageUrl());

                    setCallToAction(nativeAd.getClickToActionText());
                    setClickDestinationUrl(nativeAd.getClickToActionUrl());
                    setStarRating(getDoubleRating(nativeAd.getStarrating()));

                    // TODO inside preCacheImages
                    //mCustomEventNativeListener.onNativeAdLoaded(SmaatoForwardingNativeAd.this);


                    final List<String> imageUrls = new ArrayList<>();
                    final String mainImageUrl = getMainImageUrl();
                    if (mainImageUrl != null) {
                        imageUrls.add(getMainImageUrl());
                    }
                    final String iconUrl = getIconImageUrl();
                    if (iconUrl != null) {
                        imageUrls.add(getIconImageUrl());
                    }

                    // check this Image caching
                    preCacheImages(mContext, imageUrls, new NativeImageHelper.ImageListener() {
                        @Override
                        public void onImagesCached() {
                            mCustomEventNativeListener.onNativeAdLoaded(SmaatoForwardingNativeAd.this);
                        }

                        @Override
                        public void onImagesFailedToCache(NativeErrorCode errorCode) {
                            mCustomEventNativeListener.onNativeAdFailed(errorCode);
                        }
                    });

                    return null;
                }
            }.execute();

        }

        private Double getDoubleRating(float starrating) {
            return (double) starrating;
        }

        @Override
        public void onError(final ErrorCode errorCode, String errorMessage) {

            new CrashReportTemplate<Void>() {
                @Override
                public Void process() throws Exception {

                    if (errorCode == null || errorCode == ErrorCode.UNSPECIFIED) {
                        mCustomEventNativeListener.onNativeAdFailed(NativeErrorCode.UNSPECIFIED);
                    } else if (errorCode == ErrorCode.NO_AD_AVAILABLE) {
                        mCustomEventNativeListener.onNativeAdFailed(NativeErrorCode.NETWORK_NO_FILL);
                    } else if (errorCode == ErrorCode.NO_CONNECTION_ERROR) {
                        mCustomEventNativeListener.onNativeAdFailed(NativeErrorCode.NETWORK_INVALID_STATE);
                    } else {
                        mCustomEventNativeListener.onNativeAdFailed(NativeErrorCode.UNSPECIFIED);
                    }

                    return null;
                }
            }.execute();

        }

    }
}