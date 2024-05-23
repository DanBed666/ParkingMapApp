package com.example.parkingmapapp;

import java.io.Serializable;

public class Parking implements Serializable
{ String pking;
    String capacity;
    String fee;
    String supervised;
    String operator;
    String sample;

    public Parking(String parking, String capacity, String fee, String supervised, String operator) {
        this.pking = parking;
        this.capacity = capacity;
        this.fee = fee;
        this.supervised = supervised;
        this.operator = operator;
    }

    public Parking(String pk)
    {
        this.pking = pk;
    }

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

    public String getSample() {
        return sample;
    }

    public void setSample(String sample) {
        this.sample = sample;
    }
}
