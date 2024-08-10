package com.example.parkingmapapp;

import com.google.gson.annotations.SerializedName;

public class Address
{
    @SerializedName("road")
    String road;
    @SerializedName("neighbourhood")
    String neighbourhood;
    @SerializedName("suburb")
    String suburb;
    @SerializedName("city")
    String city;
    @SerializedName("state")
    String state;
    @SerializedName("postcode")
    String postcode;

    public String getRoad() {
        return road;
    }

    public void setRoad(String road) {
        this.road = road;
    }

    public String getNeighbourhood() {
        return neighbourhood;
    }

    public void setNeighbourhood(String neighbourhood) {
        this.neighbourhood = neighbourhood;
    }

    public String getSuburb() {
        return suburb;
    }

    public void setSuburb(String suburb) {
        this.suburb = suburb;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }
}
