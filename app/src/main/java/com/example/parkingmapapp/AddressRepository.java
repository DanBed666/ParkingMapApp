package com.example.parkingmapapp;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddressRepository
{
    public MutableLiveData<Address> getAddress(String geoPoint, String apiKey)
    {
        MutableLiveData<Address> mutableLiveData = new MutableLiveData<>();
        RetrofitBuilder.getRetrofitService2().getAddress(geoPoint, apiKey).enqueue(new Callback<Address>()
        {
            @Override
            public void onResponse(@NonNull Call<Address> call, @NonNull Response<Address> response)
            {
                if (response.body() == null)
                {
                    Log.i("RESPONSENULL", call.request().url().toString());
                }
                else
                {
                    Log.i("RESPONSE", call.request().url().toString());
                    Log.i("RESPONSE", response.body().getItems().get(0).getTitle());
                    mutableLiveData.setValue(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Address> call, @NonNull Throwable throwable)
            {
                Log.i("RESPONSE2", call.request().url().url().getPath());
                Log.i("RESPONSE2", call.request().url().toString());
                Log.i("RESPONSE2", Objects.requireNonNull(throwable.getMessage()));
            }
        });

        return mutableLiveData;
    }
}
