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
        String nm = "Brak";
        String pk = kmlPlacemark.getExtendedData("parking");
        String cpc = kmlPlacemark.getExtendedData("capacity");;
        String fee = kmlPlacemark.getExtendedData("fee");;
        String svd = kmlPlacemark.getExtendedData("supervised");;
        String ope = kmlPlacemark.getExtendedData("operator");;

        DatabaseReference p = parkings.child(id);

        Log.i("KLUCZ", id + "   " + Objects.requireNonNull(p.getKey()));

        parkings.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                Log.i("KLUCZ2", String.valueOf(snapshot.hasChild(id)));

                if (!snapshot.hasChild(id))
                {
                    Log.i("DODANO", "dodano");
                    parking = new Parking(nm, pk, cpc, fee, svd, ope);
                    parkings.child(id).setValue(parking);
                }
                else
                {
                    Log.i("NIEDODANO", "niedodano");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

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
        String nm = "Brak";
        String pk = kmlPlacemark.getExtendedData("parking");
        String cpc = kmlPlacemark.getExtendedData("capacity");;
        String fee = kmlPlacemark.getExtendedData("fee");;
        String svd = kmlPlacemark.getExtendedData("supervised");;
        String ope = kmlPlacemark.getExtendedData("operator");;

        DatabaseReference p = parkings.child(id);

        Log.i("KLUCZ", id + "   " + Objects.requireNonNull(p.getKey()));

        parkings.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                Log.i("KLUCZ2", String.valueOf(snapshot.hasChild(id)));

                if (!snapshot.hasChild(id))
                {
                    Log.i("DODANO", "dodano");
                    parking = new Parking(nm, pk, cpc, fee, svd, ope);
                    parkings.child(id).setValue(parking);
                }
                else
                {
                    Log.i("NIEDODANO", "niedodano");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

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
}
