package com.example.sarvbhog.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sarvbhog.Adapters.sh1Adapter;
import com.example.sarvbhog.Adapters.sh2Adapter;
import com.example.sarvbhog.Classes.ProducerClass;
import com.example.sarvbhog.Classes.RequestClass;
import com.example.sarvbhog.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;


public class Sh2_fragment extends Fragment {


    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseAuth mAuth= FirebaseAuth.getInstance();
    HashMap<String,HashMap<String,String>> hm;
    RecyclerView recyclerView;
    sh2Adapter adapter;
    ArrayList<String> rids;
    ArrayList<String> push_ids;
    ArrayList<ProducerClass> requests;

    private static final String TAG = "Sh2_fragment";
    
    public Sh2_fragment() {
        // Required empty public constructor
    }

    void displayRequestStatus(HashMap<String,String> h, String rid){
        ProducerClass rc = new ProducerClass();

        rc.phone= h.get("phone");
        rc.name = h.get("name");
        rc.addr = h.get("addr");
        rc.count = Integer.parseInt(h.get("count"));
        rc.distributor_id = h.get("distributor_id");
        rc.distributor_name=h.get("distributor_name");
        rc.distributor_phone=h.get("distributor_phone");
        rc.foodType = h.get("foodType");
        rc.city = h.get("city");
        rc.state = h.get("state");
//        rids.add(rid);

        requests.add(rc);
        Log.e(TAG, "notifuing dataset cghanged");
        adapter.notifyDataSetChanged();
    }


    void getRequestObject(final String rid){
        hm.put(rid, new HashMap<String, String>());
        database.getReference("prepare").child(rid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String key = dataSnapshot.getKey();
                String value = dataSnapshot.getValue().toString();
                hm.get(rid).put(key,value);
                Log.e(TAG, "hm.get(rid).size(): "+hm.get(rid).size());


                if(hm.get(rid).size()== ProducerClass.getSize()){
                    Log.e(TAG, "entered here with oush size: "+push_ids.size());
                    displayRequestStatus(hm.get(rid),rid);
//                    Log.e(TAG, "hm.get(rid).size(): "+hm.get(rid).size());
//                    adapter.notifyDataSetChanged();
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
    }

    void getPush_ids(){
        DatabaseReference myref = database.getReference("users").child(mAuth.getCurrentUser().getUid()).child("sh2");
        myref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                push_ids.add(dataSnapshot.getKey());
                rids.add(dataSnapshot.getValue().toString());
                getRequestObject(dataSnapshot.getValue().toString());
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_sh1_fragment, container, false);

        rids = new ArrayList<>();
        push_ids = new ArrayList<>();
        requests = new ArrayList<>();
        hm = new HashMap<>();

        recyclerView = (RecyclerView) view.findViewById(R.id.rv_frag_sh1);

        adapter = new sh2Adapter(requests,rids,push_ids,container.getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        getPush_ids();

        return view;
    }

}