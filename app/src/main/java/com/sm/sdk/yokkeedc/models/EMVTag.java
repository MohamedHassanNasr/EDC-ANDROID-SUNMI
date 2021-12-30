package com.sm.sdk.yokkeedc.models;

import android.provider.BaseColumns;

import java.io.Serializable;

public class EMVTag implements Serializable, BaseColumns {
    public static final String TABLE_NAME = "tbEMVTag";
    public static final String COL_TAG_CD = "TAG_CD";
    public static final String COL_TAG_VAL = "TAG_VAL";
    public static final String SQL_CREATE = "create table tbEMVTag " +
            "(_id integer primary key autoincrement, "+
            "TAG_CD text, TAG_VAL text)";
    public static final String SQL_DELETE = "drop table if exists tbEMVTag";

    private String id;
    private String tagCode;
    private String tagVal;

    public EMVTag (String id, String tagCode, String tagVal) {
        this.id = id;
        this.tagCode = tagCode;
        this.tagVal = tagVal;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getTagCode() {
        return tagCode;
    }

    public void setTagCode(String tagCode) {
        this.tagCode = tagCode;
    }

    public void setTagVal(String tagVal) {
        this.tagVal = tagVal;
    }

    public String getTagVal() {
        return tagVal;
    }
}
