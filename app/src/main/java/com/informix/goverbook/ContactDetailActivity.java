package com.informix.goverbook;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class  ContactDetailActivity extends AppCompatActivity implements View.OnClickListener{
    private Toolbar toolbar;
    UserContact userContact;
    TextView tvPhone;
    TextView tvEmail;
    TextView tvOrg;
    Intent intent;
    DBHelper dbHelper;
    boolean saved;
    MenuItem faveItem;
    NavigationView navigationView;
    TextView tvPhoneDop;


    @Override
    protected void onCreate(Bundle savedInstanceState){

        setTheme(R.style.AppDefault);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);

        dbHelper = new DBHelper(this);
        tvPhone = (TextView) findViewById(R.id.tvPhone);
        TextView tvStatus = (TextView) findViewById(R.id.tvStatus);
        tvEmail = (TextView) findViewById(R.id.tvEmail);
        tvPhoneDop = (TextView) findViewById(R.id.tvPhoneDop);
        tvOrg = (TextView) findViewById(R.id.tvOrg);
        TextView tvIpPhone = (TextView) findViewById(R.id.tvIpPhone);
        ImageButton btnDial = (ImageButton) findViewById(R.id.btnDial);
        ImageButton btnEmail = (ImageButton) findViewById(R.id.btnEmail);
        DBHelper dbHelper =new DBHelper(this);
        intent=getIntent();
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        userContact =dbHelper.searchUserById(intent.getIntExtra("userid", 0), database);
        tvIpPhone.setText(userContact.CONTACTS);

            if (userContact.CONTACTS.equals("-") || userContact.CONTACTS.equals("")) {
                findViewById(R.id.linearLayoutContacts).setVisibility(View.GONE);
            } else {
                tvIpPhone.setText(userContact.CONTACTS);
            }

        tvStatus.setText(userContact.getSTATUS());
        tvEmail.setText(userContact.getEMAIL());
        tvOrg.setText(userContact.getORG());
        String s = userContact.getEMAIL();
        if (s.equals("")){
            findViewById(R.id.linearLayoutEMAIL).setVisibility(View.GONE);
        }

        //parse Phone Number
        s = userContact.getPHONE();

        if (s.equals("") || s.toCharArray()[0] == 'ф' || s.toCharArray()[0] == 'Ф')
        {
            tvPhone.setText("");
            findViewById(R.id.linearLayoutPhone).setVisibility(View.GONE);
            if (!s.equals("")) {
                findViewById(R.id.linearLayoutPhoneDop).setVisibility(View.VISIBLE);}
            tvPhoneDop.setText(s);
        }
        else {
            s = s.replaceAll(" \\(", "(");
            s = s.replaceAll("\\) ", ")");
            String[] str = s.split(" ", 2);
            str[0] = str[0].replaceAll("(?!-)(?!\\+)(?![()])\\p{P}", "");
            tvPhone.setText(str[0]);
            if (str.length > 1) {
                tvPhoneDop.setText(str[1]);
            }

            if (tvPhoneDop.getText() != null && tvPhoneDop.getText().length() != 0 && !tvPhoneDop.getText().equals("")) {
                findViewById(R.id.linearLayoutPhoneDop).setVisibility(View.VISIBLE);
            }
        }

        TextView tvDetailFio = (TextView) findViewById(R.id.tvDetailFio);
        tvDetailFio.setText(userContact.FIO);
        initToolbar();
        initNavigationView();

        if (tvPhone.getText().length()>0) {
            btnDial.setOnClickListener(this);
        }
        else
            btnDial.setEnabled(false);

        if (tvEmail.getText().length()>0) {
            btnEmail.setOnClickListener(this);
        }
        else
            btnEmail.setEnabled(false);

        supportInvalidateOptionsMenu();


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


    private String parseNumber(String text) {
        String num=text.replaceAll("-", "");
        return num;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_detail_contact_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        this.faveItem= menu.findItem(R.id.fave);

        saved=dbHelper.getItemSaved(userContact.FIO,dbHelper);

        if (saved){
            faveItem.setIcon(R.mipmap.ic_star_active);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        fragment.setHasOptionsMenu(true);
        super.onAttachFragment(fragment);
    }

    private void initToolbar() {

        toolbar = (Toolbar)findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

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

    private void setupMenu() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_contact_detail);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            drawer.openDrawer(GravityCompat.START);
        }


    }


    private void addFave()

    {
        Toast toast;

        if (saved) {
            toast = Toast.makeText(getApplicationContext(),
                    "Контакт удален из избранных", Toast.LENGTH_SHORT);
                    faveItem.setIcon(R.mipmap.ic_star_outline);
                    dbHelper.deleteFaveContact(userContact.id, dbHelper);
                    saved=false;

        }else {
            toast = Toast.makeText(getApplicationContext(),
                    "Контакт был добавлен в избранные", Toast.LENGTH_SHORT);
            dbHelper.saveFave(userContact.FIO, DBHelper.TYPE_WORKER, userContact.id, dbHelper);
            faveItem.setIcon(R.mipmap.ic_star_active);
            saved=true;

        }

        toast.show();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnDial:
                String number = parseNumber(tvPhone.getText().toString());
                Uri call = Uri.parse("tel:" + number);
                Intent intent = new Intent(Intent.ACTION_DIAL, call);
                startActivity(intent);
            break;
            case R.id.btnEmail:
                Uri uri = Uri.fromParts("mailto", tvEmail.getText().toString(), null);
                intent = new Intent(Intent.ACTION_SENDTO, uri);
                intent.putExtra(Intent.EXTRA_SUBJECT, "Тема");
                intent.putExtra(Intent.EXTRA_TEXT, "Текст");
                startActivity(Intent.createChooser(intent, "Send Email"));
            break;
        }
    }
}
