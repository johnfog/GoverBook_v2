package com.informix.goverbook;

import android.content.Context;
import android.widget.ExpandableListView;

import com.informix.goverbook.adapters.ExpListAdapter;

import java.util.ArrayList;

/**
 * Created by adm on 04.02.2016.
 */
public class OrgContact {
    ArrayList<ArrayList<UserContact>> userContacts;
    ArrayList<String> departs;
    ArrayList<ArrayList<String>> formingStatus = new ArrayList<ArrayList<String>>();
    ArrayList<ArrayList<String>> formingFIO = new ArrayList<ArrayList<String>>();
    ArrayList<ArrayList<Integer>> formingUsersID = new ArrayList<ArrayList<Integer>>();
    ExpListAdapter adapter;

    public OrgContact(ArrayList<ArrayList<UserContact>> userContacts, ArrayList<String> departs) {
        this.userContacts = userContacts;
        this.departs = departs;
    }


    public Integer GetUserIdOnOrg(Integer departposition,Integer userposition){
        return formingUsersID.get(departposition).get(userposition);
    }

    public void DrawOrgContact(ExpandableListView listView,Context context){
        for (ArrayList<UserContact> luser:userContacts){
            ArrayList<String> str=new ArrayList<String>();
            ArrayList<String> str2=new ArrayList<String>();
            ArrayList<Integer> id = new ArrayList<Integer>();

            for (UserContact kuser:luser){
                str.add(kuser.FIO);
                str2.add(kuser.STATUS);
                id.add(kuser.getId());

            }
            formingFIO.add(str);
            formingStatus.add(str2);
            formingUsersID.add(id);
        }

        adapter = new ExpListAdapter(context,departs, formingStatus,formingFIO,false);
        listView.setAdapter(adapter);

    }
}
