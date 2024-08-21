package com.example.parkingmapapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CarsAddedActivity extends AppCompatActivity implements RefreshListener
{
    RecyclerView recyclerView;
    CarsAdapter carsAdapter;
    Button new_car;
    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    RefreshListener refreshListener;
    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cars_added);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) ->
        {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        new_car = findViewById(R.id.new_car);

        refreshListener = this;

        recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(true);
        new_car.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(getApplicationContext(), AddCarActivity.class));
            }
        });

        getCars();
    }

    public void getCars()
    {
        db.collection("cars").whereEqualTo("userId", user.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task)
            {
                if (task.isSuccessful())
                {
                    carsAdapter = new CarsAdapter(getApplicationContext(), task.getResult().getDocuments(), refreshListener);
                    recyclerView.setAdapter(carsAdapter);
                    int pos = 0;

                    for (DocumentSnapshot ds : task.getResult().getDocuments())
                    {
                        pos++;
                        carsAdapter.notifyItemChanged(pos);
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

    @Override
    public void refresh()
    {
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        Log.i("STOP", "stop");
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }
}