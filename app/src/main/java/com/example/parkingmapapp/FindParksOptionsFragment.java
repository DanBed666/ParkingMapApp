package com.example.parkingmapapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FindParksOptionsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FindParksOptionsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    String findingTag = "amenity=parking";
    Query findingQuery;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public FindParksOptionsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FindParksOptionsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FindParksOptionsFragment newInstance(String param1, String param2) {
        FindParksOptionsFragment fragment = new FindParksOptionsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        RadioGroup feeRG;
        RadioGroup supervisedRG;
        EditText capacityET;
        EditText parkingET;
        Button find;

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_find_parks_options, container, false);
        //supervisedRG = v.findViewById(R.id.rg_sup);
        find = v.findViewById(R.id.btn_find);

        Spinner spinnerType = v.findViewById(R.id.spinner_type);
        Spinner spinnerAccess = v.findViewById(R.id.spinner_access);
        Spinner spinnerFee = v.findViewById(R.id.spinner_fee);
        Spinner spinnerSupervised = v.findViewById(R.id.spinner_supervised);
        Spinner spinnerBus = v.findViewById(R.id.spinner_bus);
        Spinner spinnerTrucks = v.findViewById(R.id.spinner_truck);
        Spinner spinnerDisabled = v.findViewById(R.id.spinner_disabled);
        Spinner spinnerMoto = v.findViewById(R.id.spinner_moto);
        capacityET = v.findViewById(R.id.et_capacity);

        String[] types = {"Dowolny", "Naziemny", "Przyległy do drogi", "Wielopoziomowy", "Podziemny"};
        String[] typesEN = {"", "surface", "street_side", "multi-storey", "underground"};
        String[] accessTab = {"Dowolny", "Otwarty", "Prywatny", "Dla klientów"};
        String[] accessTabEN = {"", "yes", "private", "customers"};
        String[] opcje = {"Dowolny", "Tak", "Nie"};
        String[] opcjeEN = {"", "yes", "no"};

        assert getArguments() != null;
        Utils u = (Utils) getArguments().getSerializable("OBJECT");

        find.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                assert u != null;
                findingQuery = db.collection("parkings");

                getValue(types, spinnerType, typesEN, "parking");
                getValue(accessTab, spinnerAccess, accessTabEN, "access");
                getValue(opcje, spinnerFee, opcjeEN, "fee");
                getValue(opcje, spinnerSupervised, opcjeEN, "supervised");
                getValue(opcje, spinnerBus, opcjeEN, "capacity:bus");
                getValue(opcje, spinnerTrucks, opcjeEN, "capacity:truck");
                getValue(opcje, spinnerDisabled, opcjeEN, "capacity:disabled");
                getValue(opcje, spinnerMoto, opcjeEN, "capacity:motorcycle");

                if (!capacityET.getText().toString().isEmpty())
                {
                    findingTag += String.format("][capacity<%s", capacityET.getText().toString());
                    findingQuery = findingQuery.whereLessThan("capacity", capacityET.getText().toString());
                }

                Log.i("TAGF", findingTag);
                u.findParkings(findingTag);
                u.findParkingsDB(findingQuery);

                Log.i("FIND", "click!");

                assert getFragmentManager() != null;
                getFragmentManager().beginTransaction().remove(FindParksOptionsFragment.this).commit();
            }
        });

        return v;
    }
    public void getValue(String [] tab, Spinner spinner, String [] tabEN, String queryTitle)
    {
        ArrayAdapter<String> aa = new ArrayAdapter<>(requireActivity().getApplicationContext(), android.R.layout.simple_spinner_item, tab);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(aa);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                if (!tabEN[position].isEmpty())
                {
                    findingTag += String.format("][%s=%s", queryTitle, tabEN[position]);
                    findingQuery = findingQuery.whereEqualTo(queryTitle, tabEN[position]);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
    }
}