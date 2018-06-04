package com.appyfurious.validation.interceptor;

import android.support.annotation.NonNull;

import com.appyfurious.validation.CryptoAES128;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

/**
 * EncryptionInterceptor.java
 * getfitandroid
 * <p>
 * Created by gayMan on 25.05.2018.
 * <p>
 * Copyright © 2018 Appyfurious. All rights reserved.
 */

public class EncryptionInterceptor implements Interceptor {

    private CryptoAES128 mEncryptor;

    public EncryptionInterceptor(String mSecretKey) {
        mEncryptor = new CryptoAES128(mSecretKey);
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException, NullPointerException {

        Request request = chain.request();
        RequestBody oldBody = request.body();
        Buffer buffer = new Buffer();
        oldBody.writeTo(buffer);
        String strOldBody = buffer.readUtf8();
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        String encryptBody = mEncryptor.encrypt(strOldBody);
        RequestBody body = RequestBody.create(mediaType, encryptBody);
        request = request.newBuilder().method(request.method(), body).build();
        return chain.proceed(request);
    }

}