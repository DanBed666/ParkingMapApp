package com.example.parkingmapapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
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

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Button reserve;
    String keyId;
    private final String API_KEY = "FiyHNQAmeoWKRcEdp5KyYWOAaAKf-7hvtqkz--lGBDc";

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
        TextView info;
        Button infosp;

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_info, container, false);
        route = v.findViewById(R.id.btn_route);
        info = v.findViewById(R.id.tv_info);
        infosp = v.findViewById(R.id.btn_info);
        reserve = v.findViewById(R.id.btn_reservation);

        assert getArguments() != null;
        RouteManager rm = (RouteManager) getArguments().getSerializable("ROUTE");
        keyId = getArguments().getString("KEYID");
        Parking p = (Parking) getArguments().getSerializable("PARKING");
        boolean verified = getArguments().getBoolean("VERIFIED");

        DatabaseManager dbm = new DatabaseManager();
        GetTagData get = new GetTagData();

        Query q = db.collection("parkings").whereEqualTo("id", keyId);
        dbm.getElements(q, new OnElementsGet()
        {
            @Override
            public void setOnElementsGet(List<DocumentSnapshot> documentSnapshotList)
            {
                for (DocumentSnapshot d : documentSnapshotList)
                {
                    get.getAddressHere(d.get("latitude") + "," + d.get("longitude"),
                            API_KEY, info);

                    if (Objects.equals(d.getString("fee"), "yes") &&
                            !(Objects.requireNonNull(d.getString("kwota")).isEmpty()))
                    {
                        reserve.setVisibility(View.VISIBLE);
                        setReserve(reserve);
                    }

                    Log.i("EXDE", (String) Objects.requireNonNull(d.get("name")));
                }
            }
        });

        route.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                assert rm != null;
                rm.setRoute();
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

    public void setReserve(Button reserve)
    {
        reserve.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(requireActivity().getApplicationContext(), ParkingBookingActivity.class);
                intent.putExtra("KEYID", keyId);
                startActivity(intent);
            }
        });
    }
}