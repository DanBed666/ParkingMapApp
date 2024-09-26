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
    PaymentSheet paymentSheet;
    String parkingId;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    PaymentManager paymentManager = new PaymentManager();
    String finalPriceStr;
    int hours;
    GetTagData get = new GetTagData();
    DatabaseManager dbm = new DatabaseManager();
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

        Button pay = findViewById(R.id.btn_pay);

        PaymentConfiguration.init(getApplicationContext(), PUBLIC_KEY);

        paymentSheet = new PaymentSheet(this, paymentSheetResult -> {
            onPaymentResult(paymentSheetResult);
        });

        parkingId = getIntent().getStringExtra("KEYID");
        String price = getIntent().getStringExtra("PRICE");
        hours = getIntent().getIntExtra("HOURS", 0);

        assert price != null;
        Log.i("PRICE", price);

        int finalPrice = Integer.parseInt(price) * hours;
        finalPriceStr = finalPrice + "00";
        paymentManager.getCustomerId(finalPriceStr);
        Map <String, String> credentials = paymentManager.getCredentials();
        pay.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.i("COMPLETE", Objects.requireNonNull(credentials.get("id")));
                Log.i("COMPLETE", Objects.requireNonNull(credentials.get("ephemeral")));
                Log.i("COMPLETE", Objects.requireNonNull(credentials.get("secret")));
                paymentFlow(credentials.get("id"), credentials.get("ephemeral"), credentials.get("secret"));
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

    private void onPaymentResult(PaymentSheetResult paymentSheetResult)
    {
        if (paymentSheetResult instanceof PaymentSheetResult.Completed)
        {
            paymentManager.getCustomerId(finalPriceStr);
            Toast.makeText(getApplicationContext(), "Płatność zakończona sukcesem!",
                    Toast.LENGTH_SHORT).show();
            String ticketId = get.generateTicketId();
            createTicket(ticketId);
            Intent intent = new Intent(getApplicationContext(), TicketActivity.class);
            intent.putExtra("TICKETID", ticketId);
            intent.putExtra("PARKINGID", parkingId);
            startActivity(intent);
            finish();
        }
        else if (paymentSheetResult instanceof PaymentSheetResult.Failed)
        {
            Toast.makeText(getApplicationContext(), "Payment Failure", Toast.LENGTH_SHORT).show();
            Log.i("KEYS", Objects.requireNonNull((
                    (PaymentSheetResult.Failed) paymentSheetResult).getError().getMessage()));
        }
        else if (paymentSheetResult instanceof PaymentSheetResult.Canceled)
        {
            Toast.makeText(getApplicationContext(), "Payment Cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    public void createTicket(String ticketId)
    {
        Ticket ticket = new Ticket(user.getUid(), get.getActualDate(),
                get.getValidDate(hours), ticketId, parkingId);
        dbm.addElement("tickets", ticketId, ticket);
    }
}