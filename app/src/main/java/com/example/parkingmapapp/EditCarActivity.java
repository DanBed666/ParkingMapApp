package com.example.parkingmapapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class EditCarActivity extends AppCompatActivity {

    String carId;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String documentId;
    SwitchMaterial primarySw;
    Spinner typSpinner;
    String[] types = {"Samochód osobowy", "Tir", "Motocykl", "Autokar"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_car);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        DatabaseManager dbm = new DatabaseManager();
        carId = getIntent().getStringExtra("CARID");
        documentId = getIntent().getStringExtra("DOCUMENTID");
        EditText markaET = findViewById(R.id.et_marka);
        EditText modelET = findViewById(R.id.et_model);
        typSpinner = findViewById(R.id.spinner_car);
        EditText numerET = findViewById(R.id.et_numer);
        EditText rokET = findViewById(R.id.et_rok);
        Button confirm = findViewById(R.id.btn_add);
        primarySw = findViewById(R.id.switch_primary);

        EditText [] editTexts = new EditText[]{markaET, modelET, numerET, rokET};

        ArrayAdapter<String> aa = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, types);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typSpinner.setAdapter(aa);

        Query q = db.collection("cars").whereEqualTo("id", carId);
        dbm.getElements(q, new OnElementsGet()
        {
            @Override
            public void setOnElementsGet(List<DocumentSnapshot> documentSnapshotList)
            {
                for (DocumentSnapshot document : documentSnapshotList)
                {
                    Log.d("LOL", document.getId() + " => " + document.getData());
                    Log.i("XD", "xd");
                    String marka = (String) document.getData().get("marka");
                    String model = (String) document.getData().get("model");
                    String type = (String) document.getData().get("type");
                    String reg = (String) document.getData().get("registrationNumber");
                    String year = (String) document.getData().get("year");
                    boolean primary = (boolean) document.getData().get("primary");

                    editTexts[0].setText(marka);
                    editTexts[1].setText(model);

                    for (int i = 0; i < types.length; i++)
                    {
                        if (Objects.equals(type, types[i]))
                        {
                            typSpinner.setSelection(i);
                            break;
                        }
                    }

                    editTexts[2].setText(reg);
                    editTexts[3].setText(year);

                    if (primary)
                        primarySw.setChecked(true);
                }
            }
        });
        confirm.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int position = typSpinner.getSelectedItemPosition();

                String marka = markaET.getText().toString();
                String model = modelET.getText().toString();
                String typ = types[position];
                String numer = numerET.getText().toString();
                String rok = rokET.getText().toString();
                boolean primary = false;

                if (primarySw.isChecked())
                    primary = true;

                Map<String, Object> mapa = new HashMap<>();
                mapa.put("marka", marka);
                mapa.put("model", model);
                mapa.put("primary", primary);
                mapa.put("registrationNumber", numer);
                mapa.put("type", typ);
                mapa.put("year", rok);

                dbm.addElement("cars", documentId, mapa);

                Intent intent = new Intent(getApplicationContext(), AddCarActivity.class);
                setResult(RESULT_OK, intent);
                finish();
                Toast.makeText(getApplicationContext(), "Zmiany zostały zapisane!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}