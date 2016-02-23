package com.informix.goverbook.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.informix.goverbook.R;

import java.util.List;

/**
 * Created by johnfog on 23.02.2016.
 */
public class SpinnerAdapter extends ArrayAdapter<String> {
    private List<String> areaNames;
    private List<String> areaIds;
    private Activity activity;


    public SpinnerAdapter(Context context, int resource, String[][] areas) {
        super(context, resource);

        for (int i=0;i<areas[0].length;i++){
            areaNames.add(areas[0][i]);
            areaIds.add(areas[1][i]);
        }
    }



}
