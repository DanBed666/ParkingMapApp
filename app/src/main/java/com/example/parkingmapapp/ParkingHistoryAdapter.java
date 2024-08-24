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

public class ParkingHistoryAdapter extends RecyclerView.Adapter<ParkingHistoryAdapter.ParkingHistoryViewHolder>
{
    Context context;
    List<DocumentSnapshot> documentSnapshotList;

    public ParkingHistoryAdapter(Context context, List<DocumentSnapshot> documentSnapshotList)
    {
        this.context = context;
        this.documentSnapshotList = documentSnapshotList;
    }

    @NonNull
    @Override
    public ParkingHistoryAdapter.ParkingHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.single_parking_history, parent, false);
        return new ParkingHistoryAdapter.ParkingHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParkingHistoryAdapter.ParkingHistoryViewHolder holder, int position)
    {
        holder.status.setText(documentSnapshotList.get(position).getString("status"));
        holder.edited.setText(documentSnapshotList.get(position).getString("edited"));
    }

    @Override
    public int getItemCount()
    {
        return documentSnapshotList.size();
    }

    public static class ParkingHistoryViewHolder extends RecyclerView.ViewHolder
    {
        TextView status;
        TextView edited;
        public ParkingHistoryViewHolder(@NonNull View itemView)
        {
            super(itemView);
            status = itemView.findViewById(R.id.tv_status);
            edited = itemView.findViewById(R.id.tv_edited);
        }
    }
}
