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
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
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
    TextView nameTV;
    TextView parkingTV;
    TextView capacityTV;
    TextView feeTV;
    TextView supervisedTV;
    TextView operatorTV;
    TextView accessTV;
    TextView capacityDisTV;
    TextView capacityTruTV;
    TextView capacityBusTV;
    TextView capacityMotoTV;
    Button pass;
    Button notPass;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
    TextView priceTV;
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
        nameTV = findViewById(R.id.tv_name);
        parkingTV = findViewById(R.id.tv_parking);
        capacityTV = findViewById(R.id.tv_capacity);
        feeTV = findViewById(R.id.tv_fee);
        supervisedTV = findViewById(R.id.tv_supervised);
        operatorTV = findViewById(R.id.tv_operator);
        accessTV = findViewById(R.id.tv_access);
        capacityDisTV = findViewById(R.id.tv_capacitydis);
        capacityTruTV = findViewById(R.id.tv_capacitytrucks);
        capacityBusTV = findViewById(R.id.tv_capacitybus);
        capacityMotoTV = findViewById(R.id.tv_capacitymoto);
        pass = findViewById(R.id.btn_pass);
        notPass = findViewById(R.id.btn_notpass);
        priceTV = findViewById(R.id.tv_price);
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
                        String uId = ds.getString("uId");
                        String name = ds.getString("name");
                        String parking = ds.getString("pking");
                        String access = ds.getString("access");
                        String capacity = ds.getString("capacity");
                        String capacityDis = ds.getString("capacityDisabled");
                        String capacityTru = ds.getString("capacityTrucks");
                        String capacityBus = ds.getString("capacityBus");
                        String capacityMoto = ds.getString("capacityMotorcycle");
                        String fee = ds.getString("fee");
                        String supervised = ds.getString("supervised");
                        String operator = ds.getString("operator");
                        String price = ds.getString("kwota");
                        String created = ds.getString("dataCreated");
                        String edited = ds.getString("dataEdited");

                        nameTV.setText("Nazwa: " + name);
                        parkingTV.setText("Nawierzchnia: " + parking);
                        capacityTV.setText("Wielkość: " + capacity);
                        feeTV.setText("Płatny: " + fee);
                        supervisedTV.setText("Strzeżony: " + supervised);
                        operatorTV.setText("Operator: " + operator);
                        accessTV.setText("Dostęp: " + access);
                        capacityDisTV.setText("Miejsca dla niepełnosprawnych: " + capacityDis);
                        capacityTruTV.setText("Miejsca dla tirów: " + capacityTru);
                        capacityBusTV.setText("Miejsca dla autobusów: " + capacityBus);
                        capacityMotoTV.setText("Miejsca dla motocykli: " + capacityMoto);
                        priceTV.setText("Cena: " + price);
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
                                startActivity(intent);
                            }
                        });

                        double lat = ds.getDouble("latitude");
                        double lon = ds.getDouble("longitude");
                        String create = ds.getString("action");

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

                        Parking newParking = new Parking(uId, id, editId, name, parking, access, capacity, capacityDis, capacityTru, capacityBus, capacityMoto,
                                fee, supervised, operator, lat, lon, created, edited, getActualDate(), "Utworzono",
                                schedule, price, true, "Zweryfikowany", Calendar.getInstance().getTime());

                        pass.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                if (create.equals("Utworzono"))
                                {
                                    addParking(id, newParking, "parkings");
                                    mapa.put("verified", true);
                                    mapa.put("status", "Zweryfikowany");
                                    mapa.put("dataVerified", getActualDate());
                                    editParking(mapa, "edits", editId);
                                }
                                else
                                {
                                    addMap(mapa, ds);
                                    mapa.put("verified", true);
                                    mapa.put("status", "Zweryfikowany");
                                    mapa.put("dataVerified", getActualDate());
                                    editParking(mapa, "parkings", id);
                                    editParking(mapa, "edits", editId);
                                }

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
        mapa.put("action", "Edytowano");
        mapa.put("access", ds.getString("access"));
        mapa.put("capacityDisabled", ds.getString("capacityDisabled"));
        mapa.put("capacityTrucks", ds.getString("capacityTrucks"));
        mapa.put("capacityBus", ds.getString("capacityBus"));
        mapa.put("capacityMotorcycle", ds.getString("capacityMotorcycle"));
        mapa.put("dataEdited", getActualDate());
        mapa.put("harmonogram", ds.get("harmonogram"));
        mapa.put("kwota", ds.getString("kwota"));
    }

    public void addParking(String id, Parking newParking, String col)
    {
        db.collection(col).document(id).set(newParking).addOnSuccessListener(new OnSuccessListener<Void>()
        {
            @Override
            public void onSuccess(Void unused)
            {
                Log.i("CREATEDADD", "created");
            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Log.e("ERROR", Objects.requireNonNull(e.getMessage()));
            }
        });
    }
}