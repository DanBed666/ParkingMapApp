package com.example.parkingmapapp;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.osmdroid.bonuspack.kml.KmlFeature;
import org.osmdroid.bonuspack.kml.KmlLineString;
import org.osmdroid.bonuspack.kml.KmlPlacemark;
import org.osmdroid.bonuspack.kml.KmlPoint;
import org.osmdroid.bonuspack.kml.KmlPolygon;
import org.osmdroid.bonuspack.kml.KmlTrack;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.Polygon;
import org.osmdroid.views.overlay.Polyline;

import java.util.Objects;

public class KMLStyler implements KmlFeature.Styler
{
    InfoFragment fragment;
    Utils u;
    Context context;
    MapView map;
    GeoPoint startPoint;
    FragmentInterface listener;
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://parkingmapapp-39ec0-default-rtdb.europe-west1.firebasedatabase.app/");
    DatabaseReference parkings = database.getReference("parkings");
    DatabaseReference addedparkings = database.getReference("addedparkings");
    FirebaseFirestore db = FirebaseFirestore.getInstance();
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
        String id = addParkings2(kmlPlacemark, kmlPoint.getPosition());

        marker.setOnMarkerClickListener(new Marker.OnMarkerClickListener()
        {
            @Override
            public boolean onMarkerClick(Marker marker, MapView mapView)
            {
                addFragment(marker.getPosition(), id);

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
        String id = addParkings2(kmlPlacemark, kmlPolygon.getBoundingBox().getCenter());

        polygon.setOnClickListener(new Polygon.OnClickListener()
        {
            @Override
            public boolean onClick(Polygon polygon, MapView mapView, GeoPoint eventPos)
            {
                addFragment(eventPos, id);

                return true;
            }
        });
    }

    @Override
    public void onTrack(Polyline polyline, KmlPlacemark kmlPlacemark, KmlTrack kmlTrack)
    {

    }

    public String addParkings(KmlPlacemark kmlPlacemark, GeoPoint loc)
    {
        Log.i("WYKONUJE", "TAK");

        String id = kmlPlacemark.mId;
        String nm = "Brak";
        String pk = kmlPlacemark.getExtendedData("parking");
        String cpc = kmlPlacemark.getExtendedData("capacity");
        String fee = kmlPlacemark.getExtendedData("fee");
        String svd = kmlPlacemark.getExtendedData("supervised");
        String ope = kmlPlacemark.getExtendedData("operator");
        double lat = 0;
        double lon = 0;

        Log.i("GEOLAT", String.valueOf(loc.getLatitude()));
        Log.i("GEOLON", String.valueOf(loc.getLongitude()));
        lat = loc.getLatitude();
        lon = loc.getLongitude();

        DatabaseReference p = parkings.child(id);

        Log.i("KLUCZ", id + "   " + Objects.requireNonNull(p.getKey()));

        double finalLat = lat;
        double finalLon = lon;

        parkings.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                Log.i("KLUCZ2", String.valueOf(snapshot.hasChild(id)));

                if (!snapshot.hasChild(id))
                {
                    Log.i("DODANO", "dodano");
                    parking = new Parking(nm, pk, cpc, fee, svd, ope, finalLat, finalLon);
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

        setMarker();

        return id;
    }

    public String addParkings2(KmlPlacemark kmlPlacemark, GeoPoint loc)
    {
        Log.i("WYKONUJE", "TAK");

        String id = kmlPlacemark.mId;
        String nm = "Brak";
        String pk = kmlPlacemark.getExtendedData("parking");
        String cpc = kmlPlacemark.getExtendedData("capacity");
        String fee = kmlPlacemark.getExtendedData("fee");
        String svd = kmlPlacemark.getExtendedData("supervised");
        String ope = kmlPlacemark.getExtendedData("operator");
        double lat = 0;
        double lon = 0;

        Log.i("GEOLAT", String.valueOf(loc.getLatitude()));
        Log.i("GEOLON", String.valueOf(loc.getLongitude()));
        lat = loc.getLatitude();
        lon = loc.getLongitude();

        double finalLat = lat;
        double finalLon = lon;

        parking = new Parking(nm, pk, cpc, fee, svd, ope, finalLat, finalLon);

        db.collection("parkings").add(parking).addOnSuccessListener(new OnSuccessListener<DocumentReference>()
        {
            @Override
            public void onSuccess(DocumentReference documentReference)
            {
                Log.d("TEST", "DocumentSnapshot added with ID: " + documentReference.getId());
            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Log.d("ERROR", "Error: " + e.getMessage());
            }
        });

        setMarker();

        return id;
    }

    public void addFragment(GeoPoint geoPoint, String id)
    {
        Log.i("WYKONUJE2", "TAK2");
        fragment = new InfoFragment();
        u = new Utils(context, map, startPoint, geoPoint);

        Bundle bundle = new Bundle();
        bundle.putSerializable("OBJECT", u);
        bundle.putSerializable("PARKING", parking);
        bundle.putString("KEYID", id);
        fragment.setArguments(bundle);
        Log.i("IDFRAG", id);

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
    }

    public void setMarker()
    {
         addedparkings.addValueEventListener(new ValueEventListener()
         {
             @Override
             public void onDataChange(@NonNull DataSnapshot snapshot)
             {
                 for (DataSnapshot s : snapshot.getChildren())
                 {
                     Log.i("IDBASE", Objects.requireNonNull(s.getKey()));

                     addedparkings.child(s.getKey()).addValueEventListener(new ValueEventListener()
                     {
                         @Override
                         public void onDataChange(@NonNull DataSnapshot snapshot)
                         {
                             Double latitude = snapshot.child("latitude").getValue(Double.class);
                             Double longtitude = snapshot.child("longtitude").getValue(Double.class);

                             Marker marker = new Marker(map);

                             if (latitude != null && longtitude != null)
                             {
                                 GeoPoint geoPoint = new GeoPoint(latitude, longtitude);
                                 marker.setPosition(geoPoint);
                                 marker.setOnMarkerClickListener(new Marker.OnMarkerClickListener()
                                 {
                                     @Override
                                     public boolean onMarkerClick(Marker marker, MapView mapView)
                                     {
                                         addFragment(geoPoint, s.getKey());
                                         return true;
                                     }
                                 });
                             }

                             map.getOverlays().add(marker);
                         }

                         @Override
                         public void onCancelled(@NonNull DatabaseError error)
                         {

                         }
                     });
                 }
             }

             @Override
             public void onCancelled(@NonNull DatabaseError error)
             {

             }
         });
    }
}
