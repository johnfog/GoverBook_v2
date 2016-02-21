package com.informix.goverbook.fragments;

/**
 * Created by adm on 18.02.2016.
 */
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.informix.goverbook.ContactDetailActivity;
import com.informix.goverbook.DBHelper;
import com.informix.goverbook.MainActivity;
import com.informix.goverbook.R;
import com.informix.goverbook.UserContact;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkersFragment extends AbstractTabFragment {
    private static final int LAYOUT = R.layout.fragment_workers;
    ArrayList<UserContact> userContact;
    DBHelper dbHelper;
    SQLiteDatabase database;
    ListView searchFioResult;


    public static WorkersFragment getInstance(Context context) {
        Bundle args = new Bundle();
        WorkersFragment fragment = new WorkersFragment();
        fragment.setArguments(args);
        fragment.setContext(context);
        fragment.setTitle(context.getString(R.string.workers_title));
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(LAYOUT, container, false);
        dbHelper = new DBHelper(context);
        database = dbHelper.getReadableDatabase();
        listLastWorkers();

        return view;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void listLastWorkers(){
        List<Map<String, String>> data = new ArrayList<Map<String, String>>();
        userContact = dbHelper.ListLast(database);

        for (int i=0;i<userContact.size();i++) {
            Map<String, String> datum = new HashMap<String, String>(2);
            datum.put("fio", userContact.get(i).getFIO());
            datum.put("status", userContact.get(i).getSTATUS());
            data.add(datum);
        }



        SimpleAdapter adapter1 = new SimpleAdapter(context, data, android.R.layout.simple_list_item_2,
                new String[] {"fio", "status"},
                new int[] {android.R.id.text1,
                        android.R.id.text2});

        searchFioResult = (ListView) view.findViewById(R.id.searchFioResult);
        searchFioResult.setAdapter(adapter1);

        searchFioResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ContactDetailActivity.class);
                intent.putExtra("userid", userContact.get(position).getId());
                startActivity(intent);
            }
        });

    }



}