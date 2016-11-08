package com.mobileappclass.assignment3;

import android.*;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Careena on 11/5/16.
 */
public class MyService extends Service {

    MyDBHandler myDBHandler;
    private LocationManager locationManager;
    private LocationListener locationListener;
    String datetime;
    double xCord;
    double yCord;

    //initial set up for service
    @Override
    public void onCreate() {
        super.onCreate();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        myDBHandler = new MyDBHandler(this, null, null, 1);

    }


    //for start service
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //  Toast.makeText(this, "Service is started", Toast.LENGTH_SHORT).show();

        initLocationListener();
       // performLocationCheck();

        System.out.println("399 Service started .....");
        // create the thread and pass Service id
        Thread thread = new Thread(new MyThreadClass(startId));
        thread.start();
        return START_STICKY;    //constant value of service class
    }


    //will be called before service is destroyed
    @Override
    public void onDestroy() {
        //Toast.makeText(this, "Service Destroyed .....", Toast.LENGTH_SHORT).show();
        System.out.println("399 Service Destroyed .....");
    }

    //since this is start service, return null (used for bind service)
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    final class MyThreadClass implements Runnable {

        int SERVICE_ID;

        public MyThreadClass(int serviceID) {
            this.SERVICE_ID = serviceID;
        }

        @Override
        public void run() {

            int i = 0;

            synchronized (this) {
                while (i < 1000) {
                    try {
                        // System.out.println("399 My Service i " + i);

                        if(datetime != null) {
                            myDBHandler.addRow(datetime, xCord, yCord);
                            System.out.println(" 999 DB  Inserted row "  + datetime + " " + xCord + " " + yCord);
                        }
                        Intent intent = new Intent();
                        intent.putExtra(MainActivity.MY_MSG, i);
                        intent.setAction(MainActivity.MY_FILTER);
                        sendBroadcast(intent);
                        wait(10000);
                        i++;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                //stopSelf(SERVICE_ID); //stop service after 15s
            }
        }

    }


    private void initLocationListener() {
        try {
            locationManager.requestLocationUpdates("gps", 5000, 0, new LocationListener() {
                @Override
                public void onLocationChanged (Location location){

                    Date date = new Date(location.getTime());
                    SimpleDateFormat simpleDate = new SimpleDateFormat("MM-dd HH:mm:ss a");

                    simpleDate.setTimeZone(TimeZone.getTimeZone("EST"));
                    String formattedDate = simpleDate.format(date);

                    datetime = formattedDate;
                    xCord = location.getLatitude();
                    yCord = location.getLongitude();

                    System.out.println("\n899--- x " + location.getLatitude() + " y " + location.getLongitude());
                }

                @Override
                public void onStatusChanged (String provider,int status, Bundle extras){

                }

                @Override
                public void onProviderEnabled (String provider){

                }

                @Override
                public void onProviderDisabled (String provider){
                    System.out.println(" disabled");
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
        }
        catch (SecurityException e){
            e.printStackTrace();
        }

    }

    private void performLocationCheck() {

//        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.INTERNET}
//                        , 10);
//            }
//            return;
//        } else {
        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        locationManager.requestLocationUpdates("gps", 5000, 0, locationListener);
        //  }

    }






    //location <code></code>


}

