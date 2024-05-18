package com.example.parkingmapapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InfoFragment extends Fragment
{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public InfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InfoFragment newInstance(String param1, String param2) {
        InfoFragment fragment = new InfoFragment();
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
        Button route;
        Button add;
        TextView test;
        TextView info;

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_info, container, false);
        route = v.findViewById(R.id.btn_route);
        test = v.findViewById(R.id.tv_test);
        add = v.findViewById(R.id.btn_add);
        info = v.findViewById(R.id.tv_info);

        assert getArguments() != null;
        Utils u = (Utils) getArguments().getSerializable("OBJECT");
        String id = getArguments().getString("ID");
        String keyId = getArguments().getString("KEYID");
        Parking p = (Parking) getArguments().getSerializable("PARKING");

        test.setText(id);
        assert p != null;
        info.setText(p.getSample());

        route.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                assert u != null;
                u.setRoute();
            }
        });

        add.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(requireActivity().getApplicationContext(), AddParking.class);
                intent.putExtra("PARKING", p);
                intent.putExtra("KEYID", keyId);
                startActivity(intent);
            }
        });

        return v;
    }
}