package com.example.parkingmapapp;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.stripe.model.Customer;
import com.stripe.model.EphemeralKey;
import com.stripe.model.ExpandableField;
import com.stripe.model.PaymentIntent;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.Header;

public class PaymentRespository
{
    public MutableLiveData<String> getCustomerId(String secretKey)
    {
        MutableLiveData<String> mutableLiveData = new MutableLiveData<>();
        RetrofitBuilder.getRetrofitService().createCustomer(secretKey).enqueue(new Callback<Customer>()
        {
            @Override
            public void onResponse(Call<Customer> call, Response<Customer> response)
            {
                assert response.body() != null;
                String id = response.body().getId();
                Log.i("WYKON", "lol");
                mutableLiveData.setValue(id);
            }

            @Override
            public void onFailure(Call<Customer> call, Throwable throwable)
            {

            }
        });

        return mutableLiveData;
    }

    public MutableLiveData<String> getEphemeralKey(Map<String, String> headers, String customerID)
    {
        MutableLiveData<String> mutableLiveData = new MutableLiveData<>();
        RetrofitBuilder.getRetrofitService().createEphemeralKey(headers, customerID).enqueue(new Callback<EphemeralKey>()
        {
            @Override
            public void onResponse(@NonNull Call<EphemeralKey> call, @NonNull Response<EphemeralKey> response)
            {
                assert response.body() != null;
                String ephemeralKey = response.body().getSecret();
                Log.i("WYKON", "lol");
                mutableLiveData.setValue(ephemeralKey);
            }

            @Override
            public void onFailure(@NonNull Call<EphemeralKey> call, @NonNull Throwable throwable)
            {

            }
        });

        return mutableLiveData;
    }

    public MutableLiveData<String> getClientSecret(String authorization, String customerID, String amount, String currency, String auto_pay_meth_enabled)
    {
        MutableLiveData<String> mutableLiveData = new MutableLiveData<>();
        RetrofitBuilder.getRetrofitService().createPaymentIntent(authorization, customerID, amount, currency, auto_pay_meth_enabled).enqueue(new Callback<PaymentIntent2>()
        {
            @Override
            public void onResponse(@NonNull Call<PaymentIntent2> call, @NonNull Response<PaymentIntent2> response)
            {
                assert response.body() != null;
                String clientSecret = response.body().getClientSecret();
                Log.i("WYKON", "lol");
                mutableLiveData.setValue(clientSecret);
            }

            @Override
            public void onFailure(@NonNull Call<PaymentIntent2> call, @NonNull Throwable throwable)
            {
                Log.i("WYKON", Objects.requireNonNull(throwable.getMessage()));
                Log.i("WYKON", Objects.requireNonNull(throwable.getLocalizedMessage()));
            }
        });

        return mutableLiveData;
    }
}
