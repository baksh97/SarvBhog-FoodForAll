package com.example.sarvbhog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.sarvbhog.CommonFunctions.showToast;

public class IncompleteRequestMarkerDialog extends Dialog{
    public Activity c;
    public Dialog d;
    TextView addr_tv,count_tv,name_tv,phone_tv;
    Button serveBtn,cancelBtn;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    private static final String TAG = "IncompleteRequestMarker";
//    private Marker m;

    private String addr, phone, name,count,rid,did;

    public IncompleteRequestMarkerDialog(Activity a, String name, String addr, String count, String rid, String phone, String did) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
        this.addr=addr;
        this.name=name;
        this.count=count;
        this.phone = phone;
        this.rid=rid;
        this.did = did;
//        this.m=m;
//        a.
    }


    void initViews() {
        addr_tv = (TextView) findViewById(R.id.addr_table_ird);
        name_tv = (TextView) findViewById(R.id.name_table_ird);
        count_tv = (TextView) findViewById(R.id.count_table_ird);
        phone_tv = (TextView) findViewById(R.id.phone_table_ird);
        serveBtn = (Button) findViewById(R.id.server_btn_ird);
        cancelBtn = (Button) findViewById(R.id.cancel_btn_ird);

        addr_tv.setText(addr);
        name_tv.setText(name);
        phone_tv.setText(phone);
        count_tv.setText(count);

        serveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DatabaseReference myref = database.getReference("requests").child(rid);
                myref.child("distributor_assigned").setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        myref.child("distributor_id").setValue(did).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                myref.child("distributor_name").setValue(SelectSHType.name).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        final DatabaseReference ref = database.getReference("users").child(did).child("sh1");
                                        myref.child("distributor_phone").setValue(SelectSHType.phone);
                                        database.getReference("users").child(did).addChildEventListener(new ChildEventListener() {
                                            @Override
                                            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                                if(dataSnapshot.getKey().equals("requests_served")) {
                                                    int counts = Integer.parseInt(dataSnapshot.getValue().toString());
                                                    counts += Integer.parseInt(count);
                                                    Log.d(TAG, "counts: " + counts);
                                                    database.getReference("users").child(did).child("requests_served").setValue(counts).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            ref.push().setValue(rid).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    showToast(getContext(),"Connected successfully!");
                                                                    c.startActivity(new Intent(c, SelectSHType.class));
                                                                }
                                                            });
                                                        }
                                                    });
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
                                });
                            }
                        });
                    }
                });

            }
        });


        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.incomplete_request_marker_dialog);
        initViews();



    }

}
