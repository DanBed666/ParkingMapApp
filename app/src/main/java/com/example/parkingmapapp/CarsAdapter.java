package com.example.parkingmapapp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public class CarsAdapter extends RecyclerView.Adapter<CarsAdapter.CarsViewHolder>
{
    Context context;
    List<DocumentSnapshot> exampleList;
    RefreshListener refreshListener;
    public CarsAdapter(Context applicationContext, List<DocumentSnapshot> example, RefreshListener refresh)
    {
        context = applicationContext;
        exampleList = example;
        refreshListener = refresh;
    }

    @NonNull
    @Override
    public CarsAdapter.CarsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.single_car, parent, false);
        return new CarsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarsAdapter.CarsViewHolder holder, int position)
    {
        String documentId = exampleList.get(position).getId();
        String id = exampleList.get(position).getString("id");
        String marka = exampleList.get(position).getString("marka");
        String model = exampleList.get(position).getString("model");
        String type = exampleList.get(position).getString("type");
        String year = exampleList.get(position).getString("year");
        String registrationNumber = exampleList.get(position).getString("registrationNumber");

        Log.i("MARKA2", "marka");

        holder.marka.setText(marka);
        holder.marka.setText(model);
        holder.marka.setText(type);
        holder.marka.setText(year);
        holder.marka.setText(registrationNumber);

        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(context, EditCarActivity.class);
                Log.i("DOCUMENT", documentId);
                intent.putExtra("CARID", id);
                intent.putExtra("DOCUMENTID", documentId);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return exampleList.size();
    }

    public static class CarsViewHolder extends RecyclerView.ViewHolder
    {
        TextView marka;
        TextView model;
        TextView typ;
        TextView rok;
        TextView numer;
        public CarsViewHolder(@NonNull View itemView)
        {
            super(itemView);
            marka = itemView.findViewById(R.id.marka);
            model = itemView.findViewById(R.id.model);
            typ = itemView.findViewById(R.id.typ);
            rok = itemView.findViewById(R.id.rok);
            numer = itemView.findViewById(R.id.numer);
        }
    }
}
