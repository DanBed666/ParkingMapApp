package com.example.parkingmapapp;

import android.content.Context;
import android.util.Log;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.Serializable;

public class LocationManager implements Serializable
{
    MyLocationNewOverlay mLocationOverlay;
    public LocationManager(MyLocationNewOverlay mLocationOverlay)
    {
        this.mLocationOverlay = mLocationOverlay;
    }

    public void setMyLocation(MapView map)
    {
        GeoPoint location = mLocationOverlay.getMyLocation();
        map.getController().setCenter(location);
    }

    public void showMyLocation(MapView map)
    {
        mLocationOverlay.enableFollowLocation();
        map.getOverlays().add(mLocationOverlay);
        map.getController().setZoom(18.0);
    }

    public GeoPoint getMyLocation()
    {
        return mLocationOverlay.getMyLocation();
    }
}
