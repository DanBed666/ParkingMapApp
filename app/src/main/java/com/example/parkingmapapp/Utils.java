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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Utils implements Serializable, Parcelable
{
    Context ctx;
    GeoPoint startPoint;
    MapView map;
    GeoPoint endPoint;
    Polyline roadOverlay;
    FragmentInterface listener;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public Utils(Context c, MapView m, GeoPoint start, GeoPoint end)
    {
        ctx = c;
        map = m;
        startPoint = start;
        endPoint = end;
    }

    public Utils(Context c, MapView m, GeoPoint start, FragmentInterface end)
    {
        ctx = c;
        map = m;
        startPoint = start;
        listener = end;
    }

    protected Utils(Parcel in)
    {
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

    public void findParkings(String tag)
    {
        GeoPoint location = startPoint;
        //GeoPoint location =  new GeoPoint(52.2297700, 21.0117800);
        OverpassAPIProvider overpassProvider = new OverpassAPIProvider();
        BoundingBox range = new BoundingBox(location.getLatitude() + 0.05, location.getLongitude() + 0.05,
                location.getLatitude() - 0.05, location.getLongitude() - 0.05);
        String url = overpassProvider.urlForTagSearchKml(tag, range, 500, 30);
        KmlDocument kmlDocument = new KmlDocument();

        boolean ok = overpassProvider.addInKmlFolder(kmlDocument.mKmlRoot, url);
        KMLStyler kmlStyler = new KMLStyler(ctx, map, location, listener);
        howManyRecords(new IsParkingEdited()
        {
            @Override
            public void idsToRemove(List<String> ids)
            {
                for (String id : ids)
                {
                    kmlDocument.mKmlRoot.mItems.removeIf(kmlFeature -> kmlFeature.mId.equals(id));
                    Log.i("USUNIETO", id);
                }

                if (ok)
                {
                    FolderOverlay kmlOverlay = (FolderOverlay) kmlDocument.mKmlRoot.buildOverlay(map, null, kmlStyler, kmlDocument);
                    map.getOverlays().add(kmlOverlay);
                }
                else
                {
                    Toast.makeText(ctx, "Nie znaleziono parkingów w danym obszarze!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void findParkingsDB(Query q)
    {
        q.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task)
            {
                if (task.isSuccessful())
                {
                    for (DocumentSnapshot ds : task.getResult().getDocuments())
                    {
                        Marker marker = new Marker(map);
                        Double latitude = ds.getDouble("latitude");
                        Double longitude = ds.getDouble("longitude");

                        if (latitude != null && longitude != null)
                        {
                            GeoPoint position = new GeoPoint(latitude, longitude);
                            marker.setPosition(position);
                            Log.i("MARKER", position.getLatitude() + " " + position.getLongitude());
                            marker.setOnMarkerClickListener(new Marker.OnMarkerClickListener()
                            {
                                @Override
                                public boolean onMarkerClick(Marker marker, MapView mapView)
                                {
                                    FragmentInfoManager fragmentInfoManager = new FragmentInfoManager(ctx, map, startPoint, listener, true);
                                    fragmentInfoManager.addFragment(position, ds.getId());
                                    return true;
                                }
                            });
                            map.getOverlays().add(marker);
                        }
                    }
                }
            }
        });
    }

    public void howManyRecords(IsParkingEdited callback)
    {
        db.collection("parkings").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
        {
            List<String> ideks = new ArrayList<>();
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task)
            {
                for (DocumentSnapshot ds : task.getResult().getDocuments())
                {
                    if (Boolean.TRUE.equals(ds.getBoolean("sample")))
                    {
                        ideks.add(ds.getId());
                    }
                }

                Log.i("ROZMIAREK", String.valueOf(ideks.size()));
                callback.idsToRemove(ideks);
            }
        });
    }
}
