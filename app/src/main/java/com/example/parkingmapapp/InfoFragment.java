package com.example.parkingmapapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;
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

    FirebaseDatabase database = FirebaseDatabase.getInstance("https://parkingmapapp-39ec0-default-rtdb.europe-west1.firebasedatabase.app/");
    DatabaseReference parkings = database.getReference("parkings");
    DatabaseReference addedparkings = database.getReference("addedparkings");
    FirebaseFirestore db = FirebaseFirestore.getInstance();

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
        TextView info;
        Button infosp;

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_info, container, false);
        route = v.findViewById(R.id.btn_route);
        info = v.findViewById(R.id.tv_info);
        infosp = v.findViewById(R.id.btn_info);

        assert getArguments() != null;
        Utils u = (Utils) getArguments().getSerializable("OBJECT");
        String keyId = getArguments().getString("KEYID");
        Parking p = (Parking) getArguments().getSerializable("PARKING");

        getInfo(keyId, info);

        route.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                assert u != null;
                u.setRoute();
            }
        });
        infosp.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(requireActivity().getApplicationContext(), ParkingInfoActivity.class);
                intent.putExtra("KEYID", keyId);
                intent.putExtra("PARKING", p);
                startActivity(intent);
            }
        });

        return v;
    }

    public void getInfo(String id, TextView info)
    {
        DatabaseReference p = parkings.child(id);
        p.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                DataSnapshot s = snapshot.child("name");

                if (s.exists())
                {
                    Log.i("SAMPLE", Objects.requireNonNull(s.getValue(String.class)));
                    info.setText(s.getValue(String.class));
                }
                else
                {
                    Log.i("SAMPLE", "Brak");
                    info.setText("Brak danych");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });

        DatabaseReference p2 = addedparkings.child(id);
        p2.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                DataSnapshot s = snapshot.child("name");

                if (s.exists())
                {
                    Log.i("SAMPLE", Objects.requireNonNull(s.getValue(String.class)));
                    info.setText(s.getValue(String.class));
                }
                else
                {
                    Log.i("SAMPLE", "Brak");
                    info.setText("Brak danych");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });
    }
}