package com.example.parkingmapapp;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;

public class FragmentInfoManager
{
    Context context;
    MapView map;
    GeoPoint startPoint;
    FragmentInterface listener;

    public FragmentInfoManager(Context context, MapView map, GeoPoint startPoint, FragmentInterface listener)
    {
        this.context = context;
        this.map = map;
        this.startPoint = startPoint;
        this.listener = listener;
    }

    public void addFragment(GeoPoint endPoint, String id)
    {
        Log.i("WYKONUJE2", "TAK2");
        InfoFragment fragment = new InfoFragment();
        Utils u = new Utils(context, map, startPoint, endPoint);

        Bundle bundle = new Bundle();
        bundle.putSerializable("OBJECT", u);
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
}
