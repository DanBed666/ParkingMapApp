package com.example.parkingmapapp;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Map;

public class HarmonogramActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_harmonogram);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Map<String, String> schedule = (Map<String, String>) getIntent().getSerializableExtra("SCHEDULE");

        TextView titleHarm = findViewById(R.id.tv_supervisedeins);
        TextView monHarm = findViewById(R.id.tv_supervisedmon);
        TextView tueHarm = findViewById(R.id.tv_supervisedtue);
        TextView wedHarm = findViewById(R.id.tv_supervisedwed);
        TextView thuHarm = findViewById(R.id.tv_supervisedthu);
        TextView friHarm = findViewById(R.id.tv_supervisedfri);
        TextView satHarm = findViewById(R.id.tv_supervisedsat);
        TextView sunHarm = findViewById(R.id.tv_supervisedsun);

        titleHarm.setText("Parking całodobowy: " + schedule.get("Parking całodobowy"));
        monHarm.setText("Poniedziałek: " + schedule.get("Poniedziałek"));
        tueHarm.setText("Wtorek: " + schedule.get("Wtorek"));
        wedHarm.setText("Środa: " + schedule.get("Środa"));
        thuHarm.setText("Czwartek: " + schedule.get("Czwartek"));
        friHarm.setText("Piątek: " + schedule.get("Piątek"));
        satHarm.setText("Sobota: " + schedule.get("Sobota"));
        sunHarm.setText("Niedziela: " + schedule.get("Niedziela"));
    }
}