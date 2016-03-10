package com.informix.goverbook.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.informix.goverbook.R;
import com.informix.goverbook.fragments.AbstractTabFragment;
import com.informix.goverbook.fragments.FaveFragment;
import com.informix.goverbook.fragments.OrgsFragment;
import com.informix.goverbook.fragments.WorkersFragment;

import java.util.HashMap;
import java.util.Map;

public class TabsAdapter extends FragmentPagerAdapter {
    private static int NUM_ITEMS = 3;
    private int[] imageResId = { R.mipmap.ic_user_group_active, R.mipmap.ic_org_active, R.mipmap.ic_star_active,};


    private Map<Integer, AbstractTabFragment> tabs;
    private Context context;

    public TabsAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
        initTabsMap(context);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabs.get(position).getTitle();
    }

    @Override
    public Fragment getItem(int position) {
        return tabs.get(position);
    }

    @Override
    public int getCount() {
        return tabs.size();
    }



    public View getTabView(int position) {
        // Given you have a custom layout in `res/layout/custom_tab.xml` with a TextView and ImageView
        View v = LayoutInflater.from(context).inflate(R.layout.custom_tab, null);
        ImageView img = (ImageView) v.findViewById(R.id.tabIcon);
        img.setImageResource(imageResId[position]);
        return v;
    }

    private void initTabsMap(Context context) {
        tabs = new HashMap<>();
        tabs.put(0, WorkersFragment.getInstance(context));
        tabs.put(1, OrgsFragment.getInstance(context));
        tabs.put(2, FaveFragment.getInstance(context));
    }
}