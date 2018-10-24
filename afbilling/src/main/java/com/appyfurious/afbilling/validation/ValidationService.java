package com.appyfurious.afbilling.validation;

import com.appyfurious.afbilling.validation.body.ValidationBody;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * ValidationService.java
 * getfitandroid
 * <p>
 * Created by o.gay on 25.05.2018.
 * <p>
 * Copyright © 2018 Appyfurious. All rights reserved.
 */

public interface ValidationService {

    @POST("android/verify")
    Call<ResponseBody> validate(@Query("api_key") String apiKey,
                                @Body ValidationBody body);

}