package com.example.parkingmapapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
        View view = LayoutInflater.from(context).inflate(R.layout.single_car, parent, false);
        return new BookingsAdapter.BookingsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingsAdapter.BookingsViewHolder holder, int position)
    {

    }

    @Override
    public int getItemCount()
    {
        return 0;
    }

    public static class BookingsViewHolder extends RecyclerView.ViewHolder
    {

        public BookingsViewHolder(@NonNull View itemView)
        {
            super(itemView);
        }
    }
}
