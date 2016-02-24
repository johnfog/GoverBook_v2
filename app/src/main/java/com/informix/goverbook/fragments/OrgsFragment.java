package com.informix.goverbook.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.informix.goverbook.R;

public class OrgsFragment extends AbstractTabFragment {
    private static final int LAYOUT = R.layout.fragment_orgs;

    public static OrgsFragment getInstance(Context context) {
        Bundle args = new Bundle();
        OrgsFragment fragment = new OrgsFragment();
        fragment.setArguments(args);
        fragment.setContext(context);
        fragment.setTitle(context.getString(R.string.org_title));
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