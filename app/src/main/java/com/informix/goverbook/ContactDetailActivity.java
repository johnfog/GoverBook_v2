package com.informix.goverbook;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
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
    private static final int LAYOUT = R.style.AppDefault;
    private Toolbar toolbar;
    UserContact userContact;
    TextView tvPhone;
    TextView tvEmail;
    Intent intent;
    DBHelper dbHelper;
    boolean saved;
    MenuItem faveItem;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_detail_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        this.faveItem= menu.findItem(R.id.fave);

        saved=dbHelper.getItemSaved(userContact.FIO,dbHelper);

        if (saved){
            faveItem.setIcon(R.mipmap.ic_star);
        }
        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState){

        setTheme(LAYOUT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);

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

        dbHelper = new DBHelper(this);
        tvPhone = (TextView) findViewById(R.id.tvPhone);
        TextView tvStatus = (TextView) findViewById(R.id.tvStatus);
        tvEmail = (TextView) findViewById(R.id.tvEmail);
        TextView tvIpPhone = (TextView) findViewById(R.id.tvIpPhone);
        ImageButton btnDial = (ImageButton) findViewById(R.id.btnDial);
        ImageButton btnEmail = (ImageButton) findViewById(R.id.btnEmail);
        DBHelper dbHelper =new DBHelper(this);
        intent=getIntent();
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        userContact =dbHelper.searchUserById(intent.getIntExtra("userid", 0), database);
        tvIpPhone.setText(userContact.CONTACTS);
        tvStatus.setText(userContact.getSTATUS());
        tvEmail.setText(userContact.getEMAIL());
        tvPhone.setText(userContact.getPHONE());
        TextView tvDetailFio = (TextView) findViewById(R.id.tvDetailFio);
        tvDetailFio.setText(userContact.FIO);
        initToolbar();

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

//        ActivityCompat.invalidateOptionsMenu(this);
//        invalidateOptionsMenu();
        supportInvalidateOptionsMenu();


    }


    private String parseNumber(String text) {
        String num=text.replaceAll("-", "");
        return num;
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        fragment.setHasOptionsMenu(true);
        super.onAttachFragment(fragment);
    }

    private void initToolbar() {


        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
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
            faveItem.setIcon(R.mipmap.ic_star);
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
