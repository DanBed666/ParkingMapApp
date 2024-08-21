package com.example.parkingmapapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

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
    String id;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
    String price;
    String finalPriceStr;
    int hours;
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

        Button pay = findViewById(R.id.btn_pay);

        PaymentConfiguration.init(getApplicationContext(), PUBLIC_KEY);

        paymentSheet = new PaymentSheet(this, paymentSheetResult -> {
            onPaymentResult(paymentSheetResult);
        });

        bearerToken = "Bearer " + SECRET_KEY;
        headers.put("Authorization", bearerToken);
        headers.put("Stripe-Version", "2024-06-20");

        id = getIntent().getStringExtra("KEYID");
        String price = getIntent().getStringExtra("PRICE");
        hours = getIntent().getIntExtra("HOURS", 0);

        int finalPrice = Integer.parseInt(price) * hours;
        finalPriceStr = finalPrice + "00";

        assert price != null;
        Log.i("PRICE", price);

        getCustomerId(finalPriceStr);
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
            getCustomerId(finalPriceStr);
            Toast.makeText(getApplicationContext(), "Płatność zakończona sukcesem!", Toast.LENGTH_SHORT).show();
            String ticketId = generateTicketId();
            Intent intent = new Intent(getApplicationContext(), TicketActivity.class);
            intent.putExtra("TICKETID", ticketId);
            startActivity(intent);
            Ticket ticket = new Ticket(user.getUid(), getActualDate(), getValidDate(hours), ticketId);
            addTicket(ticket);
            finish();
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

    public void getCustomerId(String finalPrice)
    {
        paymentViewModel.getCustomerIdVm(bearerToken).observeForever(new Observer<String>()
        {
            @Override
            public void onChanged(String id)
            {
                customerId = id;
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
                ephemeralKey = key;
                Log.i("PAYMENTID", key);
            }
        });
    }

    public void getClientSecret(String customerId, String finalPrize)
    {
        paymentViewModel.getClientSecretVm(bearerToken, customerId, finalPrize, "pln", "true").observeForever(new Observer<String>()
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

    public String generateTicketId()
    {
        StringBuilder chain = new StringBuilder();
        Random rand = new Random();

        for (int i = 0; i < 12; i++)
        {
            chain.append((char) (rand.nextInt(26) + 65));
        }

        return chain.toString();
    }

    public void addTicket(Ticket ticket)
    {
        db.collection("tickets").document(ticket.getTicketId()).set(ticket).addOnSuccessListener(new OnSuccessListener<Void>()
        {
            @Override
            public void onSuccess(Void unused)
            {
                Log.i("CREATED", "created");
            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Log.i("CREATED", e.getMessage());
            }
        });
    }

    public String getActualDate()
    {
        Calendar calender = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        String formattedDate = df.format(calender.getTime());
        return formattedDate;
    }

    public String getValidDate(int hours)
    {
        Calendar calender = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        calender.add(Calendar.HOUR, hours);
        String formattedDate = df.format(calender.getTime());
        return formattedDate;
    }
}