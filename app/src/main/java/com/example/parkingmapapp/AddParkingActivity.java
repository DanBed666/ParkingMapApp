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

import org.osmdroid.util.GeoPoint;
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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class AddParkingActivity extends AppCompatActivity implements HarmValueListener
{
    Map<String, String> schedule = new HashMap<>();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    GetTagData get = new GetTagData();
    String[] typesEN = {"", "surface", "street_side", "multi-storey", "underground"};
    String[] accessTabEN = {"", "yes", "private", "customers"};
    String[] opcjeEN = {"", "yes", "no"};
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
        EditText cenaET = findViewById(R.id.et_cena);
        Button harmonogram = findViewById(R.id.btn_hours);

        String[] types = getResources().getStringArray(R.array.types);
        String[] accessTab = getResources().getStringArray(R.array.access);
        String[] opcje = getResources().getStringArray(R.array.options);

        Spinner [] spinners = new Spinner[]{spinnerType, spinnerAccess, spinnerFee, spinnerSupervised, spinnerBus, spinnerTrucks, spinnerDisabled, spinnerMoto};
        EditText [] editTexts = new EditText[]{nameET, capacityET, operatorET, cenaET};

        GeoPoint location = getIntent().getParcelableExtra("LOCATION");
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

        createBTN.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                addListener(location, spinners, editTexts);
            }
        });
    }

    public void addListener(GeoPoint location, Spinner [] spinners, EditText [] editTexts)
    {
        //Get values from editText, spinners
        String id = get.generateId();
        String name = editTexts[0].getText().toString();
        String capacity = editTexts[1].getText().toString();
        String parking = getValue(spinners[0], typesEN);
        String fee = getValue(spinners[1], opcjeEN);
        String supervised = getValue(spinners[2], opcjeEN);
        String operator = editTexts[2].getText().toString();
        String access = getValue(spinners[3], accessTabEN);
        String capacityDis = getValue(spinners[4], opcjeEN);
        String capacityTru = getValue(spinners[5], opcjeEN);
        String capacityBus = getValue(spinners[6], opcjeEN);
        String capacityMoto = getValue(spinners[7], opcjeEN);
        String prize = editTexts[3].getText().toString();
        String editId = get.generateId();

        if (schedule.isEmpty())
            schedule.put("Brak", "nie ma");

        //Create new object and add creation

        assert location != null;
        assert user != null;
        Parking newParking = new Parking(user.getUid(), id, editId, name, parking, access, capacity,
                capacityDis, capacityTru, capacityBus, capacityMoto,
                fee, supervised, operator, location.getLatitude(), location.getLongitude(),
                get.getActualDate(), "", "", "Utworzono",
                schedule, prize, false, "Oczekujący", Calendar.getInstance().getTime(), false);

        Intent intent = new Intent(getApplicationContext(), MapActivity.class);
        intent.putExtra("MyData", "created");
        intent.putExtra("ID", id);
        intent.putExtra("GEOPOINT", (Parcelable) location);
        setResult(1, intent);

        ParkingManager pm = new ParkingManager();
        pm.addParking(newParking, id, editId);

        Intent i = new Intent(getApplicationContext(), ParkingEditHistoryActivity.class);
        i.putExtra("ID", id);
        startActivity(i);

        finish();
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

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(getApplicationContext(), MapActivity.class);
        intent.putExtra("MyData", "back");
        setResult(1, intent);
        super.onBackPressed();
    }
}