package com.example.parkingmapapp;

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
import androidx.lifecycle.Observer;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class AddParkingActivity extends AppCompatActivity implements HarmValueListener
{
    AddressViewModel addressViewModel;
    Map<String, String> schedule;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_parking2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) ->
        {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

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
        Button createBTN = findViewById(R.id.btn_create);
        EditText cena = findViewById(R.id.et_cena);
        Button harmonogram = findViewById(R.id.btn_hours);

        String[] types = getResources().getStringArray(R.array.types);
        String[] typesEN = {"", "surface", "street_side", "multi-storey", "underground"};
        String[] accessTab = getResources().getStringArray(R.array.access);
        String[] accessTabEN = {"", "yes", "private", "customers"};
        String[] opcje = getResources().getStringArray(R.array.options);
        String[] opcjeEN = {"", "yes", "no"};

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        addressViewModel = new AddressViewModel();

        GeoPoint location = getIntent().getParcelableExtra("LOCATION");
        Utils utils = (Utils) getIntent().getSerializableExtra("UTILS");

        harmonogram.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                HarmonogramFragment fragment = new HarmonogramFragment();
                Bundle bundle = new Bundle();
                Map <String, String> schedule = new HashMap<>();
                schedule.put("Brak", "Brak");
                bundle.putSerializable("SCHEDULE", (Serializable) schedule);
                fragment.setArguments(bundle);

                getSupportFragmentManager().beginTransaction().
                        setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                        .show(fragment)
                        .replace(R.id.fragment2, fragment)
                        .commit();
            }
        });

        initializeAdapter(types, spinnerType);
        initializeAdapter(opcje, spinnerFee);
        initializeAdapter(opcje, spinnerSupervised);
        initializeAdapter(accessTab, spinnerAccess);
        initializeAdapter(opcje, spinnerDisabled);
        initializeAdapter(opcje, spinnerTrucks);
        initializeAdapter(opcje, spinnerBus);
        initializeAdapter(opcje, spinnerMoto);

        createBTN.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String id = generateId();
                String name = nameET.getText().toString();
                String capacity = capacityET.getText().toString();
                String parking = getValue(spinnerType, typesEN);
                String fee = getValue(spinnerFee, opcjeEN);
                String supervised = getValue(spinnerSupervised, opcjeEN);
                String operator = operatorET.getText().toString();
                String access = getValue(spinnerAccess, accessTabEN);
                String capacityDis = getValue(spinnerDisabled, opcjeEN);
                String capacityTru = getValue(spinnerTrucks, opcjeEN);
                String capacityBus = getValue(spinnerBus, opcjeEN);
                String capacityMoto = getValue(spinnerMoto, opcjeEN);
                String prize = cena.getText().toString();

                assert location != null;

                assert user != null;
                Parking newParking = new Parking(user.getUid(), id, name, parking, access, capacity, capacityDis, capacityTru, capacityBus, capacityMoto,
                        fee, supervised, operator, location.getLatitude(), location.getLongitude(), true, true, getActualDate(), "",
                        schedule, prize);

                db.collection("parkings").document(id).set(newParking).addOnSuccessListener(new OnSuccessListener<Void>()
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

                addressViewModel.getAddressVM(location.getLatitude() + "," + location.getLongitude(),
                        "FiyHNQAmeoWKRcEdp5KyYWOAaAKf-7hvtqkz--lGBDc").observeForever(new Observer<Address>()
                {
                    @Override
                    public void onChanged(Address address)
                    {
                        Log.i("ADRES", address.getItems().get(0).getTitle());
                        Log.i("ADRESADRADRADR", address.getItems().get(0).getTitle());
                        Edits edits = new Edits(user.getUid(), id, false, true, name, address.getItems().get(0).getTitle(), "", getActualDate());
                        Log.i("EDIT88", edits.getDataCreated());
                        checkIfExists(id, edits);
                    }
                });

                assert utils != null;
                utils.setMarker(new GeoPoint(location.getLatitude(), location.getLongitude()), id);

                finish();
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

    @Override
    public void onStringReceived(Map<String, String> ham)
    {
        schedule = ham;

        for (Map.Entry<String, String> entry : ham.entrySet())
        {
            Log.i("DAY", entry.getKey() + " " + entry.getValue());
        }
    }

    public void checkIfExists(String id, Edits edits)
    {
        db.collection("edits").document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task)
            {
                if (task.isSuccessful())
                {
                    DocumentSnapshot document = task.getResult();

                    if (document.exists())
                    {
                        Log.i("REKORD", "istnieje " + id);
                    }
                    else
                    {
                        Log.i("REKORD", "nie istnieje " + id);
                        addEdit(id, edits);
                    }
                }
                else
                {
                    Log.d("ERROR", "Failed with: ", task.getException());
                }
            }
        });
    }

    public void addEdit(String id, Edits edits)
    {
        Log.i("EDIT", edits.getId());
        db.collection("edits").document(id).set(edits).addOnSuccessListener(new OnSuccessListener<Void>()
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
}