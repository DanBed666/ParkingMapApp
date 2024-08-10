package com.example.parkingmapapp;

import org.osmdroid.util.GeoPoint;

import java.io.Serializable;

public class Parking implements Serializable
{
    private String uId;
    private String id;
    private String name;
    private String pking;
    private String capacity;
    private String fee;
    private String supervised;
    private String operator;
    private double latitude;
    private double longtitude;
    private boolean edited;
    Address address;

    public Parking(String id, String name, String pking, String capacity, String fee, String supervised, String operator, double latitude, double longtitude, boolean edited, Address address)
    {
        this.id = id;
        this.name = name;
        this.pking = pking;
        this.capacity = capacity;
        this.fee = fee;
        this.supervised = supervised;
        this.operator = operator;
        this.latitude = latitude;
        this.longtitude = longtitude;
        this.edited = edited;
        this.address = address;
    }

    public Parking(String uId, String id, String name, String pking, String capacity, String fee, String supervised, String operator, double latitude, double longtitude, boolean edited) {
        this.uId = uId;
        this.id = id;
        this.name = name;
        this.pking = pking;
        this.capacity = capacity;
        this.fee = fee;
        this.supervised = supervised;
        this.operator = operator;
        this.latitude = latitude;
        this.longtitude = longtitude;
        this.edited = edited;
    }
    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(double longtitude) {
        this.longtitude = longtitude;
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
    public boolean isEdited() {
        return edited;
    }
    public void setEdited(boolean edited) {
        this.edited = edited;
    }
}
