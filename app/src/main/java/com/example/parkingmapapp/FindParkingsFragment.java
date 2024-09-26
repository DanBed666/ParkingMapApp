package com.example.parkingmapapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FindParkingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FindParkingsFragment extends Fragment {

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
    String[] typesEN = {"", "surface", "street_side", "multi-storey", "underground"};
    String[] accessTabEN = {"", "yes", "private", "customers"};
    String[] opcjeEN = {"", "yes", "no"};

    public FindParkingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FindParkingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FindParkingsFragment newInstance(String param1, String param2) {
        FindParkingsFragment fragment = new FindParkingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_find_parkings, container, false);

        Spinner spinnerType = v.findViewById(R.id.spinner_type);
        Spinner spinnerAccess = v.findViewById(R.id.spinner_access);
        Spinner spinnerFee = v.findViewById(R.id.spinner_fee);
        Spinner spinnerSupervised = v.findViewById(R.id.spinner_supervised);
        Spinner spinnerBus = v.findViewById(R.id.spinner_bus);
        Spinner spinnerTrucks = v.findViewById(R.id.spinner_truck);
        Spinner spinnerDisabled = v.findViewById(R.id.spinner_disabled);
        Spinner spinnerMoto = v.findViewById(R.id.spinner_moto);
        EditText capacityET = v.findViewById(R.id.et_capacity);
        TextView busTV = v.findViewById(R.id.tv_bus);
        TextView tirTV = v.findViewById(R.id.tv_tir);
        TextView motoTV = v.findViewById(R.id.tv_moto);
        Button find = v.findViewById(R.id.btn_find);
        Button close = v.findViewById(R.id.btn_close);

        String[] types = getResources().getStringArray(R.array.types);
        String[] accessTab = getResources().getStringArray(R.array.access);
        String[] opcje = getResources().getStringArray(R.array.options);

        Spinner [] spinners = new Spinner[]{spinnerType, spinnerAccess, spinnerFee, spinnerSupervised, spinnerBus, spinnerTrucks, spinnerDisabled, spinnerMoto};
        TextView [] textViews = new TextView[]{busTV, tirTV, motoTV};

        assert getArguments() != null;
        ParkingManager pm = (ParkingManager) getArguments().getSerializable("PM");

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
                    filterByCars(document, spinners, textViews);
                }
            }
        });

        find.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                setQueries(spinners, capacityET, pm);
                closeFragment();
            }
        });

        close.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                closeFragment();
            }
        });

        return v;
    }

    public void initializeAdapter(String [] tab, Spinner spinner)
    {
        ArrayAdapter<String> aa = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, tab);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(aa);
        Log.i("POSITION", "Wykonuje");
    }

    public void closeFragment()
    {
        assert getFragmentManager() != null;
        getFragmentManager().beginTransaction().remove(FindParkingsFragment.this).commit();
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

    public void setQueries(Spinner [] spinners, EditText capacityET, ParkingManager pm)
    {
        getTag8(spinners[0], typesEN, "parking");
        getTag8(spinners[1], accessTabEN, "access");
        getTag8(spinners[2], opcjeEN, "fee");
        getTag8(spinners[3], opcjeEN, "supervised");
        getTag8(spinners[4], opcjeEN, "capacity:bus");
        getTag8(spinners[5], opcjeEN, "capacity:truck");
        getTag8(spinners[6], opcjeEN, "capacity:disabled");
        getTag8(spinners[7], opcjeEN, "capacity:motorcycle");

        if (!capacityET.getText().toString().isEmpty())
        {
            findingTag += String.format("][capacity~'%s'",
                    setCapacity(Integer.parseInt(capacityET.getText().toString())));

            findingQuery = findingQuery.whereLessThan("capacity",
                    capacityET.getText().toString());
        }

        pm.findParkings(findingTag);
        pm.findParkingsDB(findingQuery);
    }

    public void filterByCars(DocumentSnapshot document, Spinner [] spinners, TextView [] textViews)
    {
        String typ = (String) Objects.requireNonNull(document.getData()).get("type");
        boolean primary = (boolean) document.getData().get("primary");

        if (primary)
        {
            if (Objects.equals(typ, "Samochód osobowy"))
            {
                spinners[4].setVisibility(View.GONE);  //busSpinner
                textViews[0].setVisibility(View.GONE); //busTV
                spinners[5].setVisibility(View.GONE);  //truckSpinner
                textViews[1].setVisibility(View.GONE); //truckTV
                spinners[7].setVisibility(View.GONE);  //motoSpinner
                textViews[2].setVisibility(View.GONE); //motoTV
            }
            else if (Objects.equals(typ, "Tir"))
            {
                spinners[4].setVisibility(View.GONE);  //busSpinner
                textViews[0].setVisibility(View.GONE); //busTV
                spinners[7].setVisibility(View.GONE);  //motoSpinner
                textViews[2].setVisibility(View.GONE); //motoTV
            }
            else if (Objects.equals(typ, "Motocykl"))
            {
                spinners[4].setVisibility(View.GONE);  //busSpinner
                textViews[0].setVisibility(View.GONE); //busTV
                spinners[5].setVisibility(View.GONE);  //truckSpinner
                textViews[1].setVisibility(View.GONE); //truckTV
            }
            else if (Objects.equals(typ, "Autokar"))
            {
                spinners[5].setVisibility(View.GONE);  //truckSpinner
                textViews[1].setVisibility(View.GONE); //truckTV
                spinners[7].setVisibility(View.GONE);  //motoSpinner
                textViews[2].setVisibility(View.GONE); //motoTV
            }
        }
    }
}