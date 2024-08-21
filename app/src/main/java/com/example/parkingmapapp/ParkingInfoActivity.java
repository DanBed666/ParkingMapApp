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
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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

        id = getIntent().getStringExtra("KEYID");
        Parking p = (Parking) getIntent().getSerializableExtra("PARKING");
        String adr = getIntent().getStringExtra("ADDRESS");

        assert id != null;
        Log.i("PARKING_ID", id);

        getElementsFromDB("parkings");
        updateElementsFromDB("parkings");
        edit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getApplicationContext(), EditParkingInfoActivity.class);
                intent.putExtra("KEYID", id);
                intent.putExtra("PARKING", p);
                intent.putExtra("DOCUMENTID", documentId);
                intent.putExtra("ADDRESS", adr);
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
                        documentId = document.getId();
                    }
                }
                else
                {
                    Log.w("ERR", "Error getting documents.", task.getException());
                }
            }
        });
    }

    public void updateElementsFromDB(String col)
    {
        db.collection(col).whereEqualTo("id", id).addSnapshotListener(new EventListener<QuerySnapshot>()
        {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error)
            {
                assert value != null;
                for (DocumentChange d : value.getDocumentChanges())
                {
                    Log.i("TYP", String.valueOf(d.getType()));

                    String nam = (String) d.getDocument().get("name");
                    String pkg = (String) d.getDocument().get("pking");
                    String cpc = (String) d.getDocument().get("capacity");
                    String f33 = (String) d.getDocument().get("fee");
                    String sup = (String) d.getDocument().get("supervised");
                    String ope = (String) d.getDocument().get("operator");

                    name.setText("Name: " + nam);
                    parking.setText("Parking: " + pkg);
                    capacity.setText("Capacity: " + cpc);
                    fee.setText("Fee: " + f33);
                    supervised.setText("Supervised: " + sup);
                    operator.setText("Operator: " + ope);
                }
            }
        });
    }
}