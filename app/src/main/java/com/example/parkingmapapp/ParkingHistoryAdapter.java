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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Objects;

public class ParkingHistoryAdapter extends RecyclerView.Adapter<ParkingHistoryAdapter.ParkingHistoryViewHolder>
{
    Context context;
    List<DocumentSnapshot> documentSnapshotList;
    String edit;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    AddressViewModel addressViewModel;

    public ParkingHistoryAdapter(Context context, List<DocumentSnapshot> documentSnapshotList, String edit)
    {
        this.context = context;
        this.documentSnapshotList = documentSnapshotList;
        this.edit = edit;
        addressViewModel = new AddressViewModel();
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
        String editId = documentSnapshotList.get(position).getString("editId");
        String id = documentSnapshotList.get(position).getString("id");
        String uId = documentSnapshotList.get(position).getString("uId");
        String data = "Brak";

        if (Objects.equals(documentSnapshotList.get(position).getString("action"), "Utworzono"))
        {
            data = documentSnapshotList.get(position).getString("dataCreated");
        }
        else if (Objects.equals(documentSnapshotList.get(position).getString("action"), "Edytowano"))
        {
            data = documentSnapshotList.get(position).getString("dataEdited");
        }

        holder.action.setText(documentSnapshotList.get(position).getString("action"));

        getAddressNominatim(documentSnapshotList.get(position).get("latitude") + "," + documentSnapshotList.get(position).get("longitude"),
                "FiyHNQAmeoWKRcEdp5KyYWOAaAKf-7hvtqkz--lGBDc", holder.adres);

        holder.edited.setText(data);

        getUser(uId, holder.user);

        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(new Intent(context, VerifyChangesActivity.class));
                intent.putExtra("EDITID", editId);
                intent.putExtra("PARKINGID", id);
                intent.putExtra("EDITST", edit);
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
        return documentSnapshotList.size();
    }

    public static class ParkingHistoryViewHolder extends RecyclerView.ViewHolder
    {
        TextView status;
        TextView edited;
        TextView action;
        TextView user;
        TextView adres;
        public ParkingHistoryViewHolder(@NonNull View itemView)
        {
            super(itemView);
            status = itemView.findViewById(R.id.tv_status);
            edited = itemView.findViewById(R.id.tv_dataEdited);
            action = itemView.findViewById(R.id.tv_action);
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
