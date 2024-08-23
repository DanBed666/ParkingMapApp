package com.example.parkingmapapp;

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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class VerifyChangesActivity extends AppCompatActivity
{

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String id;
    TextView name;
    TextView parking;
    TextView capacity;
    TextView fee;
    TextView supervised;
    TextView operator;
    TextView access;
    TextView capacityDis;
    TextView capacityTru;
    TextView capacityBus;
    TextView capacityMoto;
    Button pass;
    Button notPass;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_verify_changes);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) ->
        {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        id = getIntent().getStringExtra("ID");
        name = findViewById(R.id.tv_name);
        parking = findViewById(R.id.tv_parking);
        capacity = findViewById(R.id.tv_capacity);
        fee = findViewById(R.id.tv_fee);
        supervised = findViewById(R.id.tv_supervised);
        operator = findViewById(R.id.tv_operator);
        access = findViewById(R.id.tv_access);
        capacityDis = findViewById(R.id.tv_capacitydis);
        capacityTru = findViewById(R.id.tv_capacitytrucks);
        capacityBus = findViewById(R.id.tv_capacitybus);
        capacityMoto = findViewById(R.id.tv_capacitymoto);
        pass = findViewById(R.id.btn_pass);
        notPass = findViewById(R.id.btn_notpass);

        getVerifies();
    }

    public void getVerifies()
    {
        db.collection("cars").whereEqualTo("id", id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task)
            {
                if (task.isSuccessful())
                {
                    for (DocumentSnapshot ds : task.getResult().getDocuments())
                    {
                        name.setText(ds.getString("name"));
                        parking.setText(ds.getString("pking"));
                        capacity.setText(ds.getString("capacity"));
                        fee.setText(ds.getString("fee"));
                        supervised.setText(ds.getString("supervised"));
                        operator.setText(ds.getString("operator"));
                        access.setText(ds.getString("access"));
                        capacityDis.setText(ds.getString("capacityDisabled"));
                        capacityTru.setText(ds.getString("capacityTrucks"));
                        capacityBus.setText(ds.getString("capacityBus"));
                        capacityMoto.setText(ds.getString("capacityMotorcycle"));
                        ds.getString("kwota");
                        ds.get("harmonogram");

                        Map<String, Object> mapa = new HashMap<>();
                        mapa.put("name", ds.getString("name"));
                        mapa.put("pking", ds.getString("pking"));
                        mapa.put("capacity", ds.getString("capacity"));
                        mapa.put("fee", ds.getString("fee"));
                        mapa.put("supervised", ds.getString("supervised"));
                        mapa.put("operator", ds.getString("operator"));
                        mapa.put("edited", true);
                        mapa.put("access", ds.getString("access"));
                        mapa.put("capacityDisabled", ds.getString("capacityDisabled"));
                        mapa.put("capacityTrucks", ds.getString("capacityTrucks"));
                        mapa.put("capacityBus", ds.getString("capacityBus"));
                        mapa.put("capacityMotorcycle", ds.getString("capacityMotorcycle"));
                        mapa.put("dataEdited", getActualDate());
                        mapa.put("harmonogram", ds.get("harmonogram"));
                        mapa.put("kwota", ds.getString("kwota"));

                        pass.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                editParking(mapa);
                                delete();
                            }
                        });

                        notPass.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                delete();
                            }
                        });
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

    public void delete()
    {
        db.collection("tickets").document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>()
        {
            @Override
            public void onSuccess(Void unused)
            {
                Log.i("CREATED", "usunieto");
            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Log.i("CREATED", e.getMessage());
            }
        });
    }

    public void editParking(Map<String, Object> mapa)
    {
        db.collection("parkings").document(id).update(mapa).addOnSuccessListener(new OnSuccessListener<Void>()
        {
            @Override
            public void onSuccess(Void documentReference)
            {
                Log.d("TEST", "DocumentSnapshot added with ID");
                Edits edits = new Edits(user.getUid(), id, true, false, name, adres, getActualDate(), "");
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