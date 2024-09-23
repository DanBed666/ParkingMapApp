package com.example.parkingmapapp;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.osmdroid.bonuspack.kml.KmlDocument;
import org.osmdroid.bonuspack.location.OverpassAPIProvider;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MapActions
{
    public void showCompass(Context context, MapView map)
    {
        CompassOverlay mCompassOverlay = new CompassOverlay(context,
                new InternalCompassOrientationProvider(context), map);
        mCompassOverlay.enableCompass();
        map.getOverlays().add(mCompassOverlay);
    }

    public void clearMarkers(MapView map)
    {
        map.getOverlays().removeIf(o -> o instanceof Marker);
        map.getOverlays().removeIf(o -> o instanceof FolderOverlay);
        map.invalidate();
    }

    public void setMultiTouch(MapView map)
    {
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
    }


    /*
    public void setMarker()
    {
        Marker marker = new Marker(map);
        marker.setPosition(new GeoPoint(52.2297700, 21.0117800));
        //marker.setIcon(ResourcesCompat.getDrawable(getResources(), org.osmdroid.bonuspack.R.drawable.person, null));
        map.getOverlays().add(marker);
    }

    //Marker marker = new Marker(map);
    //marker.setPosition(new GeoPoint(52.2297700, 21.0117800));
    //marker.setIcon(ResourcesCompat.getDrawable(getResources(), org.osmdroid.bonuspack.R.drawable.person, null));
    //map.getOverlays().add(marker);
    /
     */
}
