package com.sm.sdk.yokke.models;

import android.provider.BaseColumns;

import java.io.Serializable;

public class JspeedyParam implements Serializable, BaseColumns {
    public static final String TABLE_NAME = "tbJspeedyParam";
    public static final String COL_TAG_CD = "TAG_CD";
    public static final String COL_TAG_VAL = "TAG_VAL";
    public static final String SQL_CREATE = "create table tbJspeedyParam " +
            "(_id integer primary key autoincrement, "+
            "TAG_CD text, TAG_VAL text)";
    public static final String SQL_DELETE = "drop table if exists tbJspeedyParam";

    private String id;
    private String tagCode;
    private String tagVal;

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

    public String getTagVal() {
        return tagVal;
    }

    public void setTagVal(String tagVal) {
        this.tagVal = tagVal;
    }

    public JspeedyParam(String id, String tagCode, String tagVal) {
        this.id = id;
        this.tagCode = tagCode;
        this.tagVal = tagVal;
    }
}
