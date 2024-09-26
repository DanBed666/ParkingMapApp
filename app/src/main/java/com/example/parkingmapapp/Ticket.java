package com.example.parkingmapapp;

import android.graphics.Bitmap;

public class Ticket
{
    String uId;
    String reservationDate;
    String validThruDate;
    String ticketId;
    String parkingId;

    public Ticket(String uId, String reservationDate, String validThruDate, String ticketId, String parkingId) {
        this.uId = uId;
        this.reservationDate = reservationDate;
        this.validThruDate = validThruDate;
        this.ticketId = ticketId;
        this.parkingId = parkingId;
    }

    public String getParkingId() {
        return parkingId;
    }

    public void setParkingId(String parkingId) {
        this.parkingId = parkingId;
    }

    public String getValidThruDate() {
        return validThruDate;
    }

    public void setValidThruDate(String validThruDate) {
        this.validThruDate = validThruDate;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(String reservationDate) {
        this.reservationDate = reservationDate;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }
}
