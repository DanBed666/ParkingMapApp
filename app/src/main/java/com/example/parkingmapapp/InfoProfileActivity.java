package com.example.parkingmapapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class InfoProfileActivity extends AppCompatActivity
{
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_info_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) ->
        {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        DatabaseManager dbm = new DatabaseManager();

        String caseC = getIntent().getStringExtra("CASE");
        id = getIntent().getStringExtra("ID");
        TextView name = findViewById(R.id.tv_name);
        TextView surname = findViewById(R.id.tv_surname);
        TextView ranga = findViewById(R.id.tv_ranga);
        TextView created = findViewById(R.id.tv_created);
        TextView edited = findViewById(R.id.tv_edited);
        TextView registered = findViewById(R.id.tv_date);
        Spinner spinner = findViewById(R.id.spinner_access);
        String[] roles = getResources().getStringArray(R.array.ranga);

        TextView [] userTVS = new TextView[]{name, surname, ranga, registered};

        Button guy = findViewById(R.id.btn_guy);
        Button changePass = findViewById(R.id.btn_change_pass);
        Button changeMail = findViewById(R.id.btn_change_email);
        Button adminPanel = findViewById(R.id.btn_panel);
        Button confirm = findViewById(R.id.btn_confirm);
        Button verifyPanel = findViewById(R.id.btn_ver);

        ArrayAdapter<String> aa = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, roles);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(aa);

        if (Objects.equals(caseC, "admin"))
        {
            guy.setVisibility(View.GONE);
            changePass.setVisibility(View.GONE);
            changeMail.setVisibility(View.GONE);
            adminPanel.setVisibility(View.GONE);
        }

        confirm.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int position = spinner.getSelectedItemPosition();
                Map<String, Object> map = new HashMap<>();
                map.put("ranga", roles[position]);
                dbm.editElement("users", id, map);
            }
        });

        adminPanel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(getApplicationContext(), AdminPanelActivity.class));
            }
        });

        verifyPanel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(getApplicationContext(), VerifyPanelActivity.class));
            }
        });

        guy.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(getApplicationContext(), GuyActivity.class));
            }
        });

        changeMail.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(getApplicationContext(), EmailChangeActivity.class));
            }
        });

        changePass.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(getApplicationContext(), PasswordChangeActivity.class));
            }
        });

        Query q1 = db.collection("edits").whereEqualTo("uId", id).whereEqualTo("action", "Utworzono");
        dbm.getElements(q1, new OnElementsGet()
        {
            @Override
            public void setOnElementsGet(List<DocumentSnapshot> documentSnapshotList)
            {
                created.setText(String.valueOf(documentSnapshotList.size()));
            }
        });

        Query q2 = db.collection("edits").whereEqualTo("uId", id).whereEqualTo("action", "Edytowano");
        dbm.getElements(q2, new OnElementsGet()
        {
            @Override
            public void setOnElementsGet(List<DocumentSnapshot> documentSnapshotList)
            {
                edited.setText(String.valueOf(documentSnapshotList.size()));
            }
        });

        Query q = db.collection("users").whereEqualTo("uId", id);
        dbm.getElements(q, new OnElementsGet()
        {
            @Override
            public void setOnElementsGet(List<DocumentSnapshot> documentSnapshotList)
            {
                for (DocumentSnapshot ds : documentSnapshotList)
                {
                    userTVS[0].setText(ds.getString("name"));
                    userTVS[1].setText(ds.getString("surname"));
                    userTVS[2].setText(ds.getString("ranga"));
                    userTVS[3].setText(ds.getString("registerDate"));

                    if (Objects.equals(ds.getString("ranga"), "Administrator"))
                    {
                        adminPanel.setVisibility(View.VISIBLE);
                    }

                    if (Objects.equals(ds.getString("ranga"), "Administrator") || Objects.equals(ds.getString("ranga"), "Moderator"))
                    {
                        verifyPanel.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }
}