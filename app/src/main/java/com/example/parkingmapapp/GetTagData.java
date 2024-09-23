package com.example.parkingmapapp;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.Observer;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

public class GetTagData
{
    public String generateId()
    {
        StringBuilder chain = new StringBuilder();
        Random rand = new Random();

        for (int i = 0; i < 20; i++)
        {
            chain.append((char) (rand.nextInt(26) + 65));
        }

        return chain.toString();
    }

    public String getActualDate()
    {
        Calendar calender = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        String formattedDate = df.format(calender.getTime());
        return formattedDate;
    }

    public String getValidDate(int hours)
    {
        Calendar calender = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        calender.add(Calendar.HOUR, hours);
        String formattedDate = df.format(calender.getTime());
        return formattedDate;
    }

    public String generateTicketId()
    {
        StringBuilder chain = new StringBuilder();
        Random rand = new Random();

        for (int i = 0; i < 20; i++)
        {
            chain.append((char) (rand.nextInt(26) + 65));
        }

        return chain.toString();
    }

    public void generateQrCode(String ticketId, ImageView imageView)
    {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

        try
        {
            BitMatrix bitMatrix = multiFormatWriter.encode(ticketId, BarcodeFormat.QR_CODE, 400, 400);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            imageView.setImageBitmap(bitmap);
        }
        catch (WriterException e)
        {
            e.printStackTrace();
        }
    }

    public void getAddressHere(String geoPoint, String apiKey, TextView info)
    {
        AddressViewModel addressViewModel = new AddressViewModel();
        addressViewModel.getAddressVM(geoPoint, apiKey).observeForever(new Observer<Address>()
        {
            @Override
            public void onChanged(Address address)
            {
                info.setText(address.getItems().get(0).getTitle());
            }
        });
    }
}



