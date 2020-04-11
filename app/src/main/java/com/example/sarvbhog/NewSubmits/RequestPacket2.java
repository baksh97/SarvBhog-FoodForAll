package com.example.sarvbhog.NewSubmits;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sarvbhog.Classes.RequestClass;
import com.example.sarvbhog.R;
import com.example.sarvbhog.Status.RequestStatus;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import static com.example.sarvbhog.CommonFunctions.showToast;
import static com.example.sarvbhog.CommonFunctions.writeData;

public class RequestPacket2 extends AppCompatActivity {

    FirebaseDatabase database;

    TextInputLayout name_til, count_til, phone_til;
    TextView addr_tv;
    Button submit;

    String city,state;
    String addr;
    double lat,lon;
    int count;

    Context thisContext = RequestPacket2.this;

    private static final String TAG = "RequestPacket2";

    void initViews(){
        database = FirebaseDatabase.getInstance();
        final DatabaseReference myref = database.getReference("requests");

        name_til =  (TextInputLayout) findViewById(R.id.name_til_rp2);
        phone_til = (TextInputLayout) findViewById(R.id.phone_til_rp2);
        count_til = (TextInputLayout) findViewById(R.id.count_til_rp2);
        submit = (Button) findViewById(R.id.submit_request_btn_rp2);
        addr_tv = (TextView) findViewById(R.id.location_tv_rp2);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = name_til.getEditText().getText().toString();
                String phone = phone_til.getEditText().getText().toString();
                try {
                    count = Integer.parseInt(count_til.getEditText().getText().toString());
                    if(count==0){
                        showToast(thisContext, "Number of people can't be 0!");
                    }else {
                        final String key = myref.push().getKey();
                        RequestClass m = new RequestClass();
                        m.addr = addr;
                        m.lat = lat;
                        m.lon = lon;
                        m.city = city;
                        m.state = state;
                        m.count=count;
                        m.distributor_assigned=false;
                        m.distributor_name = "";
                        m.distributor_phone="";
                        m.completed=false;
                        m.name=name;
                        m.phone=phone;
                        m.distributor_id="";
                        myref.child(key).setValue(m).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                myref.child("requestsRegionWise").child(state).child(city).push().setValue(key).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            writeData(RequestPacket2.this,"requests.txt",key+"\n");
                                            Intent intent = new Intent(RequestPacket2.this, RequestStatus.class);
                                            intent.putExtra("requestID", key);
                                            showToast(thisContext,"Added successfully!");
                                            startActivity(intent);
                                            finish();
                                        }
                                        else{
                                            showToast(thisContext,"Could not upload. Please try again!");
                                        }
                                    }
                                });
                            }
                        });
                    }
                }
                catch (Exception e){
                    showToast(thisContext,"Number of people should be a number!");
                }
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_packet2);

        initViews();

        Intent intent = getIntent();
        addr = intent.getStringExtra("addr");
        addr_tv.setText(addr);

        lat = intent.getDoubleExtra("lat",0);
        lon = intent.getDoubleExtra("lon",0);
        city = intent.getStringExtra("city");
        state = intent.getStringExtra("state");

    }
}
