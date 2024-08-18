package com.example.parkingmapapp;

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
import androidx.lifecycle.Observer;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.osmdroid.util.GeoPoint;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;
import java.util.Random;

public class AddParkingActivity extends AppCompatActivity
{
    AddressViewModel addressViewModel;
    Address addressAdr;
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

        String[] types = {"Dowolny", "Naziemny", "Przyległy do drogi", "Wielopoziomowy", "Podziemny"};
        String[] typesEN = {"", "surface", "street_side", "multi-storey", "underground"};
        String[] accessTab = {"Dowolny", "Otwarty", "Prywatny", "Dla klientów"};
        String[] accessTabEN = {"", "yes", "private", "customers"};
        String[] opcje = {"Dowolny", "Tak", "Nie"};
        String[] opcjeEN = {"", "yes", "no"};

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        addressViewModel = new AddressViewModel();

        GeoPoint location = getIntent().getParcelableExtra("LOCATION");

        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {

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
                String parking = getValue(types, spinnerType, typesEN);
                String fee = getValue(opcje, spinnerFee, opcjeEN);
                String supervised = getValue(opcje, spinnerSupervised, opcjeEN);
                String operator = operatorET.getText().toString();
                String access = getValue(accessTab, spinnerAccess, accessTabEN);
                String capacityDis = getValue(opcje, spinnerDisabled, opcjeEN);
                String capacityTru = getValue(opcje, spinnerTrucks, opcjeEN);
                String capacityBus = getValue(opcje, spinnerBus, opcjeEN);
                String capacityMoto = getValue(opcje, spinnerMoto, opcjeEN);

                assert location != null;
                addressViewModel.getAddressVM(location.getLatitude() + "%2C" + location.getLongitude(), "FiyHNQAmeoWKRcEdp5KyYWOAaAKf-7hvtqkz--lGBDc").observeForever(new Observer<Address>()
                {
                    @Override
                    public void onChanged(Address address)
                    {
                        addressAdr = address;
                    }
                });

                assert user != null;
                Parking newParking = new Parking(user.getUid(), id, name, parking, access, capacity, capacityDis, capacityTru, capacityBus, capacityMoto,
                        fee, supervised, operator, location.getLatitude(), location.getLongitude(), true, true, getActualDate(), "");

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

                finish();
            }
        });
    }

    public String generateId()
    {
        Random random = new Random();
        StringBuilder chain = new StringBuilder();

        for (int i = 1; i <= 10; i++)
        {
            char c = (char)(random.nextInt(26) + 'a');
            chain.append(c);
        }

        return chain.toString();
    }

    public String getActualDate()
    {
        Calendar calender = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        String formattedDate = df.format(calender.getTime());
        return formattedDate;
    }

    public String getValue(String [] tab, Spinner spinner, String [] tabEN)
    {
        ArrayAdapter<String> aa = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, tab);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(aa);
        final String[] value = new String[1];

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                value[0] = tabEN[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                value[0] = "";
            }
        });

        return value[0];
    }
}