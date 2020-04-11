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

import com.example.sarvbhog.Adapters.requestsAdapter;
import com.example.sarvbhog.Classes.RequestClass;
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

public class RequestStatus extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference myref;

    TextView tv1,tv2,tv3,tv4,tv5,tv6;
    HashMap<String,HashMap<String,String>> hm;
//    ArrayList
    RecyclerView recyclerView;
    private static final String TAG = "RequestStatus";

    requestsAdapter adapter;
    ArrayList<RequestClass> requests;
    ArrayList<String> rids;

    void initViews(){
        database = FirebaseDatabase.getInstance();
        myref = database.getReference("requests");
        requests = new ArrayList<>();
        rids = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.requests_list_rs);

        adapter = new requestsAdapter(requests,rids,this);
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
        RequestClass rc = new RequestClass();

        rc.phone= h.get("phone");
        rc.name = h.get("name");
        rc.addr = h.get("addr");
        rc.count = Integer.parseInt(h.get("count"));
        rc.distributor_id = h.get("distributor_id");
        rc.distributor_name=h.get("distributor_name");
        rc.distributor_phone = h.get("distributor_phone");
        rc.city = h.get("city");
        rc.state = h.get("state");
        rids.add(rid);

        requests.add(rc);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_status);

        initViews();

        ArrayList<String> rids = readFromFile("requests.txt",this);
//        Toast.makeText(this, rids.toString(), Toast.LENGTH_SHORT).show();
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

                    if(hm.get(s1).size()== RequestClass.getSize()){
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

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, SelectSHType.class));
    }

}
