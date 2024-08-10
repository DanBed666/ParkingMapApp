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

                        name.setText(nam);
                        parking.setText(pkg);
                        capacity.setText(cpc);
                        fee.setText(f33);
                        supervised.setText(sup);
                        operator.setText(ope);
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

                Parking parking = new Parking(id, nam, pkg, cpc, f33, sup, ope, latitude[0], longitude[0], true, address[0]);
                editParking(parking);

                finish();
            }
        });
    }

    public void editParking(Parking parking)
    {
        Map<String, Object> mapa = new HashMap<>();
        mapa.put("name", parking.getName());
        mapa.put("pking", parking.getPking());
        mapa.put("capacity", parking.getCapacity());
        mapa.put("fee", parking.getFee());
        mapa.put("supervised", parking.getSupervised());
        mapa.put("operator", parking.getOperator());
        mapa.put("edited", parking.isEdited());

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
}