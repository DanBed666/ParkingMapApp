package com.example.parkingmapapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

    FirebaseDatabase database = FirebaseDatabase.getInstance("https://parkingmapapp-39ec0-default-rtdb.europe-west1.firebasedatabase.app/");
    DatabaseReference addedparkings = database.getReference("addedparkings");

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
        feeRG = v.findViewById(R.id.rg_fee);
        supervisedRG = v.findViewById(R.id.rg_sup);
        capacityET = v.findViewById(R.id.et_capacity);
        parkingET = v.findViewById(R.id.et_parking);
        find = v.findViewById(R.id.btn_find);

        assert getArguments() != null;
        Utils u = (Utils) getArguments().getSerializable("OBJECT");

        find.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                assert u != null;
                String choose = getChooseYN(supervisedRG);
                //u.findParkings(String.format("amenity=parking][supervised=%s", choose));
                u.findParkings("amenity=parking][fee=no][supervised=no");

                assert getFragmentManager() != null;
                getFragmentManager().beginTransaction().remove(FindParksOptionsFragment.this).commit();
            }
        });

        return v;
    }

    public String getChooseYN(RadioGroup radioGroup)
    {
        String wybor = "";
        String supervised;
        int supervisedId = radioGroup.getCheckedRadioButtonId();

        if (supervisedId != -1)
        {
            RadioButton radioButton = radioGroup.findViewById(supervisedId);
            supervised = radioButton.getText().toString();

            if (supervised.equals("Tak"))
            {
                wybor = "yes";
            }
            else if (supervised.equals("Nie"))
            {
                wybor = "no";
            }
        }

        return wybor;
    }

    public void databaseFilter()
    {
        addedparkings.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                Log.i("ILE", String.valueOf(snapshot.getChildrenCount()));

                for (DataSnapshot s : snapshot.getChildren())
                {
                    addedparkings.child(Objects.requireNonNull(s.getKey())).addValueEventListener(new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot)
                        {
                            Log.i("KIDOS", Objects.requireNonNull(snapshot.getKey()));
                            String parking = snapshot.child("supervised").getValue(String.class);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error)
                        {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });
    }
}