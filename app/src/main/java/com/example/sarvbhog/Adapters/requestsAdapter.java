package com.example.sarvbhog.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.sarvbhog.CommonFunctions;
import com.example.sarvbhog.R;
import com.example.sarvbhog.Classes.RequestClass;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import static com.example.sarvbhog.CommonFunctions.showToast;

//public class requestsAdapter {
//}

public class requestsAdapter extends RecyclerView.Adapter<requestsAdapter.MyViewHolder> {

    private static final String TAG = "requestsAdapter";

    FirebaseDatabase database = FirebaseDatabase.getInstance();


    private ArrayList<RequestClass> requests;
    private ArrayList<String> rids;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView addr_tv, name_tv, phone_tv, count_tv, dis_tv, dis_phone_tv;
        Button delete_btn;
        public MyViewHolder(View view) {
            super(view);
            dis_tv = (TextView) view.findViewById(R.id.distributor_tv_rs);
            name_tv = (TextView) view.findViewById(R.id.name_tv_rs);
            phone_tv = (TextView) view.findViewById(R.id.phone_tv_rs);
            count_tv = (TextView) view.findViewById(R.id.count_tv_rs);
            addr_tv = (TextView) view.findViewById(R.id.location_tv_rs);
            delete_btn = (Button) view.findViewById(R.id.delete_btn_rs);
            dis_phone_tv = (TextView) view.findViewById(R.id.distributorphone_tv_rs);
        }
    }

    public requestsAdapter(ArrayList<RequestClass> requests, ArrayList<String> rids, Context context)
    {
        this.requests = requests;
        this.rids = rids;
        this.context = context;
    }

    @Override
    public requestsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rs_list_item,parent, false);
        return new requestsAdapter.MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final requestsAdapter.MyViewHolder holder, final int position)
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
                DatabaseReference myref = database.getReference("requests").child(rids.get(position));
                Log.d(TAG,rids.get(position));
                myref.removeValue();
//                myref = database.getReference("requests").child("requestsRegionWise").child(r.state).child(r.city).child(rids.get(position));
//                myref.removeValue();
                String s = CommonFunctions.readFromFile("requests.txt",context);
                s=s.replace(rids.get(position)+"\n", "");
                CommonFunctions.orverwriteData(context,"requests.txt",s);

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

