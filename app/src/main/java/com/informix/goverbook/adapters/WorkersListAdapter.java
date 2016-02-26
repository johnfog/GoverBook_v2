package com.informix.goverbook.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.informix.goverbook.DBHelper;
import com.informix.goverbook.R;

import java.util.ArrayList;

/**
 * Created by adm on 20.02.2016.
 */
public class WorkersListAdapter extends ArrayAdapter{
    private Context context;
    private String[] name;
    private String[] status;

    public WorkersListAdapter(Context context, String[] name,String[] status) {
        super(context, R.layout.workers_adapter_view, name);
        this.context = context;
        this.name=name;
        this.status=status;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.workers_adapter_view, parent, false);
        TextView textViewName = (TextView) rowView.findViewById(R.id.workers_text_name);
        TextView textViewStatus = (TextView) rowView.findViewById(R.id.workers_text_status);
        textViewName.setText(name[position]);
        textViewStatus.setText(status[position]);
        return rowView;
    }
}
