package com.informix.goverbook;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

public class OrgResultsActivity extends AppCompatActivity {
    private Toolbar toolbar;
    DBHelper dbHelper;
    OrgContact org;
    Intent intent;
    ExpandableListView searchResult;
    boolean expanded = false;
    int groupCount;
    String clickedOrgName;
    boolean saved;
    MenuItem faveItem;
    NavigationView navigationView;
    SharedPreferences mSettings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppDefault);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_org_results);


        mSettings = PreferenceManager.getDefaultSharedPreferences(this);
        expanded=mSettings.getBoolean("always_expand",false);

        dbHelper = DBHelper.getInstance(this);
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        searchResult = (ExpandableListView) findViewById(R.id.orgResultList);
        TextView orgName = (TextView) findViewById(R.id.tvOrgName);
        intent = getIntent();
        clickedOrgName = intent.getStringExtra("orgName");
        orgName.setText(clickedOrgName);

        org=dbHelper.searchOrgByName(clickedOrgName, database);
        org.DrawOrgContact(searchResult, getApplicationContext());
        searchResult.setGroupIndicator(getResources().getDrawable(R.drawable.userliststate));

        groupCount=searchResult.getCount();

        for (int i=0;i<groupCount;i++) {
            if (searchResult.getItemAtPosition(i).equals("Отдел не указан")) {
                searchResult.expandGroup(i);
            }
        }


        searchResult.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Intent intent = new Intent(OrgResultsActivity.this, ContactDetailActivity.class);
                intent.putExtra("userid", org.GetUserIdOnOrg(groupPosition, childPosition));
                startActivity(intent);
                return false;
            }
        });

        initToolbar();
        initNavigationView();
        supportInvalidateOptionsMenu();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

    }

    private void initNavigationView() {
        navigationView = (NavigationView) findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_settings:
                        break;
                }
                return false;
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_detail_org_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        this.faveItem= menu.findItem(R.id.fave);

        saved=dbHelper.getItemSaved(clickedOrgName,dbHelper);

        if (saved){
            faveItem.setIcon(R.mipmap.ic_star_active);
        }

        //Обработчик настроек пользователя

        if (expanded)
            expandItems();
        else
            collapseItems();

        return super.onPrepareOptionsMenu(menu);
    }


    private void initToolbar() {

        saved=dbHelper.getItemSaved(clickedOrgName,dbHelper);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.expand:
                        if (expanded)
                            collapseItems();
                        else
                            expandItems();
                        break;
                    case R.id.navbar_menu:
                        setupMenu();
                        break;
                    case R.id.fave:
                        addFave();
                        break;

                }
                return false;
            }
        });



    }

    private void addFave() {
        Toast toast;

        if (saved) {
            toast = Toast.makeText(getApplicationContext(),
                    "Контакт удален из избранных", Toast.LENGTH_SHORT);
            faveItem.setIcon(R.mipmap.ic_star_outline);
            dbHelper.deleteFaveOrg(clickedOrgName,dbHelper);
            saved=false;

        }else {
            toast = Toast.makeText(getApplicationContext(),
                    "Контакт был добавлен в избранные", Toast.LENGTH_SHORT);
            dbHelper.saveFave(clickedOrgName, DBHelper.TYPE_ORG, 0, dbHelper);
            faveItem.setIcon(R.mipmap.ic_star_active);
            saved=true;

        }
        toast.show();
    }

    private void collapseItems() {
        for (int i=0;i<groupCount;i++) {
            try {
                searchResult.collapseGroup(i);
            }
            catch(Exception e) {

        }

    }

        toolbar.getMenu().getItem(0).setIcon(R.mipmap.ic_chevron_double_down);
        expanded=false;
    }


    private void expandItems() {

        for (int i=0;i<groupCount;i++) {
            try {
                searchResult.expandGroup(i);
            }
            catch(Exception e) {}
        }
        toolbar.getMenu().getItem(0).setIcon(R.mipmap.ic_chevron_double_up);
        expanded=true;
    }

    private void setupMenu() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_org_result);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            drawer.openDrawer(GravityCompat.START);
        }
    }

    // Метод сворачиваня клавиатуры
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }
}
