package com.example.parkingmapapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ParkingBookingActivity extends AppCompatActivity
{
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String id;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_parking_booking);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) ->
        {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button pay = findViewById(R.id.btn_pay);
        Button pay2 = findViewById(R.id.btn_pay2);
        Button pay3 = findViewById(R.id.btn_pay3);

        intent = new Intent(getApplicationContext(), PaymentActivity.class);

        id = getIntent().getStringExtra("KEYID");

        pay.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                intent.putExtra("KEYID", id);
                intent.putExtra("HOURS", 1);
                startActivity(intent);
            }
        });

        pay2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                intent.putExtra("KEYID", id);
                intent.putExtra("HOURS", 2);
                startActivity(intent);
            }
        });

        pay3.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                intent.putExtra("KEYID", id);
                intent.putExtra("HOURS", 3);
                startActivity(intent);
            }
        });

        setPrice(pay, pay2, pay3);
    }

    public void setPrice(Button pay, Button pay2, Button pay3)
    {
        DatabaseManager dbm = new DatabaseManager();
        Query q = db.collection("parkings").whereEqualTo("id", id);

        dbm.getElements(q, new OnElementsGet()
        {
            @Override
            public void setOnElementsGet(List<DocumentSnapshot> documentSnapshotList)
            {
                for (DocumentSnapshot document : documentSnapshotList)
                {
                    String kwota = (String) document.getData().get("kwota");
                    assert kwota != null;
                    Log.i("KWOTA", kwota);

                    pay.setText(String.format("1 godzina, cena: %d zł", Integer.parseInt(kwota)));
                    pay2.setText(String.format("2 godziny, cena: %d zł", Integer.parseInt(kwota) * 2));
                    pay3.setText(String.format("3 godziny, cena: %d zł", Integer.parseInt(kwota) * 3));
                    intent.putExtra("PRICE", kwota);
                }
            }
        });
    }
}