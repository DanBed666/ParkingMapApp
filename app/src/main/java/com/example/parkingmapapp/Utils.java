package com.example.parkingmapapp;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.Polyline;

import java.io.Serializable;
import java.util.ArrayList;

public class Utils implements Serializable, Parcelable
{
    Context ctx;
    GeoPoint startPoint;
    MapView map;
    GeoPoint endPoint;
    Polyline roadOverlay;

    public Utils(Context c, MapView m, GeoPoint start, GeoPoint end)
    {
        ctx = c;
        map = m;
        startPoint = start;
        endPoint = end;
    }

    protected Utils(Parcel in) {
        startPoint = in.readParcelable(GeoPoint.class.getClassLoader());
        endPoint = in.readParcelable(GeoPoint.class.getClassLoader());
    }

    public static final Creator<Utils> CREATOR = new Creator<Utils>() {
        @Override
        public Utils createFromParcel(Parcel in) {
            return new Utils(in);
        }

        @Override
        public Utils[] newArray(int size) {
            return new Utils[size];
        }
    };

    public void setRoute()
    {
        RoadManager roadManager = new OSRMRoadManager(ctx, Configuration.getInstance().getUserAgentValue());
        ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();
        waypoints.add(startPoint);
        waypoints.add(endPoint);
        Road road = roadManager.getRoad(waypoints);
        roadOverlay = RoadManager.buildRoadOverlay(road);
        map.getOverlays().add(roadOverlay);
        map.invalidate();
    }
    public void clearRoute()
    {
        map.getOverlays().remove(roadOverlay);
        map.invalidate();
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeParcelable(startPoint, flags);
        dest.writeParcelable(endPoint, flags);
    }
}
