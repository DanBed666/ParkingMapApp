package com.example.parkingmapapp;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.osmdroid.bonuspack.kml.KmlFeature;
import org.osmdroid.bonuspack.kml.KmlLineString;
import org.osmdroid.bonuspack.kml.KmlPlacemark;
import org.osmdroid.bonuspack.kml.KmlPoint;
import org.osmdroid.bonuspack.kml.KmlPolygon;
import org.osmdroid.bonuspack.kml.KmlTrack;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.Polygon;
import org.osmdroid.views.overlay.Polyline;

public class KMLStyler implements KmlFeature.Styler
{
    Context context;
    MapView map;
    GeoPoint startPoint;
    FragmentInterface listener;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    AddressViewModel addressViewModel;
    public KMLStyler(Context ctx, MapView m, GeoPoint s, FragmentInterface l)
    {
        context = ctx;
        map = m;
        startPoint = s;
        listener = l;
        addressViewModel = new AddressViewModel();
    }

    @Override
    public void onFeature(Overlay overlay, KmlFeature kmlFeature)
    {

    }

    @Override
    public void onPoint(Marker marker, KmlPlacemark kmlPlacemark, KmlPoint kmlPoint)
    {
        String id = kmlPlacemark.mId;
        DatabaseManager databaseManager = new DatabaseManager(kmlPlacemark, kmlPoint.getPosition(), context);
        databaseManager.checkIfExists(id);
        howManyRecords();

        marker.setOnMarkerClickListener(new Marker.OnMarkerClickListener()
        {
            @Override
            public boolean onMarkerClick(Marker marker, MapView mapView)
            {
                boolean verified = true;
                FragmentInfoManager fragmentInfoManager = new FragmentInfoManager(context, map, startPoint, listener, verified);
                fragmentInfoManager.addFragment(marker.getPosition(), id);
                Log.i("IDPOINT", id);

                return true;
            }
        });
    }

    @Override
    public void onLineString(Polyline polyline, KmlPlacemark kmlPlacemark, KmlLineString kmlLineString)
    {

    }

    @Override
    public void onPolygon(Polygon polygon, KmlPlacemark kmlPlacemark, KmlPolygon kmlPolygon)
    {
        polygon.setVisible(false);
        String id = kmlPlacemark.mId;

        Marker marker = new Marker(map);
        double latitude = kmlPolygon.getBoundingBox().getCenterLatitude();
        double longitude = kmlPolygon.getBoundingBox().getCenterLongitude();
        marker.setPosition(new GeoPoint(latitude, longitude));
        marker.setOnMarkerClickListener(new Marker.OnMarkerClickListener()
        {
            @Override
            public boolean onMarkerClick(Marker marker, MapView mapView)
            {
                boolean verified = true;
                FragmentInfoManager fragmentInfoManager = new FragmentInfoManager(context, map, startPoint, listener, verified);
                fragmentInfoManager.addFragment(marker.getPosition(), id);
                Log.i("IDPOINT", id);

                return true;
            }
        });

        DatabaseManager databaseManager = new DatabaseManager(kmlPlacemark, kmlPolygon.getBoundingBox().getCenter(), context);
        databaseManager.checkIfExists(id);
        howManyRecords();

        map.getOverlays().add(marker);
    }

    @Override
    public void onTrack(Polyline polyline, KmlPlacemark kmlPlacemark, KmlTrack kmlTrack)
    {

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
