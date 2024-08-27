package com.example.parkingmapapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.osmdroid.util.GeoPoint;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class EditParkingInfoActivity extends AppCompatActivity implements HarmValueListener
{
    String id;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String documentId;
    Map<String, String> schedule = new HashMap<>();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
    String adres;
    Spinner spinnerType;
    Spinner spinnerAccess;
    Spinner spinnerFee;
    Spinner spinnerSupervised;
    Spinner spinnerBus;
    Spinner spinnerTrucks;
    Spinner spinnerDisabled;
    Spinner spinnerMoto;
    EditText nameET;
    EditText capacityET;
    EditText operatorET;
    Button edit;
    EditText cenaET;
    Button harmonogram;
    String [] typesEN = {"", "surface", "street_side", "multi-storey", "underground"};;
    String [] accessTabEN = {"", "yes", "private", "customers"};
    String [] opcjeEN = {"", "yes", "no"};
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

        id = getIntent().getStringExtra("KEYID");
        documentId = getIntent().getStringExtra("DOCUMENTID");
        adres = getIntent().getStringExtra("ADDRESS");

        spinnerType = findViewById(R.id.spinner_type);
        spinnerAccess = findViewById(R.id.spinner_access);
        spinnerFee = findViewById(R.id.spinner_fee);
        spinnerSupervised = findViewById(R.id.spinner_supervised);
        spinnerBus = findViewById(R.id.spinner_bus);
        spinnerTrucks = findViewById(R.id.spinner_truck);
        spinnerDisabled = findViewById(R.id.spinner_disabled);
        spinnerMoto = findViewById(R.id.spinner_moto);
        nameET = findViewById(R.id.et_nazwa);
        capacityET = findViewById(R.id.et_capacity);
        operatorET = findViewById(R.id.et_operator);
        edit = findViewById(R.id.btn_create);
        cenaET = findViewById(R.id.et_cena);
        harmonogram = findViewById(R.id.btn_hours);

        String[] types = getResources().getStringArray(R.array.types);
        String[] accessTab = getResources().getStringArray(R.array.access);
        String[] opcje = getResources().getStringArray(R.array.options);

        initializeAdapter(types, spinnerType);
        initializeAdapter(opcje, spinnerFee);
        initializeAdapter(opcje, spinnerSupervised);
        initializeAdapter(accessTab, spinnerAccess);
        initializeAdapter(opcje, spinnerDisabled);
        initializeAdapter(opcje, spinnerTrucks);
        initializeAdapter(opcje, spinnerBus);
        initializeAdapter(opcje, spinnerMoto);

        spinnerFee.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                if (position == 1)
                {
                    cenaET.setVisibility(View.VISIBLE);
                }
                else
                {
                    cenaET.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        spinnerSupervised.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                if (position == 1)
                {
                    harmonogram.setVisibility(View.VISIBLE);
                }
                else
                {
                    harmonogram.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
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
                        String acc = (String) document.getData().get("access");
                        String cdis = (String) document.getData().get("capacity:disabled");
                        String ctru = (String) document.getData().get("capacity:truck");
                        String cbus = (String) document.getData().get("capacity:bus");
                        String cmot = (String) document.getData().get("capacity:motorcycle");
                        String cena = (String) document.getData().get("kwota");
                        schedule = (Map<String, String>) document.getData().get("harmonogram");

                        double latitude = (double) document.getData().get("latitude");
                        double longitude = (double) document.getData().get("longitude");
                        String created = (String) document.getData().get("dataCreated");
                        String edited = (String) document.getData().get("dataEdited");
                        String verified = (String) document.getData().get("dataVerified");
                        String action = (String) document.getData().get("action");
                        String status = (String) document.getData().get("status");

                        nameET.setText(nam);
                        capacityET.setText(cpc);
                        operatorET.setText(ope);
                        cenaET.setText(cena);

                        spinnerType.setSelection(getPosition(pkg, typesEN));
                        spinnerAccess.setSelection(getPosition(acc, accessTabEN));
                        spinnerDisabled.setSelection(getPosition(cdis, opcjeEN));
                        spinnerFee.setSelection(getPosition(f33, opcjeEN));
                        spinnerSupervised.setSelection(getPosition(sup, opcjeEN));
                        spinnerBus.setSelection(getPosition(cbus, opcjeEN));
                        spinnerMoto.setSelection(getPosition(cmot, opcjeEN));
                        spinnerTrucks.setSelection(getPosition(ctru, opcjeEN));

                        editListener(latitude, longitude, created);
                    }
                }
                else
                {
                    Log.w("ERR", "Error getting documents.", task.getException());
                }
            }
        });

        harmonogram.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                HarmonogramFragment fragment = new HarmonogramFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("SCHEDULE", (Serializable) schedule);
                fragment.setArguments(bundle);

                getSupportFragmentManager().beginTransaction().
                        setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                        .show(fragment)
                        .replace(R.id.fragment2, fragment)
                        .commit();
            }
        });
    }

    private void editListener(double lat, double lon, String created)
    {
        edit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String name = nameET.getText().toString();
                String capacity = capacityET.getText().toString();
                String parking = getValue(spinnerType, typesEN);
                String fee = getValue(spinnerFee, opcjeEN);
                String supervised = getValue(spinnerSupervised, opcjeEN);
                String operator = operatorET.getText().toString();
                String access = getValue(spinnerAccess, accessTabEN);
                String cdis = getValue(spinnerDisabled, opcjeEN);
                String ctru = getValue(spinnerTrucks, opcjeEN);
                String cbus = getValue(spinnerBus, opcjeEN);
                String cmot = getValue(spinnerMoto, opcjeEN);
                String price = cenaET.getText().toString();

                Map<String, Object> mapa = new HashMap<>();
                mapa.put("name", name);
                mapa.put("pking", parking);
                mapa.put("capacity", capacity);
                mapa.put("fee", fee);
                mapa.put("supervised", supervised);
                mapa.put("operator", operator);
                mapa.put("access", access);
                mapa.put("capacityDisabled", cdis);
                mapa.put("capacityTrucks", ctru);
                mapa.put("capacityBus", cbus);
                mapa.put("capacityMotorcycle", cmot);
                mapa.put("dataEdited", getActualDate());
                mapa.put("harmonogram", schedule);
                mapa.put("kwota", price);
                mapa.put("action", "Edytowano");

                //checkIfExists(id);

                for (Map.Entry<String, Object> element : mapa.entrySet())
                {
                    if (element.getValue() == null)
                    {
                        mapa.put(element.getKey(), "Brak");
                    }
                    else if (Objects.equals(element.getKey(), "harmonogram"))
                    {
                        if (schedule.isEmpty())
                        {
                            schedule.put("Brak", "Nic");
                        }
                    }
                }

                String editedId = generateId();

                Parking newParking2 = new Parking(user.getUid(), id, editedId, name, parking, access, capacity, cdis, ctru, cbus, cmot,
                        fee, supervised, operator, lat, lon, created, getActualDate(), "", "Edytowano",
                        schedule, price, false, "Oczekujący");

                Log.i("EDS", editedId);

                getUser(mapa, newParking2);

                finish();
            }
        });
    }

    public void editParking(Map<String, Object> mapa)
    {
        Log.i("EDITPAR", documentId);
        db.collection("parkings").document(documentId).update(mapa).addOnSuccessListener(new OnSuccessListener<Void>()
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

    public void addEdit(Parking parking)
    {
        db.collection("edits").document(parking.getEditId()).set(parking).addOnSuccessListener(new OnSuccessListener<Void>()
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

    public void getUser(Map<String, Object> mapa, Parking newParking)
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
                        if (Objects.equals(ds.getString("ranga"), "Administrator") || Objects.equals(ds.getString("ranga"), "Moderator"))
                        {
                            mapa.put("verified", true);
                            mapa.put("status", "Zweryfikowany");
                            mapa.put("dataVerified", getActualDate());
                            newParking.setVerified(true);
                            newParking.setStatus("Zweryfikowany");
                            newParking.setDataVerified(getActualDate());
                            editParking(mapa);
                            addEdit(newParking);
                            Intent i = new Intent(getApplicationContext(), ParkingEditHistoryActivity.class);
                            i.putExtra("ID", id);
                            startActivity(i);
                        }
                        else
                        {
                            addEdit(newParking);
                            Intent i = new Intent(getApplicationContext(), ParkingEditHistoryActivity.class);
                            i.putExtra("ID", id);
                            startActivity(i);
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

    public String getActualDate()
    {
        Calendar calender = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        String formattedDate = df.format(calender.getTime());
        return formattedDate;
    }

    public void initializeAdapter(String [] tab, Spinner spinner)
    {
        ArrayAdapter<String> aa = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, tab);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(aa);
    }

    public String getValue(Spinner spinner, String [] tabEN)
    {
        String value;
        int position = spinner.getSelectedItemPosition();
        value = tabEN[position];

        return value;
    }

    public int getPosition(String value, String [] tabEN)
    {
        int position = 0;

        if (value == null)
            value = "";

        for (int i = 0; i < tabEN.length; i++)
        {
            if (value.equals(tabEN[i]))
            {
                position = i;
            }
        }

        return position;
    }

    public String generateId()
    {
        Random random = new Random();
        StringBuilder chain = new StringBuilder();

        for (int i = 1; i <= 20; i++)
        {
            char c = (char)(random.nextInt(26) + 'a');
            chain.append(c);
        }

        return chain.toString();
    }

    @Override
    public void onStringReceived(Map<String, String> ham)
    {
        schedule = ham;
    }
}