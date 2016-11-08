package com.mobileappclass.assignment3;

import android.app.Fragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Careena on 11/5/16.
 */
public class OnlineFragment  extends Fragment {

    Button syncButton;
    ListView listView;
    TextView wifiTextView;
    TextView serverStatusTextView;
    public static String serverStatus;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_online, container, false);
        syncButton = (Button) view.findViewById(R.id.syncButton);
        listView = (ListView) view.findViewById(R.id.onlineList);
        wifiTextView = (TextView) view.findViewById(R.id.wifiName);
        serverStatusTextView = (TextView) view.findViewById(R.id.serverStatus);

        final MainActivity mainActivity = (MainActivity) getActivity();

        syncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.syncWithFirebase();
            }
        });

        setListViewHeight();
        displayServerList(mainActivity.serverList);

        setWifiTextView(mainActivity.WIFI_NAME);
        serverStatusTextView.setText(serverStatus);

        return view;
    }

    public void displayServerList(ArrayList<String> localList){

        int numOfItems = 200;

        if(localList.size() > numOfItems) {
            ArrayList<String> limitedList = new ArrayList<>(localList.subList(0, numOfItems));
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, limitedList);
            listView.setAdapter(adapter);
        }
        else {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, localList);
            listView.setAdapter(adapter);
        }


    }

    public void setWifiTextView(String wifiName){
        wifiTextView.setText("WiFi Name : " + wifiName);
    }



    public void setStatusTextView(String status){
        serverStatusTextView.setText(status);
    }


    public void setListViewHeight(){

        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) listView.getLayoutParams();

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            lp.height = 180;
            listView.setLayoutParams(lp);
        }
        else{

            lp.height = 500;
            listView.setLayoutParams(lp);

        }

    }

}