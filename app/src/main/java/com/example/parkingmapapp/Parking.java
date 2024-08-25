package com.example.parkingmapapp;

import org.osmdroid.util.GeoPoint;

import java.io.Serializable;
import java.util.Map;

public class Parking implements Serializable
{
    private String uId;
    private String id;
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
    private double longtitude;
    private boolean edited;
    private boolean created;
    private String dataCreated;
    private String dataEdited;
    private Map<String, String> harmonogram;
    private String kwota;
    private String address;
    private boolean verified;
    private String status;
    private String editId;

    public Parking(String uId, String id, String editId, String name, String pking, String access, String capacity, String capacityDisabled,
                   String capacityTrucks, String capacityBus, String capacityMotorcycle, String fee, String supervised,
                   String operator, double latitude, double longtitude, boolean edited, boolean created, String dataCreated,
                   String dataEdited, Map<String, String> harmonogram, String kwota, boolean verified, String status)
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
        this.longtitude = longtitude;
        this.edited = edited;
        this.created = created;
        this.dataCreated = dataCreated;
        this.dataEdited = dataEdited;
        this.harmonogram = harmonogram;
        this.kwota = kwota;
        this.verified = verified;
        this.status = status;
    }

    public Parking(String uId, String id, String editId, String name, String pking, String access, String capacity, String capacityDisabled,
                   String capacityTrucks, String capacityBus, String capacityMotorcycle, String fee, String supervised,
                   String operator, String dataEdited, Map<String, String> harmonogram,
                   String kwota, boolean verified, String status, String address)
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
        this.dataEdited = dataEdited;
        this.harmonogram = harmonogram;
        this.kwota = kwota;
        this.verified = verified;
        this.status = status;
        this.address = address;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getDataEdited() {
        return dataEdited;
    }

    public void setDataEdited(String dataEdited) {
        this.dataEdited = dataEdited;
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

    public boolean isCreated() {
        return created;
    }

    public void setCreated(boolean created) {
        this.created = created;
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
