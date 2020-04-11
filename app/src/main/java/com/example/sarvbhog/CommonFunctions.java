package com.example.sarvbhog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CommonFunctions {

    static public void showToast(Context context, String message,int color ){
        Toast toast = Toast.makeText(context,message,Toast.LENGTH_SHORT);
//            Toast toast = Toast.makeText(context, message, duration);
        View view = toast.getView();

//Gets the actual oval background of the Toast then sets the colour filter
        view.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_IN);

//Gets the TextView from the Toast so it can be editted
        TextView text = view.findViewById(android.R.id.message);
        text.setTextColor(Color.WHITE);

        toast.show();

    }

    static public void showToast(Context context, String message){
        Toast toast = Toast.makeText(context,message,Toast.LENGTH_SHORT);
//            Toast toast = Toast.makeText(context, message, duration);
        View view = toast.getView();

//Gets the actual oval background of the Toast then sets the colour filter
        view.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);

//Gets the TextView from the Toast so it can be editted
        TextView text = view.findViewById(android.R.id.message);
        text.setTextColor(Color.WHITE);

        toast.show();

    }


    static public String readFromFile(String fileName, Context context) {

//        ArrayList<String> rids = new ArrayList<>();
        String ret="";
        try {
            InputStream inputStream = context.openFileInput(fileName);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString).append("\n");
//                    rids.add(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }

    static public void writeData(Context mContext, String fileName, String data){
        File file = mContext.getFilesDir();
//            if(!file.exists()){
//                file.mkdir();
//            }
        data = readFromFile(fileName,mContext)+'\n' + data;
//        Log.d(TAG,"writing data: "+data);


        try{
            File gpxfile = new File(file, fileName);
//                if(file.exists())file.delete();
//            Log.d(TAG,gpxfile.getAbsolutePath());
            FileWriter writer = new FileWriter(gpxfile);
//                writer.write(data);
            writer.append(data);
            writer.flush();
            writer.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    static public void orverwriteData(Context mContext, String fileName, String data){
        File file = mContext.getFilesDir();
//            if(!file.exists()){
//                file.mkdir();
//            }
//        data = readFromFile(fileName,mContext)+'\n' + data;
//        Log.d(TAG,"writing data: "+data);


        try{
            File gpxfile = new File(file, fileName);
//                if(file.exists())file.delete();
//            Log.d(TAG,gpxfile.getAbsolutePath());
            FileWriter writer = new FileWriter(gpxfile);
//                writer.write(data);
            writer.append(data);
            writer.flush();
            writer.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
