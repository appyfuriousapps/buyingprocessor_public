package com.appyfurious.afbilling.validation;

import com.appyfurious.afbilling.validation.interceptor.EncryptionInterceptor;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * ValidationClient.java
 * getfitandroid
 * <p>
 * Created by o.davidovich on 25.05.2018.
 * <p>
 * Copyright © 2018 Appyfurious. All rights reserved.
 */

public class ValidationClient {

    private static Retrofit retrofit = null;
    private static ValidationService validationService = null;

    private static void getValidationClient(String secretKey, String baseUrl) {
        if (retrofit == null) {

            EncryptionInterceptor encryptionInterceptor = new EncryptionInterceptor(secretKey);

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(encryptionInterceptor)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            if (validationService == null) {
                validationService = retrofit.create(ValidationService.class);
            }
        }
    }

    public static ValidationService getValidationService(String secretKey, String baseUrl) {
        if (validationService == null) {
            getValidationClient(secretKey, baseUrl);
        }

        return validationService;
    }

}