package com.example.parkingmapapp;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;

public class FragmentInfoManager
{
    Context context;
    MapView map;
    GeoPoint startPoint;
    FragmentInterface listener;
    boolean verified;

    public FragmentInfoManager(Context context, MapView map, GeoPoint startPoint, FragmentInterface listener, boolean ver)
    {
        this.context = context;
        this.map = map;
        this.startPoint = startPoint;
        this.listener = listener;
        verified = ver;
    }

    public void setMarkerActions(String id, GeoPoint position)
    {
        Marker marker = new Marker(map);
        marker.setPosition(position);
        marker.setOnMarkerClickListener(new Marker.OnMarkerClickListener()
        {
            @Override
            public boolean onMarkerClick(Marker marker, MapView mapView)
            {
                addFragment(marker.getPosition(), id);
                Log.i("IDPOINT", id);

                return true;
            }
        });

        map.getOverlays().add(marker);
    }

    public void setMarkerActions(String id, Marker marker)
    {
        marker.setOnMarkerClickListener(new Marker.OnMarkerClickListener()
        {
            @Override
            public boolean onMarkerClick(Marker marker, MapView mapView)
            {
                boolean verified = true;
                FragmentInfoManager fragmentInfoManager = new FragmentInfoManager(context, map, startPoint, listener, verified);
                fragmentInfoManager.addFragment(marker.getPosition(), id);
                Log.i("IDPOINT", id);

                return true;
            }
        });

        map.getOverlays().add(marker);
    }

    public void addFragment(GeoPoint endPoint, String id)
    {
        InfoFragment fragment = new InfoFragment();
        RouteManager rm = new RouteManager(context, map, startPoint, endPoint);
        Bundle bundle = new Bundle();
        bundle.putSerializable("ROUTE", rm);
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
                rm.clearRoute();
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
}
