package com.example.parkingmapapp;

public class Car
{
    String userId;
    String marka;
    String model;
    String type;
    String registrationNumber;
    String year;

    public Car(String userId, String marka, String model, String type, String registrationNumber, String year) {
        this.userId = userId;
        this.marka = marka;
        this.model = model;
        this.type = type;
        this.registrationNumber = registrationNumber;
        this.year = year;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
