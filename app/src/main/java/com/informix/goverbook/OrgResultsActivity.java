package com.informix.goverbook;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
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
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppDefault);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_org_results);

        dbHelper = new DBHelper(this);
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        searchResult = (ExpandableListView) findViewById(R.id.orgResultList);
        TextView orgName = (TextView) findViewById(R.id.tvOrgName);
        intent = getIntent();
        clickedOrgName = intent.getStringExtra("orgName");
        orgName.setText(clickedOrgName);
        initToolbar();


        org=dbHelper.searchOrgByName(clickedOrgName, database);
        org.DrawOrgContact(searchResult, getApplicationContext());
        searchResult.setGroupIndicator(getResources().getDrawable(R.drawable.userliststate));

        groupCount=searchResult.getCount();


        for (int i=0;i<searchResult.getCount();i++) {
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

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu=menu;
        return super.onCreateOptionsMenu(menu);
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.toolbar_detail_menu);
        saved=dbHelper.getItemSaved(clickedOrgName,dbHelper);


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
                    case R.id.search:
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

        dbHelper.saveFave(clickedOrgName,DBHelper.TYPE_ORG,0,dbHelper);

        if (saved) {
            toast = Toast.makeText(getApplicationContext(),
                    "Организация была добавлена в Избранные", Toast.LENGTH_SHORT);
        }
            else
        {
            toast = Toast.makeText(getApplicationContext(),
                    "Организация уже в Избранных", Toast.LENGTH_SHORT);
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
        intent = new Intent(OrgResultsActivity.this, SettingsActivity.class);
        startActivityForResult(intent, 1);
    }

    // Метод сворачиваня клавиатуры
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }
}
