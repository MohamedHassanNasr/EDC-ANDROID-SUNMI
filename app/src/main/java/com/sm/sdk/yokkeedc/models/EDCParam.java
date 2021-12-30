package com.sm.sdk.yokkeedc.models;

import android.provider.BaseColumns;

import java.io.Serializable;

public class EDCParam implements Serializable, BaseColumns {
    private String id;
    private String paramName;
    private String paramVal;
    public static final String TABLE_NAME = "tbEDCParam";
    public static final String COL_PARAM_NAME = "PARAM_NAME";
    public static final String COL_PARAM_VAL = "PARAM_VAL";
    public static final String SQL_CREATE = "create table tbEDCParam " +
            "(_id integer primary key autoincrement, "+
            "PARAM_NAME text, PARAM_VAL text)";
    public static final String SQL_DELETE = "drop table if exists tbEDCParam";
    public EDCParam(String id, String paramCode, String paramVal) {
        this.id = id;
        this.paramName = paramCode;
        this.paramVal = paramVal;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getParamVal() {
        return paramVal;
    }

    public void setParamVal(String paramVal) {
        this.paramVal = paramVal;
    }

//    public static String EDC_PARAM_MID = "1001";
//    public static String EDC_PARAM_TID = "1002";
//    public static String EDC_PARAM_MERCHANT_NAME = "1003";
//    public static String EDC_PARAM_MERCHANT_ADDR1 = "1006";
//    public static String EDC_PARAM_MERCHANT_ADDR2 = "1007";
//    public static String EDC_PARAM_MERCHANT_ADDR3 = "1008";
//
//    public static String[] arrParamMerchant = {
//            EDC_PARAM_MID, EDC_PARAM_TID, EDC_PARAM_MERCHANT_NAME,
//            EDC_PARAM_MERCHANT_ADDR1, EDC_PARAM_MERCHANT_ADDR2, EDC_PARAM_MERCHANT_ADDR3
//    };
}
