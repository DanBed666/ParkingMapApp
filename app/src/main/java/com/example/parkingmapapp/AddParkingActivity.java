package com.example.parkingmapapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
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
import com.google.firebase.firestore.QuerySnapshot;

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
    Map<String, String> schedule = new HashMap<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    GeoPoint location;
    String generatedId;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
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

        addressViewModel = new AddressViewModel();

        location = getIntent().getParcelableExtra("LOCATION");
        generatedId = getIntent().getStringExtra("ID");

        harmonogram.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                HarmonogramFragment fragment = new HarmonogramFragment();
                Bundle bundle = new Bundle();
                Map <String, String> schedule = new HashMap<>();
                schedule.put("Brak", "Nie ma");
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

        spinnerFee.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                if (position == 1)
                {
                    cena.setVisibility(View.VISIBLE);
                }
                else
                {
                    cena.setVisibility(View.GONE);
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

                if (schedule.isEmpty())
                    schedule.put("Brak", "nie ma");

                assert location != null;
                String editId = generateId();

                assert user != null;
                Parking newParking = new Parking(user.getUid(), id, editId, name, parking, access, capacity, capacityDis, capacityTru, capacityBus, capacityMoto,
                        fee, supervised, operator, location.getLatitude(), location.getLongitude(), getActualDate(), "", "", "Utworzono",
                        schedule, prize, false, "Oczekujący", Calendar.getInstance().getTime());

                Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                intent.putExtra("MyData", "created");
                intent.putExtra("ID", id);
                intent.putExtra("GEOPOINT", (Parcelable) location);
                setResult(1, intent);

                getUser(id, editId, newParking);

                finish();
            }
        });
    }

    public void getUser(String id, String editId, Parking newParking)
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
                            newParking.setVerified(true);
                            newParking.setStatus("Zweryfikowany");
                            newParking.setDataVerified(getActualDate());
                            addParking(id, newParking, "parkings");
                            addParking(editId, newParking, "edits");
                            Intent i = new Intent(getApplicationContext(), ParkingEditHistoryActivity.class);
                            i.putExtra("ID", id);
                            startActivity(i);
                        }
                        else
                        {
                            addParking(editId, newParking, "edits");
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

    /*
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        SharedPreferences sp = getSharedPreferences("LoginInfos", 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("email", "dupa");
        editor.apply();
    }
    */

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(getApplicationContext(), MapActivity.class);
        intent.putExtra("MyData", "back");
        setResult(1, intent);
        super.onBackPressed();
    }
}