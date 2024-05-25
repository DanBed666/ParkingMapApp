package com.example.parkingmapapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditParkingInfoActivity extends AppCompatActivity
{
    EditText parking;
    EditText capacity;
    EditText fee;
    EditText supervised;
    EditText operator;
    Button edit;

    FirebaseDatabase database = FirebaseDatabase.getInstance("https://parkingmapapp-39ec0-default-rtdb.europe-west1.firebasedatabase.app/");
    DatabaseReference parkings = database.getReference("parkings");
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_parking_info);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) ->
        {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        parking = findViewById(R.id.et_parking);
        capacity = findViewById(R.id.et_capacity);
        fee = findViewById(R.id.et_fee);
        supervised = findViewById(R.id.et_supervised);
        operator = findViewById(R.id.et_operator);
        edit = findViewById(R.id.btn_edit);
        String id = getIntent().getStringExtra("KEYID");
        Parking p = (Parking) getIntent().getSerializableExtra("PARKING");

        assert id != null;
        parkings.child(id).addValueEventListener(new ValueEventListener()
        {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                String pkg = snapshot.child("pking").getValue(String.class);
                String cpc = snapshot.child("capacity").getValue(String.class);
                String f33 = snapshot.child("fee").getValue(String.class);
                String sup = snapshot.child("supervised").getValue(String.class);
                String ope = snapshot.child("operator").getValue(String.class);

                parking.setText(pkg);
                capacity.setText(cpc);
                fee.setText(f33);
                supervised.setText(sup);
                operator.setText(ope);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });

        edit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String pkg = parking.getText().toString();
                String cpc = capacity.getText().toString();
                String f33 = fee.getText().toString();
                String sup = supervised.getText().toString();
                String ope = operator.getText().toString();

                assert p != null;
                p.setPking(pkg);
                p.setCapacity(cpc);
                p.setFee(f33);
                p.setSupervised(sup);
                p.setOperator(ope);

                parkings.child(id).child("pking").setValue(p.getPking());
                parkings.child(id).child("capacity").setValue(p.getCapacity());
                parkings.child(id).child("fee").setValue(p.getFee());
                parkings.child(id).child("supervised").setValue(p.getSupervised());
                parkings.child(id).child("operator").setValue(p.getOperator());

                finish();
            }
        });
    }
}