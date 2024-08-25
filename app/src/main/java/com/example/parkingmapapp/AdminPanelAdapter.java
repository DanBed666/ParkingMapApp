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

import java.util.ArrayList;
import java.util.List;

public class AdminPanelAdapter extends RecyclerView.Adapter<AdminPanelAdapter.AdminPanelViewHolder>
{
    Context context;
    List<DocumentSnapshot> documentSnapshots = new ArrayList<>();
    public AdminPanelAdapter(Context applicationContext, List<DocumentSnapshot> documents)
    {
        context = applicationContext;
        documentSnapshots = documents;
    }

    @NonNull
    @Override
    public AdminPanelAdapter.AdminPanelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.single_user, parent, false);
        return new AdminPanelAdapter.AdminPanelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminPanelAdapter.AdminPanelViewHolder holder, int position)
    {
        holder.nazwa.setText(documentSnapshots.get(position).getString("nick"));
        String id = documentSnapshots.get(position).getString("uId");
        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(context, InfoProfileActivity.class);
                intent.putExtra("CASE", "admin");
                intent.putExtra("ID", id);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return documentSnapshots.size();
    }

    public class AdminPanelViewHolder extends RecyclerView.ViewHolder
    {
        TextView nazwa;
        public AdminPanelViewHolder(@NonNull View itemView)
        {
            super(itemView);
            nazwa = itemView.findViewById(R.id.tv_nazwa);
        }
    }
}
