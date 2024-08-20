package com.example.parkingmapapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EditCarActivity extends AppCompatActivity {

    String carId;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    EditText markaET;
    EditText modelET;
    EditText typET;
    EditText numerET;
    EditText rokET;
    FirebaseAuth mAuth;
    FirebaseUser user;
    String documentId;
    SwitchMaterial primarySw;
    Spinner typSpinner;
    String[] types = {"Samochód osobowy", "Tir", "Motocykl", "Autokar"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_car);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        carId = getIntent().getStringExtra("CARID");
        documentId = getIntent().getStringExtra("DOCUMENTID");
        markaET = findViewById(R.id.et_marka);
        modelET = findViewById(R.id.et_model);
        typSpinner = findViewById(R.id.spinner_car);
        numerET = findViewById(R.id.et_numer);
        rokET = findViewById(R.id.et_rok);
        Button confirm = findViewById(R.id.btn_add);
        primarySw = findViewById(R.id.switch_primary);


        ArrayAdapter<String> aa = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, types);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typSpinner.setAdapter(aa);

        getCars();
        confirm.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int position = typSpinner.getSelectedItemPosition();

                String marka = markaET.getText().toString();
                String model = modelET.getText().toString();
                String typ = types[position];
                String numer = numerET.getText().toString();
                String rok = rokET.getText().toString();
                boolean primary = false;

                if (primarySw.isChecked())
                    primary = true;

                addCar(new Car(carId, user.getUid(), marka, model, typ, numer, rok, primary));
                Intent intent = new Intent(getApplicationContext(), AddCarActivity.class);
                setResult(RESULT_OK, intent);
                finish();
                Toast.makeText(getApplicationContext(), "Zmiany zostały zapisane!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getCars()
    {
        db.collection("cars").whereEqualTo("id", carId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task)
            {
                if (task.isSuccessful())
                {
                    for (QueryDocumentSnapshot document : task.getResult())
                    {
                        Log.d("LOL", document.getId() + " => " + document.getData());
                        Log.i("XD", "xd");
                        String marka = (String) document.getData().get("marka");
                        String model = (String) document.getData().get("model");
                        String type = (String) document.getData().get("type");
                        String reg = (String) document.getData().get("registrationNumber");
                        String year = (String) document.getData().get("year");
                        boolean primary = (boolean) document.getData().get("primary");

                        markaET.setText(marka);
                        modelET.setText(model);

                        for (int i = 0; i < types.length; i++)
                        {
                            if (Objects.equals(type, types[i]))
                            {
                                typSpinner.setSelection(i);
                                break;
                            }
                        }

                        numerET.setText(reg);
                        rokET.setText(year);

                        if (primary)
                            primarySw.setChecked(true);
                    }
                }
                else
                {
                    Log.w("ERR", "Error getting documents.", task.getException());
                }
            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Log.e("ERR2", "Error getting documents.", e);
            }
        });
    }

    public void addCar(Car car)
    {
        Map<String, Object> mapa = new HashMap<>();
        mapa.put("marka", car.getMarka());
        mapa.put("model", car.getModel());
        mapa.put("primary", car.isPrimary());
        mapa.put("registrationNumber", car.getRegistrationNumber());
        mapa.put("type", car.getType());
        mapa.put("year", car.getYear());
        db.collection("cars").document(documentId).update(mapa).addOnSuccessListener(new OnSuccessListener<Void>()
        {
            @Override
            public void onSuccess(Void documentReference)
            {
                Log.d("TEST", "DocumentSnapshot added with ID");
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
}