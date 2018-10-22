package com.appyfurious.ad;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.appyfurious.afbilling.R;

/**
 * AFRewardedLoadingDialogFragment.java
 * buyingprocessor_public
 * <p>
 * Created by o.davidovich on 22.10.2018.
 * <p>
 * Copyright © 2018 Appyfurious. All rights reserved.
 */

public class AFRewardedLoadingDialogFragment extends DialogFragment {

    public static final String TAG = "AFRewardedLoadingDialogFragment";

    //private View mRootView;
    private ProgressBar mProgressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Widget_DeviceDefault_Light_ProgressBar);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.layout_rewarded_loading, container, false);
        mProgressBar = rootView.findViewById(R.id.af_rewarded_loading_pb);
        return rootView;
    }


    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.rewarded_progress_bg);

            RelativeLayout.LayoutParams mLayoutParams =
                    new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);
            mLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            mProgressBar.setLayoutParams(mLayoutParams);
        }
    }
}
