package com.example.parkingmapapp;

import android.content.Intent;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AccountActivity extends AppCompatActivity
{
    EditText nameET;
    EditText surnameET;
    Button confirm;
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://parkingmapapp-39ec0-default-rtdb.europe-west1.firebasedatabase.app/");
    DatabaseReference users = database.getReference("users");
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user;
    Button changeMail;
    Button changePass;
    Button deleteAcc;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String documentId;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        nameET = findViewById(R.id.et_name);
        surnameET = findViewById(R.id.et_surname);
        confirm = findViewById(R.id.btn_confirm);
        changeMail = findViewById(R.id.btn_mailchange);
        changePass = findViewById(R.id.btn_passchange);
        deleteAcc = findViewById(R.id.btn_delete);
        user = mAuth.getCurrentUser();

        assert user != null;
        documentId = getIntent().getStringExtra("DOCUMENTID");

        db.collection("users").whereEqualTo("uId", user.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
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
                        String nameDb = (String) document.getData().get("name");
                        String surnameDb = (String) document.getData().get("surname");
                        nameET.setText(nameDb);
                        surnameET.setText(surnameDb);
                    }
                }
                else
                {
                    Log.w("ERR", "Error getting documents.", task.getException());
                }
            }
        });

        /*users.child(user.getUid()).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                DataSnapshot name = snapshot.child("name");
                DataSnapshot surname = snapshot.child("surname");

                if (name.exists() && surname.exists())
                {
                    nameET.setText(name.getValue(String.class));
                    surnameET.setText(surname.getValue(String.class));
                    Log.i("NAME", Objects.requireNonNull(name.getValue(String.class)));
                    Log.i("SURNAME", Objects.requireNonNull(surname.getValue(String.class)));
                }
                else
                {
                    Log.i("BRAK", Objects.requireNonNull("BRAK"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });

         */

        confirm.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String name = nameET.getText().toString();
                String surname = surnameET.getText().toString();
                User u = new User(user.getUid(), name, surname);

                addUser(u);
                finish();
                Toast.makeText(getApplicationContext(), "Zmiany zostały zapisane!", Toast.LENGTH_SHORT).show();
            }
        });

        changeMail.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(getApplicationContext(), EmailChangeActivity.class));
            }
        });

        changePass.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(getApplicationContext(), PasswordChangeActivity.class));
            }
        });

        deleteAcc.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(getApplicationContext(), DeleteAccountActivity.class));
            }
        });
    }

    public void addUser(User user)
    {
        Map<String, Object> mapa = new HashMap<>();
        mapa.put("name", user.getName());
        mapa.put("surname", user.getSurname());
        db.collection("users").document(documentId).update(mapa).addOnSuccessListener(new OnSuccessListener<Void>()
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