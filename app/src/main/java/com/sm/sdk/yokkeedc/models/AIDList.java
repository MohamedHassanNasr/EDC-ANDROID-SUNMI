package com.sm.sdk.yokkeedc.models;

import android.provider.BaseColumns;

import java.io.Serializable;

public class AIDList implements Serializable, BaseColumns {
    public static final String TABLE_NAME = "tbEMVAIDList";
    public static final String COL_EMV_APP_ID = "EMV_APP_ID";
    // public static final String COL_EMV_APP_NM = "EMV_APP_NM";
    public static final String SQL_CREATE = "create table tbEMVAIDList " +
            "(_id integer primary key autoincrement, "+
            "EMV_APP_ID text)";
    public static final String SQL_DELETE = "drop table if exists tbEMVAIDList";

    private String id;
    private String aid;
    //private String appName;

    public AIDList (String id, String aid) {
        this.id = id;
        this.aid = aid;
        //this.appName = appName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public String getAid() {
        return aid;
    }


}
