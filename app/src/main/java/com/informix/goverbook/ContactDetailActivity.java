package com.informix.goverbook;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class  ContactDetailActivity extends AppCompatActivity implements View.OnClickListener{
    UserContact userContact;
    TextView tvPhone;
    TextView tvEmail;
    Intent intent;




    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);
        TextView tvFio = (TextView) findViewById(R.id.tvFio);
        tvPhone = (TextView) findViewById(R.id.tvPhone);
        TextView tvStatus = (TextView) findViewById(R.id.tvStatus);
        tvEmail = (TextView) findViewById(R.id.tvEmail);
        TextView tvIpPhone = (TextView) findViewById(R.id.tvIpPhone);
        Button btnDial = (Button) findViewById(R.id.btnDial);
        Button btnEmail = (Button) findViewById(R.id.btnEmail);



        DBHelper dbHelper =new DBHelper(this);
        intent=getIntent();
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        userContact =dbHelper.searchUserById(intent.getIntExtra("userid", 0), database);
        tvFio.setText(userContact.FIO);
        tvIpPhone.setText(userContact.CONTACTS);
        tvStatus.setText(userContact.getSTATUS());
        tvEmail.setText(userContact.getEMAIL());
        tvPhone.setText(userContact.getPHONE());

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



    }

    private String parseNumber(String text) {
        String num=text.replaceAll("-","");
        return num;
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
