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
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.osmdroid.util.GeoPoint;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class EditParkingInfoActivity extends AppCompatActivity implements HarmValueListener
{
    String id;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String documentId;
    Map<String, String> schedule = new HashMap<>();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String [] typesEN = {"", "surface", "street_side", "multi-storey", "underground"};
    String [] accessTabEN = {"", "yes", "private", "customers"};
    String [] opcjeEN = {"", "yes", "no"};
    GetTagData get = new GetTagData();
    DatabaseManager dbm = new DatabaseManager();
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
        String[] accessTab = getResources().getStringArray(R.array.access);
        String[] opcje = getResources().getStringArray(R.array.options);

        Spinner [] spinners = new Spinner[]{spinnerType, spinnerAccess, spinnerFee, spinnerSupervised, spinnerBus, spinnerTrucks, spinnerDisabled, spinnerMoto};
        EditText [] editTexts = new EditText[]{nameET, capacityET, operatorET, cenaET};

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

        loadDataDB(spinners, editTexts, edit);

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
    public void loadDataDB(Spinner [] spinners, EditText [] editTexts, Button edit)
    {
        Query q = db.collection("parkings").whereEqualTo("id", id);
        dbm.getElements(q, new OnElementsGet()
        {
            @Override
            public void setOnElementsGet(List<DocumentSnapshot> documentSnapshotList)
            {
                for (DocumentSnapshot document : documentSnapshotList)
                {
                    //Get values from database and intialize it to spinners, editTexts
                    String nam = (String) document.getData().get("name");
                    String pkg = (String) document.getData().get("pking");
                    String cpc = (String) document.getData().get("capacity");
                    String f33 = (String) document.getData().get("fee");
                    String sup = (String) document.getData().get("supervised");
                    String ope = (String) document.getData().get("operator");
                    String acc = (String) document.getData().get("access");
                    String cdis = (String) document.getData().get("capacityDisabled");
                    String ctru = (String) document.getData().get("capacityTruck");
                    String cbus = (String) document.getData().get("capacityBus");
                    String cmot = (String) document.getData().get("capacityMotorcycle");
                    String cena = (String) document.getData().get("kwota");
                    schedule = (Map<String, String>) document.getData().get("harmonogram");
                    double latitude = (double) document.getData().get("latitude");
                    double longitude = (double) document.getData().get("longitude");
                    String created = (String) document.getData().get("dataCreated");

                    editTexts[0].setText(nam);
                    editTexts[1].setText(cpc);
                    editTexts[2].setText(ope);
                    editTexts[3].setText(cena);
                    spinners[0].setSelection(getPosition(pkg, typesEN));
                    spinners[1].setSelection(getPosition(acc, accessTabEN));
                    spinners[2].setSelection(getPosition(cdis, opcjeEN));
                    spinners[3].setSelection(getPosition(f33, opcjeEN));
                    spinners[4].setSelection(getPosition(sup, opcjeEN));
                    spinners[5].setSelection(getPosition(cbus, opcjeEN));
                    spinners[6].setSelection(getPosition(cmot, opcjeEN));
                    spinners[7].setSelection(getPosition(ctru, opcjeEN));

                    editListener(latitude, longitude, created, edit, spinners, editTexts);
                }
            }
        });
    }

    private void editListener(double lat, double lon, String created, Button edit, Spinner [] spinners, EditText [] editTexts)
    {
        edit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Get values from editText, spinners
                String name = editTexts[0].getText().toString();
                String capacity = editTexts[1].getText().toString();
                String parking = getValue(spinners[0], typesEN);
                String access = getValue(spinners[1], accessTabEN);
                String fee = getValue(spinners[2], opcjeEN);
                String supervised = getValue(spinners[3], opcjeEN);
                String operator = editTexts[2].getText().toString();
                String cbus = getValue(spinners[4], opcjeEN);
                String ctru = getValue(spinners[5], opcjeEN);
                String cdis = getValue(spinners[6], opcjeEN);
                String cmot = getValue(spinners[7], opcjeEN);
                String price = editTexts[3].getText().toString();

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
                mapa.put("dataEdited", get.getActualDate());
                mapa.put("harmonogram", schedule);
                mapa.put("kwota", price);
                mapa.put("action", "Edytowano");
                mapa.put("sample", false);

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

                String editedId = get.generateId();

                //Create new object and add edit

                Parking newParking2 = new Parking(user.getUid(), id, editedId, name, parking,
                        access, capacity, cdis, ctru, cbus, cmot,
                        fee, supervised, operator, lat, lon, created,
                        get.getActualDate(), "", "Edytowano",
                        schedule, price, false, "Oczekujący",
                        Calendar.getInstance().getTime(), false, "xyz");

                Log.i("EDS", editedId);

                ParkingManager pm = new ParkingManager();
                pm.editParking(newParking2, mapa, id, editedId);

                Intent i = new Intent(getApplicationContext(), ParkingEditHistoryActivity.class);
                i.putExtra("ID", id);
                startActivity(i);

                finish();
            }
        });
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