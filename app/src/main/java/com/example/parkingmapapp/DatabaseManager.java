package com.example.parkingmapapp;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.osmdroid.bonuspack.kml.KmlFeature;
import org.osmdroid.util.GeoPoint;

import java.util.Calendar;
import java.util.Objects;
import java.util.Random;

public class DatabaseManager
{
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    KmlFeature kmlPlacemark;
    GeoPoint loc;
    Context context;
    public DatabaseManager(KmlFeature kml, GeoPoint l, Context c)
    {
        kmlPlacemark = kml;
        loc = l;
        context = c;
    }

    public void addParkings2()
    {
        Log.i("WYKONUJE", "TAK");

        String id = kmlPlacemark.mId;

        String nm = "Brak";
        String pk = kmlPlacemark.getExtendedData("parking"); if(pk == null) pk="Brak";
        String cpc = kmlPlacemark.getExtendedData("capacity"); if(cpc == null) cpc="Brak";
        String fee = kmlPlacemark.getExtendedData("fee"); if(fee == null) fee="Brak";
        String svd = kmlPlacemark.getExtendedData("supervised"); if(svd == null) svd="Brak";
        String ope = kmlPlacemark.getExtendedData("operator"); if(ope == null) ope="Brak";
        String acc = kmlPlacemark.getExtendedData("access"); if(acc == null) acc="Brak";
        String cpcd = kmlPlacemark.getExtendedData("capacity:disabled"); if(cpcd == null) cpcd="Brak";
        String cpcb = kmlPlacemark.getExtendedData("capacity:bus"); if(cpcb == null) cpcb="Brak";
        String cpct = kmlPlacemark.getExtendedData("capacity:truck"); if(cpct == null) cpct="Brak";
        String cpcm = kmlPlacemark.getExtendedData("capacity:motorcycle"); if(cpcm == null) cpcm="Brak";
        double lat = loc.getLatitude();
        double lon = loc.getLongitude();
        String price = "Brak";
        Log.i("GEOLAT", String.valueOf(loc.getLatitude()));
        Log.i("GEOLON", String.valueOf(loc.getLongitude()));

        if (ope.equals("yes")) price = String.valueOf(new Random().nextInt(9) + 3);

        Parking parking = new Parking("xyz123", id, "editId", nm, pk, acc, cpc, cpcd, cpct, cpcb, cpcm, fee, svd, ope,
                lat, lon, "18-03-2024 18:19", "28-06-2024 06:47", "28-06-2024 09:27", "Edytowano",
                new ExampleHarms().getHarmonogram(), price, true, "Zweryfikowany", Calendar.getInstance().getTime(), true);

        addRecord(id, parking);
    }

    public void addRecord(String id, Parking parking)
    {
        db.collection("parkings").document(id).set(parking).addOnSuccessListener(new OnSuccessListener<Void>()
        {
            @Override
            public void onSuccess(Void unused)
            {
                Log.i("CREATED", "created");
            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Log.e("ERROR", Objects.requireNonNull(e.getMessage()));
            }
        });
    }

    public void checkIfExists(String id)
    {
        db.collection("parkings").document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task)
            {
                if (task.isSuccessful())
                {
                    DocumentSnapshot document = task.getResult();

                    if (document.exists())
                    {
                        Log.i("REKORD", "istnieje " + id);
                    }
                    else
                    {
                        Log.i("REKORD", "nie istnieje " + id);
                        addParkings2();
                    }
                }
                else
                {
                    Log.d("ERROR", "Failed with: ", task.getException());
                }
            }
        });
    }
}
