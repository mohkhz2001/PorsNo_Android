package com.mohammadkz.porsno_android.API;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class AppConfig {
    private static Retrofit retrofit;
    public static final String url = "http://185.190.39.159/Porseshno_backend/API/";

    public static Retrofit getRetrofit() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder().baseUrl(url)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create()).build();
        }

        return retrofit;
    }
}
