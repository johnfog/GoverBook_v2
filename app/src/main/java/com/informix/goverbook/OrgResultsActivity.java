package com.informix.goverbook;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ExpandableListView;

public class OrgResultsActivity extends AppCompatActivity {
    private Toolbar toolbar;
    DBHelper dbHelper;
    OrgContact org;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppDefault);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_org_results);

        dbHelper = new DBHelper(this);
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        ExpandableListView searchResult = (ExpandableListView) findViewById(R.id.orgResultList);
        intent = getIntent();
        String clickedOrgName = intent.getStringExtra("orgName");
        initToolbar(clickedOrgName);
        org=dbHelper.searchOrgByName(clickedOrgName, database);
        org.DrawOrgContact(searchResult, getApplicationContext());
        searchResult.setGroupIndicator(getResources().getDrawable(R.drawable.userliststate));

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

    private void initToolbar(String orgName) {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(orgName);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return false;
            }
        });
        toolbar.inflateMenu(R.menu.toolbar_menu);
    }

    // Метод сворачиваня клавиатуры
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }
}
