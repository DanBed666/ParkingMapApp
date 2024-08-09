package com.example.parkingmapapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.osmdroid.util.GeoPoint;

import java.util.Objects;
import java.util.Random;

public class AddParkingActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_parking2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) ->
        {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        EditText nameET;
        EditText parkingET;
        EditText capacityET;
        EditText feeET;
        EditText supervisedET;
        EditText operatorET;
        Button createET;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        nameET = findViewById(R.id.et_name);
        parkingET = findViewById(R.id.et_pking);
        capacityET = findViewById(R.id.et_capacity);
        feeET = findViewById(R.id.et_fee);
        supervisedET = findViewById(R.id.et_supervised);
        operatorET = findViewById(R.id.et_operator);
        createET = findViewById(R.id.btn_create);

        GeoPoint location = getIntent().getParcelableExtra("LOCATION");

        createET.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String id = generateId();
                String name = nameET.getText().toString();
                String parking = parkingET.getText().toString();
                String capacity = capacityET.getText().toString();
                String fee = feeET.getText().toString();
                String supervised = supervisedET.getText().toString();
                String operator = operatorET.getText().toString();

                assert location != null;
                assert user != null;
                Parking newParking = new Parking(user.getUid(), id, name, parking, capacity, fee, supervised, operator, location.getLatitude(), location.getLongitude(), true);

                db.collection("parkings").document(id).set(newParking).addOnSuccessListener(new OnSuccessListener<Void>()
                {
                    @Override
                    public void onSuccess(Void unused)
                    {
                        Log.i("CREATEDADD", "created");
                    }
                }).addOnFailureListener(new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        Log.e("ERROR", Objects.requireNonNull(e.getMessage()));
                    }
                });

                db.collection("addedparkings").document(id).set(newParking).addOnSuccessListener(new OnSuccessListener<Void>()
                {
                    @Override
                    public void onSuccess(Void unused)
                    {
                        Log.i("CREATEDADD", "created");
                    }
                }).addOnFailureListener(new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        Log.e("ERROR", Objects.requireNonNull(e.getMessage()));
                    }
                });

                finish();
            }
        });
    }

    public String generateId()
    {
        Random random = new Random();
        StringBuilder chain = new StringBuilder();

        for (int i = 1; i <= 10; i++)
        {
            char c = (char)(random.nextInt(26) + 'a');
            chain.append(c);
        }

        return chain.toString();
    }
}