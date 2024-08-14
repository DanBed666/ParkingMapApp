package com.example.parkingmapapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public class BookingsAdapter extends RecyclerView.Adapter<BookingsAdapter.BookingsViewHolder>
{
    Context context;
    List<DocumentSnapshot> exampleList;
    public BookingsAdapter(Context applicationContext, List<DocumentSnapshot> example)
    {
        context = applicationContext;
        exampleList = example;
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
        holder.data.setText(exampleList.get(position).getString("reservationDate"));
        String ticketId = exampleList.get(position).getString("ticketId");
        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(context, TicketActivity.class);
                intent.putExtra("TICKETID", ticketId);
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
        public BookingsViewHolder(@NonNull View itemView)
        {
            super(itemView);
            id = itemView.findViewById(R.id.id);
            nazwa = itemView.findViewById(R.id.nazwa);
            adres = itemView.findViewById(R.id.adres);
            data = itemView.findViewById(R.id.data);
        }
    }
}
