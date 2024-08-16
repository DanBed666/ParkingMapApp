package com.example.parkingmapapp;

import com.stripe.model.Customer;
import com.stripe.model.EphemeralKey;
import com.stripe.model.PaymentIntent;
import java.util.Map;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitService
{
    String BASE_URL = "https://api.stripe.com/v1/";
    String NOMINATIM_URL = "https://revgeocode.search.hereapi.com/v1/";

    @POST("customers")
    Call<Customer> createCustomer(@Header("Authorization") String authorization);

    @FormUrlEncoded
    @POST("ephemeral_keys")
    Call<EphemeralKey> createEphemeralKey(@HeaderMap Map<String, String> headers, @Field("customer") String customerID);

    /*
    @POST("payment_intents")
    Call<PaymentIntent> createPaymentIntent(@Header("Authorization") String authorization,
                                            @Body String customerID,
                                            @Body String amount,
                                            @Body String currency,
                                            @Body String automatic_payment_methods_enabled);

     */
    @FormUrlEncoded
    @POST("payment_intents")
    Call<PaymentIntent2> createPaymentIntent(@Header("Authorization") String authorization,
                                            @Field("customer") String customerID,
                                            @Field("amount") String amount,
                                            @Field("currency") String currency,
                                            @Field("automatic_payment_methods[enabled]") String automatic_payment_methods_enabled);


    @GET("revgeocode")
    Call<Address> getAddress(@Query("at")String geoPoint, @Query("apiKey")String apiKey);
}
