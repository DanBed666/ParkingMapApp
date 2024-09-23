package com.example.parkingmapapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Objects;

public class SettingsActivity extends AppCompatActivity
{
    String documentId;
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

        FirebaseAuth mAuth;
        FirebaseUser user;
        TextView email;
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        TextView name = findViewById(R.id.tv_name);
        TextView surname = findViewById(R.id.tv_surname);
        Button cars = findViewById(R.id.btn_cars);
        Button parkings = findViewById(R.id.btn_parkings);
        Button bookings = findViewById(R.id.btn_bookings);
        Button logout = findViewById(R.id.btn_logout);
        Button deleteAcc = findViewById(R.id.btn_delete);
        Button profileInfo = findViewById(R.id.btn_info);

        email = findViewById(R.id.tv_email);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        assert user != null;
        email.setText(user.getEmail());
        Log.i("UID", user.getUid());

        profileInfo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getApplicationContext(), InfoProfileActivity.class);
                intent.putExtra("CASE", "konto");
                intent.putExtra("ID", user.getUid());
                startActivity(intent);
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

        cars.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(getApplicationContext(), CarsAddedActivity.class));
            }
        });

        parkings.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(getApplicationContext(), ParkingsAddedActivity.class));
            }
        });

        bookings.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(getApplicationContext(), BookingsActivity.class));
            }
        });

        DatabaseManager dbm = new DatabaseManager();
        Query q = db.collection("users").whereEqualTo("uId", user.getUid());

        dbm.getElements(q, new OnElementsGet()
        {
            @Override
            public void setOnElementsGet(List<DocumentSnapshot> documentSnapshotList)
            {
                for (DocumentSnapshot document : documentSnapshotList)
                {
                    Log.d("LOL2", document.getId() + " => " + document.getData());
                    Log.i("XD2", "xd");
                    String nameDb = (String) Objects.requireNonNull(document.getData()).get("name");
                    String surnameDb = (String) document.getData().get("surname");
                    name.setText(nameDb);
                    surname.setText(surnameDb);
                    documentId = document.getId();
                }
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