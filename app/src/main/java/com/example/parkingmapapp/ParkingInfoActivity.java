package com.example.parkingmapapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ParkingInfoActivity extends AppCompatActivity
{
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String documentId;
    String id;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_parking_info);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) ->
        {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView name = findViewById(R.id.tv_name);
        TextView parking = findViewById(R.id.tv_parking);
        TextView capacity = findViewById(R.id.tv_capacity);
        TextView fee = findViewById(R.id.tv_fee);
        TextView supervised = findViewById(R.id.tv_supervised);
        TextView operator = findViewById(R.id.tv_operator);
        Button edit = findViewById(R.id.btn_edit);
        TextView access = findViewById(R.id.tv_access);
        TextView capacityDis = findViewById(R.id.tv_capacitydis);
        TextView capacityTru = findViewById(R.id.tv_capacitytrucks);
        TextView capacityBus = findViewById(R.id.tv_capacitybus);
        TextView capacityMoto = findViewById(R.id.tv_capacitymoto);
        TextView created_date = findViewById(R.id.tv_created);
        TextView edited_date = findViewById(R.id.tv_edited);
        TextView verified_date = findViewById(R.id.tv_verified);
        TextView status = findViewById(R.id.tv_status);
        Button history = findViewById(R.id.btn_hist);
        TextView userTV = findViewById(R.id.tv_user);
        Button btnHarm = findViewById(R.id.btn_harm);
        TextView price = findViewById(R.id.tv_price);
        TextView nickVer = findViewById(R.id.tv_ver);

        id = getIntent().getStringExtra("KEYID");
        Parking p = (Parking) getIntent().getSerializableExtra("PARKING");
        String adr = getIntent().getStringExtra("ADDRESS");

        TextView [] textViews = new TextView[]{name, parking, capacity, fee, supervised, operator, access,
        capacityDis, capacityTru, capacityBus, capacityMoto, price, created_date, edited_date, verified_date, status, userTV};

        assert id != null;
        Log.i("PARKING_ID", id);

        getElementsFromDB("parkings", textViews, btnHarm);

        edit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EditParkingInfoActivity.class);
                intent.putExtra("KEYID", id);
                intent.putExtra("PARKING", p);
                intent.putExtra("DOCUMENTID", documentId);
                startActivity(intent);
            }
        });

        userTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), InfoProfileActivity.class));
            }
        });

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getApplicationContext(), ParkingEditHistoryActivity.class);
                intent.putExtra("ID", id);
                intent.putExtra("EDIT", "info");
                startActivity(intent);
            }
        });
    }

    public void getElementsFromDB(String col, TextView [] textViews, Button btnHarm)
    {
        DatabaseManager dbm = new DatabaseManager();
        Query q = db.collection(col).whereEqualTo("id", id);

        dbm.getElements(q, new OnElementsGet()
        {
            @Override
            public void setOnElementsGet(List<DocumentSnapshot> documentSnapshotList)
            {
                for (DocumentSnapshot document : documentSnapshotList)
                {
                    loadData(document, textViews, btnHarm);
                }
            }
        });
    }

    public void loadData(DocumentSnapshot document, TextView [] textViews, Button btnHarm)
    {
        Log.d("LOL", document.getId() + " => " + document.getData());
        Log.i("XD", document.getId() + " => " + document.getData());
        String nam = (String) document.getData().get("name");
        String pkg = (String) document.getData().get("pking");
        String cpc = (String) document.getData().get("capacity");
        String f33 = (String) document.getData().get("fee");
        String sup = (String) document.getData().get("supervised");
        String ope = (String) document.getData().get("operator");
        String acc = (String) document.getData().get("access");
        String cdis = (String) document.getData().get("capacityDisabled");
        String ctru = (String) document.getData().get("capacityTruck");
        String cbus = (String) document.getData().get("capacityBus");
        String cmot = (String) document.getData().get("capacityMotorcycle");
        String kwota = (String) document.getData().get("kwota");
        Map<String, String> harm = (Map<String, String>) document.getData().get("harmonogram");
        String createdDate = (String) document.getData().get("dataCreated");
        String editedDate = (String) document.getData().get("dataEdited");
        String verifiedDate = (String) document.getData().get("dataVerified");
        String stat = (String) document.getData().get("status");
        String user = (String) document.getData().get("uId");

        if (sup.equals("yes"))
        {
            btnHarm.setVisibility(View.VISIBLE);
        }

        btnHarm.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getApplicationContext(), HarmonogramActivity.class);
                intent.putExtra("SCHEDULE", (Serializable) harm);
                startActivity(intent);
            }
        });

        textViews[0].setText("Nazwa: " + nam);
        textViews[1].setText("Typ parkingu: " + pkg);
        textViews[2].setText("Wielkość: " + cpc);
        textViews[3].setText("Opłaty: " + f33);
        textViews[4].setText("Parking strzeżony: " + sup);
        textViews[5].setText("Operator: " + ope);
        textViews[6].setText("Dostęp: " + acc);
        textViews[7].setText("Miejsca dla niepełnosprawnych: " + cdis);
        textViews[8].setText("Miejsca dla tirów: " + ctru);
        textViews[9].setText("Miejsca dla busów: " + cbus);
        textViews[10].setText("Miejsca dla motocykli: " + cmot);
        textViews[11].setText(kwota);
        textViews[12].setText(createdDate);
        textViews[13].setText(editedDate);
        textViews[14].setText(verifiedDate);
        textViews[15].setText(stat);

        documentId = document.getId();
        getUser(user, textViews[16]);
    }

    public void getUser(String uId, TextView userTV)
    {
        DatabaseManager dbm = new DatabaseManager();
        Query q = db.collection("users").whereEqualTo("uId", uId);

        dbm.getElements(q, new OnElementsGet()
        {
            @Override
            public void setOnElementsGet(List<DocumentSnapshot> documentSnapshotList)
            {
                for (DocumentSnapshot ds : documentSnapshotList)
                {
                    userTV.setText(ds.getString("nick"));
                }
            }
        });
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }
}