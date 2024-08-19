package com.example.parkingmapapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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

public class EditParkingInfoActivity extends AppCompatActivity implements HarmValueListener
{
    String id;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String documentId;
    Map<String, String> schedule = new HashMap<>();
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

        Spinner spinnerType = findViewById(R.id.spinner_type);
        Spinner spinnerAccess = findViewById(R.id.spinner_access);
        Spinner spinnerFee = findViewById(R.id.spinner_fee);
        Spinner spinnerSupervised = findViewById(R.id.spinner_supervised);
        Spinner spinnerBus = findViewById(R.id.spinner_bus);
        Spinner spinnerTrucks = findViewById(R.id.spinner_truck);
        Spinner spinnerDisabled = findViewById(R.id.spinner_disabled);
        Spinner spinnerMoto = findViewById(R.id.spinner_moto);
        EditText nameET = findViewById(R.id.et_nazwa);
        EditText capacityET = findViewById(R.id.et_capacity);
        EditText operatorET = findViewById(R.id.et_operator);
        Button edit = findViewById(R.id.btn_create);
        EditText cenaET = findViewById(R.id.et_cena);
        Button harmonogram = findViewById(R.id.btn_hours);

        String[] types = getResources().getStringArray(R.array.types);
        String[] typesEN = {"", "surface", "street_side", "multi-storey", "underground"};
        String[] accessTab = getResources().getStringArray(R.array.access);
        String[] accessTabEN = {"", "yes", "private", "customers"};
        String[] opcje = getResources().getStringArray(R.array.options);
        String[] opcjeEN = {"", "yes", "no"};

        initializeAdapter(types, spinnerType);
        initializeAdapter(opcje, spinnerFee);
        initializeAdapter(opcje, spinnerSupervised);
        initializeAdapter(accessTab, spinnerAccess);
        initializeAdapter(opcje, spinnerDisabled);
        initializeAdapter(opcje, spinnerTrucks);
        initializeAdapter(opcje, spinnerBus);
        initializeAdapter(opcje, spinnerMoto);
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
                        assert schedule != null;
                        Log.i("PON", Objects.requireNonNull(schedule.get("Poniedziałek")));

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
                String cmot= getValue(spinnerMoto, opcjeEN);

                Map<String, Object> mapa = new HashMap<>();
                mapa.put("name", name);
                mapa.put("pking", parking);
                mapa.put("capacity", capacity);
                mapa.put("fee", fee);
                mapa.put("supervised", supervised);
                mapa.put("operator", operator);
                mapa.put("edited", true);
                mapa.put("access", access);
                mapa.put("capacityDisabled", cdis);
                mapa.put("capacityTrucks", ctru);
                mapa.put("capacityBus", cbus);
                mapa.put("capacityMotorcycle", cmot);
                mapa.put("dataEdited", getActualDate());
                mapa.put("harmonogram", schedule);

                for (Map.Entry<String, Object> element : mapa.entrySet())
                {
                    if (element.getValue() == null)
                    {
                        mapa.put(element.getKey(), "Brak");
                    }
                }

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

    @Override
    public void onStringReceived(Map<String, String> ham)
    {
        schedule = ham;
    }
}