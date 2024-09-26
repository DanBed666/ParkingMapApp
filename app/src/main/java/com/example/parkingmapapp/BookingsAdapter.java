package com.example.parkingmapapp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class BookingsAdapter extends RecyclerView.Adapter<BookingsAdapter.BookingsViewHolder>
{
    Context context;
    List<DocumentSnapshot> exampleList;
    AddressViewModel addressViewModel;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    RefreshListener refreshListener;
    private final String API_KEY = "FiyHNQAmeoWKRcEdp5KyYWOAaAKf-7hvtqkz--lGBDc";
    public BookingsAdapter(Context applicationContext, List<DocumentSnapshot> example, RefreshListener listener)
    {
        context = applicationContext;
        exampleList = example;
        addressViewModel = new AddressViewModel();
        refreshListener = listener;
    }
    @NonNull
    @Override
    public BookingsAdapter.BookingsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.single_ticket, parent, false);
        return new BookingsAdapter.BookingsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingsAdapter.BookingsViewHolder holder, int position)
    {
        String ticketId = exampleList.get(position).getString("ticketId");
        String parkingId = exampleList.get(position).getString("parkingId");
        String data = exampleList.get(position).getString("reservationDate");
        String validThruDate = exampleList.get(position).getString("validThruDate");

        holder.data.setText(data);
        holder.id.setText(ticketId);
        holder.isValid.setText("Ważny do: " + validThruDate);

        getAddress(parkingId, holder.adres);

        if(!checkValidity(validThruDate))
        {
            holder.isValid.setText("Bilet nieważny!");
            holder.delete.setVisibility(View.VISIBLE);

            holder.delete.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    deleteTicket(ticketId);
                }
            });
        }
        //holder.isValid.setText();
        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(context, TicketActivity.class);
                intent.putExtra("TICKETID", ticketId);
                intent.putExtra("PARKINGID", parkingId);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return exampleList.size();
    }

    public static class BookingsViewHolder extends RecyclerView.ViewHolder
    {
        TextView id;
        TextView nazwa;
        TextView adres;
        TextView data;
        TextView isValid;
        Button delete;
        public BookingsViewHolder(@NonNull View itemView)
        {
            super(itemView);
            id = itemView.findViewById(R.id.id);
            nazwa = itemView.findViewById(R.id.nazwa);
            adres = itemView.findViewById(R.id.adres);
            data = itemView.findViewById(R.id.data);
            isValid = itemView.findViewById(R.id.isValid);
            delete = itemView.findViewById(R.id.btn_delete);
        }
    }
    public boolean checkValidity(String validDate)
    {
        Calendar calender = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        String actualDate = df.format(calender.getTime());
        boolean isValid = true;

        try
        {
            Date actual = df.parse(actualDate);
            Date valid = df.parse(validDate);

            if (actual.after(valid))
                isValid = false;
        }
        catch(ParseException ex)
        {
            ex.printStackTrace();
        }

        return isValid;
    }

    public void getAddress(String parkingId, TextView adresTV)
    {
        GetTagData get = new GetTagData();
        DatabaseManager dbm = new DatabaseManager();
        Query q2 = db.collection("parkings").whereEqualTo("id", parkingId);
        dbm.getElements(q2, new OnElementsGet()
        {
            @Override
            public void setOnElementsGet(List<DocumentSnapshot> documentSnapshotList)
            {
                for (DocumentSnapshot d : documentSnapshotList)
                {
                    get.getAddressHere(d.get("latitude") + "," + d.get("longitude"),
                            API_KEY, adresTV);
                }
            }
        });
    }

    public void deleteTicket(String ticketId)
    {
        assert ticketId != null;
        db.collection("tickets").document(ticketId).delete().addOnSuccessListener(new OnSuccessListener<Void>()
        {
            @Override
            public void onSuccess(Void unused)
            {
                Log.i("CREATED", "usunieto");
                refreshListener.refresh();
            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Log.i("CREATED", e.getMessage());
            }
        });
    }
}
