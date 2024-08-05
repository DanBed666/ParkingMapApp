package com.example.parkingmapapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PaymentActivity extends AppCompatActivity
{
    private final String PUBLIC_KEY = "pk_test_51Pj6PVRrVVwJdPXUYAxptYWgjXZowfZ3UrCllUVMykgq0zeoo7ux78GMQkrrS4mrniOwEI0ZOZxfdT9hX8XGUpkI00hExNrw5a";
    private final String SECRET_KEY = "sk_test_51Pj6PVRrVVwJdPXUpDHtDQd7rg3pL8JqX3ciVb4SskPxlHnNRg9BPDDtgRfOnVRiAOFrPCxQFid9IbuMLFNerGv500wby6gmbq";
    PaymentSheet paymentSheet;
    PaymentViewModel paymentViewModel;
    String bearerToken;
    Map<String, String> headers = new HashMap<>();
    String customerId;
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

        paymentViewModel = new PaymentViewModel();

        Button pay;
        pay = findViewById(R.id.btn_pay);

        PaymentConfiguration.init(getApplicationContext(), PUBLIC_KEY);

        paymentSheet = new PaymentSheet(this, paymentSheetResult -> {
            onPaymentResult(paymentSheetResult);
        });

        bearerToken = "Bearer " + SECRET_KEY;
        headers.put("Authorization", bearerToken);
        headers.put("Stripe-Version", "2024-06-20");

        getCustomerId();
        pay.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.i("COMPLETE", customerId);
                Log.i("COMPLETE", ephemeralKey);
                Log.i("COMPLETE", clientSecret);
                paymentFlow(customerId, ephemeralKey, clientSecret);
            }
        });
    }

    private void onPaymentResult(PaymentSheetResult paymentSheetResult)
    {
        if (paymentSheetResult instanceof PaymentSheetResult.Completed)
        {
            Toast.makeText(getApplicationContext(), "Payment Success", Toast.LENGTH_SHORT).show();
        }
        else if (paymentSheetResult instanceof PaymentSheetResult.Failed)
        {
            Toast.makeText(getApplicationContext(), "Payment Failure", Toast.LENGTH_SHORT).show();
            Log.i("KEYS", Objects.requireNonNull(((PaymentSheetResult.Failed) paymentSheetResult).getError().getMessage()));
        }
        else if (paymentSheetResult instanceof PaymentSheetResult.Canceled)
        {
            Toast.makeText(getApplicationContext(), "Payment Cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    public void getCustomerId()
    {
        paymentViewModel.getCustomerIdVm(bearerToken).observeForever(new Observer<String>()
        {
            @Override
            public void onChanged(String id)
            {
                customerId = id;
                Log.i("PAYMENTID", id);

                getEphemeralKey(id);
                getClientSecret(id);
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
                ephemeralKey = key;
                Log.i("PAYMENTID", key);
            }
        });
    }

    public void getClientSecret(String customerId)
    {
        paymentViewModel.getClientSecretVm(bearerToken, customerId, "1700", "pln", "true").observeForever(new Observer<String>()
        {
            @Override
            public void onChanged(String secret)
            {
                clientSecret = secret;
                Log.i("PAYMENTID", secret);
            }
        });
    }

    public void paymentFlow(String customerId, String ephemeralKey, String clientSecret)
    {
        paymentSheet.presentWithPaymentIntent(clientSecret, new PaymentSheet.Configuration(
                "ABC company",
                new PaymentSheet.CustomerConfiguration(customerId, ephemeralKey)
        ));
    }
}