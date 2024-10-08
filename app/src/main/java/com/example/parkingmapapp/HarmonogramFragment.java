package com.example.parkingmapapp;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HarmonogramFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HarmonogramFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Map<String, String> harmonogram = new HashMap<>();
    HarmValueListener listener;

    public HarmonogramFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HarmonogramFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HarmonogramFragment newInstance(String param1, String param2) {
        HarmonogramFragment fragment = new HarmonogramFragment();
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
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_harmonogram, container, false);

        SwitchMaterial allDay = v.findViewById(R.id.switch_all);
        SwitchMaterial monday = v.findViewById(R.id.switch_mon);
        SwitchMaterial tuesday = v.findViewById(R.id.switch_tue);
        SwitchMaterial wednesday = v.findViewById(R.id.switch_wed);
        SwitchMaterial thursday = v.findViewById(R.id.switch_thu);
        SwitchMaterial friday = v.findViewById(R.id.switch_fri);
        SwitchMaterial saturday = v.findViewById(R.id.switch_sat);
        SwitchMaterial sunday = v.findViewById(R.id.switch_sun);

        EditText monBegin = v.findViewById(R.id.mon_start);
        EditText monEnd = v.findViewById(R.id.mon_end);
        EditText tueBegin = v.findViewById(R.id.tue_start);
        EditText tueEnd = v.findViewById(R.id.tue_end);
        EditText wedBegin = v.findViewById(R.id.wed_start);
        EditText wedEnd = v.findViewById(R.id.wed_end);
        EditText thuBegin = v.findViewById(R.id.thu_start);
        EditText thuEnd = v.findViewById(R.id.thu_end);
        EditText friBegin = v.findViewById(R.id.fri_start);
        EditText friEnd = v.findViewById(R.id.fri_end);
        EditText satBegin = v.findViewById(R.id.sat_start);
        EditText satEnd = v.findViewById(R.id.sat_end);
        EditText sunBegin = v.findViewById(R.id.sun_start);
        EditText sunEnd = v.findViewById(R.id.sun_end);
        Button save = v.findViewById(R.id.btn_save);

        assert getArguments() != null;

        if (getArguments().getSerializable("SCHEDULE") != null)
        {
            harmonogram = (Map<String, String>) getArguments().getSerializable("SCHEDULE");
            assert harmonogram != null;
            //Log.i("WT", Objects.requireNonNull(harmonogram.get("Wtorek")));
        }

        allDay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked)
                {
                    harmonogram.put("Parking całodobowy", "24/7");
                    monday.setClickable(false);
                    tuesday.setClickable(false);
                    wednesday.setClickable(false);
                    thursday.setClickable(false);
                    friday.setClickable(false);
                    saturday.setClickable(false);
                    sunday.setClickable(false);
                }
            }
        });

        if (!getValuesMapAll(allDay))
        {
            getValuesMap(monday, monBegin, monEnd, "Poniedziałek");
            getValuesMap(tuesday, tueBegin, tueEnd, "Wtorek");
            getValuesMap(wednesday, wedBegin, wedEnd, "Środa");
            getValuesMap(thursday, thuBegin, thuEnd, "Czwartek");
            getValuesMap(friday, friBegin, friEnd, "Piątek");
            getValuesMap(saturday, satBegin, satEnd, "Sobota");
            getValuesMap(sunday, sunBegin, sunEnd, "Niedziela");
        }

        save.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getValue(monday, monBegin, monEnd, "Poniedziałek");
                getValue(tuesday, tueBegin, tueEnd, "Wtorek");
                getValue(wednesday, wedBegin, wedEnd, "Środa");
                getValue(thursday, thuBegin, thuEnd, "Czwartek");
                getValue(friday, friBegin, friEnd, "Piątek");
                getValue(saturday, satBegin, satEnd, "Sobota");
                getValue(sunday, sunBegin, sunEnd, "Niedziela");
                listener.onStringReceived(harmonogram);
                assert getFragmentManager() != null;
                getFragmentManager().beginTransaction().remove(HarmonogramFragment.this).commit();
            }
        });

        return v;
    }

    public void getValue(SwitchMaterial materialSwitch, EditText begin, EditText end, String day)
    {
        if (materialSwitch.isChecked())
        {
            harmonogram.put(day, String.format("%s-%s", begin.getText().toString(), end.getText().toString()));
        }
    }

    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);

        try
        {
            listener = (HarmValueListener) context;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(context.toString()
                    + " must implement TaskIdListener");
        }
    }
    public boolean getValuesMapAll(SwitchMaterial element)
    {
        boolean allDay = false;

        for (Map.Entry<String, String> day : harmonogram.entrySet())
        {
            if (day.getKey() == element.getText())
            {
                element.setChecked(true);
                allDay = true;
                harmonogram.remove("Brak");
            }
        }

        return allDay;
    }

    public void getValuesMap(SwitchMaterial element, EditText begin, EditText end, String day)
    {
        harmonogram.remove("Brak");
        String hours = harmonogram.get(day);
        StringBuilder beginStr = new StringBuilder();
        StringBuilder endStr = new StringBuilder();

        Log.i("DAY8", day + " " + hours);

        if (hours != null)
        {
            element.setChecked(true);

            for (int i = 0; i < 5; i++)
            {
                beginStr.append(hours.toCharArray()[i]);
            }

            for (int i = 6; i < hours.length(); i++)
            {
                endStr.append(hours.toCharArray()[i]);
            }
        }

        begin.setText(beginStr.toString());
        end.setText(endStr.toString());
    }
}