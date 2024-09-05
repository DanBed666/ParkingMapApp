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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class VerifyPanelAdapter extends RecyclerView.Adapter<VerifyPanelAdapter.VerifyPanelViewHolder>
{
    Context applicationContext;
    List<DocumentSnapshot> documentSnapshotList;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    AddressViewModel addressViewModel;

    public VerifyPanelAdapter(Context applicationContext, List<DocumentSnapshot> documents)
    {
        this.applicationContext = applicationContext;
        this.documentSnapshotList = documents;
        addressViewModel = new AddressViewModel();
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
        String uId = documentSnapshotList.get(position).getString("uId");
        Date date = documentSnapshotList.get(position).getDate("lastActionDate");

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        String formattedDate = df.format(date.getTime());
        holder.edited.setText(formattedDate);

        getAddressNominatim(documentSnapshotList.get(position).get("latitude") + "," + documentSnapshotList.get(position).get("longitude"),
                "FiyHNQAmeoWKRcEdp5KyYWOAaAKf-7hvtqkz--lGBDc", holder.adres);

        getUser(uId, holder.user);
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
        return documentSnapshotList.size();
    }

    public static class VerifyPanelViewHolder extends RecyclerView.ViewHolder
    {
        TextView name;
        TextView status;
        TextView edited;
        TextView user;
        TextView adres;
        public VerifyPanelViewHolder(@NonNull View itemView)
        {
            super(itemView);
            name = itemView.findViewById(R.id.tv_name);
            status = itemView.findViewById(R.id.tv_status);
            edited = itemView.findViewById(R.id.tv_dataEdited);
            user = itemView.findViewById(R.id.tv_user);
            adres = itemView.findViewById(R.id.tv_adres);
        }
    }

    public void getUser(String uId, TextView userTV)
    {
        db.collection("users").whereEqualTo("uId", uId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task)
            {
                if (task.isSuccessful())
                {
                    for (DocumentSnapshot ds : task.getResult().getDocuments())
                    {
                        userTV.setText(ds.getString("nick"));
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {

            }
        });
    }
}
