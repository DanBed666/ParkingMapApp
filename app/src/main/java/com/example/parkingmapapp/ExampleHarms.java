package com.example.parkingmapapp;

import java.util.HashMap;
import java.util.Map;

public class ExampleHarms
{
    Map<String, String> harmonogram = new HashMap<>();
    public ExampleHarms()
    {
        harmonogram.put("Poniedziałek", "17:00-22:00");
        harmonogram.put("Wtorek", "17:00-22:00");
        harmonogram.put("Środa", "17:00-22:00");
        harmonogram.put("Czwartek", "17:00-22:00");
        harmonogram.put("Piątek", "17:00-22:00");
        harmonogram.put("Sobota", "17:00-22:00");
    }

    public Map<String, String> getHarmonogram()
    {
        return harmonogram;
    }

    public void setHarmonogram(Map<String, String> harmonogram)
    {
        this.harmonogram = harmonogram;
    }
}
