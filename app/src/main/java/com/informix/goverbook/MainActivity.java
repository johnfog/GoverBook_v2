package com.informix.goverbook;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.informix.goverbook.activitys.AboutActivity;
import com.informix.goverbook.activitys.Howtoupdate;
import com.informix.goverbook.activitys.SettingsActivity;
import com.informix.goverbook.adapters.ExpListAdapter;
import com.informix.goverbook.adapters.FaveListAdapter;
import com.informix.goverbook.adapters.TabsAdapter;
import com.informix.goverbook.adapters.WorkersListAdapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int LAYOUT = R.layout.activity_main;

    private Toolbar toolbar;
    private ViewPager viewPager;
    private EditText etSearch;
    private Button btnClear;
    Intent intent;
    DBHelper dbHelper;
    SQLiteDatabase database;
    ArrayList<UserContact> userContact;
    ExpandableListView searchResultOrg;
    ArrayList<ArrayList<String>> orgNames = new ArrayList<>();
    ExpListAdapter adapterForOrgs;
    ArrayList<Integer> orgId = new ArrayList<>();
    ListView searchFioResult;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private Spinner spinner;
    String[] areaIds;
    List<String> areaNames;
    SharedPreferences mSettings;
    private int selectedArea;
    String tab1text ="";
    String tab2text ="";
    String tab1textPrevious = "";
    String tab2textPrevious = "a";


    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppDefault);
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);
        initDb();

        //Находим элементы
        dbHelper = new DBHelper(this);
        database = dbHelper.getReadableDatabase();
        etSearch = (EditText) findViewById(R.id.searchString);
        btnClear = (Button) findViewById(R.id.btn_clear);
        spinner = (Spinner) findViewById(R.id.areaSpinner);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout_main);

        //Инициализируем
        mSettings = PreferenceManager.getDefaultSharedPreferences(this);

        initTabs();
        initNavigationView();
        initToolbar();
        initClearButton();
        initSpinner();


        textChangedListener(etSearch);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


    }

    private void textChangedListener(EditText editText)
    {
        editText.addTextChangedListener(new TextWatcher() {


        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        if (!etSearch.getText().toString().equals("")) { //if edittext include text
            btnClear.setVisibility(View.VISIBLE);
        } else { //not include text
            btnClear.setVisibility(View.GONE);
        }
    }

        @Override
        public void afterTextChanged(Editable s) {

            if (viewPager.getCurrentItem() == 0) tab1text = etSearch.getText().toString();
            if (viewPager.getCurrentItem() == 1) tab2text = etSearch.getText().toString();

            if (s.length() > 2) {
            if (viewPager.getCurrentItem() == 0 && !tab1textPrevious.equals(tab1text))
            {
                startSearchFio();
                tab1textPrevious = tab1text;
            }
            if (viewPager.getCurrentItem() == 1 && !tab2textPrevious.equals(tab2text)) {
                startSearchOrg();
                tab2textPrevious = tab2text;
                Log.i("test", "tab2textPrevous = " + tab2textPrevious + "tab2text = " + tab2text);
            }

        }
            if (tab1text.equals("") && viewPager.getCurrentItem() == 0 && !tab1textPrevious.equals(tab1text)) tabActions();
            if (tab2text.equals("") && viewPager.getCurrentItem() == 1 && !tab2textPrevious.equals(tab2text)) tabActions();
    }
    });
    }

    private void initClearButton() {
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etSearch.setText("");
                if (viewPager.getCurrentItem() == 0) {
                    tab1text = "";
                    etSearch.setText(tab1text);
                    tabActions();
                }
                if (viewPager.getCurrentItem() == 1) {
                    tab2text = "";
                    etSearch.setText(tab2text);
                    tabActions();
                }
            }
        });
    }

    private void initSpinner() {
        String[][] list;
        String areaInSetting;

        list=dbHelper.areaGetter(database);
        areaNames = Arrays.asList(list[0]);
        areaIds=list[1];

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, areaNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        areaInSetting=mSettings.getString(getResources().getString(R.string.SELECTED_AREA), getString(R.string.default_city_name));
        selectedArea=areaNames.indexOf(areaInSetting);
        spinner.setSelection(selectedArea);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedArea = position;
                etSearch.setText("");
                tab1text = "";
                tab2text = "";

                if (viewPager.getCurrentItem() == 1) {
                    if (areaIds[selectedArea].equals(getString(R.string.default_city_id))) {
                        ListOrgMain(database);
                        Log.i("test", "Внутри слушателя спиннера");
                    } else
                        displayOrgOnArea();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    private void displayOrgOnArea() {

        String[][] orgListByType;

        orgListByType = dbHelper.ListOrgOnType("ALLID", areaIds[selectedArea], database);

        ArrayList<String> orgInArea= new ArrayList<>();
        orgInArea.addAll(Arrays.asList(orgListByType[0]));

        adapterForOrgs = new ExpListAdapter(getApplicationContext(),orgInArea,true);
        searchResultOrg.setAdapter(adapterForOrgs);

        if (searchResultOrg.getItemAtPosition(0).toString().equals(getResources().getString(R.string.nothing_found)))
        {
            searchResultOrg.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                @Override
                public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                    return false;
                }
            });
        }
        else {
            searchResultOrg.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                @Override
                public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                    if (adapterForOrgs.isOrg(groupPosition)) {

                        String clickedOrgName = adapterForOrgs.getGroup(groupPosition).toString();
                        intent = new Intent(MainActivity.this, OrgResultsActivity.class);
                        intent.putExtra("orgName", clickedOrgName);
                        startActivity(intent);
                    }

                    return false;
                }
            });

        }

}


    // Метод сворачиваня клавиатуры
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        initSpinner();

//        if (drawer.isDrawerOpen(GravityCompat.START))
//            drawer.closeDrawer(GravityCompat.START);


        if (viewPager.getCurrentItem()==2)
        {
            tab3Actions();
        }

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

        toolbar.setNavigationIcon(R.drawable.ic_menu_w);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(MainActivity.this);
                setupMenu();

            }
        });
    }

    private void initNavigationView() {

        navigationView = (NavigationView) findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_last:
                        viewPager.setCurrentItem(0);
                        etSearch.setText("");
                        tabActions();
                        drawer.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.nav_how_update:
                        intent = new Intent(MainActivity.this, Howtoupdate.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_settings:
                        intent = new Intent(MainActivity.this, SettingsActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_about:
                        intent = new Intent(MainActivity.this, AboutActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_update:
                        File mTargetFile = new File("/data/data/com.informix.goverbook/cache" + "/base.zip");
                        new UpdateDatabase(MainActivity.this, mTargetFile, "Качаю").execute("http://www.rcitsakha.ru/rcit/zz/base.zip");
                        break;
                }

                return false;
            }
        });
    }




    private void initTabs() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        TabsAdapter adapter = new TabsAdapter(this, getSupportFragmentManager());
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);


        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        viewPager.setCurrentItem(0);
                        etSearch.setText(tab1text);
                        //tabActions();
                        etSearch.setSelection(etSearch.getText().length());
                        break;
                    case 1:
                        viewPager.setCurrentItem(1);
                        etSearch.setText(tab2text);
                        if (areaIds[selectedArea].equals(getString(R.string.default_city_id))) {
                            //tabActions();
                        } else {
                            //tabActions();
                            displayOrgOnArea();
                        }
                        etSearch.setSelection(etSearch.getText().length());
                        break;
                    case 2:
                        viewPager.setCurrentItem(2);
                        tab3Actions();
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


        tabLayout.getTabAt(0).setIcon(getResources().getDrawable(R.drawable.ic_workers));
        tabLayout.getTabAt(1).setIcon(getResources().getDrawable(R.drawable.ic_org));
        tabLayout.getTabAt(2).setIcon(getResources().getDrawable(R.drawable.ic_fave));



    }

    private void startSearchOrg() {
        String[][] list;
        ArrayList<String> orgName = new ArrayList<>();
        ArrayList<Integer> orgNameId = new ArrayList<>();

        ExpandableListView listView = (ExpandableListView) findViewById(R.id.searchOrgResult);
        list = dbHelper.SearchOrg(etSearch.getText().toString(),areaIds[selectedArea],database);

        orgName.clear();
        for (int i = 0; i < (list[0].length); i++) {
            orgName.add(list[0][i]);
            orgNameId.add(Integer.parseInt(list[1][i]));
        }

        adapterForOrgs = new ExpListAdapter(getApplicationContext(),orgName,true);
        listView.setAdapter(adapterForOrgs);


        if (listView.getItemAtPosition(0).toString().equals("Ничего не найдено"))
        {
            searchResultOrg.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                @Override
                public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                    return false;
                }
            });

        }

    }

    private void setupMenu() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            drawer.openDrawer(GravityCompat.START);
        }


    }


    private void tabActions() {


        if (tab1text.equals("") && viewPager.getCurrentItem()== 0){
            listLastWorkers();
        }
        if (tab2text.equals("") && viewPager.getCurrentItem()== 1){
            ListOrgMain(database);
        }

        if (viewPager.getCurrentItem()== 1) {
            searchResultOrg.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                    String clickedOrgName = adapterForOrgs.getChildById(groupPosition, childPosition);
                    intent = new Intent(MainActivity.this, OrgResultsActivity.class);
                    intent.putExtra("orgName", clickedOrgName);
                    startActivity(intent);
                    return false;
                }
            });
        }
    }


//    private void tab2Actions() {
//
//        //etSearch.setText("");
//        etSearch.setText(tab2text);
//        if (tab2text.equals("")){
//            ListOrgMain(database);
//        }
//
//        etSearch.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//                if (!etSearch.getText().toString().equals("")) { //if edittext include text
//                    btnClear.setVisibility(View.VISIBLE);
//                } else { //not include text
//                    btnClear.setVisibility(View.GONE);
//                }
//
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                tab2text = etSearch.getText().toString();
//                if (s.length() > 2) {
//                    startSearchOrg();
//                }
//                // else ListOrgMain(database);
//                Log.i("edittext", "tab1text " + tab1text);
//                Log.i("edittext", "tab2text " + tab2text);
//
//            }
//        });
//
//
//        searchResultOrg.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
//            @Override
//            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
//                String clickedOrgName = adapterForOrgs.getChildById(groupPosition, childPosition);
//                intent = new Intent(MainActivity.this, OrgResultsActivity.class);
//                intent.putExtra("orgName", clickedOrgName);
//                startActivity(intent);
//                return false;
//            }
//        });
//
//    }

    private void tab3Actions() {
        ListFaveList();

    }

    public void startSearchFio(){
        userContact = dbHelper.searchByFio(etSearch.getText().toString(),areaIds[selectedArea], database);
        ItemMenuUsers itemMenuUsers = new ItemMenuUsers(userContact);
        searchFioResult = (ListView) findViewById(R.id.searchFioResult);
        itemMenuUsers.DrawMenu(searchFioResult);

        if (userContact.get(0).getFIO().equals(getString(R.string.nothing_found)))
        {
            searchFioResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                }
            });
        }
            else {
            searchFioResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    dbHelper.saveLast(userContact.get(position).FIO, userContact.get(position).STATUS, userContact.get(position).id, dbHelper);
                    intent = new Intent(MainActivity.this, ContactDetailActivity.class);
                    intent.putExtra("userid", userContact.get(position).getId());
                    startActivity(intent);
                }
            });
        }
    }

    private void ListOrgMain(SQLiteDatabase database) {
        String[][] list;
        ArrayList<String> orgTypes = new ArrayList<>();
        ArrayList<Integer> orgTypesId = new ArrayList<>();
        ArrayList<String> orgOnMain = new ArrayList<>();
        ArrayList<Integer> orgOnMainId = new ArrayList<>();
        String[][] orgListByType;
        ArrayList<String> inTypeOrgNames;


        //Добавляем Организации для главной страницы

        list = dbHelper.ListOrgOnMain(database);
        for (int i = 0; i < (list[0].length); i++) {
            orgOnMain.add(list[0][i]);
            orgOnMainId.add(Integer.parseInt(list[1][i]));
        }


        adapterForOrgs = new ExpListAdapter(getApplicationContext(),orgOnMain,true);

        // Добавляем Типы организаций

        list = dbHelper.ListOrgType(database);
        for (int i = 0; i < (list[0].length); i++) {
            orgTypes.add(list[0][i]);
            orgTypesId.add(Integer.parseInt(list[1][i]));
        }

        for (int i=0;i< (orgTypesId.size());i++) {
            orgListByType = dbHelper.ListOrgOnType(String.valueOf(orgTypesId.get(i)),"35", database);
            inTypeOrgNames= new ArrayList<>();

            for (int k = 0; k < (orgListByType[0].length); k++) {
                inTypeOrgNames.add(orgListByType[0][k]);
                orgId.add(Integer.parseInt(orgListByType[1][k]));
            }
            this.orgNames.add(inTypeOrgNames);
        }

        ExpListAdapter adapterForOrgs2 = new ExpListAdapter(getApplicationContext(),orgTypes,orgNames,false);

        adapterForOrgs.addAdapter(adapterForOrgs2);

        searchResultOrg = (ExpandableListView) findViewById(R.id.searchOrgResult);
        searchResultOrg.setAdapter(adapterForOrgs);
        searchResultOrg.setGroupIndicator(getResources().getDrawable(R.drawable.userliststate));


        if (searchResultOrg.getItemAtPosition(0).toString().equals("Ничего не найдено"))
        {
            searchResultOrg.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                @Override
                public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                    return false;
                }
            });

        }
        else {
            searchResultOrg.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                @Override
                public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                    if (adapterForOrgs.isOrg(groupPosition)) {

                        String clickedOrgName = adapterForOrgs.getGroup(groupPosition).toString();
                        intent = new Intent(MainActivity.this, OrgResultsActivity.class);
                        intent.putExtra("orgName", clickedOrgName);
                        startActivity(intent);
                    }

                    return false;
                }
            });
        }

        etSearch.setOnEditorActionListener(new EditText.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                startSearchOrg();
                hideSoftKeyboard(MainActivity.this);
                return actionId == EditorInfo.IME_ACTION_DONE;
            }
        });

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        int isDbUpdated = 0;
//        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
//            isDbUpdated = Integer.parseInt(data.getStringExtra("isDbUpdated"));
//        }
//
//    }


    public void listLastWorkers() {
        userContact = dbHelper.ListLast(database);
        searchFioResult = (ListView) findViewById(R.id.searchFioResult);

        String[] names = new String[userContact.size()];
        String[] orgs = new String[userContact.size()];

        for (int i = 0; i < userContact.size(); i++) {
            names[i] = userContact.get(i).getFIO();
            orgs[i] = userContact.get(i).getSTATUS();
        }

        WorkersListAdapter adapter1 = new WorkersListAdapter(searchFioResult.getContext(), names, orgs);
        searchFioResult.setAdapter(adapter1);

        searchFioResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, ContactDetailActivity.class);
                intent.putExtra("userid", userContact.get(position).getId());
                startActivity(intent);
            }
        });

    }


    private void ListFaveList() {
        final String[][] favelist;
        favelist = dbHelper.ListFave(database);

        ListView listView = (ListView) findViewById(R.id.faveList);
        FaveListAdapter faveListAdapter = new FaveListAdapter(this,favelist[0],favelist[1]);
        listView.setAdapter(faveListAdapter);

        if (listView.getItemAtPosition(0).toString().equals("Список пуст"))
        {
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                }
            });
        }
        else {
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

    }

}



