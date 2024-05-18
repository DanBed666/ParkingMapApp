package com.example.parkingmapapp;

import java.io.Serializable;

public class Parking implements Serializable
{
    String amenity;
    String description;
    String sample;

    public String getSample() {
        return sample;
    }

    public void setSample(String sample) {
        this.sample = sample;
    }

    public Parking(String amenity, String description) {
        this.amenity = amenity;
        this.description = description;
    }

    public String getAmenity() {
        return amenity;
    }

    public void setAmenity(String amenity) {
        this.amenity = amenity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
