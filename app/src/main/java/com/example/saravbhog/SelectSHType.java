package com.example.saravbhog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SelectSHType extends AppCompatActivity {

    Button request, prepare, distribute;

//    static final String COARSE_LOCATION =

    static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    static final int LOCATION_PERMISSION_CODE = 1001;
    static boolean mLocationPermissionsGranted = false;

    private void checkPermissions(){
//        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
                initViews();
            }else{
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted = false;

        switch(requestCode){
            case LOCATION_PERMISSION_CODE:{
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionsGranted = false;
//                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
//                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;
                    //initialize our map
                    initViews();
                }
            }
        }
    }


    void initViews(){
        request = (Button) findViewById(R.id.request_btn);
        prepare = (Button) findViewById(R.id.prepare_btn);
        distribute = (Button) findViewById(R.id.distribute_btn);
        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SelectSHType.this, RequestPacket.class));
            }
        });

        prepare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SelectSHType.this, PreparePacket.class));
            }
        });

        distribute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SelectSHType.this, DistributePacket.class));
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_shtype);

        if(MapFunctions.isServicesOK(SelectSHType.this)){
            checkPermissions();
        }




    }
}
