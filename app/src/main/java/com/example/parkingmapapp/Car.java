package com.example.parkingmapapp;

public class Car
{
    String id;
    String uId;
    String marka;
    String model;
    String type;
    String registrationNumber;
    String year;
    boolean primary;

    public Car(String id, String userId, String marka, String model, String type, String registrationNumber, String year, boolean primary) {
        this.id = id;
        this.uId = userId;
        this.marka = marka;
        this.model = model;
        this.type = type;
        this.registrationNumber = registrationNumber;
        this.year = year;
        this.primary = primary;
    }

    public boolean isPrimary() {
        return primary;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getMarka() {
        return marka;
    }

    public void setMarka(String marka) {
        this.marka = marka;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }
}
