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
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.sarvbhog.Classes.ProducerClass;
import com.example.sarvbhog.Classes.RequestClass;
import com.example.sarvbhog.CommonFunctions;
import com.example.sarvbhog.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import static com.example.sarvbhog.CommonFunctions.showToast;

//public class requestsAdapter {
//}

public class producersAdapter extends RecyclerView.Adapter<producersAdapter.MyViewHolder> {

    FirebaseDatabase database = FirebaseDatabase.getInstance();


    private ArrayList<ProducerClass> requests;
    private ArrayList<String> rids;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView addr_tv, name_tv, phone_tv, count_tv, dis_tv,foodtype_tv, dis_phone_tv;
        Button delete_btn;
        public MyViewHolder(View view) {
            super(view);
            dis_tv = (TextView) view.findViewById(R.id.distributor_tv_ps);
            name_tv = (TextView) view.findViewById(R.id.name_tv_ps);
            phone_tv = (TextView) view.findViewById(R.id.phone_tv_ps);
            count_tv = (TextView) view.findViewById(R.id.count_tv_ps);
            addr_tv = (TextView) view.findViewById(R.id.location_tv_ps);
            dis_phone_tv = (TextView) view.findViewById(R.id.distributorphone_tv_ps);
            delete_btn = (Button) view.findViewById(R.id.delete_btn_ps);
            foodtype_tv = (TextView) view.findViewById(R.id.foodtype_tv_ps);
        }
    }

    public producersAdapter(ArrayList<ProducerClass> requests, ArrayList<String> rids, Context context)
    {
        this.requests = requests;
        this.rids = rids;
        this.context = context;
    }

    @Override
    public producersAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ps_list_item,parent, false);
        return new producersAdapter.MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final producersAdapter.MyViewHolder holder, final int position)
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
        holder.delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference myref = database.getReference("prepare").child(rids.get(position));
                myref.removeValue();
                myref = database.getReference("prepare").child("prepareRegionWise").child(r.state).child(r.city).child(rids.get(position));
                myref.removeValue();
                String s = CommonFunctions.readFromFile("prepare.txt",context);
                s.replace(rids.get(position)+"\n", "");
                CommonFunctions.orverwriteData(context,"prepare.txt",s);

                requests.remove(position);
                rids.remove(position);
                showToast(context,"Deleted Successfully!");
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

