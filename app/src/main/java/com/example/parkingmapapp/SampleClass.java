package com.example.parkingmapapp;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.osmdroid.bonuspack.kml.KmlDocument;
import org.osmdroid.bonuspack.kml.KmlFeature;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SampleClass
{
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<String> ids = new ArrayList<>();
    List<String> err = ids;
    public void checkIfEdited(String dupa)
    {
        ids.add(dupa);
        Log.i("SIZE", String.valueOf(ids.size()));
    }
}
