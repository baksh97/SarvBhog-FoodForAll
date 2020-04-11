package com.example.sarvbhog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
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

public class CompleteRequestMarkerDialog extends Dialog{
    public Activity c;
    public Dialog d;
    TextView addr_tv,count_tv,name_tv,phone_tv, distributor_tv, dis_phone_tv;
    Button serveBtn,cancelBtn;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
//    private Marker m;

    private String addr, phone, name,count,rid,did, d_name, d_phone;

    public CompleteRequestMarkerDialog(Activity a, String name, String addr, String count, String rid, String phone, String did, String d_name,String d_phone) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
        this.addr=addr;
        this.name=name;
        this.count=count;
        this.phone = phone;
        this.rid=rid;
        this.did = did;
        this.d_name=d_name;
        this.d_phone=d_phone;
//        this.m=m;
//        a.
    }


    void initViews() {
        addr_tv = (TextView) findViewById(R.id.addr_table_rd);
        name_tv = (TextView) findViewById(R.id.name_table_rd);
        count_tv = (TextView) findViewById(R.id.count_table_rd);
        phone_tv = (TextView) findViewById(R.id.phone_table_rd);
        cancelBtn = (Button) findViewById(R.id.cancel_btn_rd);
        distributor_tv = (TextView) findViewById(R.id.distributor_table_rd);
        dis_phone_tv = (TextView) findViewById(R.id.distributorphone_table_rd);

        dis_phone_tv.setText(d_phone);
        distributor_tv.setText(d_name);
        addr_tv.setText(addr);
        name_tv.setText(name);
        phone_tv.setText(phone);
        count_tv.setText(count);

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
        setContentView(R.layout.complete_request_marker_dialog);
        initViews();
    }

}
