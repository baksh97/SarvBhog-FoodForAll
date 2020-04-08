package com.example.saravbhog;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class MapFunctions {

    static final int ERROR_DIALOG_REQUEST=1000;


    public static boolean isServicesOK(Activity activity){
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(activity);
        if(available== ConnectionResult.SUCCESS) {
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(activity,available,ERROR_DIALOG_REQUEST);
            dialog.show();
        }
        else{
            Toast.makeText(activity, "You can't make Map Requests!", Toast.LENGTH_SHORT).show();
        }
        return false;
    }








}
