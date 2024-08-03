package com.example.parkingmapapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.checkout.base.model.Country;
import com.checkout.base.model.Environment;
import com.checkout.frames.api.PaymentFlowHandler;
import com.checkout.frames.api.PaymentFormMediator;
import com.checkout.frames.screen.paymentform.model.BillingFormAddress;
import com.checkout.frames.screen.paymentform.model.PaymentFormConfig;
import com.checkout.frames.screen.paymentform.model.PrefillData;
import com.checkout.frames.style.component.PayButtonComponentStyle;
import com.checkout.frames.style.component.ScreenHeaderStyle;
import com.checkout.frames.style.component.addresssummary.AddressSummaryComponentStyle;
import com.checkout.frames.style.component.base.ButtonStyle;
import com.checkout.frames.style.component.base.ContainerStyle;
import com.checkout.frames.style.component.base.ImageStyle;
import com.checkout.frames.style.component.base.InputComponentStyle;
import com.checkout.frames.style.component.base.TextLabelStyle;
import com.checkout.frames.style.component.base.TextStyle;
import com.checkout.frames.style.screen.PaymentDetailsStyle;
import com.checkout.frames.style.screen.PaymentFormStyle;
import com.checkout.frames.style.theme.DefaultPaymentFormTheme;
import com.checkout.frames.style.theme.PaymentFormComponent;
import com.checkout.frames.style.theme.PaymentFormComponentBuilder;
import com.checkout.frames.style.theme.PaymentFormComponentField;
import com.checkout.frames.style.theme.PaymentFormTheme;
import com.checkout.threedsecure.model.ThreeDSRequest;
import com.checkout.threedsecure.model.ThreeDSResult;
import com.checkout.tokenization.model.Address;
import com.checkout.tokenization.model.Phone;
import com.checkout.tokenization.model.TokenDetails;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class PaymentActivity extends AppCompatActivity
{
    private final String PUBLIC_KEY = "pk_test_51Pj6PVRrVVwJdPXUYAxptYWgjXZowfZ3UrCllUVMykgq0zeoo7ux78GMQkrrS4mrniOwEI0ZOZxfdT9hX8XGUpkI00hExNrw5a";
    private final String SECRET_KEY = "sk_test_51Pj6PVRrVVwJdPXUpDHtDQd7rg3pL8JqX3ciVb4SskPxlHnNRg9BPDDtgRfOnVRiAOFrPCxQFid9IbuMLFNerGv500wby6gmbq";
    PaymentSheet paymentSheet;
    String customerID;
    String ephemeralKey;
    String clientSecret;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payment);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) ->
        {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button pay;
        pay = findViewById(R.id.btn_pay);

        PaymentConfiguration.init(getApplicationContext(), PUBLIC_KEY);

        paymentSheet = new PaymentSheet(this, paymentSheetResult -> {
            onPaymentResult(paymentSheetResult);
        });

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://api.stripe.com/v1/customers",
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String s)
                    {
                        try
                        {
                            JSONObject jsonObject = new JSONObject(s);
                            customerID = jsonObject.getString("id");
                            Log.i("KEYS", customerID);

                            getEthemeralKey(customerID);
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError volleyError)
            {

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + SECRET_KEY);
                Log.i("KEYS", Objects.requireNonNull(headers.get("Authorization")));
                return headers;
            }
        };

        pay.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                paymentFlow();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private void onPaymentResult(PaymentSheetResult paymentSheetResult)
    {
        if (paymentSheetResult instanceof PaymentSheetResult.Completed)
        {
            Toast.makeText(getApplicationContext(), "Payment Success", Toast.LENGTH_SHORT).show();
        }
        else if (paymentSheetResult instanceof PaymentSheetResult.Failed)
        {
            Log.i("KEYS", Objects.requireNonNull(((PaymentSheetResult.Failed) paymentSheetResult).getError().getMessage()));
            Log.i("KEYS", PUBLIC_KEY);
            Log.i("KEYS", SECRET_KEY);
        }
    }

    public void getEthemeralKey(String customerID)
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://api.stripe.com/v1/ephemeral_keys",
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String s)
                    {
                        try
                        {
                            JSONObject jsonObject = new JSONObject(s);
                            ephemeralKey = jsonObject.getString("secret");
                            Log.i("KEYS", ephemeralKey);

                            getClientSecret(customerID, ephemeralKey);
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError volleyError)
            {

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + SECRET_KEY);
                headers.put("Stripe-Version", "2024-06-20");
                return headers;
            }

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<>();
                params.put("customer", customerID);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private void getClientSecret(String customerID, String ephemeralKey)
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://api.stripe.com/v1/payment_intents",
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String s)
                    {
                        try
                        {
                            JSONObject jsonObject = new JSONObject(s);
                            clientSecret = jsonObject.getString("client_secret");
                            Log.i("KEYS", clientSecret);
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError volleyError)
            {

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + SECRET_KEY);
                return headers;
            }

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<>();
                params.put("customer", customerID);
                params.put("amount", "10" + "00");
                params.put("currency", "pln");
                params.put("automatic_payment_methods[enabled]", "true");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    public void paymentFlow()
    {
        paymentSheet.presentWithPaymentIntent(clientSecret, new PaymentSheet.Configuration(
                "ABC Company",
                new PaymentSheet.CustomerConfiguration(customerID, ephemeralKey)
        ));
    }
}