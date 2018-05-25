package com.appyfurious.validation;

import android.content.Context;
import android.support.annotation.NonNull;

import com.appyfurious.validation.event.FacebookInteractor;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ValidationCallback.java
 * getfitandroid
 * <p>
 * Created by o.davidovich on 25.05.2018.
 * <p>
 * Copyright © 2018 Appyfurious. All rights reserved.
 */

public class ValidationCallback implements Callback<ResponseBody> {

    private CryptoAES128 mEncryptor;
    private ValidationListener mValidationListener;

    private String mSecretKey;

    public ValidationCallback(@NotNull String secretKey, ValidationListener listener) {
        this.mSecretKey = secretKey;
        this.mValidationListener = listener;
    }

    @Override
    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
        if (response.isSuccessful()) {

            mEncryptor = new CryptoAES128(mSecretKey);
            String decryptString;
            try {
                decryptString = mEncryptor.decrypt(response.body().string());
                JSONObject jsonResponse = new JSONObject(decryptString);
                JSONObject jsonData = jsonResponse.getJSONObject("data");
                boolean isValid = jsonData.getBoolean("isValid");
                if (isValid) {
                    validationSuccess();
                } else {
                    mValidationListener.onValidationFailure("Validation Error");
                }
            } catch (IOException e) {
                validationSuccess();
            } catch (JSONException e) {
                validationSuccess();
            }
        } else {
            validationSuccess();
        }
    }

    @Override
    public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
        validationSuccess();
    }

    private void validationSuccess() {
        mValidationListener.onValidationSuccess();
        FacebookInteractor.logAddedToCartEvent(mValidationListener.getValidationContext());
    }

    public interface ValidationListener {

        Context getValidationContext();

        void onValidationSuccess();

        void onValidationFailure(String errorMessage);
    }

}