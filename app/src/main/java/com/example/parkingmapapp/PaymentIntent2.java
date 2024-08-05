package com.example.parkingmapapp;

import com.google.gson.annotations.SerializedName;
import com.stripe.model.Customer;
import com.stripe.model.ExpandableField;

public class PaymentIntent2
{
    @SerializedName("client_secret")
    String clientSecret;

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }
}
