package com.example.parkingmapapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

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
    AddressViewModel addressViewModel;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String addressStr;
    Button delete;
    CloseMarker listener;
    Button reserve;
    String keyId;

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
        addressViewModel = new AddressViewModel();

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_info, container, false);
        route = v.findViewById(R.id.btn_route);
        info = v.findViewById(R.id.tv_info);
        infosp = v.findViewById(R.id.btn_info);
        reserve = v.findViewById(R.id.btn_reservation);
        delete = v.findViewById(R.id.btn_delete);

        assert getArguments() != null;
        Utils u = (Utils) getArguments().getSerializable("OBJECT");
        keyId = getArguments().getString("KEYID");
        Parking p = (Parking) getArguments().getSerializable("PARKING");

        getInfo(keyId, info);
        getEdits(keyId);

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
                intent.putExtra("ADDRESS", addressStr);
                startActivity(intent);
            }
        });
        delete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                listener.closeMarker();
                deleteParking(keyId);
                getFragmentManager().beginTransaction().remove(InfoFragment.this).commit();
            }
        });

        return v;
    }

    public void getInfo(String id, TextView info)
    {
        db.collection("parkings").whereEqualTo("id", id).addSnapshotListener(new EventListener<QuerySnapshot>()
        {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error)
            {
                assert value != null;
                for (DocumentChange d : value.getDocumentChanges())
                {
                    getAddressNominatim(d.getDocument().get("latitude") + "," + d.getDocument().get("longtitude"),
                            "FiyHNQAmeoWKRcEdp5KyYWOAaAKf-7hvtqkz--lGBDc", info);

                    if (d.getDocument().get("fee") == "yes" && d.getDocument().get("kwota") != null)
                    {
                        reserve.setVisibility(View.VISIBLE);
                        setReserve(reserve);
                    }
                    else
                    {
                        reserve.setVisibility(View.GONE);
                    }

                    Log.i("TYP", String.valueOf(d.getType()));
                    Log.i("EXDE", (String) Objects.requireNonNull(d.getDocument().get("name")));
                }
            }
        });
    }

    public void getAddressNominatim(String geoPoint, String apiKey, TextView info)
    {
        addressViewModel.getAddressVM(geoPoint, apiKey).observeForever(new Observer<Address>()
        {
            @Override
            public void onChanged(Address address)
            {
                Log.i("ADRES", address.getItems().get(0).getTitle());
                Log.i("ADRESADRADRADR", address.getItems().get(0).getTitle());
                info.setText(address.getItems().get(0).getTitle());
                addressStr = address.getItems().get(0).getTitle();
            }
        });
    }

    public void getEdits(String id)
    {
        db.collection("edits").whereEqualTo("id", id).addSnapshotListener(new EventListener<QuerySnapshot>()
        {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error)
            {
                assert value != null;
                for (DocumentChange d : value.getDocumentChanges())
                {
                    if (Boolean.TRUE.equals(d.getDocument().getBoolean("created")))
                    {
                        delete.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    public void deleteParking(String id)
    {
        db.collection("parkings").document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>()
        {
            @Override
            public void onSuccess(Void unused)
                {
                    Log.i("CREATED", "usunieto");
                }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Log.i("CREATED", e.getMessage());
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);

        try
        {
            listener = (CloseMarker) context;
        }
        catch (ClassCastException castException)
        {
            /** The activity does not implement the listener. */
        }
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