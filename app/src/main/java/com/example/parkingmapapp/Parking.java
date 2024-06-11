package com.example.parkingmapapp;

import org.osmdroid.util.GeoPoint;

import java.io.Serializable;

public class Parking implements Serializable
{
    String name;
    String pking;
    String capacity;
    String fee;
    String supervised;
    String operator;
    GeoPoint location;

    public Parking(String name, String pking, String capacity, String fee, String supervised, String operator, GeoPoint location) {
        this.name = name;
        this.pking = pking;
        this.capacity = capacity;
        this.fee = fee;
        this.supervised = supervised;
        this.operator = operator;
        this.location = location;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPking() {
        return pking;
    }

    public void setPking(String pking) {
        this.pking = pking;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getSupervised() {
        return supervised;
    }

    public void setSupervised(String supervised) {
        this.supervised = supervised;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }
}
