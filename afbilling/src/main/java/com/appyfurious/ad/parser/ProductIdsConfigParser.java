package com.appyfurious.ad.parser;

import com.appyfurious.AFProductIdConfiguration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.realm.RealmList;

/**
 * ProductIdsConfigParser.java
 * buyingprocessor_public
 * <p>
 * Created by o.davidovich on 05.10.2018.
 * <p>
 * Copyright © 2018 Appyfurious. All rights reserved.
 */
public class ProductIdsConfigParser {

    private static final String ID = "ID";
    private static final String KEY = "key";
    private static final String VALUE = "value";

    private int id;
    private String key;
    private String value;

    private String mOriginal;

    private RealmList<AFProductIdConfiguration> mProductIdConfigurations;

    public ProductIdsConfigParser(String original) {
        mOriginal = original;
        parse();
    }

    private void parse() {
        JSONArray source = null;
        try {
            source = new JSONArray(mOriginal);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (source != null) {
            for (int i = 0; i < source.length(); i++) {
                JSONObject productObj = null;
                try {
                    productObj = (JSONObject) source.get(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (productObj != null) {
                    try {
                        id = Integer.parseInt((String) productObj.get(ID));
                        key = (String) productObj.get(KEY);
                        value = (String) productObj.get(VALUE);

                        if (mProductIdConfigurations == null) {
                            mProductIdConfigurations = new RealmList<>();
                        }


                        mProductIdConfigurations.add(new AFProductIdConfiguration(id, key, value));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
    }

    public RealmList<AFProductIdConfiguration> getProductIdConfigurations() {
        return mProductIdConfigurations;
    }

}
