package com.mobileappclass.assignment3;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Careena on 11/6/16.
 */
public class QueryFragment extends Fragment {

    Button queryButton;
    ListView listView;
    EditText netID;
    Spinner dropdown;
    ArrayList<String> items;
    ArrayList<String> ascQueryList;
    ArrayList<String> descQueryList;
    ArrayAdapter<String> adapter;
    boolean asc;
    boolean desc;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_query, container, false);

        queryButton = (Button) view.findViewById(R.id.queryButton);
        listView = (ListView) view.findViewById(R.id.queryList);
        netID = (EditText) view.findViewById(R.id.netidEdit);
        dropdown = (Spinner) view.findViewById(R.id.spinner1);
        items = new ArrayList<>();
        items.add("Ascending");
        items.add("Descending");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);



        final MainActivity mainActivity = (MainActivity) getActivity();

        queryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (netID.getText().toString() != null) {

                    mainActivity.queryServer(netID.getText().toString());
                    netID.setText("");
                } else {
                    Toast.makeText(getActivity(), "Insert netID", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }


    public void displayQueryList(ArrayList<String> localList) {

          ascQueryList = localList;
          descQueryList = localList;
          Collections.reverse(descQueryList);
          if(desc){
            Collections.reverse(localList);
        }
         adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, localList);
        listView.setAdapter(adapter);
        setSpinner();
    }

    public void setSpinner() {

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).toString().equals("Ascending")) {
                    asc = true;
                    desc = false;
                    displayQueryListTwo(ascQueryList);
                    System.out.println("asc");
                } else {
                    asc = false;
                    desc = true;
                    displayQueryListTwo(descQueryList);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });

    }

    public void displayQueryListTwo(ArrayList<String> localListTwo) {

        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, localListTwo);
        listView.setAdapter(adapter);
    }

}