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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Query findingQuery = db.collection("parkings").whereEqualTo("verified", true);
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    Spinner spinnerBus;
    Spinner spinnerTrucks;
    Spinner spinnerMoto;
    TextView busTV;
    TextView tirTV;
    TextView motoTV;
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
        EditText capacityET;
        Button find;

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_find_parks_options, container, false);
        //supervisedRG = v.findViewById(R.id.rg_sup);
        find = v.findViewById(R.id.btn_find);

        FirebaseUser userId = mAuth.getCurrentUser();

        assert userId != null;
        getCars(userId.getUid());

        Spinner spinnerType = v.findViewById(R.id.spinner_type);
        Spinner spinnerAccess = v.findViewById(R.id.spinner_access);
        Spinner spinnerFee = v.findViewById(R.id.spinner_fee);
        Spinner spinnerSupervised = v.findViewById(R.id.spinner_supervised);
        spinnerBus = v.findViewById(R.id.spinner_bus);
        spinnerTrucks = v.findViewById(R.id.spinner_truck);
        Spinner spinnerDisabled = v.findViewById(R.id.spinner_disabled);
        spinnerMoto = v.findViewById(R.id.spinner_moto);
        capacityET = v.findViewById(R.id.et_capacity);

        busTV = v.findViewById(R.id.tv_bus);
        tirTV = v.findViewById(R.id.tv_tir);
        motoTV = v.findViewById(R.id.tv_moto);

        String[] types = getResources().getStringArray(R.array.types);
        String[] typesEN = {"", "surface", "street_side", "multi-storey", "underground"};
        String[] accessTab = getResources().getStringArray(R.array.access);
        String[] accessTabEN = {"", "yes", "private", "customers"};
        String[] opcje = getResources().getStringArray(R.array.options);
        String[] opcjeEN = {"", "yes", "no"};

        assert getArguments() != null;
        Utils u = (Utils) getArguments().getSerializable("OBJECT");

        getValue(types, spinnerType);
        getValue(accessTab, spinnerAccess);
        getValue(opcje, spinnerFee);
        getValue(opcje, spinnerSupervised);
        getValue(opcje, spinnerBus);
        getValue(opcje, spinnerTrucks);
        getValue(opcje, spinnerDisabled);
        getValue(opcje, spinnerMoto);

        //spinnerBus.setVisibility(View.GONE);
        //busTV.setVisibility(View.GONE);

        find.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                assert u != null;

                Log.i("POZYCJA", String.valueOf(spinnerType.getSelectedItemPosition()));
                Log.i("POZYCJA", String.valueOf(spinnerFee.getSelectedItemPosition()));

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
                    findingTag += String.format("][capacity~'%s'", algos(Integer.parseInt(capacityET.getText().toString())));
                    //findingTag += "][capacity~'^([1-9])$'";
                    findingQuery = findingQuery.whereLessThan("capacity", capacityET.getText().toString());
                }

                Log.i("TAGF", findingTag);
                u.findParkings(findingTag);
                u.findParkingsDB(findingQuery);

                Log.i("FIND", "click!");

                Log.i("TAG", findingTag);
                Log.i("QUERY", findingQuery.get().toString());

                assert getFragmentManager() != null;
                getFragmentManager().beginTransaction().remove(FindParksOptionsFragment.this).commit();
            }
        });

        return v;
    }
    public void getValue(String [] tab, Spinner spinner)
    {
        ArrayAdapter<String> aa = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, tab);
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

    public void getCars(String userId)
    {
        db.collection("cars").whereEqualTo("userId", userId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task)
            {
                if (task.isSuccessful())
                {
                    for (QueryDocumentSnapshot document : task.getResult())
                    {
                        String typ = (String) document.getData().get("type");
                        boolean primary = (boolean) document.getData().get("primary");

                        assert typ != null;
                        Log.i("TIPO", typ);

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
                else
                {
                    Log.w("ERR", "Error getting documents.", task.getException());
                }
            }
        });
    }

    public String algos(int number)
    {
        String p = "";
        String word;
        int w = number / 10;
        int r = number % 10;

        if (number >= 1 && number <= 9) p = String.format("[1-%d]", number);
        else if (number >= 10 && number <= 19) p = String.format("[1-9]|[1][0-%d]", r);
        else if (number >= 20 && number <= 99)
        {
            String lancuch = "";
            for (int i = 1; i < w; i++)
            {
                lancuch += i;
            }
            p = String.format("[1-9]|[%s][0-9]|[%d][0-%d]", lancuch, w, r);
        }
        else if (number >= 100 && number <= 109) p = String.format("[1-9]{1}|[0-9]{2}|1[0][0-%d]", r);
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
        else if (number >= 200 && number <= 209) p = String.format("[1-9]{1}|[0-9]{2}|1[0123456789][0-9]|2[0][0-%d]", r);

        word = String.format("^(%s)$", p);

        return word;
    }
}