package com.mobileappclass.assignment3;

import android.*;
import android.Manifest;
import android.app.ActivityManager;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private MyDBHandler myDBHandler;

    FragmentManager manager;
    OfflineFragment offlineFragment;
    OnlineFragment onlineFragment;
    QueryFragment queryFragment;

    public static ArrayList<String> currentList  = new ArrayList<>();
    public static ArrayList<String> serverList = new ArrayList<>();
    public static ArrayList<String> queryList = new ArrayList<>();


    public static final String MY_FILTER = "broadcastReceiver";
    public static final String MY_MSG = "_message";
    public static  String NETWORK_TYPE;
    public static boolean WIFI_CONNECTED = false;
    public static String WIFI_NAME = "";
    IntentFilter filter;
    //  TextView myTextView;

    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if(bundle != null) {
                int iVal = bundle.getInt(MY_MSG);
//                myTextView.setText(Integer.toString(iVal));


                System.out.println("1211 broadcast");
                if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {

                    System.out.println(" 1211 action conn  man");
                    Bundle extras = intent.getExtras();
                    if (extras != null) {

                        System.out.println("1211 Extras not null");

                        if (extras.get("networkType").equals(ConnectivityManager.TYPE_MOBILE)) {
                            NETWORK_TYPE = "Mobile Network";
                            WIFI_CONNECTED = false;
                            WIFI_NAME = "";

                        } else if (extras.get("networkType").equals(ConnectivityManager.TYPE_WIFI) &&
                                !extras.get("extraInfo").equals("<unknown ssid>")) {

                            NETWORK_TYPE = "WiFi Network : " + extras.get("extraInfo");
                            WIFI_CONNECTED = true;
                            WIFI_NAME = extras.get("extraInfo") + "";
                        } else {

                            NETWORK_TYPE = "No Network";
                            WIFI_CONNECTED = false;
                            WIFI_NAME = "";

                        }

                    }

                }


                if (WIFI_CONNECTED) {
                    syncWithFirebase();
                }
                else{
                    onlineFragment.setStatusTextView(" ");
                }

                /*******Landscape*******/
                if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

                    offlineFragment = (OfflineFragment) manager.findFragmentById(R.id.fragment1);
                    onlineFragment = (OnlineFragment) manager.findFragmentById(R.id.fragment2);

                   // setListViewHeight(true);
                }
                else if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {

                    offlineFragment = (OfflineFragment) manager.findFragmentByTag("fragment_offline_tag");
                    onlineFragment = (OnlineFragment) manager.findFragmentByTag("fragment_online_tag");

                }

                setLists();

            }
        }
    };





    public void setLists(){



        if (onlineFragment != null && onlineFragment.isVisible()) {
            System.out.println("299 visible!!");
            if (serverList != null) {
                onlineFragment.displayServerList(serverList);
            }


        }

            if (offlineFragment != null && offlineFragment.isVisible()) {
                System.out.println("299 visible!!");
                currentList = myDBHandler.getRows();
                if (currentList != null) {
                    offlineFragment.displayLocalList(currentList);
                }
            }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


//        currentList = new ArrayList<>();
//        serverList = new ArrayList<>();
//        queryList = new ArrayList<>();

        if(savedInstanceState != null){
            currentList = (ArrayList<String>) savedInstanceState.getSerializable("currentList");
            serverList = (ArrayList<String>) savedInstanceState.getSerializable("serverList");
            queryList = (ArrayList<String>) savedInstanceState.getSerializable("queryList");
        }


        myDBHandler = new MyDBHandler(this, null, null, 1);

        manager = getFragmentManager();

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            offlineFragment = (OfflineFragment) manager.findFragmentById(R.id.fragment1);
            onlineFragment = (OnlineFragment) manager.findFragmentById(R.id.fragment2);

        }
        else {
            FragmentTransaction transaction = manager.beginTransaction();
            offlineFragment = new OfflineFragment();
            transaction.add(R.id.group, offlineFragment, "fragment_offline_tag");
            transaction.commit();
        }
        setLists();


        // offlineFragment.displayLocalList(myDBHandler.getRows());
        System.out.println("service started !!!");


        if(!isMyServiceRunning(MyService.class)) {
            Intent intent = new Intent(this, MyService.class);
            startService(intent);

        }
        filter = new IntentFilter();
        filter.addAction(MY_FILTER);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        // filter.addAction(NETWORK_WIFI);
        // filter.addAction(NETWORK_MOBILE);


        registerReceiver(myReceiver, filter); //broadcast receiver gets registered with activity

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Students");

        ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        final StringBuffer serverListItem = new StringBuffer();

                        for(DataSnapshot netid : dataSnapshot.getChildren()){
                            for(DataSnapshot datetime : netid.getChildren()){
                                for(DataSnapshot child : datetime.getChildren()){

                                    serverListItem.append(child.getValue() + " ");
                                }
                                serverList.add(0,serverListItem.toString());
                                serverListItem.delete(0, serverListItem.length());
                            }
                        }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_offline) {
            currentList = myDBHandler.getRows();

            FragmentTransaction transaction = manager.beginTransaction();
            offlineFragment = new OfflineFragment();
            transaction.replace(R.id.group, offlineFragment, "fragment_offline_tag");
            transaction.commit();

            //offlineFragment.displayLocalList(currentList);

        } else if (item.getItemId() == R.id.action_online) {

            //  getCurrentSsid(this);

            onlineFragment = new OnlineFragment();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.group, onlineFragment, "fragment_online_tag");
            transaction.commit();

        }
        else if (item.getItemId() == R.id.action_query) {
            //  getCurrentSsid(this);
            queryFragment = new QueryFragment();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.group, queryFragment, "fragment_query_tag");
            transaction.commit();

        }

        setLists();
        return super.onOptionsItemSelected(item);
    }



    public void startService(View view){

        Intent intent = new Intent(this, MyService.class);
        startService(intent); // to start service, call this


    }

    public void stopService(View view){
        Intent intent = new Intent(this, MyService.class);

        stopService(intent); // to stop service, call this

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(this, MyService.class);
        stopService(intent);
        unregisterReceiver(myReceiver);
    }


    public void syncWithFirebase(){

        if (onlineFragment != null && onlineFragment.isVisible()) {
            System.out.println("299 visible!!");
            if (serverList != null) {
                onlineFragment.setStatusTextView("Connected");
            }
        }

        new MyAsyncTask().execute();
    }


    public void queryServer(String netid){
        queryList.clear();
        System.out.println("878133");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Students").child(netid);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot netid) {
                StringBuffer queryListItem = new StringBuffer();
              //  for (DataSnapshot netid : dataSnapshot.getChildren()) {

                    for (DataSnapshot datetime : netid.getChildren()) {

                        for (DataSnapshot child : datetime.getChildren()) {

                            queryListItem.append(child.getValue() + " ");
                        }
                        System.out.println("878133 " + queryListItem);
                        queryList.add(0, queryListItem.toString());
                        queryListItem.delete(0, queryListItem.length());
                    }


                queryFragment.displayQueryList(queryList);
                }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        QueryFragment checkVisible = (QueryFragment) manager.findFragmentByTag("fragment_query_tag");

        if (checkVisible != null && checkVisible.isVisible()) {
            queryFragment.displayQueryList(queryList);
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean("savedInstance", true);
        outState.putSerializable("currentList", currentList);
        outState.putSerializable("serverList", serverList);
        outState.putSerializable("queryList", queryList);
    }

    class MyAsyncTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(Void... values) {

        }

        @Override
        protected Void doInBackground(Void... params) {

            System.out.println("asyn back");

            ArrayList<String> uploadList = myDBHandler.getRows();
            int i =0;


            while (i < uploadList.size()) {
                System.out.println("upload");

                String[] listItems = uploadList.get(i).split("\t");
                System.out.println(" 2299 " + uploadList.get(i));
                System.out.println(" 2299 -- " + listItems[0] + " " + listItems[1] +" " + listItems[2]);
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Students"); // What database can I actually talk to?
                DatabaseReference students = ref.child("cmb499");
                DatabaseReference netid = students.child(listItems[0].replace("p.m.", ""));
                netid.child("date").setValue(listItems[0].replace("p.m.", ""));
                netid.child("netid").setValue("cmb499");
                netid.child("x").setValue(listItems[1]);
                netid.child("y").setValue(listItems[2], new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        System.out.println("Inserted into Firebase");
                    }
                });
                i++;
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            Toast.makeText(MainActivity.this, "Successfully uploaded to FireBase", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);


        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                System.out.println("878 "+serviceClass.getName());
                return true;
            }
        }
        return false;
    }




}
