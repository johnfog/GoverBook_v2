package com.informix.goverbook;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.informix.goverbook.adapters.ExpListAdapter;
import com.informix.goverbook.adapters.FaveListAdapter;
import com.informix.goverbook.adapters.TabsAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final int LAYOUT = R.layout.activity_main;

    private Toolbar toolbar;
    private ViewPager viewPager;


    private EditText etSearch;
    Intent intent;
    DBHelper dbHelper;
    SQLiteDatabase database;
    ArrayList<UserContact> userContact;
    ExpandableListView searchResultOrg;
    ArrayList<ArrayList<String>> groups = new ArrayList<ArrayList<String>>();
    ExpListAdapter adapterForTypes;
    ExpListAdapter adapterForOrgs;
    ListView searchFioResult;
    SharedPreferences spref;
    ArrayList<Integer> orgId = new ArrayList();


    private static final String REAL_AREA = "REAL_AREA";
    public static final String YOUR_AREA_POSITION = "YOUR_AREA_POSITION";
    private static final String YOUR_AREA_ID = "YOUR_AREA_ID";



    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppDefault);
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);
        initTabs();
        initNavigationView();
        initToolbar();
        initDb();

        dbHelper = new DBHelper(this);
        database = dbHelper.getReadableDatabase();
        etSearch = (EditText) findViewById(R.id.searchString);
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        tab1Actions();
        viewPager.setCurrentItem(0);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        int isDbUpdated = 0;
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            isDbUpdated = Integer.parseInt(data.getStringExtra("isDbUpdated"));
        }

    }

    public void startSearchFio(){
        userContact = dbHelper.searchByFio(etSearch.getText().toString(), database);
        ItemMenuUsers itemMenuUsers = new ItemMenuUsers(userContact);
        searchFioResult = (ListView) findViewById(R.id.searchFioResult);
        itemMenuUsers.DrawMenu(searchFioResult);
        searchFioResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dbHelper.saveLast(userContact.get(position).FIO,userContact.get(position).STATUS,userContact.get(position).id,dbHelper);
                intent = new Intent(MainActivity.this, ContactDetailActivity.class);
                intent.putExtra("userid", userContact.get(position).getId());
                startActivity(intent);
            }
        });

    }


    public void listLastWorkers(){
        List<Map<String, String>> data = new ArrayList<Map<String, String>>();
        userContact = dbHelper.ListLast(database);

        for (int i=0;i<userContact.size();i++) {
            Map<String, String> datum = new HashMap<String, String>(2);
            datum.put("fio", userContact.get(i).FIO);
            datum.put("status", userContact.get(i).STATUS);
            data.add(datum);
        }



        SimpleAdapter adapter1 = new SimpleAdapter(getBaseContext(), data, android.R.layout.simple_list_item_2,
                new String[] {"fio", "status"},
                new int[] {android.R.id.text1,
                        android.R.id.text2});

        searchFioResult = (ListView) findViewById(R.id.searchFioResult);
        searchFioResult.setAdapter(adapter1);

        searchFioResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                intent = new Intent(MainActivity.this, ContactDetailActivity.class);
                intent.putExtra("userid", userContact.get(position).getId());
                startActivity(intent);
            }
        });

    }



    private void ListOrg(SQLiteDatabase database) {
        String[][] list;
        ArrayList<String> orgTypes = new ArrayList<String>();
        ArrayList<Integer> orgTypesId = new ArrayList<Integer>();
        String[][] orgListByType;
        ArrayList<String> orgNames;

        try {
        list = dbHelper.ListOrg(database);
        for (int i = 0; i < (list[0].length); i++) {
            orgTypes.add(list[0][i]);
            orgTypesId.add(Integer.parseInt(list[1][i]));
        }

        for (int i=0;i< (orgTypesId.size());i++) {
            orgListByType = dbHelper.ListOrgOnId(String.valueOf(orgTypesId.get(i)),database);
            orgNames= new ArrayList<String>();

            for (int k = 0; k < (orgListByType[0].length); k++) {
                orgNames.add(orgListByType[0][k]);
                orgId.add(Integer.parseInt(orgListByType[1][k]));
            }
            groups.add(orgNames);
        }

        adapterForTypes = new ExpListAdapter(getApplicationContext(), groups,orgTypes,true);
        searchResultOrg = (ExpandableListView) findViewById(R.id.searchOrgResult);
        searchResultOrg.setAdapter(adapterForTypes);
        searchResultOrg.setGroupIndicator(getResources().getDrawable(R.drawable.userliststate));


        etSearch.setOnEditorActionListener(new EditText.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                startSearchOrg();
                hideSoftKeyboard(MainActivity.this);
                return actionId == EditorInfo.IME_ACTION_DONE;
            }
        });
         } catch (Exception e) {}
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


    }


    // Метод сворачиваня клавиатуры
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    private void tab1Actions() {
        etSearch.setText("");

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 2) {
                    startSearchFio();
                }
            }
        });

    }
    
    private void tab3Actions() {
        ListFaveList();
        
    }

    private void ListFaveList() {
        final String[][] favelist;
        favelist =dbHelper.ListFave(database);

        ListView listView = (ListView) findViewById(R.id.faveList);
        FaveListAdapter faveListAdapter = new FaveListAdapter(this,favelist[0],favelist[1]);
        listView.setAdapter(faveListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (favelist[0][position].equals(DBHelper.TYPE_ORG)) {
                    String clickedOrgName = parent.getItemAtPosition(position).toString();
                    intent = new Intent(MainActivity.this, OrgResultsActivity.class);
                    intent.putExtra("orgName", clickedOrgName);
                    startActivity(intent);
                }

                if (favelist[0][position].equals(DBHelper.TYPE_WORKER)) {
                    int clickedId = Integer.parseInt(favelist[2][position]);
                    intent = new Intent(MainActivity.this, ContactDetailActivity.class);
                    intent.putExtra("userid", clickedId);
                    startActivity(intent);
                }

            }
        });

    }

    private void tab2Actions() {
        groups.clear();
        ListOrg(database);
        etSearch.setText("");

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 2) {
                    startSearchOrg();
                }
            }
        });

        searchResultOrg.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return false;
            }
        });

        searchResultOrg.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                String clickedOrgName = adapterForTypes.getChildById(groupPosition, childPosition);
                intent = new Intent(MainActivity.this, OrgResultsActivity.class);
                intent.putExtra("orgName", clickedOrgName);
                startActivity(intent);
                return false;
            }
        });

    }


    private void startSearchOrg() {
        String[][] list;
        ArrayList<String> orgTypes = new ArrayList<String>();
        ArrayList<Integer> orgTypesId = new ArrayList<Integer>();

        ExpandableListView listView = (ExpandableListView) findViewById(R.id.searchOrgResult);
        list = dbHelper.SearchOrg(etSearch.getText().toString(),database);

        orgTypes.clear();
        for (int i = 0; i < (list[0].length); i++) {
            orgTypes.add(list[0][i]);
            orgTypesId.add(Integer.parseInt(list[1][i]));
        }

        adapterForOrgs = new ExpListAdapter(getApplicationContext(),orgTypes,false);
        listView.setAdapter(adapterForOrgs);

        listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                String clickedOrgName = adapterForOrgs.getGroup(groupPosition).toString();
                intent = new Intent(MainActivity.this, OrgResultsActivity.class);
                intent.putExtra("orgName", clickedOrgName);
                startActivity(intent);
                return false;
            }
        });
    }


    private void initDb() {
        try {
            new MoveDatabaseFirstRun(getApplicationContext()).createDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("GoverBook");

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivityForResult(intent, 1);

                return false;
            }
        });
        toolbar.inflateMenu(R.menu.toolbar_menu);
    }

    private void initNavigationView() {



    }


    private void initTabs() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        TabsAdapter adapter = new TabsAdapter(this, getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);


        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        viewPager.setCurrentItem(0);
                        tab1Actions();
                        toolbar.setTitle("Сотрудники");
                        listLastWorkers();
                        break;
                    case 1:
                        viewPager.setCurrentItem(1);
                        tab2Actions();
                        toolbar.setTitle("Организации");
                        break;
                    case 2:
                        viewPager.setCurrentItem(2);
                        tab3Actions();
                        toolbar.setTitle("Избранное");
                        break;

                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }

}



