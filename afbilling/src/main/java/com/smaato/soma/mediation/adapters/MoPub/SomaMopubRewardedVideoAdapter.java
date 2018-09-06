package com.smaato.soma.mediation.adapters.MoPub;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.mopub.common.BaseLifecycleListener;
import com.mopub.common.LifecycleListener;
import com.mopub.common.MoPubReward;
import com.mopub.mobileads.CustomEventRewardedVideo;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubRewardedVideoManager;
import com.smaato.soma.CrashReportTemplate;
import com.smaato.soma.video.RewardedVideo;
import com.smaato.soma.video.RewardedVideoListener;

import java.util.Map;

public class SomaMopubRewardedVideoAdapter extends CustomEventRewardedVideo implements RewardedVideoListener {

    private static final String PUBLISHER_ID = "publisherId";
    private static final String AD_SPACE_ID = "adSpaceId";

    private RewardedVideo rewardedVideo;
    private String adSpaceIdString;
    private BaseLifecycleListener lifecycleListener = new BaseLifecycleListener() {
        @Override
        public void onDestroy(@NonNull Activity activity) {
            if (rewardedVideo != null) {
                rewardedVideo.destroy();
            }
        }
    };

    @Nullable
    @Override
    protected LifecycleListener getLifecycleListener() {
        return lifecycleListener;
    }

    @Override
    protected boolean checkAndInitializeSdk(@NonNull Activity launcherActivity, @NonNull Map<String, Object> localExtras, @NonNull Map<String, String> serverExtras) throws Exception {
        // not required for Smaato SDK
        return false;
    }

    @Override
    protected void loadWithSdkInitialized(@NonNull final Activity activity, @NonNull Map<String, Object> localExtras, @NonNull final Map<String, String> serverExtras) throws Exception {
//        Debugger.setDebugMode(Debugger.Level_3);

        if (TextUtils.isEmpty(serverExtras.get(PUBLISHER_ID)) || TextUtils.isEmpty(serverExtras.get(AD_SPACE_ID))) {
            // Using class name as the network ID for this callback since the ad unit ID is
            // invalid.
            MoPubRewardedVideoManager.onRewardedVideoLoadFailure(
                    SomaMopubRewardedVideoAdapter.class,
                    SomaMopubRewardedVideoAdapter.class.getSimpleName(),
                    MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
        }

        new CrashReportTemplate<Void>() {
            @Override
            public Void process() throws Exception {
                int publisherId = Integer.parseInt(serverExtras.get(PUBLISHER_ID));
                adSpaceIdString = serverExtras.get(AD_SPACE_ID);
                int adSpaceId = Integer.parseInt(adSpaceIdString);

                if (rewardedVideo == null) {
                    rewardedVideo = new RewardedVideo(activity);
                    rewardedVideo.setRewardedVideoListener(SomaMopubRewardedVideoAdapter.this);
                }
                rewardedVideo.getAdSettings().setPublisherId(publisherId);
                rewardedVideo.getAdSettings().setAdspaceId(adSpaceId);

                /* *******************************
                 * Set user targeting parameters
                 ******************************* */
                /*
                  Set user age
                 */
                // rewardedVideo.getUserSettings().setAge(35);

                /*
                  Set user gender. Possible values:
                    UserSettings.Gender.FEMALE
                    UserSettings.Gender.MALE
                    UserSettings.Gender.UNSET
                 */
                // rewardedVideo.getUserSettings().setUserGender(UserSettings.Gender.MALE);


                /*
                  Set user location (lat/long)
                 */
                // rewardedVideo.getUserSettings().setLatitude(37.331689);
                // rewardedVideo.getUserSettings().setLongitude(-122.030731);

                /*
                  Set custom keywords as a string of comma-separated tags
                 */
                // rewardedVideo.getUserSettings().setKeywordList("Android,California");

                rewardedVideo.asyncLoadNewBanner();

                return null;
            }
        }.execute();
    }


    @Override
    protected boolean hasVideoAvailable() {
        return rewardedVideo != null && rewardedVideo.isVideoPlayable();
    }

    @Override
    protected void showVideo() {
        if (hasVideoAvailable()) {
            rewardedVideo.show();
        } else {
            MoPubRewardedVideoManager.onRewardedVideoPlaybackError(
                    SomaMopubRewardedVideoAdapter.class,
                    adSpaceIdString,
                    MoPubErrorCode.INTERNAL_ERROR);
        }
    }

    @NonNull
    @Override
    protected String getAdNetworkId() {
        return adSpaceIdString;
    }

    @Override
    protected void onInvalidate() {
        if (rewardedVideo != null) {
            rewardedVideo.destroy();
        }
    }

    // RewardedVideoListener callbacks
    @Override
    public void onRewardedVideoStarted() {
        MoPubRewardedVideoManager.onRewardedVideoStarted(
                SomaMopubRewardedVideoAdapter.class,
                adSpaceIdString);
    }

    @Override
    public void onFirstQuartileCompleted() {
        // MoPub has no corresponding callback
    }

    @Override
    public void onSecondQuartileCompleted() {
        // MoPub has no corresponding callback
    }

    @Override
    public void onThirdQuartileCompleted() {
        // MoPub has no corresponding callback
    }

    @Override
    public void onRewardedVideoCompleted() {
        MoPubRewardedVideoManager.onRewardedVideoCompleted(
                SomaMopubRewardedVideoAdapter.class,
                adSpaceIdString,
                MoPubReward.success(MoPubReward.NO_REWARD_LABEL, MoPubReward.DEFAULT_REWARD_AMOUNT));
    }

    @Override
    public void onReadyToShow() {
        MoPubRewardedVideoManager.onRewardedVideoLoadSuccess(
                SomaMopubRewardedVideoAdapter.class,
                adSpaceIdString);
    }

    @Override
    public void onWillShow() {
        // MoPub has no corresponding callback
    }

    @Override
    public void onWillOpenLandingPage() {
        MoPubRewardedVideoManager.onRewardedVideoClicked(
                SomaMopubRewardedVideoAdapter.class,
                adSpaceIdString);
    }

    @Override
    public void onWillClose() {
        MoPubRewardedVideoManager.onRewardedVideoClosed(
                SomaMopubRewardedVideoAdapter.class,
                adSpaceIdString);
    }

    @Override
    public void onFailedToLoadAd() {
        MoPubRewardedVideoManager.onRewardedVideoLoadFailure(
                SomaMopubRewardedVideoAdapter.class,
                adSpaceIdString,
                MoPubErrorCode.UNSPECIFIED);
    }
}
