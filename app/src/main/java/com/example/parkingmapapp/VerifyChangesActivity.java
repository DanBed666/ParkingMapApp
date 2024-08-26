package com.example.parkingmapapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
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
import androidx.lifecycle.Observer;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class VerifyChangesActivity extends AppCompatActivity
{

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String id;
    String editId;
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
    TextView price;
    AddressViewModel addressViewModel;
    Button showOnMap;
    Button harmBut;
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

        id = getIntent().getStringExtra("PARKINGID");
        editId = getIntent().getStringExtra("EDITID");
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
        price = findViewById(R.id.tv_price);
        addressViewModel = new AddressViewModel();
        showOnMap = findViewById(R.id.btn_showonmap);
        String edit = "nic";
        harmBut = findViewById(R.id.btn_harm);

        if (getIntent().getStringExtra("EDITST") != null)
        {
            edit = getIntent().getStringExtra("EDITST");
        }

        if (Objects.equals(edit, "info"))
        {
            showOnMap.setVisibility(View.GONE);
        }

        getVerifies();
    }

    public void getVerifies()
    {
        Log.i("VIERFIEIS", id);
        db.collection("edits").whereEqualTo("editId", editId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task)
            {
                if (task.isSuccessful())
                {
                    for (DocumentSnapshot ds : task.getResult().getDocuments())
                    {
                        name.setText("Nazwa: " + ds.getString("name"));
                        parking.setText("Nawierzchnia: " + ds.getString("pking"));
                        capacity.setText("Wielkość: " + ds.getString("capacity"));
                        fee.setText("Płatny: " + ds.getString("fee"));
                        supervised.setText("Strzeżony: " + ds.getString("supervised"));
                        operator.setText("Operator: " + ds.getString("operator"));
                        access.setText("Dostęp: " + ds.getString("access"));
                        capacityDis.setText("Miejsca dla niepełnosprawnych: " + ds.getString("capacityDisabled"));
                        capacityTru.setText("Miejsca dla tirów: " + ds.getString("capacityTrucks"));
                        capacityBus.setText("Miejsca dla autobusów: " + ds.getString("capacityBus"));
                        capacityMoto.setText("Miejsca dla motocykli: " + ds.getString("capacityMotorcycle"));
                        price.setText("Cena: " + ds.getString("kwota"));
                        Map<String, String> schedule = (Map<String, String>) ds.get("harmonogram");

                        Map<String, Object> mapa = new HashMap<>();

                        if (Objects.equals(ds.getString("supervised"), "yes"))
                        {
                            harmBut.setVisibility(View.VISIBLE);
                        }
                        harmBut.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                Intent intent = new Intent(getApplicationContext(), HarmonogramActivity.class);
                                intent.putExtra("SCHEDULE", (Serializable) schedule);
                                startActivity(new Intent(getApplicationContext(), HarmonogramActivity.class));
                            }
                        });

                        double lat = ds.getDouble("latitude");
                        double lon = ds.getDouble("longitude");

                        getUser(Boolean.TRUE.equals(ds.getBoolean("verified")));

                        showOnMap.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                Intent i = new Intent(getApplicationContext(), MapActivityShow.class);
                                i.putExtra("LAT", lat);
                                i.putExtra("LON", lon);
                                startActivity(i);
                            }
                        });

                        pass.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                addMap(mapa, ds);
                                mapa.put("verified", true);
                                mapa.put("status", "Zweryfikowany");
                                mapa.put("edited", true);
                                editParking(mapa, "parkings", id);
                                editParking(mapa, "edits", editId);
                                finish();
                            }
                        });

                        notPass.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                addMap(mapa, ds);
                                mapa.put("verified", false);
                                mapa.put("status", "Odrzucony");
                                editParking(mapa, "edits", editId);
                                finish();
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
    public void editParking(Map<String, Object> mapa, String col, String idNumber)
    {
        db.collection(col).document(idNumber).update(mapa).addOnSuccessListener(new OnSuccessListener<Void>()
        {
            @Override
            public void onSuccess(Void documentReference)
            {
                Log.i("ERROdddsR", "GUT");
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

    public void getUser(boolean verified)
    {
        db.collection("users").whereEqualTo("uId", user.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task)
            {
                if (task.isSuccessful())
                {
                    for (DocumentSnapshot ds : task.getResult().getDocuments())
                    {
                        if ((Objects.equals(ds.getString("ranga"), "Użytkownik") || verified))
                        {
                            pass.setVisibility(View.GONE);
                            notPass.setVisibility(View.GONE);
                        }
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

    public void addMap(Map<String, Object> mapa, DocumentSnapshot ds)
    {
        mapa.put("name", ds.getString("name"));
        mapa.put("pking", ds.getString("pking"));
        mapa.put("capacity", ds.getString("capacity"));
        mapa.put("fee", ds.getString("fee"));
        mapa.put("supervised", ds.getString("supervised"));
        mapa.put("operator", ds.getString("operator"));
        mapa.put("edited", false);
        mapa.put("access", ds.getString("access"));
        mapa.put("capacityDisabled", ds.getString("capacityDisabled"));
        mapa.put("capacityTrucks", ds.getString("capacityTrucks"));
        mapa.put("capacityBus", ds.getString("capacityBus"));
        mapa.put("capacityMotorcycle", ds.getString("capacityMotorcycle"));
        mapa.put("dataEdited", getActualDate());
        mapa.put("harmonogram", ds.get("harmonogram"));
        mapa.put("kwota", ds.getString("kwota"));
    }
}