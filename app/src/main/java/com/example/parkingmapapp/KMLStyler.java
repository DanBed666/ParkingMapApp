package com.example.parkingmapapp;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
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

import java.util.List;
import java.util.Objects;

public class KMLStyler implements KmlFeature.Styler
{
    Context context;
    MapView map;
    GeoPoint startPoint;
    FragmentInterface listener;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

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

    }

    @Override
    public void onPoint(Marker marker, KmlPlacemark kmlPlacemark, KmlPoint kmlPoint)
    {
        String id = kmlPlacemark.mId;
        ParkingManager pm = new ParkingManager();
        pm.addSampleParkings(id, kmlPlacemark, kmlPoint.getPosition());

        FragmentInfoManager fragmentInfoManager = new FragmentInfoManager
                (context, map, startPoint, listener, true);
        fragmentInfoManager.setMarkerActions(id, marker);
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

        double latitude = kmlPolygon.getBoundingBox().getCenterLatitude();
        double longitude = kmlPolygon.getBoundingBox().getCenterLongitude();
        GeoPoint position = new GeoPoint(latitude, longitude);

        ParkingManager pm = new ParkingManager();
        pm.addSampleParkings(id, kmlPlacemark, position);
        FragmentInfoManager fragmentInfoManager = new FragmentInfoManager
                (context, map, startPoint, listener, true);
        fragmentInfoManager.setMarkerActions(id, position);
    }

    @Override
    public void onTrack(Polyline polyline, KmlPlacemark kmlPlacemark, KmlTrack kmlTrack)
    {

    }
}
