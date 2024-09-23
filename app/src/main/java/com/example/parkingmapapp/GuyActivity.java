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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GuyActivity extends AppCompatActivity
{
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String documentId;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_guy);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        EditText nameET;
        EditText surnameET;
        Button confirm;
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user;

        nameET = findViewById(R.id.et_name);
        surnameET = findViewById(R.id.et_surname);
        confirm = findViewById(R.id.btn_confirm);
        user = mAuth.getCurrentUser();

        DatabaseManager dbm = new DatabaseManager();
        Query q = db.collection("users").whereEqualTo("uId", user.getUid());

        assert user != null;
        documentId = getIntent().getStringExtra("DOCUMENTID");

        dbm.getElements(q, new OnElementsGet()
        {
            @Override
            public void setOnElementsGet(List<DocumentSnapshot> documentSnapshotList)
            {
                for (DocumentSnapshot document : documentSnapshotList)
                {
                    Log.d("LOL", document.getId() + " => " + document.getData());
                    Log.i("XD", "xd");
                    String nameDb = (String) Objects.requireNonNull(document.getData()).get("name");
                    String surnameDb = (String) document.getData().get("surname");
                    nameET.setText(nameDb);
                    surnameET.setText(surnameDb);
                }
            }
        });

        confirm.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String name = nameET.getText().toString();
                String surname = surnameET.getText().toString();

                Map<String, Object> mapa = new HashMap<>();
                mapa.put("name", name);
                mapa.put("surname", surname);

                dbm.addElement("users", documentId, mapa);

                finish();
                Toast.makeText(getApplicationContext(), "Zmiany zostały zapisane!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}