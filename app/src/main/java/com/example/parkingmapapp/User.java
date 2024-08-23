package com.example.parkingmapapp;

import java.io.Serializable;

public class User implements Serializable
{
    private String uId;
    private String name;
    private String surname;
    private String ranga;
    private String edits;
    private String  created;
    private String nick;

    public User(String uId, String name, String surname, String ranga, String  edits, String  created, String nick)
    {
        this.uId = uId;
        this.name = name;
        this.surname = surname;
        this.ranga = ranga;
        this.edits = edits;
        this.created = created;
        this.nick = nick;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String  getEdits() {
        return edits;
    }

    public void setEdits(String  edits) {
        this.edits = edits;
    }

    public String  getCreated() {
        return created;
    }

    public void setCreated(String  created) {
        this.created = created;
    }

    public String getRanga() {
        return ranga;
    }

    public void setRanga(String ranga) {
        this.ranga = ranga;
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
