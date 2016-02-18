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
import android.widget.ListView;
import android.widget.TextView;

import com.informix.goverbook.adapters.ExpListAdapter;
import com.informix.goverbook.adapters.TabsAdapter;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int LAYOUT = R.layout.activity_main;

    private Toolbar toolbar;
    private ViewPager viewPager;
    private EditText etSearch;
    Intent intent;
    DBHelper dbHelper;
    SQLiteDatabase database;
    ArrayList<UserContact> userContact;
    ArrayList<String> orgNames;
    ArrayList<String> orgTypes = new ArrayList<String>();
    ArrayList<Integer> orgTypesId = new ArrayList<Integer>();
    ListView searchResultFio;
    ExpandableListView searchResultOrg;
    String[][] list;
    ArrayList<ArrayList<String>> groups = new ArrayList<ArrayList<String>>();
    ExpListAdapter adapterForTypes;
    ExpListAdapter adapterForOrgs;
    SharedPreferences spref;
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

        tab1Actions();

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
        searchResultFio = (ListView) findViewById(R.id.searchFioResult);
        itemMenuUsers.DrawMenu(searchResultFio);
        searchResultFio.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                intent = new Intent(MainActivity.this, ContactDetailActivity.class);
                intent.putExtra("userid", userContact.get(position).getId());
                startActivity(intent);
            }
        });

    }



    private void ListOrg(SQLiteDatabase database) {

        String[][] orgListByType;
        ArrayList<Integer> orgId = new ArrayList<Integer>();


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


    public void onClickSettings(View view) {
        intent = new Intent(this, SettingsActivity.class);
        startActivityForResult(intent, 1);
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


    private void tab2Actions() {
        groups.clear();
        orgTypes.clear();
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
        ExpandableListView listView = (ExpandableListView) findViewById(R.id.searchOrgResult);
        list = dbHelper.SearchOrg(etSearch.getText().toString(),database);

        orgTypes.clear();
        for (int i = 0; i < (list[0].length); i++) {
            orgTypes.add(list[0][i]);
            orgTypesId.add(Integer.parseInt(list[1][i]));
        }

        adapterForOrgs = new ExpListAdapter(getApplicationContext(),orgTypes,false);
        listView.setAdapter(adapterForOrgs);

        searchResultOrg.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                String clickedOrgName = adapterForTypes.getGroup(groupPosition).toString();
//                intent = new Intent(MainActivity.this, OrgResultActivity.class);
//                intent.putExtra("orgName", clickedOrgName);
//                startActivity(intent);
                Log.d("MyLog",clickedOrgName);
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
                switch (tab.getPosition()){
                    case 0:
                        tab1Actions();
                        break;
                    case 1:
                        tab2Actions();
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



