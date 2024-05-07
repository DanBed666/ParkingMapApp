package com.example.parkingmapapp;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.FragmentTransaction;

import org.osmdroid.bonuspack.kml.KmlFeature;
import org.osmdroid.bonuspack.kml.KmlLineString;
import org.osmdroid.bonuspack.kml.KmlPlacemark;
import org.osmdroid.bonuspack.kml.KmlPoint;
import org.osmdroid.bonuspack.kml.KmlPolygon;
import org.osmdroid.bonuspack.kml.KmlTrack;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.Polygon;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;

public class KMLStyler implements KmlFeature.Styler
{
    InfoFragment fragment;
    FragmentTransaction ft;
    Utils u;
    Context context;
    MapView map;
    GeoPoint startPoint;
    FragmentInterface listener;

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
        marker.setOnMarkerClickListener(new Marker.OnMarkerClickListener()
        {
            @Override
            public boolean onMarkerClick(Marker marker, MapView mapView)
            {
                fragment = new InfoFragment();
                u = new Utils(context, map, startPoint, marker.getPosition());

                Bundle bundle = new Bundle();
                bundle.putSerializable("OBJECT", u);
                fragment.setArguments(bundle);

                ft = listener.getSupportFM().beginTransaction();
                ft.replace(R.id.fragment, fragment);
                ft.commit();

                MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(new MapEventsReceiver() {
                    @Override
                    public boolean singleTapConfirmedHelper(GeoPoint p)
                    {
                        u.clearRoute();
                        return true;
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

    }

    @Override
    public void onTrack(Polyline polyline, KmlPlacemark kmlPlacemark, KmlTrack kmlTrack)
    {

    }
}
