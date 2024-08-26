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
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class InfoProfileActivity extends AppCompatActivity
{
    TextView name;
    TextView surname;
    TextView ranga;
    TextView created;
    TextView edited;
    TextView registered;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
    String caseC;
    Spinner spinner;
    Button adminPanel;
    String id;
    Button verifyPanel;
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

        caseC = getIntent().getStringExtra("CASE");
        id = getIntent().getStringExtra("ID");
        name = findViewById(R.id.tv_name);
        surname = findViewById(R.id.tv_surname);
        ranga = findViewById(R.id.tv_ranga);
        created = findViewById(R.id.tv_created);
        edited = findViewById(R.id.tv_edited);
        registered = findViewById(R.id.tv_date);
        spinner = findViewById(R.id.spinner_access);
        String[] roles = getResources().getStringArray(R.array.ranga);

        Button guy = findViewById(R.id.btn_guy);
        Button changePass = findViewById(R.id.btn_change_pass);
        Button changeMail = findViewById(R.id.btn_change_email);
        adminPanel = findViewById(R.id.btn_panel);
        Button confirm = findViewById(R.id.btn_confirm);
        verifyPanel = findViewById(R.id.btn_ver);

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
                addUser(map);
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

        getEdits("created", created);
        getEdits("edited", edited);
        getUser();
    }

    public void getUser()
    {
        db.collection("users").whereEqualTo("uId", user.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task)
            {
                if (task.isSuccessful())
                {
                    for (DocumentSnapshot ds : task.getResult().getDocuments())
                    {
                        name.setText(ds.getString("name"));
                        surname.setText(ds.getString("surname"));
                        ranga.setText(ds.getString("ranga"));
                        registered.setText(ds.getString("registerDate"));

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
            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {

            }
        });
    }

    public void addUser(Map<String, Object> mapa)
    {
        db.collection("users").document(id).update(mapa).addOnSuccessListener(new OnSuccessListener<Void>()
        {
            @Override
            public void onSuccess(Void documentReference)
            {
                Log.d("TEST", "DocumentSnapshot added with ID");
            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Log.d("ERROR", "Error: " + e.getMessage());
            }
        });
    }

    public void getEdits(String field, TextView textView)
    {
        db.collection("edits").whereEqualTo("uId", user.getUid()).whereEqualTo(field, true).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task)
            {
                Log.i("DUPPP", String.valueOf(task.getResult().getDocuments().size()));

                if (task.isSuccessful())
                {
                    textView.setText(String.valueOf(task.getResult().getDocuments().size()));
                }
            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {

            }
        });
    }
}