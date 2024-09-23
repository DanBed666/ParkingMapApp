package com.example.parkingmapapp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.Observer;

import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PaymentManager
{
    PaymentViewModel paymentViewModel = new PaymentViewModel();
    private final String SECRET_KEY = "sk_test_51Pj6PVRrVVwJdPXUpDHtDQd7rg3pL8JqX3ciVb4SskPxlHnNRg9BPDDtgRfOnVRiAOFrPCxQFid9IbuMLFNerGv500wby6gmbq";
    String bearerToken;
    public Map<String, String> getCredentials()
    {
        return credentials;
    }

    Map<String, String> headers = new HashMap<>();
    Map<String, String> credentials = new HashMap<>();
    public PaymentManager()
    {
        bearerToken = "Bearer " + SECRET_KEY;
        headers.put("Authorization", bearerToken);
        headers.put("Stripe-Version", "2024-06-20");
    }
    public void getCustomerId(String finalPrice)
    {
        paymentViewModel.getCustomerIdVm(bearerToken).observeForever(new Observer<String>()
        {
            @Override
            public void onChanged(String id)
            {
                credentials.put("id", id);
                Log.i("PAYMENTID", id);

                getEphemeralKey(id);
                getClientSecret(id, finalPrice);
            }
        });
    }

    public void getEphemeralKey(String customerId)
    {
        paymentViewModel.getEphemeralKeyVm(headers, customerId).observeForever(new Observer<String>()
        {
            @Override
            public void onChanged(String key)
            {
                credentials.put("ephemeral", key);
                Log.i("PAYMENTID", key);
            }
        });
    }

    public void getClientSecret(String customerId, String finalPrize)
    {
        paymentViewModel.getClientSecretVm(bearerToken, customerId, finalPrize,
                        "pln", "true")
                .observeForever(new Observer<String>()
        {
            @Override
            public void onChanged(String secret)
            {
                credentials.put("secret", secret);
                Log.i("PAYMENTID", secret);
            }
        });
    }
}
