package com.informix.goverbook.fragments;

/**
 * Created by adm on 18.02.2016.
 */
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.informix.goverbook.R;
import com.informix.goverbook.adapters.FaveListAdapter;

public class FaveFragment extends AbstractTabFragment {
    private static final int LAYOUT = R.layout.fragment_fave;

    public static FaveFragment getInstance(Context context) {
        Bundle args = new Bundle();
        FaveFragment fragment = new FaveFragment();
        fragment.setArguments(args);
        fragment.setContext(context);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(LAYOUT, container, false);
        return view;
    }

    public void setContext(Context context) {
        this.context = context;
    }

}