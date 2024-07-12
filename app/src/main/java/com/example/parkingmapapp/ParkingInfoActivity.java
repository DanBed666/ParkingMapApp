package com.example.parkingmapapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class ParkingInfoActivity extends AppCompatActivity
{
    TextView name;
    TextView parking;
    TextView capacity;
    TextView fee;
    TextView supervised;
    TextView operator;
    Button edit;
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://parkingmapapp-39ec0-default-rtdb.europe-west1.firebasedatabase.app/");
    DatabaseReference parkings = database.getReference("parkings");
    DatabaseReference addedparkings = database.getReference("addedparkings");
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_parking_info);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) ->
        {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        name = findViewById(R.id.tv_name);
        parking = findViewById(R.id.tv_parking);
        capacity = findViewById(R.id.tv_capacity);
        fee = findViewById(R.id.tv_fee);
        supervised = findViewById(R.id.tv_supervised);
        operator = findViewById(R.id.tv_operator);
        edit = findViewById(R.id.btn_edit);

        String id = getIntent().getStringExtra("KEYID");
        Parking p = (Parking) getIntent().getSerializableExtra("PARKING");

        assert id != null;
        Log.i("PARKING_ID", id);

        db.collection("parkings").whereEqualTo("id", id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task)
            {
                if (task.isSuccessful())
                {
                    for (QueryDocumentSnapshot document : task.getResult())
                    {
                        Log.d("LOL", document.getId() + " => " + document.getData());
                        Log.i("XD", document.getId() + " => " + document.getData());
                        String nam = (String) document.getData().get("name");
                        String pkg = (String) document.getData().get("pking");
                        String cpc = (String) document.getData().get("capacity");
                        String f33 = (String) document.getData().get("fee");
                        String sup = (String) document.getData().get("supervised");
                        String ope = (String) document.getData().get("operator");

                        name.setText("Name: " + nam);
                        parking.setText("Parking: " + pkg);
                        capacity.setText("Capacity: " + cpc);
                        fee.setText("Fee: " + f33);
                        supervised.setText("Supervised: " + sup);
                        operator.setText("Operator: " + ope);
                    }
                }
                else
                {
                    Log.w("ERR", "Error getting documents.", task.getException());
                }
            }
        });

        /*

        parkings.child(id).addValueEventListener(new ValueEventListener()
        {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                String nam = snapshot.child("name").getValue(String.class);
                String pkg = snapshot.child("pking").getValue(String.class);
                String cpc = snapshot.child("capacity").getValue(String.class);;
                String f33 = snapshot.child("fee").getValue(String.class);;
                String sup = snapshot.child("supervised").getValue(String.class);;
                String ope = snapshot.child("operator").getValue(String.class);;

                name.setText("Name: " + nam);
                parking.setText("Parking: " + pkg);
                capacity.setText("Capacity: " + cpc);
                fee.setText("Fee: " + f33);
                supervised.setText("Supervised: " + sup);
                operator.setText("Operator: " + ope);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });



        addedparkings.child(id).addValueEventListener(new ValueEventListener()
        {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                String nam = snapshot.child("name").getValue(String.class);
                String pkg = snapshot.child("pking").getValue(String.class);
                String cpc = snapshot.child("capacity").getValue(String.class);;
                String f33 = snapshot.child("fee").getValue(String.class);;
                String sup = snapshot.child("supervised").getValue(String.class);;
                String ope = snapshot.child("operator").getValue(String.class);;

                name.setText("Name: " + nam);
                parking.setText("Parking: " + pkg);
                capacity.setText("Capacity: " + cpc);
                fee.setText("Fee: " + f33);
                supervised.setText("Supervised: " + sup);
                operator.setText("Operator: " + ope);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });


         */

        edit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getApplicationContext(), EditParkingInfoActivity.class);
                intent.putExtra("KEYID", id);
                intent.putExtra("PARKING", p);
                startActivity(intent);
            }
        });
    }
}