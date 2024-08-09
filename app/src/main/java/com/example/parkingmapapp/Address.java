package com.example.parkingmapapp;

import com.google.gson.annotations.SerializedName;

public class Address
{
    @SerializedName("display_name")
    String displayName;

    public String getDisplayName()
    {
        return displayName;
    }

    public void setDisplayName(String displayName)
    {
        this.displayName = displayName;
    }
}
