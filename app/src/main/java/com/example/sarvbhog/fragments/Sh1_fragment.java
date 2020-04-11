package com.example.sarvbhog.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sarvbhog.Adapters.requestsAdapter;
import com.example.sarvbhog.Adapters.sh1Adapter;
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


public class Sh1_fragment extends Fragment {


    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseAuth mAuth= FirebaseAuth.getInstance();
    HashMap<String,HashMap<String,String>> hm;
    RecyclerView recyclerView;
    sh1Adapter adapter;
    ArrayList<String> rids;
    ArrayList<String> push_ids;
    ArrayList<RequestClass> requests;

    private static final String TAG = "Sh1_fragment";

    public Sh1_fragment() {
        // Required empty public constructor
    }

    void displayRequestStatus(HashMap<String,String> h, String rid){
        RequestClass rc = new RequestClass();

        rc.phone= h.get("phone");
        rc.name = h.get("name");
        rc.addr = h.get("addr");
        rc.count = Integer.parseInt(h.get("count"));
        rc.distributor_id = h.get("distributor_id");
        rc.distributor_name=h.get("distributor_name");
        rc.distributor_phone=h.get("distributor_phone");
        rc.city = h.get("city");
        rc.state = h.get("state");
//        rids.add(rid);

        requests.add(rc);
        Log.e(TAG, "notifuing dataset cghanged");
        adapter.notifyDataSetChanged();
    }


    void getRequestObject(final String rid){
        hm.put(rid, new HashMap<String, String>());
        database.getReference("requests").child(rid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String key = dataSnapshot.getKey();
                String value = dataSnapshot.getValue().toString();
                hm.get(rid).put(key,value);

                if(hm.get(rid).size()== RequestClass.getSize()){
                    displayRequestStatus(hm.get(rid),rid);
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
        DatabaseReference myref = database.getReference("users").child(mAuth.getCurrentUser().getUid()).child("sh1");
        myref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.e(TAG, "ADding: "+ dataSnapshot.getKey()+" and "+dataSnapshot.getValue().toString());
                push_ids.add(dataSnapshot.getKey());
                adapter.notifyDataSetChanged();
                rids.add(dataSnapshot.getValue().toString());
                adapter.notifyDataSetChanged();
                Log.e(TAG, "push)ids.size: "+push_ids.size());
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

        push_ids = new ArrayList<>();
        rids = new ArrayList<>();
        requests = new ArrayList<>();
        hm = new HashMap<>();

        recyclerView = (RecyclerView) view.findViewById(R.id.rv_frag_sh1);

        adapter = new sh1Adapter(requests,rids,push_ids,container.getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        getPush_ids();

        return view;
    }

}