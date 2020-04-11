package com.example.sarvbhog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.sarvbhog.NewSubmits.PreparePacket;
import com.example.sarvbhog.NewSubmits.RequestPacket2;
import com.example.sarvbhog.Register.Login;
import com.example.sarvbhog.Register.Register;
import com.example.sarvbhog.Status.PrepareStatus;
import com.example.sarvbhog.Status.RequestStatus;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.example.sarvbhog.CommonFunctions.showToast;

public class MyActivity extends AppCompatActivity {

    Button register_btn, login_btn, sh1_btn,sh2_btn,sh3_btn;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    Context thisContext = MyActivity.this;

    public static String name="",phone="";

    private Boolean mLocationPermissionsGranted = false;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final String WRITE_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private static final String READ_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;


    void getUserInfo(){
        if(mAuth.getCurrentUser()!=null) {
            DatabaseReference users = database.getReference("users").child(mAuth.getCurrentUser().getUid());
            users.child("name").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    name = dataSnapshot.getValue().toString();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            users.child("phone").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    phone = dataSnapshot.getValue().toString();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    void initViews(){
        register_btn = (Button) findViewById(R.id.register_btn_my);
        login_btn = (Button) findViewById(R.id.login_btn_my);
        sh1_btn = (Button) findViewById(R.id.sh1_btn_my);
        sh2_btn = (Button) findViewById(R.id.sh2_btn_my);
        sh3_btn = (Button) findViewById(R.id.sh3_btn_my);

        getUserInfo();

        if(mAuth.getCurrentUser()!=null){
            login_btn.setVisibility(View.INVISIBLE);
            register_btn.setVisibility(View.INVISIBLE);
        }

        sh1_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(thisContext,RequestPacket2.class );
//                intent.putExtra("Class", RequestPacket2.class);
                startActivity(intent);
            }
        });

        sh2_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mAuth.getCurrentUser()==null){
                    showToast(thisContext,"Please login first!", Color.DKGRAY);
                }
                else if(!mLocationPermissionsGranted){
                    showToast(thisContext,"Please provide permissions first!",Color.DKGRAY);
                    checkPermissions();
                }
                else {
                    Intent intent = new Intent(thisContext, PreparePacket.class);
//                    intent.putExtra("Class", PreparePacket.class);
                    startActivity(intent);
                }
            }
        });

        sh3_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mAuth.getCurrentUser()==null){
                    showToast(thisContext,"Please login first!", Color.DKGRAY);
                }
                else if(!mLocationPermissionsGranted){
                    showToast(thisContext,"Please provide permissions first!",Color.DKGRAY);
                    checkPermissions();
                }
                else {
                    Intent intent = new Intent(thisContext, SelectSHType.class);
//                    intent.putExtra("Class", SelectSHType.class);
                    startActivity(intent);
                }
            }
        });

        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(thisContext, Register.class));
            }
        });

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(thisContext, Login.class));
            }
        });


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        checkPermissions();
        initViews();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (mAuth.getCurrentUser() != null || id==R.id.request_status) {
            switch (id) {
                case R.id.profile:
                    startActivity(new Intent(thisContext, Profile.class));
                    return true;
                case R.id.request_status:
                    startActivity(new Intent(thisContext, RequestStatus.class));
                    return true;
                case R.id.prepare_status:
                    startActivity(new Intent(thisContext, PrepareStatus.class));
                    return true;
                case R.id.sign_out:
                    mAuth.signOut();
                    name="";
                    phone="";
                    login_btn.setVisibility(View.VISIBLE);
                    register_btn.setVisibility(View.VISIBLE);
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        }
        else{
            showToast(thisContext, "You need to login to access this!");
            return true;
        }
    }

    private void checkPermissions() {
//        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this.getApplicationContext(), READ_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this.getApplicationContext(), WRITE_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionsGranted = true;
//            initMap();
        } else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
//                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
//                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;
                    //initialize our map
//                    initMap();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }



}

