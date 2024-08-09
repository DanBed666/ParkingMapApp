package com.example.parkingmapapp;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitBuilder
{
    public static RetrofitService getRetrofitService()
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(RetrofitService.class);
    }

    public static RetrofitService getRetrofitService2()
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitService.NOMINATIM_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(RetrofitService.class);
    }
}
