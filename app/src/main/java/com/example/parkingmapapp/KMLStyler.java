package com.example.parkingmapapp;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import org.osmdroid.bonuspack.kml.KmlFeature;
import org.osmdroid.bonuspack.kml.KmlLineString;
import org.osmdroid.bonuspack.kml.KmlPlacemark;
import org.osmdroid.bonuspack.kml.KmlPoint;
import org.osmdroid.bonuspack.kml.KmlPolygon;
import org.osmdroid.bonuspack.kml.KmlTrack;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.Polygon;
import org.osmdroid.views.overlay.Polyline;

import java.util.Objects;

public class KMLStyler implements KmlFeature.Styler
{
    Context context;
    MapView map;
    GeoPoint startPoint;
    FragmentInterface listener;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FragmentInfoManager fragmentInfoManager;
    public KMLStyler(Context ctx, MapView m, GeoPoint s, FragmentInterface l)
    {
        context = ctx;
        map = m;
        startPoint = s;
        listener = l;
    }

    @Override
    public void onFeature(Overlay overlay, KmlFeature kmlFeature)
    {
        map.getOverlays().remove(overlay);
    }

    @Override
    public void onPoint(Marker marker, KmlPlacemark kmlPlacemark, KmlPoint kmlPoint)
    {
        String id = kmlPlacemark.mId;
        DatabaseManager databaseManager = new DatabaseManager(kmlPlacemark, kmlPoint.getPosition());
        databaseManager.checkIfExists(id);
        howManyRecords();

        marker.setOnMarkerClickListener(new Marker.OnMarkerClickListener()
        {
            @Override
            public boolean onMarkerClick(Marker marker, MapView mapView)
            {
                fragmentInfoManager = new FragmentInfoManager(context, map, startPoint, listener);
                fragmentInfoManager.addFragment(marker.getPosition(), id);
                Log.i("IDPOINT", id);

                return true;
            }
        });
    }

    @Override
    public void onLineString(Polyline polyline, KmlPlacemark kmlPlacemark, KmlLineString kmlLineString)
    {
        map.getOverlays().remove(polyline);
    }

    @Override
    public void onPolygon(Polygon polygon, KmlPlacemark kmlPlacemark, KmlPolygon kmlPolygon)
    {
        polygon.setVisible(false);
        String id = kmlPlacemark.mId;
        DatabaseManager databaseManager = new DatabaseManager(kmlPlacemark, kmlPolygon.getBoundingBox().getCenter());
        databaseManager.checkIfExists(id);
        howManyRecords();

        Marker marker = new Marker(map);
        double latitude = kmlPolygon.getBoundingBox().getCenterLatitude();
        double longitude = kmlPolygon.getBoundingBox().getCenterLongitude();
        marker.setPosition(new GeoPoint(latitude, longitude));
        marker.setOnMarkerClickListener(new Marker.OnMarkerClickListener()
        {
            @Override
            public boolean onMarkerClick(Marker marker, MapView mapView)
            {
                fragmentInfoManager = new FragmentInfoManager(context, map, startPoint, listener);
                fragmentInfoManager.addFragment(marker.getPosition(), id);
                Log.i("IDPOINT", id);

                return true;
            }
        });

        map.getOverlays().add(marker);
    }

    @Override
    public void onTrack(Polyline polyline, KmlPlacemark kmlPlacemark, KmlTrack kmlTrack)
    {
        map.getOverlays().remove(polyline);
    }

    public void howManyRecords()
    {
        db.collection("parkings").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task)
            {
                Log.i("ILE", String.valueOf(task.getResult().size()));
            }
        });
    }
}
