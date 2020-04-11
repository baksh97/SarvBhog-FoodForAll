package com.example.sarvbhog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class SelectLocation extends AppCompatActivity implements GoogleMap.OnMarkerClickListener, OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {
    FirebaseDatabase database = FirebaseDatabase.getInstance();


    private Context thisContext = SelectLocation.this;
    private Class c;
    //final vars
    private static final String TAG = "SelectLocation";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;

    //widgets
    private EditText mSearchText;
    private TextView markerInfo_tv;
    private ImageView mGps, ic_search, ic_cancel;
    private Button selectLocation;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    //vars
    private ArrayList<String> markerAddr;
    private ArrayList<Integer> markerCount;
    private Boolean mLocationPermissionsGranted = false;

    //newvars
    Marker currentMarker=null;


    String lastFetchedLocation = "", lastState="", lastCity="";
    double lastFetchedLon, lastFetchedLat;

    HashMap<String,HashMap<String,String>> otherMarkers;

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    String getLocationName(Address fetchedAddress){
        return fetchedAddress.getAddressLine(0);
    }

    String getAddressFromLocation(LatLng point){
        Geocoder geocoder = new Geocoder(this, Locale.ENGLISH);
        try {
            List<Address> addresses = geocoder.getFromLocation(point.latitude, point.longitude, 1);

            if (addresses.size() > 0) {
                Address fetchedAddress = addresses.get(0);
                lastFetchedLat = point.latitude;
                lastFetchedLon = point.longitude;
                lastFetchedLocation = getLocationName(fetchedAddress);
                lastState = fetchedAddress.getAdminArea();
                lastCity = fetchedAddress.getLocality();
                mSearchText.setText(getLocationName(fetchedAddress));
            } else {
                mSearchText.setText("Searching Current Address");
            }
            return mSearchText.getText().toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
//        mMap.setOnMarkerClickListener(this);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
//                mMap.clear();
                if(currentMarker!=null){
                    currentMarker.remove();
                }

                String addr = getAddressFromLocation(point);
//                RequestClass marker = new RequestClass();
                currentMarker = mMap.addMarker(new MarkerOptions().position(point).title(addr));
            }
        });

        if (mLocationPermissionsGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);


            init();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_location);
        mSearchText = (EditText) findViewById(R.id.input_search);
//        markerInfo_tv = (TextView) findViewById(R.id.marker_info_tv);
        ic_search = (ImageView) findViewById(R.id.ic_search);
        ic_cancel = (ImageView) findViewById(R.id.cancel_iv_rp1);
        mGps = (ImageView) findViewById(R.id.ic_gps);
        selectLocation = (Button) findViewById(R.id.select_location_btn);

        c = (Class)getIntent().getSerializableExtra("Class");


        getLocationPermission();
    }

    private void init(){
        Log.d(TAG, "init: initializing");

        ic_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                geoLocate();
            }
        });

        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDeviceLocation();
            }
        });

        ic_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSearchText.setText("");
                if(currentMarker!=null)currentMarker.remove();
            }
        });

        selectLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(lastCity==null || lastState==null){
                    Toast.makeText(thisContext, "Location not valid!", Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent(thisContext, c);
                    intent.putExtra("lat", lastFetchedLat);
                    intent.putExtra("lon", lastFetchedLon);
                    intent.putExtra("addr", lastFetchedLocation);
                    intent.putExtra("city", lastCity);
                    intent.putExtra("state", lastState);
                    startActivity(intent);
                    finish();
                }
            }
        });

        hideSoftKeyboard();
    }

    private void geoLocate(){
        Log.d(TAG, "geoLocate: geolocating");

        String searchString = mSearchText.getText().toString();

        Geocoder geocoder = new Geocoder(thisContext);
        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(searchString, 1);
        }catch (IOException e){
            Log.e(TAG, "geoLocate: IOException: " + e.getMessage() );
        }

        if(list.size() > 0){
            Address address = list.get(0);
            Log.d(TAG, "geoLocate: found a location: " + address.toString());
            String addr = getAddressFromLocation(new LatLng(address.getLatitude(),address.getLongitude()));

            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM,
                    address.getAddressLine(0));
        }
    }

    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try{
            if(mLocationPermissionsGranted){

                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();
                            String addr = getAddressFromLocation(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()));
//                            lastFetchedLat = currentLocation.getLatitude();
//                            lastFetchedLon = currentLocation.getLongitude();
//                            lastFetchedLocation = addr;
                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM,"My Location");

                        }else{
//                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(thisContext, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
        }
    }

    private void moveCamera(LatLng latLng, float zoom, String title){
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        if(!title.equals("My Location")){
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title);
//            mMap.clear();
            if(currentMarker!=null)currentMarker.remove();
            currentMarker = mMap.addMarker(options);
        }

        hideSoftKeyboard();
    }

    private void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void initMap(){
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
    }

    private void getLocationPermission(){
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
                initMap();
            }else{
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted = false;

        switch(requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;
                    //initialize our map
                    initMap();
                }
            }
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
//        if(marker.getTitle())
        String title = marker.getTitle();
        marker.getTag();
//        markerInfo_tv.setText(title);
        return false;
    }
}
