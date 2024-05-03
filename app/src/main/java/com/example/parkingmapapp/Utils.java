package com.example.parkingmapapp;

import android.content.Context;

import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;

public class Utils
{
    Context ctx;
    GeoPoint startPoint;
    MapView map;
    GeoPoint endPoint;
    public Utils()
    {

    }

    public Utils(Context c, MapView m, GeoPoint start, GeoPoint end)
    {
        ctx = c;
        map = m;
        startPoint = start;
        endPoint = end;
    }

    public void setRoute()
    {
        RoadManager roadManager = new OSRMRoadManager(ctx, Configuration.getInstance().getUserAgentValue());
        ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();
        waypoints.add(startPoint);
        waypoints.add(endPoint);
        Road road = roadManager.getRoad(waypoints);
        Polyline roadOverlay = RoadManager.buildRoadOverlay(road);
        map.getOverlays().add(roadOverlay);
        map.invalidate();
    }
}
