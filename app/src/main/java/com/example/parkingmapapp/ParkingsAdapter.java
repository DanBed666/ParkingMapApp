package com.example.parkingmapapp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;
import java.util.Objects;

public class ParkingsAdapter extends RecyclerView.Adapter<ParkingsAdapter.ParkingsViewHolder>
{
    Context context;
    List<DocumentSnapshot> exampleList;
    AddressViewModel addressViewModel;
    public ParkingsAdapter(Context applicationContext, List<DocumentSnapshot> example)
    {
        context = applicationContext;
        exampleList = example;
        addressViewModel = new AddressViewModel();
    }
    @NonNull
    @Override
    public ParkingsAdapter.ParkingsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.single_parking, parent, false);
        return new ParkingsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParkingsAdapter.ParkingsViewHolder holder, int position)
    {
        String editId = exampleList.get(position).getString("editId");
        String id = exampleList.get(position).getString("id");
        String action = exampleList.get(position).getString("action");

        Log.i("HISTORIA88", String.valueOf(exampleList.size()));
        Log.i("HISTORIA88", String.valueOf(position));
        //Log.i("HIST", Objects.requireNonNull(exampleList.get(position).getString("edited")));
        //Log.i("HIST", Objects.requireNonNull(exampleList.get(position).getString("dataEdited")));

        holder.nazwa.setText(Objects.requireNonNull(exampleList.get(position).get("name")).toString());

        getAddressNominatim(exampleList.get(position).get("latitude") + "," + exampleList.get(position).get("longitude"),
                "FiyHNQAmeoWKRcEdp5KyYWOAaAKf-7hvtqkz--lGBDc", holder.adres);

        if (Objects.equals(action, "Edytowano"))
        {
            holder.data.setText(Objects.requireNonNull(exampleList.get(position).get("dataEdited")).toString());
            holder.status_edyt.setText(Objects.requireNonNull("Edytowano").toString());
        }
        else if (Objects.equals(action, "Utworzono"))
        {
            holder.data.setText(Objects.requireNonNull(exampleList.get(position).get("dataCreated")).toString());
            holder.status_edyt.setText(Objects.requireNonNull("Utworzono").toString());
        }

        holder.status_weryf.setText(Objects.requireNonNull(exampleList.get(position).get("status")).toString());

        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(new Intent(context, VerifyChangesActivity.class));
                intent.putExtra("EDITID", editId);
                intent.putExtra("PARKINGID", id);
                context.startActivity(intent);
            }
        });
    }

    public void getAddressNominatim(String geoPoint, String apiKey, TextView tv)
    {
        addressViewModel.getAddressVM(geoPoint, apiKey).observeForever(new Observer<Address>()
        {
            @Override
            public void onChanged(Address address)
            {
                Log.i("ADRES", address.getItems().get(0).getTitle());
                String addressStr = address.getItems().get(0).getTitle();
                tv.setText(addressStr);
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return exampleList.size();
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
