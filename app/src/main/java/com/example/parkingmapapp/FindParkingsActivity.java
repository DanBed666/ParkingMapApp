package com.example.parkingmapapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class FindParkingsActivity extends AppCompatActivity
{
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Query findingQuery = db.collection("parkings").whereEqualTo("verified", true);
    String findingTag = "amenity=parking";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_find_parkings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) ->
        {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ParkingManager pm = (ParkingManager) Objects.requireNonNull(getIntent().getExtras()).getSerializable("PM");
        //ParkingManager pm = (ParkingManager) getIntent().getParcelableExtra("PM");

        Button find = findViewById(R.id.btn_find);
        Spinner spinnerType = findViewById(R.id.spinner_type);
        Spinner spinnerAccess = findViewById(R.id.spinner_access);
        Spinner spinnerFee = findViewById(R.id.spinner_fee);
        Spinner spinnerSupervised = findViewById(R.id.spinner_supervised);
        Spinner spinnerBus = findViewById(R.id.spinner_bus);
        Spinner spinnerTrucks = findViewById(R.id.spinner_truck);
        Spinner spinnerDisabled = findViewById(R.id.spinner_disabled);
        Spinner spinnerMoto = findViewById(R.id.spinner_moto);
        EditText capacityET = findViewById(R.id.et_capacity);

        TextView busTV = findViewById(R.id.tv_bus);
        TextView tirTV = findViewById(R.id.tv_tir);
        TextView motoTV = findViewById(R.id.tv_moto);

        String[] types = getResources().getStringArray(R.array.types);
        String[] typesEN = {"", "surface", "street_side", "multi-storey", "underground"};
        String[] accessTab = getResources().getStringArray(R.array.access);
        String[] accessTabEN = {"", "yes", "private", "customers"};
        String[] opcje = getResources().getStringArray(R.array.options);
        String[] opcjeEN = {"", "yes", "no"};

        initializeAdapter(types, spinnerType);
        initializeAdapter(accessTab, spinnerAccess);
        initializeAdapter(opcje, spinnerFee);
        initializeAdapter(opcje, spinnerSupervised);
        initializeAdapter(opcje, spinnerBus);
        initializeAdapter(opcje, spinnerTrucks);
        initializeAdapter(opcje, spinnerDisabled);
        initializeAdapter(opcje, spinnerMoto);

        DatabaseManager dbm = new DatabaseManager();
        FirebaseUser userId = FirebaseAuth.getInstance().getCurrentUser();
        assert userId != null;
        Query q = db.collection("cars").whereEqualTo("userId", userId.getUid());

        dbm.getElements(q, new OnElementsGet()
        {
            @Override
            public void setOnElementsGet(List<DocumentSnapshot> documentSnapshotList)
            {
                for (DocumentSnapshot document : documentSnapshotList)
                {
                    String typ = (String) Objects.requireNonNull(document.getData()).get("type");
                    boolean primary = (boolean) document.getData().get("primary");

                    if (primary)
                    {
                        if (Objects.equals(typ, "Samochód osobowy"))
                        {
                            spinnerBus.setVisibility(View.GONE);
                            busTV.setVisibility(View.GONE);
                            spinnerTrucks.setVisibility(View.GONE);
                            tirTV.setVisibility(View.GONE);
                            spinnerMoto.setVisibility(View.GONE);
                            motoTV.setVisibility(View.GONE);
                        }
                        else if (Objects.equals(typ, "Tir"))
                        {
                            spinnerBus.setVisibility(View.GONE);
                            busTV.setVisibility(View.GONE);
                            spinnerMoto.setVisibility(View.GONE);
                            motoTV.setVisibility(View.GONE);
                        }
                        else if (Objects.equals(typ, "Motocykl"))
                        {
                            spinnerBus.setVisibility(View.GONE);
                            busTV.setVisibility(View.GONE);
                            spinnerTrucks.setVisibility(View.GONE);
                            tirTV.setVisibility(View.GONE);
                        }
                        else if (Objects.equals(typ, "Autokar"))
                        {
                            spinnerTrucks.setVisibility(View.GONE);
                            tirTV.setVisibility(View.GONE);
                            spinnerMoto.setVisibility(View.GONE);
                            motoTV.setVisibility(View.GONE);
                        }
                    }
                }
            }
        });

        find.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getTag8(spinnerType, typesEN, "parking");
                getTag8(spinnerAccess, accessTabEN, "access");
                getTag8(spinnerFee, opcjeEN, "fee");
                getTag8(spinnerSupervised, opcjeEN, "supervised");
                getTag8(spinnerBus, opcjeEN, "capacity:bus");
                getTag8(spinnerTrucks, opcjeEN, "capacity:truck");
                getTag8(spinnerDisabled, opcjeEN, "capacity:disabled");
                getTag8(spinnerMoto, opcjeEN, "capacity:motorcycle");

                if (!capacityET.getText().toString().isEmpty())
                {
                    findingTag += String.format("][capacity~'%s'",
                            setCapacity(Integer.parseInt(capacityET.getText().toString())));
                    findingQuery = findingQuery.whereLessThan("capacity", capacityET.getText().toString());
                }

                assert pm != null;
                pm.findParkings(findingTag);
                pm.findParkingsDB(findingQuery);

                finish();
            }
        });
    }

    public void initializeAdapter(String [] tab, Spinner spinner)
    {
        ArrayAdapter<String> aa = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, tab);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(aa);
        Log.i("POSITION", "Wykonuje");
    }

    public void getTag8(Spinner spinner, String [] tabEN, String queryTitle)
    {
        int position = spinner.getSelectedItemPosition();
        Log.i("POSITION", String.valueOf(position));

        if (position != 0)
        {
            findingTag += String.format("][%s=%s", queryTitle, tabEN[position]);
            findingQuery = findingQuery.whereEqualTo(queryTitle, tabEN[position]);
        }
    }

    public String setCapacity(int number)
    {
        String p = "";
        String word;
        int w = number / 10;
        int r = number % 10;

        if (number >= 1 && number <= 9)
            p = String.format("[1-%d]", number);
        else if (number >= 10 && number <= 19)
            p = String.format("[1-9]|[1][0-%d]", r);
        else if (number >= 20 && number <= 99)
        {
            String lancuch = "";
            for (int i = 1; i < w; i++)
            {
                lancuch += i;
            }
            p = String.format("[1-9]|[%s][0-9]|[%d][0-%d]", lancuch, w, r);
        }
        else if (number >= 100 && number <= 109)
            p = String.format("[1-9]{1}|[0-9]{2}|1[0][0-%d]", r);
        else if (number >= 110 && number <= 199)
        {
            String lancuch = "";
            w = (number / 10) % 10;

            for (int i = 0; i < w; i++)
            {
                lancuch += i;
            }

            p = String.format("[1-9]{1}|[0-9]{2}|1[%s][0-9]|1[%d][0-%d]", lancuch, w, r);
        }
        else if (number >= 200 && number <= 209)
            p = String.format("[1-9]{1}|[0-9]{2}|1[0123456789][0-9]|2[0][0-%d]", r);

        word = String.format("^(%s)$", p);

        return word;
    }
}