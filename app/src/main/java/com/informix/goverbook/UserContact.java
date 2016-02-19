package com.informix.goverbook;

/**
 * Created by adm on 01.02.2016.
 */
public class UserContact {
    int id;
    String FIO;
    public String STATUS;
    String PHONE;
    String CONTACTS;
    String EMAIL;
    int DEPARTID;
    String DEPART;
    String ORG;
    int ORGIDID;
    int SORTING;
    String ORGADRESS;

    public UserContact(int id, String FIO, String status) {
        this.id = id;
        this.FIO = FIO;
        this.STATUS = status;
        this.PHONE = "";
        this.CONTACTS = "";
        this.EMAIL = "";
        this.DEPARTID = 0;
        this.ORG ="";
        this.ORGIDID = 0;
        this.SORTING = 0;
        this.DEPART = "";
        this.ORGADRESS = "";
    }

    public UserContact(int id, String FIO, String STATUS, String PHONE, String CONTACTS, String EMAIL, int DEPARTID, String ORG, int ORGIDID, int SORTING) {
        this.id = id;
        this.FIO = FIO;
        this.STATUS = STATUS;
        this.PHONE = PHONE;
        this.CONTACTS = CONTACTS;
        this.EMAIL = EMAIL;
        this.DEPARTID = DEPARTID;
        this.ORG =ORG;
        this.ORGIDID = ORGIDID;
        this.SORTING = SORTING;
    }

    public UserContact(int id, String FIO, String STATUS, String PHONE, String CONTACTS, String EMAIL, int DEPARTID, String ORG, int ORGIDID, int SORTING, String DEPART, String ORGADRESS) {
        this.id = id;
        this.FIO = FIO;
        this.STATUS = STATUS;
        this.PHONE = PHONE;
        this.CONTACTS = CONTACTS;
        this.EMAIL = EMAIL;
        this.DEPARTID = DEPARTID;
        this.ORG =ORG;
        this.ORGIDID = ORGIDID;
        this.SORTING = SORTING;
        this.DEPART = DEPART;
        this.ORGADRESS = ORGADRESS;
    }


    public String getDEPART() {
        return DEPART;
    }

    public String getORGADRESS() {
        return ORGADRESS;
    }

    public void setORG(String ORG) {
        this.ORG = ORG;
    }

    public String getORG() {

        return ORG;
    }

    public int getId() {
        return id;
    }

    public String getFIO() {
        return FIO;
    }

    public String getSTATUS() {
        return STATUS;
    }

    public String getPHONE() {
        return PHONE;
    }

    public String getCONTACTS() {
        return CONTACTS;
    }

    public String getEMAIL() {
        return EMAIL;
    }

    public int getDEPARTID() {
        return DEPARTID;
    }

    public int getORGIDID() {
        return ORGIDID;
    }

    public int getSORTING() {
        return SORTING;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setFIO(String FIO) {
        this.FIO = FIO;
    }

    public void setSTATUS(String STATUS) {
        this.STATUS = STATUS;
    }

    public void setPHONE(String PHONE) {
        this.PHONE = PHONE;
    }

    public void setCONTACTS(String CONTACTS) {
        this.CONTACTS = CONTACTS;
    }

    public void setEMAIL(String EMAIL) {
        this.EMAIL = EMAIL;
    }

    public void setDEPARTID(int DEPARTID) {
        this.DEPARTID = DEPARTID;
    }

    public void setORGIDID(int ORGIDID) {
        this.ORGIDID = ORGIDID;
    }

    public void setSORTING(int SORTING) {
        this.SORTING = SORTING;
    }


}
