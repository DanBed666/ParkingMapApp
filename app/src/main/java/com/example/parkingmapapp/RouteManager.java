package com.example.parkingmapapp;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;

import java.io.Serializable;
import java.util.ArrayList;

public class RouteManager implements Serializable, Parcelable
{
    Polyline roadOverlay;
    Context context;
    MapView map;
    GeoPoint startPoint;
    GeoPoint endPoint;

    public RouteManager(Context c, MapView m, GeoPoint s, GeoPoint e)
    {
        context = c;
        map = m;
        startPoint = s;
        endPoint = e;
    }

    public void setRoute()
    {
        RoadManager roadManager = new OSRMRoadManager(context,
                Configuration.getInstance().getUserAgentValue());
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

    protected RouteManager(Parcel in)
    {
        startPoint = in.readParcelable(GeoPoint.class.getClassLoader());
        endPoint = in.readParcelable(GeoPoint.class.getClassLoader());
    }

    public static final Creator<ParkingManager> CREATOR = new Creator<ParkingManager>() {
        @Override
        public ParkingManager createFromParcel(Parcel in) {
            return new ParkingManager(in);
        }

        @Override
        public ParkingManager[] newArray(int size) {
            return new ParkingManager[size];
        }
    };

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags)
    {
        dest.writeParcelable(startPoint, flags);
        dest.writeParcelable(endPoint, flags);
    }
}
