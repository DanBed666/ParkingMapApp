package com.example.parkingmapapp;

import android.graphics.Bitmap;

public class Ticket
{
    String uId;
    String reservationDate;
    Bitmap qrCode;

    public Ticket(String uId, String reservationDate, Bitmap qrCode) {
        this.uId = uId;
        this.reservationDate = reservationDate;
        this.qrCode = qrCode;
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

    public Bitmap getQrCode() {
        return qrCode;
    }

    public void setQrCode(Bitmap qrCode) {
        this.qrCode = qrCode;
    }
}
