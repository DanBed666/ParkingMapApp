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
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.osmdroid.util.GeoPoint;

import java.util.Objects;
import java.util.Random;

public class AddParkingActivity extends AppCompatActivity
{
    EditText nameET;
    EditText parkingET;
    EditText capacityET;
    EditText feeET;
    EditText supervisedET;
    EditText operatorET;
    Button createET;
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://parkingmapapp-39ec0-default-rtdb.europe-west1.firebasedatabase.app/");
    DatabaseReference parkings = database.getReference("parkings");
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
                String name = nameET.getText().toString();
                String parking = parkingET.getText().toString();
                String capacity = capacityET.getText().toString();
                String fee = feeET.getText().toString();
                String supervised = supervisedET.getText().toString();
                String operator = operatorET.getText().toString();

                assert location != null;
                Parking newParking = new Parking(name, parking, capacity, fee, supervised, operator, location.getLatitude(), location.getLongitude());
                parkings.child(generateId()).setValue(newParking).addOnCompleteListener(new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(getApplicationContext(), "Dodano nowy parking", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "Nie udało się dodać parkingu", Toast.LENGTH_SHORT).show();
                            Log.e("ERRORPARK", Objects.requireNonNull(Objects.requireNonNull(task.getException()).getMessage()));
                            finish();
                        }
                    }
                });
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