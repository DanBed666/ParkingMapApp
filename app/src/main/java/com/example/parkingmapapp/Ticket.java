package com.example.parkingmapapp;

import android.graphics.Bitmap;

public class Ticket
{
    String uId;
    String reservationDate;
    String ticketId;
    public Ticket(String uId, String reservationDate, String ticketId) {
        this.uId = uId;
        this.reservationDate = reservationDate;
        this.ticketId = ticketId;
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
