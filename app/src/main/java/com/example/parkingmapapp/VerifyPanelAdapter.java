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

public class VerifyPanelAdapter extends RecyclerView.Adapter<VerifyPanelAdapter.VerifyPanelViewHolder>
{
    Context applicationContext;
    List<DocumentSnapshot> documentSnapshotList;

    public VerifyPanelAdapter(Context applicationContext, List<DocumentSnapshot> documents)
    {
        this.applicationContext = applicationContext;
        this.documentSnapshotList = documents;
    }

    @NonNull
    @Override
    public VerifyPanelAdapter.VerifyPanelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(applicationContext).inflate(R.layout.single_verify, parent, false);
        return new VerifyPanelAdapter.VerifyPanelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VerifyPanelAdapter.VerifyPanelViewHolder holder, int position)
    {
        holder.status.setText(documentSnapshotList.get(position).getString("status"));
        holder.name.setText(documentSnapshotList.get(position).getString("name"));
        String editId = documentSnapshotList.get(position).getString("editId");
        String id = documentSnapshotList.get(position).getString("id");
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
                Intent intent = new Intent(new Intent(applicationContext, VerifyChangesActivity.class));
                intent.putExtra("EDITID", editId);
                intent.putExtra("PARKINGID", id);
                applicationContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return documentSnapshotList.size();
    }

    public static class VerifyPanelViewHolder extends RecyclerView.ViewHolder
    {
        TextView name;
        TextView status;
        TextView edited;
        public VerifyPanelViewHolder(@NonNull View itemView)
        {
            super(itemView);
            name = itemView.findViewById(R.id.tv_name);
            status = itemView.findViewById(R.id.tv_status);
            edited = itemView.findViewById(R.id.tv_dataEdited);
        }
    }
}
