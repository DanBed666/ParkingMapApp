package com.example.parkingmapapp;

import java.io.Serializable;

public class User implements Serializable
{
    String uId;
    String name;
    String surname;

    public User(String uId, String name, String surname)
    {
        this.uId = uId;
        this.name = name;
        this.surname = surname;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }
}
