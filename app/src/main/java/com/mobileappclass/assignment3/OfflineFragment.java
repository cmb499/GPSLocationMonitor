package com.mobileappclass.assignment3;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by Careena on 11/5/16.
 */
public class OfflineFragment extends Fragment {

    ListView listView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_offline, container, false);
        listView = (ListView) view.findViewById(R.id.offlineList);

        MainActivity mainActivity = (MainActivity) getActivity();
        ArrayList<String> showList = mainActivity.currentList;

        displayLocalList(showList);
        return view;

    }

    public void displayLocalList(ArrayList<String> localList){

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, localList);
        listView.setAdapter(adapter);
    }

}
