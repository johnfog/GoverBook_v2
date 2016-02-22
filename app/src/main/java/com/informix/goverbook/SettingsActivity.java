package com.informix.goverbook;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.informix.goverbook.DBHelper;import com.informix.goverbook.Decompress;import com.informix.goverbook.DownloadTask;import com.informix.goverbook.R;import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;import java.lang.Character;import java.lang.Exception;import java.lang.Override;import java.lang.String;import java.lang.StringBuilder;import java.lang.Void;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    DBHelper dbHelper;
    Button btnUpdatedb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        btnUpdatedb = (Button) findViewById(R.id.btnUpdatedb);
        Button readDB = (Button) findViewById(R.id.readDB);
        btnUpdatedb.setOnClickListener(this);
        readDB.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        dbHelper = new DBHelper(this);
        SQLiteDatabase database = dbHelper.getWritableDatabase();


        switch (v.getId()) {
            case R.id.readDB:
                Cursor cursor = database.query(DBHelper.TABLE_USERS, null, null, null, null, null, null);

                if (cursor.moveToFirst()) {
                    int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
                    int FIOIndex = cursor.getColumnIndex(DBHelper.KEY_FIO);
                    int STATUSIndex = cursor.getColumnIndex(DBHelper.KEY_STATUS);
                    int PHONEIndex = cursor.getColumnIndex(DBHelper.KEY_PHONE);
                    int CONTACTSIndex = cursor.getColumnIndex(DBHelper.KEY_CONTACTS);
                    int EMAILIndex = cursor.getColumnIndex(DBHelper.KEY_EMAIL);
                    int DEPARTIDIndex = cursor.getColumnIndex(DBHelper.KEY_DEPARTID);
                    int ORGIDIDIndex = cursor.getColumnIndex(DBHelper.KEY_ORGID);
                    int SORTING = cursor.getColumnIndex(DBHelper.KEY_SORTING);


                    do {
                        Log.d("mLog", "ID = " + cursor.getInt(idIndex) + " ФИО:" + cursor.getString(FIOIndex) + " Должность:" + cursor.getString(STATUSIndex) + " Телефон:" + cursor.getString(PHONEIndex) +
                                " IPТелефон:" + cursor.getString(CONTACTSIndex) + " Электронка:" + cursor.getString(EMAILIndex));

                    } while (cursor.moveToNext());
                } else
                    Log.d("mLog", "0 rows");

                cursor.close();
                break;
            case R.id.btnUpdatedb:
                database.delete(DBHelper.TABLE_USERS, null, null);
                database.delete(DBHelper.TABLE_ORG, null, null);
                database.delete(DBHelper.TABLE_AREAS, null, null);
                database.delete(DBHelper.TABLE_OTYPE, null, null);
                database.delete(DBHelper.TABLE_DEPART, null, null);


                //new Decompress().unzip(getCacheDir() + "/base.zip", getCacheDir() + "");

                new ParseTask(SettingsActivity.this, "Пересоздаю базу").execute();


        }
        dbHelper.close();
    }


    private class ParseTask extends AsyncTask<Void, Void, String> {

        String resultJson;
        private ProgressDialog mPDialog;
        private Context mContext;

        public ParseTask(Context context, String dialogMessage) {
            this.mContext = context;

            mPDialog = new ProgressDialog(context);

            mPDialog.setMessage(dialogMessage);
            mPDialog.setIndeterminate(true);
            mPDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mPDialog.setCancelable(true);

            //final ParseTask me = this;
//            mPDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
//                @Override
//                public void onCancel(DialogInterface dialog) {
//                    me.cancel(true);
//                }
//            });
            Log.i("ParseTask", "Constructor done");
        }


        //Создаем запрос и получаем JSON ответ для нашиих баз

        protected void onPreExecute() {
            File destinationFile = new File(getCacheDir() + "/base.zip");
            new DownloadTask(SettingsActivity.this, destinationFile, "Качаю", getCacheDir().toString()).execute("http://www.rcitsakha.ru/rcit/zz/base.zip");
            //mPDialog.show();
        }

        protected String doInBackground(Void... params) {
            new Decompress().unzip(getCacheDir() + "/base.zip", getCacheDir() + "");
            resultJson = getJSON(getCacheDir() + "/base.json");
            return resultJson;
        }

        protected void onPostExecute(String strJson) {
            JSONObject dataJsonObj = null;
            try {
                dataJsonObj = new JSONObject(resultJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            SQLiteDatabase database = dbHelper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();

            database.beginTransaction();
            try {
                dataJsonObj = new JSONObject(resultJson);

                //Заполняем базу Пользователей s_users
                JSONArray usersArray = dataJsonObj.getJSONArray("users");
                for (int i = 0; i < usersArray.length(); i++) {
                    JSONObject jsonuser = usersArray.getJSONObject(i);
                    contentValues.put(DBHelper.KEY_ID, jsonuser.getInt("ID"));
                    contentValues.put(DBHelper.KEY_FIO, modFioString(jsonuser.getString("FIO")));
                    contentValues.put(DBHelper.KEY_STATUS, jsonuser.getString("STATUS"));
                    contentValues.put(DBHelper.KEY_CONTACTS, jsonuser.getString("CONTACTS"));
                    contentValues.put(DBHelper.KEY_PHONE, jsonuser.getString("PHONE"));
                    contentValues.put(DBHelper.KEY_EMAIL, jsonuser.getString("EMAIL"));
                    if ((jsonuser.getString("DEPARTID")) != "null") {
                        contentValues.put(DBHelper.KEY_DEPARTID, jsonuser.getInt("DEPARTID"));
                    } else contentValues.put(DBHelper.KEY_DEPARTID, "");
                    contentValues.put(DBHelper.KEY_ORGID, jsonuser.getInt("ORGID"));
                    contentValues.put(DBHelper.KEY_SORTING, jsonuser.getInt("SORTING"));
                    database.insert(DBHelper.TABLE_USERS, null, contentValues);

                }
                database.setTransactionSuccessful();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            finally {
                database.endTransaction();
            }

                contentValues.clear();

            database.beginTransaction();
            try {
                //Заполняем базу Пользователей s_org
                JSONArray orgsArray = dataJsonObj.getJSONArray("orgs");

                // 2. перебираем и выводим контакты каждого
                for (int i = 0; i < orgsArray.length(); i++) {
                    JSONObject jsonorgs = orgsArray.getJSONObject(i);
                    contentValues.put(DBHelper.KEY_ID, jsonorgs.getInt("ID"));
                    //Удаляем амперсанды
                    contentValues.put(DBHelper.KEY_COMPANY, jsonorgs.getString("COMPANY").replaceAll("&quot;","\'"));
                    contentValues.put(DBHelper.KEY_LOWERCOMPANY, modOrgString(jsonorgs.getString("COMPANY")));
                    contentValues.put(DBHelper.KEY_ADRES, jsonorgs.getString("ADRES"));
                    if ((jsonorgs.getString("AREAID")) != "null") {
                        contentValues.put(DBHelper.KEY_AREAID, jsonorgs.getInt("AREAID"));
                    } else contentValues.put(DBHelper.KEY_AREAID, "");

                    contentValues.put(DBHelper.KEY_DESCR, jsonorgs.getString("DESCR"));

                    if ((jsonorgs.getString("TYPEID")) != "null") {
                        contentValues.put(DBHelper.KEY_TYPEID, jsonorgs.getInt("TYPEID"));
                    } else contentValues.put(DBHelper.KEY_TYPEID, "");
                    database.insert(DBHelper.TABLE_ORG, null, contentValues);

                }
                database.setTransactionSuccessful();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            finally {
                database.endTransaction();
            }

                contentValues.clear();

            database.beginTransaction();
            try {
                //Заполняем базу Пользователей s_areas
                JSONArray areasArray = dataJsonObj.getJSONArray("areas");
                // 2. перебираем и выводим контакты каждого
                for (int i = 0; i < areasArray.length(); i++) {
                    JSONObject jsonorgs = areasArray.getJSONObject(i);
                    contentValues.put(DBHelper.KEY_ID, jsonorgs.getInt(DBHelper.KEY_ID));
                    contentValues.put(DBHelper.KEY_SNAME, jsonorgs.getString(DBHelper.KEY_SNAME));

                    if (jsonorgs.getString(DBHelper.KEY_SORTING) != "null") {
                        contentValues.put(DBHelper.KEY_SORTING, jsonorgs.getInt(DBHelper.KEY_SORTING));
                    } else
                    contentValues.put(DBHelper.KEY_SORTING, "");

                database.insert(DBHelper.TABLE_AREAS, null, contentValues);

                }
                database.setTransactionSuccessful();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            finally {
                database.endTransaction();
            }


                contentValues.clear();

            database.beginTransaction();
            try {
                //Заполняем базу Пользователей s_depart
                JSONArray departArray = dataJsonObj.getJSONArray("depart");
                // 2. перебираем и выводим контакты каждого
                for (int i = 0; i < departArray.length(); i++) {
                    JSONObject jsonorgs = departArray.getJSONObject(i);
                    contentValues.put(DBHelper.KEY_ID, jsonorgs.getInt(DBHelper.KEY_ID));
                    contentValues.put(DBHelper.KEY_DEPARTMENT, jsonorgs.getString(DBHelper.KEY_DEPARTMENT).replaceAll("&quot;","\'"));

                    if ((jsonorgs.getString(DBHelper.KEY_ORGID) != "null")) {
                        contentValues.put(DBHelper.KEY_ORGID, jsonorgs.getInt(DBHelper.KEY_ORGID));
                    } else
                    contentValues.put(DBHelper.KEY_ORGID, "");

                    if ((jsonorgs.getString(DBHelper.KEY_SORTING) != "null")) {
                        contentValues.put(DBHelper.KEY_SORTING, jsonorgs.getInt(DBHelper.KEY_SORTING));
                    } else
                    contentValues.put(DBHelper.KEY_SORTING, "");

                    database.insert(DBHelper.TABLE_DEPART, null, contentValues);

                }
                database.setTransactionSuccessful();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            finally {
                database.endTransaction();
            }

                contentValues.clear();
            database.beginTransaction();
            try {
                //Заполняем базу Пользователей s_otype
                JSONArray otypeArray = dataJsonObj.getJSONArray("otype");
                // 2. перебираем и выводим контакты каждого
                for (int i = 0; i < otypeArray.length(); i++) {
                    JSONObject jsonorgs = otypeArray.getJSONObject(i);
                    contentValues.put(DBHelper.KEY_ID, jsonorgs.getInt(DBHelper.KEY_ID));
                    contentValues.put(DBHelper.KEY_TITLE, jsonorgs.getString(DBHelper.KEY_TITLE));
                    database.insert(DBHelper.TABLE_OTYPE, null, contentValues);

                }database.setTransactionSuccessful();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            finally {
                database.endTransaction();
            }

            contentValues.clear();
            database.close();

            Toast toast = Toast.makeText(SettingsActivity.this, "База успешно обновлена",Toast.LENGTH_LONG);
            toast.show();
            //mPDialog.dismiss();
            Intent output = new Intent();
            output.putExtra("isDbUpdated", "1");
            setResult(RESULT_OK, output);
            finish();

        }


        public String getJSON(String urlString) {
            String resultJson = "";
            try {
                File file = new File(urlString);
                InputStream inputStream = new FileInputStream(file);
                BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder total = new StringBuilder();
                String line;

                while ((line = r.readLine()) != null) {
                    total.append(line);
                }


                resultJson = total.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return resultJson;
        }


        public String modFioString (String FIO){
            FIO=FIO.replaceAll(" +"," ");
            char[] a = FIO.toCharArray();

            if (a.length>0) {
                a[0] = Character.toUpperCase(a[0]);
                for (int i = 1; i < a.length; i++) {
                    if (a[i] == ' ') {
                        i++;
                        a[i] = Character.toUpperCase(a[i]);
                    }
                    else
                    {
                        a[i] = Character.toLowerCase(a[i]);
                    }

                }
            }

        return String.copyValueOf(a);
    }

        public String modOrgString(String org){
            org=org.replaceAll("&quot;","'");
            org=org.toLowerCase();
            return org;
        }


  }


}
