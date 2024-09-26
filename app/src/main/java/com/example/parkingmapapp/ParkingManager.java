package com.example.parkingmapapp;

import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.stripe.model.tax.Registration;

import org.osmdroid.bonuspack.kml.KmlDocument;
import org.osmdroid.bonuspack.kml.KmlPlacemark;
import org.osmdroid.bonuspack.location.OverpassAPIProvider;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.Marker;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ParkingManager implements Serializable, Parcelable
{
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    GeoPoint startPoint;
    Context ctx;
    MapView map;
    FragmentInterface listener;
    DatabaseManager dbm;
    GetTagData get;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    public ParkingManager(GeoPoint s, Context c, MapView m, FragmentInterface l)
    {
        startPoint = s;
        ctx = c;
        map = m;
        listener = l;
        Log.i("MAPA", String.valueOf(map == null));
    }

    public ParkingManager()
    {
        dbm = new DatabaseManager();
        get = new GetTagData();
    }
    public void findParkings(String tag)
    {
        GeoPoint location = startPoint;
        OverpassAPIProvider overpassProvider = new OverpassAPIProvider();
        BoundingBox range = new BoundingBox(location.getLatitude() + 0.05, location.getLongitude() + 0.05,
                location.getLatitude() - 0.05, location.getLongitude() - 0.05);
        String url = overpassProvider.urlForTagSearchKml(tag, range, 500, 30);
        KmlDocument kmlDocument = new KmlDocument();

        boolean ok = overpassProvider.addInKmlFolder(kmlDocument.mKmlRoot, url);
        KMLStyler kmlStyler = new KMLStyler(ctx, map, location, listener);
        Log.i("MAPA", String.valueOf(map == null));
        DatabaseManager dbm = new DatabaseManager();
        Query q = db.collection("parkings");
        dbm.getElements(q, new OnElementsGet()
        {
            @Override
            public void setOnElementsGet(List<DocumentSnapshot> documentSnapshotList)
            {
                for (DocumentSnapshot ds : documentSnapshotList)
                {
                    if (Boolean.TRUE.equals(ds.getBoolean("sample")))
                    {
                        kmlDocument.mKmlRoot.mItems.removeIf(kmlFeature -> kmlFeature.mId.equals(ds.getId()));
                    }
                }

                if (ok)
                {
                    Log.i("MAPA", String.valueOf(map == null));
                    FolderOverlay kmlOverlay = (FolderOverlay) kmlDocument.
                            mKmlRoot.buildOverlay(map, null, kmlStyler, kmlDocument);
                    map.getOverlays().add(kmlOverlay);
                }
                else
                {
                    Toast.makeText(ctx, "Nie znaleziono parkingów w danym obszarze!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void findParkingsDB(Query findingQuery)
    {
        DatabaseManager dbm = new DatabaseManager();
        dbm.getElements(findingQuery, new OnElementsGet()
        {
            @Override
            public void setOnElementsGet(List<DocumentSnapshot> documentSnapshotList)
            {
                for (DocumentSnapshot ds : documentSnapshotList)
                {
                    Double latitude = ds.getDouble("latitude");
                    Double longitude = ds.getDouble("longitude");

                    if (latitude != null && longitude != null)
                    {
                        GeoPoint position = new GeoPoint(latitude, longitude);
                        FragmentInfoManager fragmentInfoManager = new FragmentInfoManager
                                (ctx, map, startPoint, listener, true);
                        fragmentInfoManager.setMarkerActions(ds.getId(), position);
                    }
                }
            }
        });
    }

    public void addParking(Parking newParking, String id, String editId)
    {
        DatabaseManager dbm = new DatabaseManager();
        GetTagData get = new GetTagData();
        Query q = db.collection("users").whereEqualTo("uId", user.getUid());
        dbm.getElements(q, new OnElementsGet()
        {
            @Override
            public void setOnElementsGet(List<DocumentSnapshot> documentSnapshotList)
            {
                for (DocumentSnapshot ds : documentSnapshotList)
                {
                    if (Objects.equals(ds.getString("ranga"), "Administrator")
                            || Objects.equals(ds.getString("ranga"), "Moderator"))
                    {
                        newParking.setVerified(true);
                        newParking.setStatus("Zweryfikowany");
                        newParking.setDataVerified(get.getActualDate());
                        newParking.setuIdVer(user.getUid());
                        dbm.addElement("parkings", id, newParking);
                        dbm.addElement("edits", editId, newParking);
                    }
                    else
                    {
                        dbm.addElement("edits", editId, newParking);
                    }
                }
            }
        });
    }

    public void editParking(Parking newParking2, Map<String, Object> mapa,
                            String documentId, String editedId)
    {
        DatabaseManager dbm = new DatabaseManager();
        GetTagData get = new GetTagData();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Query q = db.collection("users").whereEqualTo("uId", user.getUid());
        dbm.getElements(q, new OnElementsGet()
        {
            @Override
            public void setOnElementsGet(List<DocumentSnapshot> documentSnapshotList)
            {
                for (DocumentSnapshot ds : documentSnapshotList)
                {
                    if (Objects.equals(ds.getString("ranga"), "Administrator")
                            || Objects.equals(ds.getString("ranga"), "Moderator"))
                    {
                        mapa.put("verified", true);
                        mapa.put("status", "Zweryfikowany");
                        mapa.put("dataVerified", get.getActualDate());
                        newParking2.setVerified(true);
                        newParking2.setStatus("Zweryfikowany");
                        newParking2.setDataVerified(get.getActualDate());
                        newParking2.setuIdVer(user.getUid());
                        dbm.editElement("parkings", documentId, mapa);
                        dbm.addElement("edits", editedId, newParking2);
                    }
                    else
                    {
                        dbm.addElement("edits", editedId, newParking2);
                    }
                }
            }
        });
    }

    public void parkingPass(DocumentSnapshot ds, Map<String, Object> mapa,
                            Parking newParking, String [] ids)
    {
        //check action
        if (ids[2].equals("Utworzono"))
        {
            dbm.addElement("parkings", ids[0], newParking); //id
            mapa.put("verified", true);
            mapa.put("status", "Zweryfikowany");
            mapa.put("dataVerified", get.getActualDate());
            mapa.put("uIdVer", user.getUid());
            dbm.editElement("edits", ids[1], mapa); //editId
        }
        else
        {
            addMap(mapa, ds);
            mapa.put("verified", true);
            mapa.put("status", "Zweryfikowany");
            mapa.put("dataVerified", get.getActualDate());
            mapa.put("uIdVer", user.getUid());
            dbm.editElement("parkings", ids[0], mapa); //id
            dbm.editElement("edits", ids[1], mapa); //editId
        }
    }

    public void parkingNoPass(DocumentSnapshot ds, Map<String,
            Object> mapa, String editId)
    {
        addMap(mapa, ds);
        mapa.put("verified", false);
        mapa.put("status", "Odrzucony");
        dbm.editElement("edits", editId, mapa);
    }

    public void addMap(Map<String, Object> mapa, DocumentSnapshot ds)
    {
        mapa.put("name", ds.getString("name"));
        mapa.put("pking", ds.getString("pking"));
        mapa.put("capacity", ds.getString("capacity"));
        mapa.put("fee", ds.getString("fee"));
        mapa.put("supervised", ds.getString("supervised"));
        mapa.put("operator", ds.getString("operator"));
        mapa.put("action", "Edytowano");
        mapa.put("access", ds.getString("access"));
        mapa.put("capacityDisabled", ds.getString("capacityDisabled"));
        mapa.put("capacityTrucks", ds.getString("capacityTrucks"));
        mapa.put("capacityBus", ds.getString("capacityBus"));
        mapa.put("capacityMotorcycle", ds.getString("capacityMotorcycle"));
        mapa.put("dataEdited", get.getActualDate());
        mapa.put("harmonogram", ds.get("harmonogram"));
        mapa.put("kwota", ds.getString("kwota"));
        mapa.put("sample", ds.getString("sample"));
    }

    public void addSampleParkings(String id, KmlPlacemark kmlPlacemark, GeoPoint position)
    {
        Query query = db.collection("parkings").whereEqualTo("id", id);
        dbm.getElements(query, new OnElementsGet()
        {
            @Override
            public void setOnElementsGet(List<DocumentSnapshot> documentSnapshotList)
            {
                if (!documentSnapshotList.isEmpty())
                {
                    Log.i("DODANO", id  + "już istnieje");
                }
                else
                {
                    Log.i("NIEMA", "wykon");
                    SampleParkings sampleParkings = new SampleParkings();
                    sampleParkings.addSampleParkings(kmlPlacemark, position);
                }
            }
        });
    }

    protected ParkingManager(Parcel in)
    {
        startPoint = in.readParcelable(GeoPoint.class.getClassLoader());
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
    }
}
