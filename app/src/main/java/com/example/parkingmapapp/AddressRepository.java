package com.example.parkingmapapp;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.stripe.model.Customer;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddressRepository
{
    public MutableLiveData<Address> getAddress(double lat, double lon, String format)
    {
        MutableLiveData<Address> mutableLiveData = new MutableLiveData<>();
        RetrofitBuilder.getRetrofitService().getAddress(lat, lon, format).enqueue(new Callback<Address>()
        {
            @Override
            public void onResponse(@NonNull Call<Address> call, @NonNull Response<Address> response)
            {
                Log.i("RESPONSE", call.request().url().toString());
                Log.i("RESPONSE", String.valueOf(response.body()));
                mutableLiveData.setValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<Address> call, @NonNull Throwable throwable)
            {
                Log.i("RESPONSE2", call.request().url().url().getPath());
                Log.i("RESPONSE2", Objects.requireNonNull(throwable.getMessage()));
            }
        });

        return mutableLiveData;
    }
}
