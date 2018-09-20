package com.smaato.soma.MediationAdapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.mediation.MediationAdRequest;
import com.google.android.gms.ads.mediation.customevent.CustomEventInterstitial;
import com.google.android.gms.ads.mediation.customevent.CustomEventInterstitialListener;
import com.smaato.soma.interstitial.Interstitial;
import com.smaato.soma.interstitial.InterstitialAdListener;

/**
 * Please do not forget to add the following line to your AndroidManifest.xml
 * <activity android:name="com.smaato.soma.interstitial.InterstitialActivity" ></activity>
 * @author Chouaieb Nabil
 */
public class InterstitialAdMobMediationAdapter implements
		CustomEventInterstitial, InterstitialAdListener {

	Interstitial interstitial;
	CustomEventInterstitialListener customEventInterstitialListener;
	Activity activity;

	@Override
	public void requestInterstitialAd(Context context,
			CustomEventInterstitialListener listener, String serverParameter,
			MediationAdRequest mediationAdRequest, Bundle customEventExtras) {
			this.customEventInterstitialListener = listener;
			Activity activity = (Activity) context;
			this.activity = activity;
			interstitial = new Interstitial(activity);
			interstitial.setInterstitialAdListener(this);
			String[] serverParams = serverParameter.split("&");
			String publisherId = serverParams[0].split("=")[1];
			String adSpaceId = serverParams[1].split("=")[1];
			interstitial.getAdSettings().setPublisherId(Integer.parseInt(publisherId));
			interstitial.getAdSettings().setAdspaceId(Integer.parseInt(adSpaceId));

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
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.smaato.soma.interstitial.InterstitialAdListener#onReadyToShow()
	 */
	@Override
	public void onReadyToShow() {
		customEventInterstitialListener.onAdLoaded();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.smaato.soma.interstitial.InterstitialAdListener#onWillShow()
	 */
	@Override
	public void onWillShow() {
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				customEventInterstitialListener.onAdOpened();
			}
		});
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.smaato.soma.interstitial.InterstitialAdListener#onWillOpenLandingPage
	 * ()
	 */
	@Override
	public void onWillOpenLandingPage() {
		customEventInterstitialListener.onAdClicked();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.smaato.soma.interstitial.InterstitialAdListener#onWillClose()
	 */
	@Override
	public void onWillClose() {
		customEventInterstitialListener.onAdClosed();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.smaato.soma.interstitial.InterstitialAdListener#onFailedToLoadAd()
	 */
	@Override
	public void onFailedToLoadAd() {
		customEventInterstitialListener
				.onAdFailedToLoad(AdRequest.ERROR_CODE_NO_FILL);
	}

	@Override
	public void onDestroy() {
		if(interstitial != null) {
			interstitial.destroy();
		}
	}

	@Override
	public void onPause() {

	}

	@Override
	public void onResume() {

	}

	@Override
	public void showInterstitial() {
		interstitial.show();
	}
}
