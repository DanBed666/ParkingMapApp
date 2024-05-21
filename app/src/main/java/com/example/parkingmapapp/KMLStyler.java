package com.example.parkingmapapp;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    InfoFragment fragment;
    FragmentTransaction ft;
    Utils u;
    Context context;
    MapView map;
    GeoPoint startPoint;
    FragmentInterface listener;
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://parkingmapapp-39ec0-default-rtdb.europe-west1.firebasedatabase.app/");
    DatabaseReference parkings = database.getReference("parkings");
    Parking parking;

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
        String amenity = kmlPlacemark.getExtendedData("amenity");
        String pk = kmlPlacemark.getExtendedData("parking");

        parking = new Parking(amenity, pk);
        DatabaseReference p = parkings.child(id);

        p.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task)
            {
                if (task.isSuccessful())
                {
                    DataSnapshot dataSnapshot = task.getResult();

                    if (!dataSnapshot.exists())
                    {
                        p.setValue(parking);
                    }
                }
            }
        });

        String id2 = kmlPlacemark.getExtendedData("parking");
        Log.i("LATITUDE", String.valueOf(kmlPoint.getPosition().getLatitude()));
        Log.i("LONGITUDE", String.valueOf(kmlPoint.getPosition().getLongitude()));

        marker.setOnMarkerClickListener(new Marker.OnMarkerClickListener()
        {
            @Override
            public boolean onMarkerClick(Marker marker, MapView mapView)
            {
                fragment = new InfoFragment();
                u = new Utils(context, map, startPoint, marker.getPosition());

                Bundle bundle = new Bundle();
                bundle.putSerializable("OBJECT", u);
                bundle.putSerializable("PARKING", parking);
                bundle.putString("ID", pk);
                bundle.putString("KEYID", id);
                fragment.setArguments(bundle);

                listener.getSupportFM().beginTransaction().
                        setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                        .show(fragment)
                        .replace(R.id.fragment, fragment)
                        .commit();

                MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(new MapEventsReceiver() {
                    @Override
                    public boolean singleTapConfirmedHelper(GeoPoint p)
                    {
                        u.clearRoute();
                        listener.getSupportFM().beginTransaction().
                                setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                                .hide(fragment)
                                .commit();
                        return false;
                    }

                    @Override
                    public boolean longPressHelper(GeoPoint p)
                    {
                        return false;
                    }
                });

                map.getOverlays().add(mapEventsOverlay);

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
        String id = kmlPlacemark.mId;
        String amenity = kmlPlacemark.getExtendedData("amenity");
        String pk = kmlPlacemark.getExtendedData("parking");

        parking = new Parking(amenity, pk);
        DatabaseReference p = parkings.child(id);

        p.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task)
            {
                if (task.isSuccessful())
                {
                    DataSnapshot dataSnapshot = task.getResult();

                    if (!dataSnapshot.exists())
                    {
                        p.setValue(parking);
                    }
                }
            }
        });

        polygon.setOnClickListener(new Polygon.OnClickListener()
        {
            @Override
            public boolean onClick(Polygon polygon, MapView mapView, GeoPoint eventPos)
            {
                fragment = new InfoFragment();
                u = new Utils(context, map, startPoint, eventPos);

                Bundle bundle = new Bundle();
                bundle.putSerializable("OBJECT", u);
                bundle.putSerializable("PARKING", parking);
                bundle.putString("ID", pk);
                bundle.putString("KEYID", id);
                fragment.setArguments(bundle);

                listener.getSupportFM().beginTransaction().
                        setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                        .show(fragment)
                        .replace(R.id.fragment, fragment)
                        .commit();

                MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(new MapEventsReceiver() {
                    @Override
                    public boolean singleTapConfirmedHelper(GeoPoint p)
                    {
                        u.clearRoute();
                        listener.getSupportFM().beginTransaction().
                                setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                                .hide(fragment)
                                .commit();
                        return false;
                    }

                    @Override
                    public boolean longPressHelper(GeoPoint p)
                    {
                        return false;
                    }
                });

                map.getOverlays().add(mapEventsOverlay);

                return true;
            }
        });
    }

    @Override
    public void onTrack(Polyline polyline, KmlPlacemark kmlPlacemark, KmlTrack kmlTrack)
    {

    }

    public void getInfo(String id)
    {
        DatabaseReference p = parkings.child(id);
        final String[] sample = {""};
        p.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                DataSnapshot s = snapshot.child("sample");

                if (s.exists())
                {
                    Log.i("SAMPLE", Objects.requireNonNull(s.getValue(String.class)));
                    parking.setSample(s.getValue(String.class));
                }
                else
                {
                    Log.i("SAMPLE", "Brak danych");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });

        Log.i("SAMPLETEXT", parking.getSample());
    }
}
