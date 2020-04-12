package com.example.sarvbhog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sarvbhog.Classes.ProducerClass;
import com.example.sarvbhog.Classes.RequestClass;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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

import static com.example.sarvbhog.CommonFunctions.showToast;

public class SelectLocation extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {
    private FirebaseAuth mAuth;

//    static final String COARSE_LOCATION =

    FirebaseDatabase database = FirebaseDatabase.getInstance();

//    public static String name="",phone="";

    public static boolean isFromSetting = false;

    Class c;

    //final vars
    private static final String TAG = "SelectSHType";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final String WRITE_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private static final String READ_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;

    //widgets
    private EditText mSearchText;
    private ImageView mGps, ic_search, ic_cancel, ic_info;
    private GoogleMap mMap;
    private Button select_location_btn;
    //    private FloatingActionButton request_fab, register_fab;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    //vars
    public static boolean selected=false;



    private ArrayList<String> markerAddr;
    private ArrayList<Integer> markerCount;
    private Boolean mLocationPermissionsGranted = false;
    private boolean doublePressForDetail=false;
    Handler h = new Handler();
    Runnable r;
    private String pressedMarker="";
    private final String INCOMPLETE_REQUEST_TAG = "IRT", COMPLETE_REQUEST_TAG = "CRT", INCOMPLETE_PREPARE_TAG="IPT",COMPLETE_PREPARE_TAG="CPT";

    //newvars
    static Marker currentMarker=null;
    private BitmapDescriptor incompleteRequest_bitmap,completeRequest_bitmap,completePrepare_bitmap,incompletePrepare_bitmap;
//    private BitmapDescriptor
//    private BitmapDescriptor
//    private BitmapDescriptor
//    private float BitmapDescriptorFactory.;


    public static String lastFetchedLocation = "", lastState="", lastCity="";
    public static double lastFetchedLon, lastFetchedLat;

    HashMap<String, HashMap<String,String>> requestsMarkers, preparemarkers, tempMarkers;

    Context thisContext = SelectLocation.this;
    Activity thisAct = this;

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

//    boolean removeIfMarkerPresent(HashMap<String,String> m,HashMap<String,HashMap<String,String>> l){
////        Log.d(TAG, "l size: "+l.size());
//        for (String k : l.keySet()){
//            HashMap<String, String> h = l.get(k);
//            boolean matched=true;
//            for (String k1: h.keySet()){
//                if(!h.get(k1).equals(m.get(k1))){
////                    Log.d(TAG, "not matched: "+h.get(k1)+" and "+m.get(k1)+" and key: "+k1);
//                    matched=false;
//                    break;
//                }
//            }
//            if(matched){
//                l.remove(k);
//            }
//        }
//        return true;
//    }

    String getLocationName(Address fetchedAddress){
//        String addr = fetchedAddress.getAddressLine(0);
        return fetchedAddress.getAddressLine(0);
//        return addr.substring(0,addr.indexOf(fetchedAddress.getLocality()))+" "+fetchedAddress.getLocality()+", "+fetchedAddress.getAdminArea();
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
//                try {
//                    getOtherRequestsInCity(fetchedAddress.getLocality(), fetchedAddress.getAdminArea());
//                    getOtherPreparesInCity(fetchedAddress.getLocality(), fetchedAddress.getAdminArea());
//                }
//                catch (Exception e){
//                }

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
        Log.d(TAG, "Map ready");
        mMap = googleMap;

//        mMap.getUiSettings().

//        UiSettings settings=mMap.getUiSettings();
//        settings.setZoomGesturesEnabled(true);

        incompleteRequest_bitmap = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW);
        completeRequest_bitmap = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
        incompletePrepare_bitmap = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE);
        completePrepare_bitmap = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN);
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
            init();

            if(currentMarker==null) {
                getDeviceLocation();
            }
            else{
                mSearchText.setText(currentMarker.getTitle());
                geoLocate();
            }

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);


        }
    }


    private void init(){
        Log.d(TAG, "init: initializing");

        mAuth = FirebaseAuth.getInstance();

//        if(mAuth.getCurrentUser()!=null){
//            DatabaseReference myref = database.getReference("users").child(mAuth.getCurrentUser().getUid());
//            myref.addChildEventListener(new ChildEventListener() {
//                @Override
//                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                    String key = dataSnapshot.getKey();
//                    String value = dataSnapshot.getValue().toString();
//                    if(key.equals("name")){
//                        name=dataSnapshot.getValue().toString();
//                    }
//                    else if(key.equals("phone")){
//                        phone = value;
//                    }
//                }
//
//                @Override
//                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//                }
//
//                @Override
//                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//                }
//
//                @Override
//                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            });
//        }

        select_location_btn = (Button) findViewById(R.id.select_location_btn);
        mSearchText = (EditText) findViewById(R.id.input_search);
        ic_search = (ImageView) findViewById(R.id.ic_search);
        ic_cancel = (ImageView) findViewById(R.id.cancel_iv_rp1);
        mGps = (ImageView) findViewById(R.id.ic_gps);

        select_location_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentMarker==null){
                    showToast(thisContext, "Please select a location!");
                }
                else {
                    selected = true;
//                    Intent intent = new Intent(thisContext, c);
//                    intent.putExtra("lat", lastFetchedLat);
//                    intent.putExtra("lon", lastFetchedLon);
//                    intent.putExtra("addr", lastFetchedLocation);
//                    intent.putExtra("city", lastCity);
//                    intent.putExtra("state", lastState);
//                    startActivity(intent);
                    onBackPressed();
                    finish();
                }
            }
        });

//        request_fab = (FloatingActionButton) findViewById(R.id.request_fab_sst);
//        register_fab = (FloatingActionButton) findViewById(R.id.register_fab_sst);



//        register_fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(mAuth.getCurrentUser()!=null){
//                    showToast(thisContext,"Already Signed in!");
//                }
//                else {
//                    startActivity(new Intent(thisContext,Register.class));
//                }
//            }
//        });
//
//        request_fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(currentMarker==null){
//                    showToast(thisContext, "Please select a location!");
//                }
//                else {
//                    Intent intent = new Intent(thisContext, RequestPacket2.class);
//                    intent.putExtra("lat", lastFetchedLat);
//                    intent.putExtra("lon", lastFetchedLon);
//                    intent.putExtra("addr", lastFetchedLocation);
//                    intent.putExtra("city", lastCity);
//                    intent.putExtra("state", lastState);
//                    startActivity(intent);
//                }
//            }
//        });

        ic_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                geoLocate();
            }
        });

        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Log.d(TAG, "onClick: clicked gps icon");
                getDeviceLocation();
            }
        });

        ic_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSearchText.setText("");
                if(currentMarker!=null){
                    currentMarker.remove();
                    currentMarker=null;
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

            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM);
            if(currentMarker!=null)
            currentMarker.remove();
            currentMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(address.getLatitude(), address.getLongitude())).title(addr));
        }
    }

    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: getting the devices current location");
        checkGPS(false);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try{
            if(mLocationPermissionsGranted){

                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: found location!");
                            if(task.getResult()==null)showToast(thisContext,"Could not find location!");
                            else {
                                Location currentLocation = (Location) task.getResult();
                                String addr = getAddressFromLocation(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
//                            lastFetchedLat = currentLocation.getLatitude();
//                            lastFetchedLon = currentLocation.getLongitude();
//                            lastFetchedLocation = addr;
                                moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM);
                                if(currentMarker!=null)currentMarker.remove();
                                currentMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())).title(addr));
                            }

                        }else{
//                            Log.d(TAG, "onComplete: current location is null");
                            showToast(thisContext, "unable to get current location");
                        }
                    }
                });
            }
        }catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
        }
    }

    private void moveCamera(LatLng latLng, float zoom){
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
//        if(!title.equals("My Location")){
//            MarkerOptions options = new MarkerOptions()
//                    .position(latLng)
//                    .title(title);
////            mMap.clear();
//            if(currentMarker!=null)currentMarker.remove();
////            currentMarker = mMap.addMarker(options);
//        }

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


    private void buildAlertMessageNoGps() {
        Log.d(TAG,"calling buildAlertMessageNoGps");
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                        isFromSetting=true;
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    void checkGPS(final boolean f){


        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
        }else{
            if(f){
                if (MapFunctions.isServicesOK(this)) {
                    Log.d(TAG, "Calling this");
                    checkPermissions();
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_location);

        c = (Class)getIntent().getSerializableExtra("Class");

        Log.d(TAG,"Calling on create checkgps");
        checkGPS(true);



    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isFromSetting) {
            if (MapFunctions.isServicesOK(this)) {
                Log.d(TAG, "Calling this");
                checkPermissions();
            }
        }

    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        if (mAuth.getCurrentUser() != null || id==R.id.request_status) {
//            switch (id) {
//                case R.id.profile:
//                    startActivity(new Intent(thisContext, Profile.class));
//                    return true;
//                case R.id.prepare:
//                    if(currentMarker==null){
//                        showToast(thisContext, "Please select a location!");
//                    }
//                    else {
//                        Intent intent = new Intent(thisContext, PreparePacket.class);
//                        intent.putExtra("lat", lastFetchedLat);
//                        intent.putExtra("lon", lastFetchedLon);
//                        intent.putExtra("addr", lastFetchedLocation);
//                        intent.putExtra("city", lastCity);
//                        intent.putExtra("state", lastState);
////                    intent.putExtra("Class", PreparePacket.class);
//
//                        startActivity(intent);
//                    }
//                    return true;
//                case R.id.request_status:
//                    startActivity(new Intent(thisContext, RequestStatus.class));
//                    return true;
//                case R.id.prepare_status:
//                    startActivity(new Intent(thisContext, PrepareStatus.class));
//                    return true;
//                case R.id.sign_out:
//                    mAuth.signOut();
//                    name="";
//                    phone="";
//                    return true;
//                default:
//                    return super.onOptionsItemSelected(item);
//            }
//        }
//        else{
//            showToast(thisContext, "You need to login to access this!");
//            return true;
//        }
//    }

    private void checkPermissions() {
//        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this.getApplicationContext(), READ_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this.getApplicationContext(), WRITE_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionsGranted = true;
            initMap();
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
                    initMap();
                }
            }
        }
    }
}
