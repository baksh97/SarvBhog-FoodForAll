package com.example.sarvbhog.Status;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sarvbhog.Adapters.producersAdapter;
import com.example.sarvbhog.Classes.ProducerClass;
import com.example.sarvbhog.Classes.RequestClass;
import com.example.sarvbhog.MyActivity;
import com.example.sarvbhog.R;
import com.example.sarvbhog.SelectSHType;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class PrepareStatus extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference myref;

    TextView tv1,tv2,tv3,tv4,tv5,tv6;
    HashMap<String,HashMap<String,String>> hm;
    //    ArrayList
    RecyclerView recyclerView;
    private static final String TAG = "RequestStatus";

    producersAdapter adapter;
    ArrayList<ProducerClass> requests;
    ArrayList<String> rids;

    void initViews(){
        database = FirebaseDatabase.getInstance();
        myref = database.getReference("prepare");
        requests = new ArrayList<>();
        rids = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.prepare_list_ps);
        adapter = new producersAdapter(requests,rids,this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        hm = new HashMap<>();
    }


    private ArrayList<String> readFromFile(String fileName, Context context) {

        ArrayList<String> rids = new ArrayList<>();

        try {
            InputStream inputStream = context.openFileInput(fileName);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    rids.add(receiveString);
                }

                inputStream.close();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return rids;
    }

    void displayRequestStatus(HashMap<String,String> h, String rid){
        ProducerClass rc = new ProducerClass();

        rc.phone= h.get("phone");
        rc.name = h.get("name");
        rc.addr = h.get("addr");
        rc.count = Integer.parseInt(h.get("count"));
        rc.distributor_id = h.get("distributor_id");
        rc.distributor_name=h.get("distributor_name");
        rc.distributor_phone = h.get("distributor_phone");
        rc.foodType = h.get("foodType");
        rc.city = h.get("city");
        rc.state = h.get("state");
        rids.add(rid);

        requests.add(rc);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MyActivity.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prepare_status);

        initViews();

        ArrayList<String> rids = readFromFile("prepare.txt",this);
        Log.d(TAG, rids.toString());
        for(String s:rids){
            final String s1 = s;
            hm.put(s,new HashMap<String, String>());
            myref.child(s).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    String key = dataSnapshot.getKey();
                    String value = dataSnapshot.getValue().toString();
                    hm.get(s1).put(key,value);

                    if(hm.get(s1).size()== ProducerClass.getSize()){
                        displayRequestStatus(hm.get(s1),s1);
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
//        }

    }
}
