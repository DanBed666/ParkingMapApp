package com.example.parkingmapapp;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.osmdroid.bonuspack.kml.KmlFeature;
import org.osmdroid.bonuspack.kml.KmlPlacemark;
import org.osmdroid.util.GeoPoint;
import java.util.Objects;

public class DatabaseManager
{
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    KmlFeature kmlPlacemark;
    GeoPoint loc;
    Context context;
    AddressViewModel addressViewModel;
    Address addressAdr;
    public DatabaseManager(KmlFeature kml, GeoPoint l, Context c)
    {
        kmlPlacemark = kml;
        loc = l;
        context = c;
        addressViewModel = new AddressViewModel();
    }

    public void addParkings2()
    {
        Log.i("WYKONUJE", "TAK");

        String id = kmlPlacemark.mId;

        String nm = "Brak";
        String pk = kmlPlacemark.getExtendedData("parking");
        String cpc = kmlPlacemark.getExtendedData("capacity");
        String fee = kmlPlacemark.getExtendedData("fee");
        String svd = kmlPlacemark.getExtendedData("supervised");
        String ope = kmlPlacemark.getExtendedData("operator");
        double lat;
        double lon;

        Log.i("GEOLAT", String.valueOf(loc.getLatitude()));
        Log.i("GEOLON", String.valueOf(loc.getLongitude()));
        lat = loc.getLatitude();
        lon = loc.getLongitude();

        getAddressNominatim(lat, lon, "json");

        double finalLat = lat;
        double finalLon = lon;

        Parking parking = new Parking(id, nm, pk, cpc, fee, svd, ope, finalLat, finalLon, false, addressAdr);
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

    public void getAddressNominatim(double lat, double lon, String format)
    {
        addressViewModel.getAddressVM(lat, lon, format).observeForever(new Observer<Address>()
        {
            @Override
            public void onChanged(Address address)
            {
                addressAdr = address;
            }
        });
    }
}
