package com.informix.goverbook.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.informix.goverbook.R;import java.util.ArrayList;

/**
 * Created by johnfog on 02.02.2016.
 */
public class ExpListAdapter extends BaseExpandableListAdapter {

    private ArrayList<ArrayList<String>> mGroups;
    private ArrayList<ArrayList<String>> mGroupsDescription;
    private ArrayList<String> menuList;
    private Context mContext;
    public Boolean Type;


    public ExpListAdapter(Context applicationContext, ArrayList<ArrayList<String>> subMenu, ArrayList<ArrayList<String>> subMenuDescription, ArrayList<String> menu,Boolean type) {
        mContext = applicationContext;
        mGroups = subMenu;
        menuList = menu;
        mGroupsDescription = subMenuDescription;
        Type = type;

    }

    public ExpListAdapter(Context applicationContext, ArrayList<ArrayList<String>> subMenu, ArrayList<String> menu,Boolean type) {
        mContext = applicationContext;
        mGroups = subMenu;
        menuList = menu;
        Type = type;
        mGroupsDescription = new ArrayList<ArrayList<String>>();

    }


    public ExpListAdapter(Context applicationContext,ArrayList<String> menu,Boolean type) {
        ArrayList<ArrayList<String>> subs=new ArrayList<ArrayList<String>>();
        mContext = applicationContext;

        menuList = menu;
        for (int z=0;z<menu.size();z++){
        subs.add(new ArrayList<String>());
        }
        mGroups = subs;
        Type = type;
        mGroupsDescription = new ArrayList<ArrayList<String>>();

    }


    @Override
    public int getGroupCount() {
        return menuList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {

            return mGroups.get(groupPosition).size();

    }

    @Override
    public Object getGroup(int groupPosition) {
        return menuList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mGroups.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    public String getChildById(int groupPosition,int childPosition) {
        return mGroups.get(groupPosition).get(childPosition);

    }

    public String getUserName(int groupPosition,int childPosition) {
        return mGroupsDescription.get(groupPosition).get(childPosition);

    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.group_view, null);
        }

        if (isExpanded){
            //Изменяем что-нибудь, если текущая Group раскрыта
        }
        else{
            //Изменяем что-нибудь, если текущая Group скрыта
        }

        TextView textGroup = (TextView) convertView.findViewById(R.id.textGroup);
        textGroup.setText(menuList.get(groupPosition));

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.child_view, null);
        }

        TextView textChild = (TextView) convertView.findViewById(R.id.textChild);
        TextView textChildDescription = (TextView) convertView.findViewById(R.id.textChildDescription);
        textChild.setText(mGroups.get(groupPosition).get(childPosition));
        if (!mGroupsDescription.isEmpty())
        textChildDescription.setText(mGroupsDescription.get(groupPosition).get(childPosition));

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}
