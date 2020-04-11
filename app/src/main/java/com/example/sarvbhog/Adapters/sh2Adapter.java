//package com.example.sarvbhog;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import androidx.recyclerview.widget.RecyclerView;
//
//import java.util.List;
//
//public class producersAdapter {
//}
//

package com.example.sarvbhog.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sarvbhog.Classes.ProducerClass;
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

public class sh2Adapter extends RecyclerView.Adapter<sh2Adapter.MyViewHolder> {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private ArrayList<ProducerClass> requests;
    private ArrayList<String> push_ids;
    private ArrayList<String> rids;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView addr_tv, name_tv, phone_tv, count_tv, dis_tv,foodtype_tv,dis_phone_tv;
        ImageView delete_iv;
        ProgressBar pb;
        public MyViewHolder(View view) {
            super(view);
            pb = (ProgressBar) view.findViewById(R.id.pb_list_item_ps);
            pb.setVisibility(View.INVISIBLE);
            dis_tv = (TextView) view.findViewById(R.id.distributor_tv_ps);
            name_tv = (TextView) view.findViewById(R.id.name_tv_ps);
            phone_tv = (TextView) view.findViewById(R.id.phone_tv_ps);
            count_tv = (TextView) view.findViewById(R.id.count_tv_ps);
            addr_tv = (TextView) view.findViewById(R.id.location_tv_ps);
            dis_phone_tv = (TextView) view.findViewById(R.id.distributorphone_tv_ps);
            delete_iv = (ImageView) view.findViewById(R.id.delete_iv_ps);
            foodtype_tv = (TextView) view.findViewById(R.id.foodtype_tv_ps);

            delete_iv.setImageResource(R.drawable.ic_cancel_black_24dp);
        }
    }

    public sh2Adapter(ArrayList<ProducerClass> requests,ArrayList<String> rids, ArrayList<String> push_ids, Context context)
    {
        this.requests = requests;
        this.rids = rids;
        this.push_ids = push_ids;
        this.context = context;
    }

    @Override
    public sh2Adapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ps_list_item,parent, false);
        return new sh2Adapter.MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final sh2Adapter.MyViewHolder holder, final int position)
    {
//        holder.msgTv.setText(msgs.get(position).text);
        final ProducerClass r = requests.get(position);
        holder.addr_tv.setText(r.addr);
        holder.dis_tv.setText(r.distributor_name);
        holder.phone_tv.setText(r.phone);
        holder.count_tv.setText(String.valueOf(r.count));
        holder.name_tv.setText(r.name);
        holder.dis_phone_tv.setText(r.distributor_phone);
        holder.foodtype_tv.setText(r.foodType);
        holder.delete_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.pb.setVisibility(View.VISIBLE);
                final String rid = rids.get(position);
                final String pid = push_ids.remove(position);
                final int c = r.count;
                database.getReference("users").child(mAuth.getCurrentUser().getUid()).child("sh2").child(pid).removeValue();
                database.getReference("users").child(mAuth.getCurrentUser().getUid()).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        if(dataSnapshot.getKey().equals("producers_connected")) {
                            int counts = Integer.parseInt(dataSnapshot.getValue().toString());
                            counts -= c;
                            database.getReference("users").child(mAuth.getCurrentUser().getUid()).child("producers_connected").setValue(counts);
                            database.getReference("prepare").child(rid).child("distributor_assigned").setValue(false);
                            database.getReference("prepare").child(rid).child("distributor_name").setValue("");
                            database.getReference("prepare").child(rid).child("distributor_id").setValue("");
                            database.getReference("prepare").child(rid).child("distributor_phone").setValue("");
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
                holder.pb.setVisibility(View.INVISIBLE);
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

