package com.example.parkingmapapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Random;

public class AddCarActivity extends AppCompatActivity
{
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_car);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) ->
        {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        EditText markaET = findViewById(R.id.et_marka);
        EditText modelET = findViewById(R.id.et_model);
        Spinner typSpinner = findViewById(R.id.spinner_car);
        EditText numerET = findViewById(R.id.et_numer);
        EditText rokET = findViewById(R.id.et_rok);
        Button confirm = findViewById(R.id.btn_add);
        SwitchMaterial primarySw = findViewById(R.id.switch_primary);

        String[] types = {"Samochód osobowy", "Tir", "Motocykl", "Autokar"};

        ArrayAdapter<String> aa = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, types);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typSpinner.setAdapter(aa);

        confirm.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int position = typSpinner.getSelectedItemPosition();

                boolean primary = false;
                String marka = markaET.getText().toString();
                String model = modelET.getText().toString();
                String typ = types[position];
                String numer = numerET.getText().toString();
                String rok = rokET.getText().toString();

                if (primarySw.isChecked())
                {
                    primary = true;
                }

                Car car = new Car(generateCarId(), user.getUid(), marka, model, typ, numer, rok, primary);

                addCar(car);
                finish();
            }
        });
    }

    public void addCar(Car car)
    {
        db.collection("cars").add(car).addOnSuccessListener(new OnSuccessListener<DocumentReference>()
        {
            @Override
            public void onSuccess(DocumentReference documentReference)
            {
                Log.d("TEST", "DocumentSnapshot added with ID: " + documentReference.getId());
            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Log.d("ERROR", "Error: " + e.getMessage());
            }
        });
    }

    public String generateCarId()
    {
        StringBuilder chain = new StringBuilder();
        Random rand = new Random();

        for (int i = 0; i < 12; i++)
        {
            chain.append((char) (rand.nextInt(26) + 65));
        }

        return chain.toString();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        if (resultCode == RESULT_OK)
        {
            Log.i("DUpa", "dupa");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}