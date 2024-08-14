package com.example.parkingmapapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.osmdroid.util.GeoPoint;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EditParkingInfoActivity extends AppCompatActivity
{
    EditText name;
    EditText parking;
    EditText capacity;
    EditText fee;
    EditText supervised;
    EditText operator;
    Button edit;
    String id;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String documentId;
    EditText accessET;
    EditText capacityDisabledET;
    EditText capacityTrucksET;
    EditText capacityBusET;
    EditText capacityMotoET;
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

        name = findViewById(R.id.et_name);
        parking = findViewById(R.id.et_parking);
        capacity = findViewById(R.id.et_capacity);
        fee = findViewById(R.id.et_fee);
        supervised = findViewById(R.id.et_supervised);
        operator = findViewById(R.id.et_operator);
        edit = findViewById(R.id.btn_edit);
        id = getIntent().getStringExtra("KEYID");
        documentId = getIntent().getStringExtra("DOCUMENTID");
        accessET = findViewById(R.id.et_access);
        capacityDisabledET = findViewById(R.id.et_capacitydis);
        capacityTrucksET = findViewById(R.id.et_capacitytru);
        capacityBusET = findViewById(R.id.et_capacitybus);
        capacityMotoET = findViewById(R.id.et_capacitymoto);
        final Double[] latitude = new Double[1];
        final Double[] longitude = new Double[1];
        final Address[] address = new Address[1];

        assert id != null;
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
                        latitude[0] = (Double) document.getData().get("latitude");
                        longitude[0] = (Double) document.getData().get("longtitude");
                        address[0] = (Address) document.getData().get("address");

                        String acc = (String) document.getData().get("access");
                        String cdis = (String) document.getData().get("capacity:disabled");
                        String ctru = (String) document.getData().get("capacity:truck");
                        String cbus = (String) document.getData().get("capacity:bus");
                        String cmot = (String) document.getData().get("capacity:motorcycle");

                        name.setText(nam);
                        parking.setText(pkg);
                        capacity.setText(cpc);
                        fee.setText(f33);
                        supervised.setText(sup);
                        operator.setText(ope);
                        accessET.setText(acc);
                        capacityDisabledET.setText(cdis);
                        capacityTrucksET.setText(ctru);
                        capacityBusET.setText(cbus);
                        capacityMotoET.setText(cmot);
                    }
                }
                else
                {
                    Log.w("ERR", "Error getting documents.", task.getException());
                }
            }
        });

        edit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String nam = name.getText().toString();
                String pkg = parking.getText().toString();
                String cpc = capacity.getText().toString();
                String f33 = fee.getText().toString();
                String sup = supervised.getText().toString();
                String ope = operator.getText().toString();
                String acc = accessET.getText().toString();
                String cdis = capacityDisabledET.getText().toString();
                String ctru = capacityTrucksET.getText().toString();
                String cbus = capacityBusET.getText().toString();
                String cmot = capacityMotoET.getText().toString();

                Map<String, Object> mapa = new HashMap<>();
                mapa.put("name", nam);
                mapa.put("pking", pkg);
                mapa.put("capacity", cpc);
                mapa.put("fee", f33);
                mapa.put("supervised", sup);
                mapa.put("operator", ope);
                mapa.put("edited", true);
                mapa.put("access", acc);
                mapa.put("capacityDisabled", cdis);
                mapa.put("capacityTrucks", ctru);
                mapa.put("capacityBus", cbus);
                mapa.put("capacityMotorcycle", cmot);
                mapa.put("dataEdited", getActualDate());

                editParking(mapa);

                finish();
            }
        });
    }

    public void editParking(Map<String, Object> mapa)
    {
        db.collection("parkings").document(documentId).update(mapa).addOnSuccessListener(new OnSuccessListener<Void>()
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

    public String getActualDate()
    {
        Calendar calender = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        String formattedDate = df.format(calender.getTime());
        return formattedDate;
    }
}