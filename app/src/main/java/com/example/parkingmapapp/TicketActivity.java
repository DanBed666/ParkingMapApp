package com.example.parkingmapapp;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class TicketActivity extends AppCompatActivity
{
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final String API_KEY = "FiyHNQAmeoWKRcEdp5KyYWOAaAKf-7hvtqkz--lGBDc";
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ticket);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) ->
        {

            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        GetTagData get = new GetTagData();
        DatabaseManager dbm = new DatabaseManager();
        ImageView imageView = findViewById(R.id.imageCode);
        TextView ticket = findViewById(R.id.ticket_id);
        TextView adres = findViewById(R.id.adres);
        TextView date = findViewById(R.id.ticket_date);
        TextView valid = findViewById(R.id.ticket_valid);
        Button back = findViewById(R.id.btn_back);
        String ticketId = getIntent().getStringExtra("TICKETID");
        String parkingId = getIntent().getStringExtra("PARKINGID");

        get.generateQrCode(ticketId, imageView);

        Query q = db.collection("tickets").whereEqualTo("ticketId", ticketId);
        dbm.getElements(q, new OnElementsGet()
        {
            @Override
            public void setOnElementsGet(List<DocumentSnapshot> documentSnapshotList)
            {
                for (DocumentSnapshot document : documentSnapshotList)
                {
                    ticket.setText(document.getString("ticketId"));
                    date.setText("Data kupna: " + document.getString("reservationDate"));
                    valid.setText("Ważny do: " + document.getString("validThruDate"));
                }
            }
        });

        Query q2 = db.collection("parkings").whereEqualTo("id", parkingId);
        dbm.getElements(q2, new OnElementsGet()
        {
            @Override
            public void setOnElementsGet(List<DocumentSnapshot> documentSnapshotList)
            {
                for (DocumentSnapshot d : documentSnapshotList)
                {
                    get.getAddressHere(d.get("latitude") + "," + d.get("longitude"),
                            API_KEY, adres);
                }
            }
        });

        //TextView [] textViews = new TextView[]{ticket, adres, date, valid};

        back.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
    }
}