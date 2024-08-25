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
        holder.name.setText(documentSnapshotList.get(position).getString("name"));
        String id = documentSnapshotList.get(position).getString("editId");
        String data;

        if (documentSnapshotList.get(position).getString("dataCreated") != null)
        {
            data = documentSnapshotList.get(position).getString("dataCreated");
        }
        else
        {
            data = documentSnapshotList.get(position).getString("dataEdited");
        }

        holder.edited.setText(data);

        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(new Intent(context, VerifyChangesActivity.class));
                intent.putExtra("ID", id);
                context.startActivity(intent);
            }
        });
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
        TextView name;
        public ParkingHistoryViewHolder(@NonNull View itemView)
        {
            super(itemView);
            status = itemView.findViewById(R.id.tv_status);
            edited = itemView.findViewById(R.id.tv_dataEdited);
            name = itemView.findViewById(R.id.tv_name);
        }
    }
}
