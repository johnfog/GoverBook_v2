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

    private ArrayList<ArrayList<String>> childName;
    private ArrayList<ArrayList<String>> childDescription;
    private ArrayList<String> groupName;
    private ArrayList<Boolean> groupIsOrg=new ArrayList<Boolean>();
    private Context mContext;

    public ExpListAdapter(Context applicationContext, ArrayList<String> groupNames, ArrayList<ArrayList<String>> childNames, ArrayList<ArrayList<String>> childDescription,Boolean groupIsOrg) {
        mContext = applicationContext;
        this.childName = childNames;
        this.groupName = groupNames;
        this.childDescription = childDescription;
        for (int i=0;i<groupNames.size();i++)
        this.groupIsOrg.add(groupIsOrg);

    }

    public ExpListAdapter(Context applicationContext,ArrayList<String> groupNames, ArrayList<ArrayList<String>> childNames,Boolean groupIsOrg) {
        mContext = applicationContext;
        this.childName = childNames;
        this.groupName = groupNames;
        childDescription = new ArrayList<ArrayList<String>>();
        for (int i=0;i<groupNames.size();i++)
            this.groupIsOrg.add(groupIsOrg);
    }

    public ExpListAdapter(Context applicationContext,ArrayList<String> groupNames,Boolean groupIsOrg) {
        ArrayList<ArrayList<String>> subs=new ArrayList<ArrayList<String>>();
        mContext = applicationContext;

        groupName = groupNames;
        for (int z=0;z<groupNames.size();z++){
        subs.add(new ArrayList<String>());
        }
        childName = subs;
        childDescription = new ArrayList<ArrayList<String>>();
        for (int i=0;i<groupName.size();i++)
            this.groupIsOrg.add(groupIsOrg);

    }



    public ArrayList<String> getGroupName() {
        return groupName;
    }

    public ArrayList<Boolean> getGroupIsOrg() {
        return groupIsOrg;
    }

    public ArrayList<ArrayList<String>> getChildName() {
        return childName;
    }

    public ArrayList<ArrayList<String>> getChildDescription() {
        return childDescription;
    }



    @Override
    public int getGroupCount() {
        return groupName.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {

            return childName.get(groupPosition).size();

    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupName.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childName.get(groupPosition).get(childPosition);
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
        return childName.get(groupPosition).get(childPosition);

    }

    public String getUserName(int groupPosition,int childPosition) {
        return childDescription.get(groupPosition).get(childPosition);

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
        textGroup.setText(groupName.get(groupPosition));

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
        textChild.setText(childName.get(groupPosition).get(childPosition));
        if (!childDescription.isEmpty())
        textChildDescription.setText(childDescription.get(groupPosition).get(childPosition));

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


    public boolean isOrg(int position){
        return groupIsOrg.get(position);
    }


    public void addAdapter(ExpListAdapter adapter){
        groupName.addAll(adapter.getGroupName());
        groupIsOrg.addAll(adapter.getGroupIsOrg());
        childName.addAll(adapter.getChildName());
        childDescription.addAll(adapter.getChildDescription());

    }

}
