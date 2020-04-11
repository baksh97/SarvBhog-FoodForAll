package com.example.sarvbhog.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sarvbhog.Classes.RequestClass;
import com.example.sarvbhog.CommonFunctions;
import com.example.sarvbhog.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import static com.example.sarvbhog.CommonFunctions.showToast;

//public class requestsAdapter {
//}

public class sh1Adapter extends RecyclerView.Adapter<sh1Adapter.MyViewHolder> {

    private static final String TAG = "requestsAdapter";

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private ArrayList<RequestClass> requests;
    private ArrayList<String> rids;
    private ArrayList<String> push_ids;
//    private String uid;

    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView addr_tv, name_tv, phone_tv, count_tv, dis_tv,dis_phone_tv;
        Button delete_btn;
        public MyViewHolder(View view) {
            super(view);
            dis_tv = (TextView) view.findViewById(R.id.distributor_tv_rs);
            name_tv = (TextView) view.findViewById(R.id.name_tv_rs);
            phone_tv = (TextView) view.findViewById(R.id.phone_tv_rs);
            count_tv = (TextView) view.findViewById(R.id.count_tv_rs);
            addr_tv = (TextView) view.findViewById(R.id.location_tv_rs);
            dis_phone_tv = (TextView) view.findViewById(R.id.distributorphone_tv_rs);
            delete_btn = (Button) view.findViewById(R.id.delete_btn_rs);
            delete_btn.setText("Disconnect");
        }
    }

    public sh1Adapter(ArrayList<RequestClass> requests, ArrayList<String> rids, ArrayList<String> push_ids,Context context)
    {
        this.requests = requests;
        this.rids = rids;
        this.push_ids = push_ids;
        this.context = context;

        Log.e(TAG, "push size: "+push_ids.size()+" and rids size: "+rids.size());
    }

    @Override
    public sh1Adapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rs_list_item,parent, false);
        return new sh1Adapter.MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final sh1Adapter.MyViewHolder holder, final int position)
    {
//        holder.msgTv.setText(msgs.get(position).text);
        final RequestClass r = requests.get(position);
        holder.addr_tv.setText(r.addr);
        holder.dis_tv.setText(r.distributor_name);
        holder.count_tv.setText(String.valueOf(r.count));
        holder.phone_tv.setText(r.phone);
        holder.name_tv.setText(r.name);
        holder.dis_phone_tv.setText(r.distributor_phone);
        holder.delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String rid = rids.get(position);
                final String pid = push_ids.get(position);
                final int c = r.count;
                database.getReference("users").child(mAuth.getCurrentUser().getUid()).child("sh1").child(pid).removeValue();
                database.getReference("users").child(mAuth.getCurrentUser().getUid()).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        if(dataSnapshot.getKey().equals("requests_served")) {
                            int counts = Integer.parseInt(dataSnapshot.getValue().toString());
                            counts -= c;
                            database.getReference("users").child(mAuth.getCurrentUser().getUid()).child("requests_served").setValue(counts);
                            database.getReference("requests").child(rid).child("distributor_assigned").setValue(false);
                            database.getReference("requests").child(rid).child("distributor_name").setValue("");
                            database.getReference("requests").child(rid).child("distributor_id").setValue("");
                            database.getReference("requests").child(rid).child("distributor_phone").setValue("");
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                requests.remove(position);
                showToast(context,"Disconnected Successfully!");
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return requests.size();
    }

}

