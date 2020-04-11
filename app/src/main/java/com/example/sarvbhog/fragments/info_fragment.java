package com.example.sarvbhog.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sarvbhog.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

public class info_fragment extends Fragment {

    private TextView phone_tv,name_tv,requests_tv,producers_tv, entity_tv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_info_fragment, container, false);

        name_tv = (TextView) view.findViewById(R.id.name_tv_infofrag);
        phone_tv = (TextView) view.findViewById(R.id.phone_tv_infofrag);
        requests_tv = (TextView) view.findViewById(R.id.requests_tv_infofrag);
        producers_tv = (TextView) view.findViewById(R.id.producers_tv_infofrag);
        entity_tv = (TextView) view.findViewById(R.id.entity_tv_infofrag);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

//    static final String COARSE_LOCATION =

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        database.getReference("users").child(mAuth.getCurrentUser().getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String key = dataSnapshot.getKey();
                if(key.equals("name")){
                    name_tv.setText(dataSnapshot.getValue().toString());
                }
                else if(key.equals("phone")){
                    phone_tv.setText(dataSnapshot.getValue().toString());
                }
                else if(key.equals("producers_connected")){
                    producers_tv.setText(dataSnapshot.getValue().toString());
                }
                else if(key.equals("requests_served")){
                    requests_tv.setText(dataSnapshot.getValue().toString());
                }
                else if(key.equals("entity")){
                    entity_tv.setText(dataSnapshot.getValue().toString());
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

        return view;
    }

}
