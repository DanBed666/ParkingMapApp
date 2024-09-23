package com.example.parkingmapapp;

import android.util.Log;

import org.osmdroid.bonuspack.kml.KmlPlacemark;
import org.osmdroid.util.GeoPoint;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class SampleParkings
{
    public Map<String, String> getExampleHarm()
    {
        Map<String, String> harmonogram = new HashMap<>();

        harmonogram.put("Poniedziałek", "17:00-22:00");
        harmonogram.put("Wtorek", "17:00-22:00");
        harmonogram.put("Środa", "17:00-22:00");
        harmonogram.put("Czwartek", "17:00-22:00");
        harmonogram.put("Piątek", "17:00-22:00");
        harmonogram.put("Sobota", "17:00-22:00");

        return harmonogram;
    }
    public void addSampleParkings(KmlPlacemark kmlPlacemark, GeoPoint location)
    {
        Log.i("WYKONUJE", "TAK");

        String id = kmlPlacemark.mId;

        String nm = "Brak";
        String pk = kmlPlacemark.getExtendedData("parking"); if(pk == null) pk="Brak";
        String cpc = kmlPlacemark.getExtendedData("capacity"); if(cpc == null) cpc="Brak";
        String fee = kmlPlacemark.getExtendedData("fee"); if(fee == null) fee="Brak";
        String svd = kmlPlacemark.getExtendedData("supervised"); if(svd == null) svd="Brak";
        String ope = kmlPlacemark.getExtendedData("operator"); if(ope == null) ope="Brak";
        String acc = kmlPlacemark.getExtendedData("access"); if(acc == null) acc="Brak";
        String cpcd = kmlPlacemark.getExtendedData("capacity:disabled"); if(cpcd == null) cpcd="Brak";
        String cpcb = kmlPlacemark.getExtendedData("capacity:bus"); if(cpcb == null) cpcb="Brak";
        String cpct = kmlPlacemark.getExtendedData("capacity:truck"); if(cpct == null) cpct="Brak";
        String cpcm = kmlPlacemark.getExtendedData("capacity:motorcycle"); if(cpcm == null) cpcm="Brak";
        String price = "Brak";

        double lat = location.getLatitude();
        double lon = location.getLongitude();
        Log.i("GEOLAT", String.valueOf(location.getLatitude()));
        Log.i("GEOLON", String.valueOf(location.getLongitude()));

        if (ope.equals("yes")) price = String.valueOf(new Random().nextInt(9) + 3);

        Parking parking = new Parking("xyz123", id, "editId", nm, pk, acc, cpc, cpcd, cpct, cpcb, cpcm, fee, svd, ope,
                lat, lon, "18-03-2024 18:19", "28-06-2024 06:47", "28-06-2024 09:27", "Edytowano",
                getExampleHarm(), price, true, "Zweryfikowany", Calendar.getInstance().getTime(), true);

        DatabaseManager databaseManager = new DatabaseManager();
        databaseManager.addElement("parkings", id, parking);
    }
}
