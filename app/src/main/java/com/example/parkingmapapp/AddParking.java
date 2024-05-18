package com.example.parkingmapapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddParking extends AppCompatActivity
{
    EditText sample;
    Button add;
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://parkingmapapp-39ec0-default-rtdb.europe-west1.firebasedatabase.app/");
    DatabaseReference users = database.getReference("parkings");
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_parking);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sample = findViewById(R.id.tv_sample);
        add = findViewById(R.id.btn_add);

        String id = getIntent().getStringExtra("KEYID");
        Parking p = (Parking) getIntent().getSerializableExtra("PARKING");

        add.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String sampleText = sample.getText().toString();
                assert p != null;
                p.setSample(sampleText);
                assert id != null;
                users.child(id).setValue(p);
                finish();
            }
        });
    }
}