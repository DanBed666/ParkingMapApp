package com.example.parkingmapapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;

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

import java.io.Serializable;
import java.util.Map;
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
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String documentId;
    String id;
    TextView access;
    TextView capacityDis;
    TextView capacityTru;
    TextView capacityBus;
    TextView capacityMoto;
    AddressViewModel addressViewModel;
    TextView created_date;
    TextView edited_date;
    TextView verified_date;
    TextView status;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
    Button history;
    TextView userTV;
    TextView price;
    Button btnHarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_parking_info);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) ->
        {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        addressViewModel = new AddressViewModel();
        name = findViewById(R.id.tv_name);
        parking = findViewById(R.id.tv_parking);
        capacity = findViewById(R.id.tv_capacity);
        fee = findViewById(R.id.tv_fee);
        supervised = findViewById(R.id.tv_supervised);
        operator = findViewById(R.id.tv_operator);
        edit = findViewById(R.id.btn_edit);
        access = findViewById(R.id.tv_access);
        capacityDis = findViewById(R.id.tv_capacitydis);
        capacityTru = findViewById(R.id.tv_capacitytrucks);
        capacityBus = findViewById(R.id.tv_capacitybus);
        capacityMoto = findViewById(R.id.tv_capacitymoto);
        created_date = findViewById(R.id.tv_created);
        edited_date = findViewById(R.id.tv_edited);
        verified_date = findViewById(R.id.tv_verified);
        status = findViewById(R.id.tv_status);
        history = findViewById(R.id.btn_hist);
        userTV = findViewById(R.id.tv_user);
        btnHarm = findViewById(R.id.btn_harm);
        price = findViewById(R.id.tv_price);

        id = getIntent().getStringExtra("KEYID");
        Parking p = (Parking) getIntent().getSerializableExtra("PARKING");
        String adr = getIntent().getStringExtra("ADDRESS");

        assert id != null;
        Log.i("PARKING_ID", id);

        getElementsFromDB("parkings");
        //getElementsFromDB("verifyparkings");

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EditParkingInfoActivity.class);
                intent.putExtra("KEYID", id);
                intent.putExtra("PARKING", p);
                intent.putExtra("DOCUMENTID", documentId);
                intent.putExtra("ADDRESS", adr);
                startActivity(intent);
            }
        });

        userTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), InfoProfileActivity.class));
            }
        });

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getApplicationContext(), ParkingEditHistoryActivity.class);
                intent.putExtra("ID", id);
                intent.putExtra("EDIT", "info");
                startActivity(intent);
            }
        });
    }

    public void getElementsFromDB(String col)
    {
        db.collection(col).whereEqualTo("id", id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
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
                        String acc = (String) document.getData().get("access");
                        String cdis = (String) document.getData().get("capacity:disabled");
                        String ctru = (String) document.getData().get("capacity:truck");
                        String cbus = (String) document.getData().get("capacity:bus");
                        String cmot = (String) document.getData().get("capacity:motorcycle");
                        String kwota = (String) document.getData().get("kwota");
                        Map<String, String> harm = (Map<String, String>) document.getData().get("harmonogram");
                        String createdDate = (String) document.getData().get("dataCreated");
                        String editedDate = (String) document.getData().get("dataEdited");
                        String verifiedDate = (String) document.getData().get("dataVerified");
                        String stat = (String) document.getData().get("status");
                        String user = (String) document.getData().get("uId");

                        getUser(user);

                        if (sup.equals("yes"))
                        {
                            btnHarm.setVisibility(View.VISIBLE);
                        }

                        btnHarm.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                Intent intent = new Intent(getApplicationContext(), HarmonogramActivity.class);
                                intent.putExtra("SCHEDULE", (Serializable) harm);
                                startActivity(new Intent(getApplicationContext(), HarmonogramActivity.class));
                            }
                        });

                        name.setText("Nazwa: " + nam);
                        parking.setText("Typ parkingu: " + pkg);
                        capacity.setText("Wielkość: " + cpc);
                        fee.setText("Opłaty: " + f33);
                        supervised.setText("Parking strzeżony: " + sup);
                        operator.setText("Operator: " + ope);
                        access.setText("Dostęp: " + acc);
                        capacityDis.setText("Miejsca dla niepełnosprawnych: " + cdis);
                        capacityTru.setText("Miejsca dla tirów: " + ctru);
                        capacityBus.setText("Miejsca dla busów: " + cbus);
                        capacityMoto.setText("Miejsca dla motocykli: " + cmot);
                        price.setText(kwota);
                        documentId = document.getId();

                        created_date.setText(createdDate);
                        edited_date.setText(editedDate);
                        verified_date.setText(verifiedDate);
                        status.setText(stat);
                    }
                }
                else
                {
                    Log.w("ERR", "Error getting documents.", task.getException());
                }
            }
        });
    }

    public void getUser(String uId)
    {
        db.collection("users").whereEqualTo("uId", uId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task)
            {
                if (task.isSuccessful())
                {
                    for (DocumentSnapshot ds : task.getResult().getDocuments())
                    {
                        userTV.setText(ds.getString("nick"));
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {

            }
        });
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