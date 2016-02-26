package com.informix.goverbook;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;

/**
 * Created by adm on 28.01.2016.
 */
public class DBHelper extends SQLiteOpenHelper {
    // Объявляем Таблицы базы
    private static final int DATABASE_VERSION = 8;
    public static final String DATABASE_NAME = "contactDb";
    public static final String TABLE_AREAS = "s_areas";
    public static final String TABLE_DEPART = "s_depart";
    public static final String TABLE_ORG = "s_org";
    public static final String TABLE_OTYPE = "s_otype";
    public static final String TABLE_USERS = "s_users";
    public static final String TABLE_FAVE = "s_fave";
    public static final String TABLE_LAST = "s_last";
    public static final String TABLE_ONMAIN = "s_onmain";

    // Объявляем Ключи таблицы s_users
    public static final String KEY_ID = "ID";
    public static final String KEY_FIO = "FIO";
    public static final String KEY_STATUS = "STATUS";
    public static final String KEY_CONTACTS = "CONTACTS";
    public static final String KEY_PHONE = "PHONE";
    public static final String KEY_EMAIL = "EMAIL";
    public static final String KEY_DEPARTID = "DEPARTID";
    public static final String KEY_ORGID = "ORGID";
    public static final String KEY_SORTING = "SORTING";

    // Объявляем Ключи таблицы s_org
    public static final String KEY_COMPANY = "COMPANY";
    public static final String KEY_LOWERCOMPANY = "LOWERCOMPANY";
    public static final String KEY_ADRES = "ADRES";
    public static final String KEY_AREAID = "AREAID";
    public static final String KEY_DESCR = "DESCR";
    public static final String KEY_TYPEID = "TYPEID";

    // Объявляем Ключи таблицы s_areas
    public static final String KEY_SNAME = "S_NAME";

    // Объявляем Ключи таблицы s_depart
    public static final String KEY_DEPARTMENT = "DEPARTMENT";

    // Объявляем Ключи таблицы s_otype
    public static final String KEY_TITLE = "TITLE";

    // Объявляем Ключи таблицы s_last
    public static final String KEY_IDUSER = "IDUSER";

    // Объявляем Ключи таблицы s_fave
    public static final String KEY_FAVETYPE = "FAVETYPE";

    public static final String TYPE_WORKER= "WORKER";
    public static final String TYPE_ORG= "ORG";

    public ListView searFioResult;






    //Обьявляем переменные для списка улусов
    ArrayList<String> areaName = new ArrayList<String>();
    public ArrayList<Integer> areaIdS= new ArrayList<Integer>();
    String[][] areas;
    SharedPreferences sPref;
    private static final String REAL_AREA = "REALAREA";





    public DBHelper (Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_USERS + "(" + KEY_ID + " integer primary key," + KEY_FIO + " text," + KEY_STATUS + " text," + KEY_CONTACTS + " text," + KEY_PHONE + " text," + KEY_EMAIL + " text," + KEY_DEPARTID + " integer," + KEY_ORGID + " integer," + KEY_SORTING + " integer)");
        db.execSQL("create table " + TABLE_ORG + "(" + KEY_ID + " integer primary key," + KEY_COMPANY + " text," + KEY_LOWERCOMPANY + " text," + KEY_ADRES + " text," + KEY_AREAID + " integer," + KEY_DESCR + " text," + KEY_TYPEID + " integer)");
        db.execSQL("create table " + TABLE_AREAS + "(" + KEY_ID + " integer primary key," + KEY_SNAME + " text," + KEY_SORTING + " integer)");
        db.execSQL("create table " + TABLE_DEPART + "(" + KEY_ID + " integer primary key," + KEY_DEPARTMENT + " text,"+ KEY_ORGID + " integer," + KEY_SORTING + " integer)");
        db.execSQL("create table " + TABLE_ONMAIN + "(" + KEY_ID + " integer primary key," + KEY_ORGID + " integer," + KEY_SNAME + " text)");
        db.execSQL("create table " + TABLE_OTYPE + "(" + KEY_ID + " integer primary key," + KEY_TITLE + " text)");
        db.execSQL("create table " + TABLE_LAST + "(" + KEY_ID + " integer primary key," + KEY_IDUSER + " integer," + KEY_FIO + " text," + KEY_STATUS + " text)");
        db.execSQL("create table " + TABLE_FAVE + "(" + KEY_ID + " integer primary key," + KEY_FAVETYPE + " integer," + KEY_IDUSER + " integer," + KEY_SNAME + " text)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

//        if (oldVersion<6){
            db.execSQL("drop table if exists " + TABLE_FAVE);
            db.execSQL("drop table if exists " + TABLE_LAST);
            db.execSQL("create table " + TABLE_LAST + "(" + KEY_ID + " integer primary key," + KEY_IDUSER + " integer," + KEY_FIO + " text," + KEY_STATUS + " text)");
            db.execSQL("create table " + TABLE_FAVE + "(" + KEY_ID + " integer primary key," + KEY_FAVETYPE + " integer," + KEY_IDUSER + " integer," + KEY_SNAME + " text)");
            db.execSQL("create table " + TABLE_ONMAIN + "(" + KEY_ID + " integer primary key," + KEY_ORGID + " integer," + KEY_SNAME + " text)");
//        }

    }


    public String[][] areaGetter(SQLiteDatabase database){

        String[][] result;
        String querry = "select * from s_areas order by SORTING ASC";
        Cursor cursor = database.rawQuery(querry, null);
        int SNAMEIndex = cursor.getColumnIndex(DBHelper.KEY_SNAME);
        int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);


        //Вывод результатов
        int i = 0;
        if (cursor.getCount() > 0) {
            result = new String[2][cursor.getCount()];
            if (cursor.moveToFirst()) {
                do {
                    result[0][i] = cursor.getString(SNAMEIndex);
                    result[1][i] = String.valueOf(cursor.getInt(idIndex));
                    i++;


                } while (cursor.moveToNext());
            }
            cursor.close();
        } else {
            result = new String[2][1];
            result[0][0] = "г. Якутск";
            result[1][0] = "35";
        }
        cursor.close();
        return result;

    }

    public ArrayList<Integer> drawAreaList(SQLiteDatabase database,Spinner spinner){
        areas = areaGetter(database);
        for (int i = 0; i < (areas[0].length); i++) {
            areaName.add(areas[0][i]);
            areaIdS.add(Integer.parseInt(areas[1][i]));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(spinner.getContext(),
                android.R.layout.simple_list_item_1, areaName);
        spinner.setAdapter(adapter);
        return areaIdS;
    }




    public ArrayList<UserContact> searchByFio(String fioString,String area,SQLiteDatabase database) {

        String querry;
        ArrayList<UserContact> result= new ArrayList<UserContact>();
        UserContact userContact;
        // Строка запроса в sql для ФИО


        querry = "SELECT s_users.id, s_users.fio, s_users.status, s_users.contacts, s_users.email, s_users.departid, s_users.orgid, s_depart.department, s_org.company, s_org.adres, s_org.descr, s_users.sorting, s_users.phone FROM s_users LEFT JOIN s_org ON s_users.orgid=s_org.id LEFT JOIN s_depart ON s_users.departid=s_depart.id WHERE "+ DBHelper.KEY_FIO +" like ?";
        querry = querry+ " and areaid=" + area+" ORDER BY fio asc";


        // Поиск всех вхождений базы данных удовлетворяющих условию в cursor

        //Заменяем первый символ поискана на заглавную букву
        char[] a = fioString.toCharArray();
        if (a.length > 0) {
            a[0] = Character.toUpperCase(a[0]);
            for (int i = 1; i < a.length; i++) {
                if (a[i] == ' ') {
                    if (i!=a.length-1) i++;
                    a[i] = Character.toUpperCase(a[i]);
                } else {
                    a[i] = Character.toLowerCase(a[i]);
                }

            }
        }

        fioString = String.valueOf(a);

        Cursor cursor = database.rawQuery(querry, new String[]{"%" + fioString + "%"});

        int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
        int FIOIndex = cursor.getColumnIndex(DBHelper.KEY_FIO);
        int STATUSIndex = cursor.getColumnIndex(DBHelper.KEY_STATUS);
        int PHONEIndex = cursor.getColumnIndex(DBHelper.KEY_PHONE);
        int CONTACTSIndex = cursor.getColumnIndex(DBHelper.KEY_CONTACTS);
        int EMAILIndex = cursor.getColumnIndex(DBHelper.KEY_EMAIL);
        int DEPARTIDIndex = cursor.getColumnIndex(DBHelper.KEY_DEPARTID);
        int ORGIDIDIndex = cursor.getColumnIndex(DBHelper.KEY_ORGID);
        int ORGIndex = cursor.getColumnIndex(DBHelper.KEY_COMPANY);
        int SORTING = cursor.getColumnIndex(DBHelper.KEY_SORTING);
        int DEPARTMENT = cursor.getColumnIndex(DBHelper.KEY_DEPARTMENT);
        int ADRES = cursor.getColumnIndex(DBHelper.KEY_ADRES);


        //Вывод результатов
        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    userContact = new UserContact(cursor.getInt(idIndex),cursor.getString(FIOIndex),cursor.getString(STATUSIndex),cursor.getString(PHONEIndex),cursor.getString(CONTACTSIndex),cursor.getString(EMAILIndex),cursor.getInt(DEPARTIDIndex),cursor.getString(ORGIndex),cursor.getInt(ORGIDIDIndex),cursor.getInt(SORTING),cursor.getString(DEPARTMENT),cursor.getString(ADRES));
                    result.add(userContact);


                } while (cursor.moveToNext());
            }
            cursor.close();
        } else {
            userContact = new UserContact(0,"Ничего не найденно","","","","",0,"",0,0);
            result.add(userContact);
        }

        cursor.close();

        return result;

    }


    public UserContact searchUserById(Integer id, SQLiteDatabase database) {

        String querry;
        UserContact result= new UserContact(0,"Ничего не найдено",null,null,null,null,0,null,0,0);
        UserContact userContact;
        // Строка запроса в sql для ФИО

        querry = "SELECT s_users.id, s_users.fio, s_users.status, s_users.contacts, s_users.email, s_users.departid, s_users.orgid, s_depart.department, s_org.company, s_org.adres, s_org.descr, s_users.sorting, s_users.phone FROM s_users LEFT JOIN s_org ON s_users.orgid=s_org.id LEFT JOIN s_depart ON s_users.departid=s_depart.id WHERE s_users."+ DBHelper.KEY_ID +" =" + id;


        // Поиск всех вхождений базы данных удовлетворяющих условию в cursor

        //Заменяем первый символ поискана на заглавную букву

        Cursor cursor = database.rawQuery(querry, null);

        int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
        int FIOIndex = cursor.getColumnIndex(DBHelper.KEY_FIO);
        int STATUSIndex = cursor.getColumnIndex(DBHelper.KEY_STATUS);
        int PHONEIndex = cursor.getColumnIndex(DBHelper.KEY_PHONE);
        int CONTACTSIndex = cursor.getColumnIndex(DBHelper.KEY_CONTACTS);
        int EMAILIndex = cursor.getColumnIndex(DBHelper.KEY_EMAIL);
        int DEPARTIDIndex = cursor.getColumnIndex(DBHelper.KEY_DEPARTID);
        int ORGIDIDIndex = cursor.getColumnIndex(DBHelper.KEY_ORGID);
        int ORGIndex = cursor.getColumnIndex(DBHelper.KEY_COMPANY);
        int SORTING = cursor.getColumnIndex(DBHelper.KEY_SORTING);
        int DEPARTMENT = cursor.getColumnIndex(DBHelper.KEY_DEPARTMENT);
        int ADRES = cursor.getColumnIndex(DBHelper.KEY_ADRES);


        //Вывод результатов
        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    userContact = new UserContact(cursor.getInt(idIndex),cursor.getString(FIOIndex),cursor.getString(STATUSIndex),cursor.getString(PHONEIndex),cursor.getString(CONTACTSIndex),cursor.getString(EMAILIndex),cursor.getInt(DEPARTIDIndex),cursor.getString(ORGIndex),cursor.getInt(ORGIDIDIndex),cursor.getInt(SORTING),cursor.getString(DEPARTMENT),cursor.getString(ADRES));
                    result=userContact;
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        cursor.close();
        return result;

    }



    public OrgContact searchOrgByName(String companyName, SQLiteDatabase database) {

        String querry;
        OrgContact result;
        UserContact userContact;
        ArrayList<UserContact> listuserContacts = new ArrayList<UserContact>();
        ArrayList<String> departs = new ArrayList<String>();
        ArrayList<ArrayList<UserContact>> userByDeparts = new ArrayList<ArrayList<UserContact>>();
        ArrayList<Integer> departsId = new ArrayList<Integer>();
        // Строка запроса в sql для ФИО

        querry = "SELECT department,s_depart.id FROM s_org INNER JOIN s_depart ON s_depart.ORGID=s_org.id where COMPANY == \""+companyName+"\" ORDER BY s_depart.sorting";

        Cursor cursor = database.rawQuery(querry, null);
        int DEPARTIndex = cursor.getColumnIndex(DBHelper.KEY_DEPARTMENT);
        int DEPARTIDIndex = cursor.getColumnIndex(DBHelper.KEY_ID);


        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    departs.add(cursor.getString(DEPARTIndex));
                    departsId.add(cursor.getInt(DEPARTIDIndex));

                } while (cursor.moveToNext());
            }
            cursor.close();
        } else {
            departs.add("Отдел не указан");
            departsId.add(0);
        }


        querry = "SELECT s_users.id, s_users.fio, s_users.status, s_users.contacts, s_users.email, s_users.departid, s_users.orgid, s_depart.department, s_org.company, s_org.adres, s_org.descr, s_users.sorting, s_users.phone FROM s_users LEFT JOIN s_org ON s_users.orgid=s_org.id LEFT JOIN s_depart ON s_users.departid=s_depart.id WHERE "+ DBHelper.TABLE_ORG+"."+ DBHelper.KEY_COMPANY +" == \""+companyName+"\" ORDER BY s_users.sorting";

        cursor = database.rawQuery(querry, null);

        int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
        int FIOIndex = cursor.getColumnIndex(DBHelper.KEY_FIO);
        int STATUSIndex = cursor.getColumnIndex(DBHelper.KEY_STATUS);
        int PHONEIndex = cursor.getColumnIndex(DBHelper.KEY_PHONE);
        int CONTACTSIndex = cursor.getColumnIndex(DBHelper.KEY_CONTACTS);
        int EMAILIndex = cursor.getColumnIndex(DBHelper.KEY_EMAIL);
        int ORGIDIDIndex = cursor.getColumnIndex(DBHelper.KEY_ORGID);
        int ORGIndex = cursor.getColumnIndex(DBHelper.KEY_COMPANY);
        int SORTING = cursor.getColumnIndex(DBHelper.KEY_SORTING);
        int DEPARTID = cursor.getColumnIndex(DBHelper.KEY_DEPARTID);


        //Вывод результатов
        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    userContact = new UserContact(cursor.getInt(idIndex), cursor.getString(FIOIndex), cursor.getString(STATUSIndex), cursor.getString(PHONEIndex), cursor.getString(CONTACTSIndex), cursor.getString(EMAILIndex), cursor.getInt(DEPARTID), cursor.getString(ORGIndex), cursor.getInt(ORGIDIDIndex), cursor.getInt(SORTING));
                    listuserContacts.add(userContact);

                } while (cursor.moveToNext());
            }
            cursor.close();
        } else {
            userContact = new UserContact(0, "Ничего не найденно", "", "", "", "", 0, "", 0, 0);
            listuserContacts.add(userContact);
        }

        cursor.close();

        ArrayList<UserContact> userWithoutDepart=new ArrayList<UserContact>();
        for (int i=0;i<departs.size();i++){
            ArrayList<UserContact> userOnDepart=new ArrayList<UserContact>();
            for (UserContact user:listuserContacts){
                if (user.DEPARTID==departsId.get(i)){
                    userOnDepart.add(user);

                }
                if (user.DEPARTID==0){
                    userWithoutDepart.add(user);
                }

            }
            userByDeparts.add(userOnDepart);

        }
        userByDeparts.add(userWithoutDepart);
        result=new OrgContact(userByDeparts,departs);
        return result;

    }


    public String[][] ListOrgType(SQLiteDatabase database) {

        String querry;
        String[][] result;
        // Строка запроса в sql для ФИО

        querry = "SELECT * FROM " + DBHelper.TABLE_OTYPE;

        Cursor cursor = database.rawQuery(querry, null);
        int IDIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
        int TitleIndex = cursor.getColumnIndex(DBHelper.KEY_TITLE);

        //Вывод результатов
        int i=0;
        if (cursor.getCount() > 0) {
            result = new String[2][cursor.getCount()];
            if (cursor.moveToFirst()) {
                do {
                    result[0][i] = cursor.getString(TitleIndex);
                    result[1][i] = cursor.getString(IDIndex);
                    i++;

                } while (cursor.moveToNext());
            }
            cursor.close();
        } else {
            result = new String[2][1];
            result[0][0] = "Иные";
            result[1][0] = "6";
        }
        cursor.close();
        return result;

    }


    public String[][] ListOrgOnMain(SQLiteDatabase database) {

        String querry;
        String[][] result;
        // Строка запроса в sql для ФИО

        querry = "SELECT * FROM " + DBHelper.TABLE_ONMAIN + " order by "+DBHelper.KEY_ORGID;

        Cursor cursor = database.rawQuery(querry, null);
        int SNAMEIndex = cursor.getColumnIndex(DBHelper.KEY_SNAME);
        int ORGIDIndex = cursor.getColumnIndex(DBHelper.KEY_ORGID);

        //Вывод результатов
        int i=0;
        if (cursor.getCount() > 0) {
            result = new String[2][cursor.getCount()];
            if (cursor.moveToFirst()) {
                do {
                    result[0][i] = cursor.getString(SNAMEIndex);
                    result[1][i] = cursor.getString(ORGIDIndex);
                    i++;

                } while (cursor.moveToNext());
            }
            cursor.close();
        } else {
            result = new String[2][1];
            result[0][0] = "";
            result[1][0] = "0";
        }
        cursor.close();
        return result;

    }




    public void saveLast(String fio,String status,int iduser,DBHelper dbHelper){
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_IDUSER, iduser);
        contentValues.put(DBHelper.KEY_FIO, fio);
        contentValues.put(DBHelper.KEY_STATUS, status);
        database.insert(DBHelper.TABLE_LAST, null, contentValues);

    }

    public boolean getItemSaved(String name,DBHelper dbHelper)
    {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        boolean saved;

        String querry;
        querry = "SELECT * FROM " + DBHelper.TABLE_FAVE+" where "+DBHelper.KEY_SNAME+" == \""+name+"\" ";
        Cursor cursor = database.rawQuery(querry, null);

        if (cursor.getCount()>0) saved=true;
        else saved = false;
        return saved;

    }



    public void saveFave(String name,String type,int id,DBHelper dbHelper){
        SQLiteDatabase database = dbHelper.getWritableDatabase();

            ContentValues contentValues = new ContentValues();
            contentValues.put(DBHelper.KEY_SNAME, name);
            contentValues.put(DBHelper.KEY_FAVETYPE, type);
            contentValues.put(DBHelper.KEY_IDUSER, id);
            database.insert(DBHelper.TABLE_FAVE, null, contentValues);

    }

    public void deleteFaveContact(int id, DBHelper dbHelper){
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        database.delete(DBHelper.TABLE_FAVE, DBHelper.KEY_IDUSER + " = " + id, null);

    }

    public void deleteFaveOrg(String name, DBHelper dbHelper){
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        database.delete(DBHelper.TABLE_FAVE, DBHelper.KEY_SNAME + " = \"" + name+"\"", null);

    }



    public String[][] ListFave(SQLiteDatabase database){
        String querry;
        String[][] result;

        querry = "SELECT * FROM " + DBHelper.TABLE_FAVE+" order by id desc";
        Cursor cursor = database.rawQuery(querry, null);

        int typeIndex = cursor.getColumnIndex(DBHelper.KEY_FAVETYPE);
        int nameIndex = cursor.getColumnIndex(DBHelper.KEY_SNAME);
        int userIndex = cursor.getColumnIndex(DBHelper.KEY_IDUSER);

        //Вывод результатов
        int i=0;
        if (cursor.getCount() > 0) {
            result = new String[3][cursor.getCount()];
            if (cursor.moveToFirst()) {
                do {
                    result[0][i] = cursor.getString(typeIndex);
                    result[1][i] = cursor.getString(nameIndex);
                    result[2][i] = cursor.getString(userIndex);
                    i++;

                } while (cursor.moveToNext());
            }
            cursor.close();
        } else {
            result = new String[3][1];
            result[0][0] = DBHelper.TYPE_WORKER;
            result[1][0] = "Список пуст";
            result[2][0] = "0";
        }

        cursor.close();



        return result;
    }




    public ArrayList<UserContact> ListLast(SQLiteDatabase database) {

        String querry;
        ArrayList<UserContact> result = new ArrayList<UserContact>();
        // Строка запроса в sql для ФИО


        querry = "SELECT * FROM " + DBHelper.TABLE_LAST+" order by id desc limit 6";


        // Поиск всех вхождений базы данных удовлетворяющих условию в cursor

        //Заменяем первый символ поискана на заглавную букву

        Cursor cursor = database.rawQuery(querry, null);

        int idIndex = cursor.getColumnIndex(DBHelper.KEY_IDUSER);
        int FIOIndex = cursor.getColumnIndex(DBHelper.KEY_FIO);
        int STATUSIndex = cursor.getColumnIndex(DBHelper.KEY_STATUS);
        UserContact userContact;

        //Вывод результатов
        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    userContact = new UserContact(cursor.getInt(idIndex),cursor.getString(FIOIndex),cursor.getString(STATUSIndex));
                    result.add(userContact);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        cursor.close();
        return result;

    }




    public String[][] ListOrgOnType(String typeId, String area, SQLiteDatabase database) {

        String querry;
        String[][] result;
        // Строка запроса в sql для ФИО

        if (typeId.equals("ALLID"))
            querry = "SELECT * FROM "+ DBHelper.TABLE_ORG +" WHERE areaid="+ area+" ORDER BY s_org.company asc";
        else
            querry = "SELECT * FROM "+ DBHelper.TABLE_ORG +" WHERE "+ DBHelper.KEY_TYPEID +" = "+ typeId+ " AND areaid="+ area+" ORDER BY s_org.company asc";

        Cursor cursor = database.rawQuery(querry, null);
        int IDIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
        int TitleIndex = cursor.getColumnIndex(DBHelper.KEY_TITLE);
        int DEPARTIDIndex = cursor.getColumnIndex(DBHelper.KEY_DEPARTID);
        int ORGIDIDIndex = cursor.getColumnIndex(DBHelper.KEY_ORGID);
        int ORGIndex = cursor.getColumnIndex(DBHelper.KEY_COMPANY);
        int SORTING = cursor.getColumnIndex(DBHelper.KEY_SORTING);
        int DEPARTMENT = cursor.getColumnIndex(DBHelper.KEY_DEPARTMENT);
        int ADRES = cursor.getColumnIndex(DBHelper.KEY_ADRES);

        //Вывод результатов
        int i=0;
        if (cursor.getCount() > 0) {
            result = new String[2][cursor.getCount()];
            if (cursor.moveToFirst()) {
                do {
                    result[0][i] = cursor.getString(ORGIndex);
                    result[1][i] = cursor.getString(IDIndex);
                    i++;

                } while (cursor.moveToNext());
            }
            cursor.close();
        } else {
            result = new String[2][1];
            result[0][0] = "Ничего не найденно";
            result[1][0] = "6";
        }
        cursor.close();
        return result;

    }


    public String[][] SearchOrg(String orgName, String area,SQLiteDatabase database) {

        String querry;
        String[][] result;
        // Строка запроса в sql для ФИО

        querry = "SELECT * FROM "+ DBHelper.TABLE_ORG +" WHERE "+ DBHelper.TABLE_ORG+"." + DBHelper.KEY_LOWERCOMPANY + " like ?";
        querry = querry+ " and areaid=" + area+" ORDER BY COMPANY asc";

        orgName=orgName.toLowerCase();

        Cursor cursor = database.rawQuery(querry, new String[]{"%" + orgName + "%"});
        int IDIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
        int TitleIndex = cursor.getColumnIndex(DBHelper.KEY_TITLE);
        int DEPARTIDIndex = cursor.getColumnIndex(DBHelper.KEY_DEPARTID);
        int ORGIDIDIndex = cursor.getColumnIndex(DBHelper.KEY_ORGID);
        int ORGIndex = cursor.getColumnIndex(DBHelper.KEY_COMPANY);
        int SORTING = cursor.getColumnIndex(DBHelper.KEY_SORTING);
        int DEPARTMENT = cursor.getColumnIndex(DBHelper.KEY_DEPARTMENT);
        int ADRES = cursor.getColumnIndex(DBHelper.KEY_ADRES);

        //Вывод результатов
        int i=0;
        if (cursor.getCount() > 0) {
            result = new String[2][cursor.getCount()];
            if (cursor.moveToFirst()) {
                do {
                    result[0][i] = cursor.getString(ORGIndex);
                    result[1][i] = cursor.getString(IDIndex);
                    i++;

                } while (cursor.moveToNext());
            }
            cursor.close();
        } else {
            result = new String[2][1];
            result[0][0] = "Ничего не найденно";
            result[1][0] = "0";
        }
        cursor.close();
        return result;

    }





}
