package com.informix.goverbook;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.Character;import java.lang.Exception;import java.lang.Override;import java.lang.String;import java.lang.StringBuilder;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateDatabase extends AsyncTask<String, Integer, String> {

    UpdateDatabase updateDatabase = null;
    DBHelper dbHelper;
    String resultJson;
    private ProgressDialog mPDialog;
    private Context mContext;
    private File mTargetFile;
    private boolean isDownloaded = false;
    final String CacheDir = "/data/data/com.informix.goverbook/cache";

    public UpdateDatabase(Context context, File targetFile, String dialogMessage) {

        dbHelper= new DBHelper(context);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        this.mContext = context;
        this.mTargetFile = targetFile;

        mPDialog = new ProgressDialog(context);

        mPDialog.setMessage(dialogMessage);
        mPDialog.setIndeterminate(true);
        mPDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mPDialog.setCancelable(true);
        mPDialog.setMax(100);
        mPDialog.setProgressNumberFormat(null);

    }

    protected void onPreExecute() {

        mPDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                cancel(true);
            }
        });
        mPDialog.show();
    }

    @Override
    protected String doInBackground(String... sUrl) {
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(sUrl[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return "Server returned HTTP " + connection.getResponseCode()
                        + " " + connection.getResponseMessage();
            }
            Log.i("DownloadTask","Response " + connection.getResponseCode());

            // this will be useful to display download percentage
            // might be -1: server did not report the length
            int fileLength = connection.getContentLength();

            // download the file
            input = connection.getInputStream();
            output = new FileOutputStream(mTargetFile,false);

            byte data[] = new byte[4096];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                // allow canceling with back button
                if (isCancelled()) {
                    Log.i("DownloadTask", "Cancelled");
                    input.close();
                    return null;
                }
                total += count;
                // publishing the progress....
                if (fileLength > 0) // only if total length is known
                    publishProgress((int) (total * 100 / fileLength));
                output.write(data, 0, count);
            }
        } catch (Exception e) {
            return e.toString();
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }

            if (connection != null)
                connection.disconnect();
        }

        new Decompress().unzip(CacheDir + "/base.zip", CacheDir);
        resultJson = getJSON(CacheDir + "/base.json");

        //Вносим изменения в базу
        JSONObject dataJsonObj = null;
        try {
            dataJsonObj = new JSONObject(resultJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        ContentValues contentValuesForOnMain = new ContentValues();
        isDownloaded = true;
        publishProgress((int) (0));
        database.beginTransaction();
        try {
            dataJsonObj = new JSONObject(resultJson);


            //Заполняем базу Пользователей s_users
            JSONArray usersArray = dataJsonObj.getJSONArray("users");

            database.delete(DBHelper.TABLE_USERS, null, null);

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

        publishProgress((int) (20));
        database.beginTransaction();
        try {
            //Заполняем базу Пользователей s_org
            JSONArray orgsArray = dataJsonObj.getJSONArray("orgs");
            database.delete(DBHelper.TABLE_ORG, null, null);
            database.delete(DBHelper.TABLE_ONMAIN, null, null);

            // 2. перебираем и выводим контакты каждого
            for (int i = 0; i < orgsArray.length(); i++) {
                JSONObject jsonorgs = orgsArray.getJSONObject(i);
                contentValues.put(DBHelper.KEY_ID, jsonorgs.getInt("ID"));
                //Удаляем амперсанды
                contentValues.put(DBHelper.KEY_COMPANY, jsonorgs.getString("COMPANY").replaceAll("&quot;", "\'"));
                contentValues.put(DBHelper.KEY_LOWERCOMPANY, modOrgString(jsonorgs.getString("COMPANY")));
                contentValues.put(DBHelper.KEY_ADRES, jsonorgs.getString("ADRES"));
                if ((jsonorgs.getString("AREAID")) != "null") {
                    contentValues.put(DBHelper.KEY_AREAID, jsonorgs.getInt("AREAID"));
                } else contentValues.put(DBHelper.KEY_AREAID, "");
                contentValues.put(DBHelper.KEY_DESCR, jsonorgs.getString("DESCR"));


                if (((jsonorgs.getString("TYPEID")) == "null") || (jsonorgs.getString("TYPEID").equals("0"))) {
                    contentValues.put(DBHelper.KEY_TYPEID, "6");
                }
                else
                if (Integer.parseInt(jsonorgs.getString("TYPEID")) > 100) {
                    contentValuesForOnMain.put(DBHelper.KEY_SNAME, jsonorgs.getString("COMPANY").replaceAll("&quot;", "\'"));
                    contentValuesForOnMain.put(DBHelper.KEY_ORGID, jsonorgs.getInt("TYPEID"));
                    contentValues.put(DBHelper.KEY_TYPEID, jsonorgs.getInt("TYPEID"));
                    database.insert(DBHelper.TABLE_ONMAIN, null, contentValuesForOnMain);
                }
                else
                {
                    contentValues.put(DBHelper.KEY_TYPEID, jsonorgs.getInt("TYPEID"));
                }

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

        publishProgress((int) (40));
        database.beginTransaction();
        try {
            //Заполняем базу Пользователей s_areas
            JSONArray areasArray = dataJsonObj.getJSONArray("areas");
            database.delete(DBHelper.TABLE_AREAS, null, null);

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

        publishProgress((int) (60));
        database.beginTransaction();
        try {
            //Заполняем базу Пользователей s_depart
            JSONArray departArray = dataJsonObj.getJSONArray("depart");

            database.delete(DBHelper.TABLE_DEPART, null, null);

            // 2. перебираем и выводим контакты каждого
            for (int i = 0; i < departArray.length(); i++) {
                JSONObject jsonorgs = departArray.getJSONObject(i);
                contentValues.put(DBHelper.KEY_ID, jsonorgs.getInt(DBHelper.KEY_ID));
                contentValues.put(DBHelper.KEY_DEPARTMENT, jsonorgs.getString(DBHelper.KEY_DEPARTMENT).replaceAll("&quot;", "\'"));

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

        publishProgress((int) (80));
        database.beginTransaction();
        try {
            //Заполняем базу Пользователей s_otype
            JSONArray otypeArray = dataJsonObj.getJSONArray("otype");
            database.delete(DBHelper.TABLE_OTYPE, null, null);

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
        return "Обновилось";
    }

    protected void onPostExecute(String strJson) {
        Toast toast = Toast.makeText(mContext, "База успешно обновлена",Toast.LENGTH_LONG);
        toast.show();
        mPDialog.dismiss();
//        Intent output = new Intent();
//        output.putExtra("isDbUpdated", "1");
//        setResult(RESULT_OK, output);
//        finish();

    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
        // if we get here, length is known, now set indeterminate to false
        mPDialog.setIndeterminate(false);
        if (isDownloaded) {mPDialog.setMessage("Вношу изменения");}
        mPDialog.setProgress(progress[0]);


    }

    @Override
    protected void onCancelled() {
        Toast.makeText(mContext, "asynctack cancelled.....", Toast.LENGTH_SHORT).show();
        mPDialog.dismiss(); /*hide the progressbar dialog here...*/
        super.onCancelled();
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


