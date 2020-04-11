package com.example.sarvbhog;

import android.app.Activity;
import android.app.Dialog;
import android.widget.Toast;

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
