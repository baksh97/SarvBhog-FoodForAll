package com.example.sarvbhog.Classes;

public class RequestClass {
    public String addr,city,state,name, phone,distributor_id, distributor_name, distributor_phone;
    public int count;
    public double lat, lon;
    public boolean distributor_assigned, completed;

    public String toString(){
        return addr+" : " + name +" : "+phone+" : " +String.valueOf(count) + " : "+String.valueOf(distributor_assigned)+" : "+String.valueOf(completed);
    }

    public static int getSize(){
        return 13;
    }
}
