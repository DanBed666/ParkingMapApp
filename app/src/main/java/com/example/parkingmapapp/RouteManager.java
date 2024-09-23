package com.example.parkingmapapp;

import android.content.Context;

import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;

import java.io.Serializable;
import java.util.ArrayList;

public class RouteManager implements Serializable
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
}
