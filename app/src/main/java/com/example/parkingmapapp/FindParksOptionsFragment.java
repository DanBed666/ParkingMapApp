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
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
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
                Query q = db.collection("parkings");

                if (feeRG.getCheckedRadioButtonId() != -1)
                {
                    findingTag += String.format("][fee=%s", getChooseYN(feeRG));
                    q = q.whereEqualTo("fee", getChooseYN(feeRG));
                }

                if (supervisedRG.getCheckedRadioButtonId() != -1)
                {
                    findingTag += String.format("][supervised=%s", getChooseYN(supervisedRG));
                    q = q.whereEqualTo("supervised", getChooseYN(supervisedRG));
                }

                Log.i("TAGF", findingTag);
                u.findParkings(findingTag);

                u.findParkingsDB(q);

                Log.i("FIND", "click!");

                assert getFragmentManager() != null;
                getFragmentManager().beginTransaction().remove(FindParksOptionsFragment.this).commit();
            }
        });

        return v;
    }

    public String getChooseYN(RadioGroup radioGroup)
    {
        String wybor = "";
        String option;
        int optionId = radioGroup.getCheckedRadioButtonId();

        RadioButton radioButton = radioGroup.findViewById(optionId);
        option = radioButton.getText().toString();

        if (option.equals("Tak"))
        {
            wybor = "yes";
        }
        else if (option.equals("Nie"))
        {
            wybor = "no";
        }

        return wybor;
    }
}