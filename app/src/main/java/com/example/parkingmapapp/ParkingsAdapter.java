package com.example.parkingmapapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public class ParkingsAdapter extends RecyclerView.Adapter<ParkingsAdapter.ParkingsViewHolder>
{
    Context context;
    List<DocumentSnapshot> exampleList;
    public ParkingsAdapter(Context applicationContext, List<DocumentSnapshot> example)
    {
        context = applicationContext;
        exampleList = example;
    }
    @NonNull
    @Override
    public ParkingsAdapter.ParkingsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.single_car, parent, false);
        return new ParkingsAdapter.ParkingsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParkingsAdapter.ParkingsViewHolder holder, int position)
    {
        holder.nazwa.setText(exampleList.get(position).getString("nazwa"));
        holder.status_edyt.setText(exampleList.get(position).getString("edited"));
        holder.status_weryf.setText(exampleList.get(position).getString("created"));
    }

    @Override
    public int getItemCount()
    {
        return 0;
    }

    public static class ParkingsViewHolder extends RecyclerView.ViewHolder
    {
        TextView nazwa;
        TextView adres;
        TextView status_edyt;
        TextView status_weryf;
        TextView data;
        public ParkingsViewHolder(@NonNull View itemView)
        {
            super(itemView);
            nazwa = itemView.findViewById(R.id.nazwa);
            adres = itemView.findViewById(R.id.adres);
            status_edyt = itemView.findViewById(R.id.status_edyt);
            status_weryf = itemView.findViewById(R.id.status_weryf);
            data = itemView.findViewById(R.id.data);
        }
    }
}
