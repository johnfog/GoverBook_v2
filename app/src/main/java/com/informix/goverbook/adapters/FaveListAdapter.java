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
public class FaveListAdapter extends ArrayAdapter{
    private Context context;
    private String[] fList;
    private String[] fType;

    public FaveListAdapter(Context context, String[] fType,String[] faveList) {
        super(context, R.layout.list_fave, faveList);
        this.context = context;
        this.fList = faveList;
        this.fType= fType;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_fave, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.faveName);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.imageFaveType);
        textView.setText(fList[position]);


        String str = fType[position];
        if (str.equals(DBHelper.TYPE_ORG)) {
            imageView.setImageResource(R.mipmap.ic_home_modern);

        }

        if (str.equals(DBHelper.TYPE_WORKER)) {
            imageView.setImageResource(R.mipmap.ic_account_card_details);
        }

        return rowView;
    }
}
