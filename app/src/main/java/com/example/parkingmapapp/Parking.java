package com.example.parkingmapapp;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

public class Parking implements Serializable
{
    private String uId;
    private String id;
    private String editId;
    private String name;
    private String pking;
    private String access;
    private String capacity;
    private String capacityDisabled;
    private String capacityTrucks;
    private String capacityBus;
    private String capacityMotorcycle;
    private String fee;
    private String supervised;
    private String operator;
    private double latitude;
    private double longitude;
    private String dataCreated;
    private String dataEdited;
    private String dataVerified;
    private String action;
    private Map<String, String> harmonogram;
    private String kwota;
    private boolean verified;
    private String status;
    private Date lastActionDate;
    private boolean sample;
    private String uIdVer;

    public Parking(String uId, String id, String editId, String name, String pking, String access, String capacity, String capacityDisabled,
                   String capacityTrucks, String capacityBus, String capacityMotorcycle, String fee, String supervised,
                   String operator, double latitude, double longitude, String dataCreated, String dataEdited, String dataVerified, String action,
                   Map<String, String> harmonogram, String kwota, boolean verified, String status, Date last, boolean sample, String uIdVer)
    {
        this.uId = uId;
        this.id = id;
        this.editId = editId;
        this.name = name;
        this.pking = pking;
        this.access = access;
        this.capacity = capacity;
        this.capacityDisabled = capacityDisabled;
        this.capacityTrucks = capacityTrucks;
        this.capacityBus = capacityBus;
        this.capacityMotorcycle = capacityMotorcycle;
        this.fee = fee;
        this.supervised = supervised;
        this.operator = operator;
        this.latitude = latitude;
        this.longitude = longitude;
        this.dataCreated = dataCreated;
        this.dataEdited = dataEdited;
        this.dataVerified = dataVerified;
        this.action = action;
        this.harmonogram = harmonogram;
        this.kwota = kwota;
        this.verified = verified;
        this.status = status;
        this.lastActionDate = last;
        this.sample = sample;
        this.uIdVer = uIdVer;
    }

    public String getuIdVer() {
        return uIdVer;
    }

    public void setuIdVer(String uIdVer) {
        this.uIdVer = uIdVer;
    }

    public boolean isSample() {
        return sample;
    }

    public void setSample(boolean sample) {
        this.sample = sample;
    }

    public Date getLastActionDate() {
        return lastActionDate;
    }

    public void setLastActionDate(Date lastActionDate) {
        this.lastActionDate = lastActionDate;
    }

    public String getDataEdited() {
        return dataEdited;
    }

    public void setDataEdited(String dataEdited) {
        this.dataEdited = dataEdited;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getEditId() {
        return editId;
    }

    public void setEditId(String editId) {
        this.editId = editId;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Map<String, String> getHarmonogram() {
        return harmonogram;
    }

    public void setHarmonogram(Map<String, String> harmonogram) {
        this.harmonogram = harmonogram;
    }

    public String getKwota() {
        return kwota;
    }

    public void setKwota(String kwota) {
        this.kwota = kwota;
    }

    public String getDataCreated() {
        return dataCreated;
    }

    public void setDataCreated(String dataCreated) {
        this.dataCreated = dataCreated;
    }

    public String getDataVerified() {
        return dataVerified;
    }

    public void setDataVerified(String dataVerified) {
        this.dataVerified = dataVerified;
    }

    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
    }

    public String getCapacityDisabled() {
        return capacityDisabled;
    }

    public void setCapacityDisabled(String capacityDisabled) {
        this.capacityDisabled = capacityDisabled;
    }

    public String getCapacityTrucks() {
        return capacityTrucks;
    }

    public void setCapacityTrucks(String capacityTrucks) {
        this.capacityTrucks = capacityTrucks;
    }

    public String getCapacityBus() {
        return capacityBus;
    }

    public void setCapacityBus(String capacityBus) {
        this.capacityBus = capacityBus;
    }

    public String getCapacityMotorcycle() {
        return capacityMotorcycle;
    }

    public void setCapacityMotorcycle(String capacityMotorcycle) {
        this.capacityMotorcycle = capacityMotorcycle;
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

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
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
