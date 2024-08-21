package com.example.parkingmapapp;

public class Edits
{
    String uId;
    String id;
    boolean edited;
    boolean created;
    String nazwa;
    String adres;
    String dataEdited;
    String dataCreated;

    public Edits(String uId, String id, boolean edited, boolean created, String nazwa, String adres, String dataEdited, String dataCreated) {
        this.uId = uId;
        this.id = id;
        this.edited = edited;
        this.created = created;
        this.nazwa = nazwa;
        this.adres = adres;
        this.dataEdited = dataEdited;
        this.dataCreated = dataCreated;
    }

    public String getDataEdited() {
        return dataEdited;
    }

    public void setDataEdited(String dataEdited) {
        this.dataEdited = dataEdited;
    }

    public String getDataCreated() {
        return dataCreated;
    }

    public void setDataCreated(String dataCreated) {
        this.dataCreated = dataCreated;
    }

    public String getAdres() {
        return adres;
    }

    public void setAdres(String adres) {
        this.adres = adres;
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

    public boolean isEdited() {
        return edited;
    }

    public void setEdited(boolean edited) {
        this.edited = edited;
    }

    public boolean isCreated() {
        return created;
    }

    public void setCreated(boolean created) {
        this.created = created;
    }

    public String getNazwa() {
        return nazwa;
    }

    public void setNazwa(String nazwa) {
        this.nazwa = nazwa;
    }
}
