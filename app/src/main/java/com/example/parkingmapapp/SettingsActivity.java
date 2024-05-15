package com.example.parkingmapapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SettingsActivity extends AppCompatActivity
{
    Button logout;
    FirebaseAuth mAuth;
    FirebaseUser user;
    TextView email;
    TextView name;
    TextView surname;
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://parkingmapapp-39ec0-default-rtdb.europe-west1.firebasedatabase.app/");
    DatabaseReference users = database.getReference("users");
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) ->
        {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        name = findViewById(R.id.tv_name);
        surname = findViewById(R.id.tv_surname);
        email = findViewById(R.id.tv_email);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        logout = findViewById(R.id.btn_logout);

        email.setText(user.getEmail());
        users.child(user.getUid()).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                name.setText(snapshot.child("name").getValue(String.class));
                surname.setText(snapshot.child("surname").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });

        logout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mAuth.signOut();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                Toast.makeText(getApplicationContext(), "Wylogowano pomyślnie", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}