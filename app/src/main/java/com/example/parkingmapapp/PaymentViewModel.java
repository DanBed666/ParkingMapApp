package com.example.parkingmapapp;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentViewModel
{
    PaymentRespository paymentRespository;
    public PaymentViewModel()
    {
        paymentRespository = new PaymentRespository();
    }
    public MutableLiveData<String> getCustomerIdVm(String secretKey)
    {
        return paymentRespository.getCustomerId(secretKey);
    }

    public MutableLiveData<String> getEphemeralKeyVm(Map<String, String> headers, String customerID)
    {
        return paymentRespository.getEphemeralKey(headers, customerID);
    }

    public MutableLiveData<String> getClientSecretVm(String authorization, String customerID, String amount, String currency, String auto_pay_meth_enabled)
    {
        return paymentRespository.getClientSecret(authorization, customerID, amount, currency, auto_pay_meth_enabled);
    }
}
