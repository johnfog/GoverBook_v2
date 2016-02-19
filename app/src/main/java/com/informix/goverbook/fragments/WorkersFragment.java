package com.informix.goverbook.fragments;

/**
 * Created by adm on 18.02.2016.
 */
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.informix.goverbook.MainActivity;
import com.informix.goverbook.R;

public class WorkersFragment extends AbstractTabFragment {
    public ListView searchFioResult;
    private static final int LAYOUT = R.layout.fragment_workers;

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
        searchFioResult = (ListView) view.findViewById(R.id.searchFioResult);
        return view;
    }

    public void setContext(Context context) {
        this.context = context;
    }



}