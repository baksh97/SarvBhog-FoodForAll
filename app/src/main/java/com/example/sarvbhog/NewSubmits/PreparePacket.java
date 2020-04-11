package com.example.sarvbhog.NewSubmits;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sarvbhog.Classes.ProducerClass;
import com.example.sarvbhog.MyActivity;
import com.example.sarvbhog.R;
import com.example.sarvbhog.SelectLocation;
import com.example.sarvbhog.SelectSHType;
import com.example.sarvbhog.Status.PrepareStatus;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.sarvbhog.CommonFunctions.showToast;
import static com.example.sarvbhog.CommonFunctions.writeData;
//import com.google.android.libraries.places.api.model.Place;
//import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
//import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;


public class PreparePacket extends AppCompatActivity {

    private static final String TAG = "PreparePacket";

    private Button submit, get_location_btn;
    private EditText count_et, foodType_et;
    private TextView addr_tv;
    ProgressBar pb;

    private String city,state,addr;
    private int count;
    private String foodType;
    private double lat,lon;

    FirebaseDatabase database;

    Context thisContext = PreparePacket.this;
    Class thisClass = PreparePacket.class;


    void iniViews(){

        database = FirebaseDatabase.getInstance();
        final DatabaseReference myref = database.getReference("prepare");

        get_location_btn = (Button) findViewById(R.id.get_location_btn_pp);
        get_location_btn.setVisibility(View.INVISIBLE);


        pb = (ProgressBar) findViewById(R.id.pb_preparepacket);
        pb.setVisibility(View.INVISIBLE);
        addr_tv = (TextView)  findViewById(R.id.location_tv_pp);
        addr_tv.setVisibility(View.INVISIBLE);
        count_et = (EditText) findViewById(R.id.count_et_pp);
        foodType_et = (EditText) findViewById(R.id.foodtype_et_pp);
        submit = (Button) findViewById(R.id.submit_btn_pp);

        get_location_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(thisContext, SelectLocation.class);
                intent.putExtra("Class", PreparePacket.class);
                startActivity(intent);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                foodType = foodType_et.getText().toString();
//                String phone = phone_til.getEditText().getText().toString();
                try {
                    count = Integer.parseInt(count_et.getText().toString());
                    if(count==0){
                        showToast(thisContext, "Number of packets can't be 0!");
                    }
                    else if(foodType.equals("")){
                        showToast(thisContext, "Please enter a food type!");
                    }
                    else {
                        pb.setVisibility(View.VISIBLE);
                        final String key = myref.push().getKey();
                        ProducerClass m = new ProducerClass();
                        m.addr = addr;
                        m.lat = lat;
                        m.lon = lon;
                        m.city=city;
                        m.state=state;
                        m.name = MyActivity.name;
                        m.phone = MyActivity.phone;
                        m.count=count;
                        m.distributor_assigned=false;
                        m.distributor_name="";
                        m.distributor_phone="";
                        m.completed=false;
                        m.foodType = foodType;
                        m.distributor_id="";
                        myref.child(key).setValue(m).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                myref.child("prepareRegionWise").child(state).child(city).push().setValue(key).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        pb.setVisibility(View.INVISIBLE);
                                        if(task.isSuccessful()){
                                            writeData(thisContext,"prepare.txt",key+"\n");
                                            Intent intent = new Intent(thisContext, PrepareStatus.class);
                                            intent.putExtra("prepareID", key);
                                            showToast(thisContext,"Added successfully!");
//                                            pb.setVisibility(View.INVISIBLE);
                                            startActivity(intent);
                                            finish();
                                        }
                                        else{
                                            showToast(thisContext, "Could not upload. Please try again!");
                                        }
                                    }
                                });
                            }
                        });
                    }
                }
                catch (Exception e){
                    pb.setVisibility(View.INVISIBLE);
                    showToast(thisContext,"Number of packets should be a number!");
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prepare_packet);

        iniViews();



//        else{
            submit.setVisibility(View.INVISIBLE);
            addr_tv.setVisibility(View.INVISIBLE);
            get_location_btn.setVisibility(View.VISIBLE);
//        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        if(SelectLocation.selected) {
            city = SelectLocation.lastCity;
            state = SelectLocation.lastState;
            lat = SelectLocation.lastFetchedLat;
            lon = SelectLocation.lastFetchedLat;
            addr = SelectLocation.lastFetchedLocation;
            SelectLocation.selected=false;

            addr_tv.setText(addr);
            addr_tv.setVisibility(View.VISIBLE);
            get_location_btn.setVisibility(View.INVISIBLE);
            submit.setVisibility(View.VISIBLE);
        }
    }
}
