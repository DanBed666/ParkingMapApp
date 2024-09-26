package com.example.parkingmapapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Objects;

public class ParkingEditHistoryActivity extends AppCompatActivity implements RefreshListener
{
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    RecyclerView recyclerView;
    String id;
    String edit = "nic";
    RefreshListener refreshListener;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_parking_edit_history);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) ->
        {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        id = getIntent().getStringExtra("ID");

        setRecyclerView();

        if (getIntent().getStringExtra("EDIT") != null)
        {
            edit = getIntent().getStringExtra("EDIT");
        }

        refreshListener = this;

        showHistory();
    }

    public void setRecyclerView()
    {
        recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(),
                DividerItemDecoration.VERTICAL));
        DividerItemDecoration itemDecorator = new DividerItemDecoration(getApplicationContext(),
                DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(Objects.requireNonNull(ContextCompat.
                getDrawable(getApplicationContext(), R.drawable.divider)));
    }

    public void showHistory()
    {
        DatabaseManager dbm = new DatabaseManager();
        Query q = db.collection("edits").whereEqualTo("id", id).orderBy("lastActionDate", Query.Direction.DESCENDING);
        dbm.getElements(q, new OnElementsGet()
        {
            @Override
            public void setOnElementsGet(List<DocumentSnapshot> documentSnapshotList)
            {
                Log.i("HISTORY", String.valueOf(documentSnapshotList.size()));
                ParkingHistoryAdapter parkingHistoryAdapter = new ParkingHistoryAdapter(getApplicationContext(),
                        documentSnapshotList, edit, refreshListener);
                recyclerView.setAdapter(parkingHistoryAdapter);
                parkingHistoryAdapter.notifyDataSetChanged();
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

    @Override
    public void refresh()
    {
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }
}