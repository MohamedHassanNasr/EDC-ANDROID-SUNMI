package com.sm.sdk.yokkeedc.models;

import android.provider.BaseColumns;

import java.io.Serializable;

public class TransDataDB implements Serializable, BaseColumns {
    private String id;
    private String amount;
    private String tip;
    private String cardNo;
    private String expDate;
    private String cardHolderName;
    private String posEntryMode;
    private String date;
    private String time;
    private String TID;
    private String MID;
    private String cardBrand;
    private String ONOFF;
    private String cardType;
    private String traceNo;
    private String batchNo;
    private String appCD;
    private String reffNo;

    public static final String TABLE_NAME = "tbTransData";
    public static final String COL_id = "id";
    public static final String COL_AMOUNT = "Amount";
    public static final String COL_TIP = "Tip";
    public static final String COL_CARD_NO = "Card_No";
    public static final String COL_Exp_Date = "Exp_Date";
    public static final String COL_CARD_HOLDER_NAME = "Card_Holder_Name";
    public static final String COL_POS_ENTRY_MODE = "Pos_Entry_Mode";
    public static final String COL_DATE = "Date";
    public static final String COL_TIME = "Time";
    public static final String COL_TID = "Tid";
    public static final String COL_MID = "MID";
    public static final String COL_CARD_BRAND = "Card_Brand";
    public static final String COL_ONOFF = "ONOFF";
    public static final String COL_CARD_TYPE = "Card_Type";
    public static final String COL_TRACE_NO = "Trace_No";
    public static final String COL_BATCH_NO = "Batch_No";
    public static final String COL_APP_CD = "App_Cd";
    public static final String COL_REFF_NO = "Reff_No";


    public static final String SQL_CREATE = "create table tbTransData " +
            "(_id integer primary key , "+
            "Amount text, Tip text, Card_No text, Exp_Date text, Card_Holder_Name text, " +
            "Pos_Entry_Mode text, Date text, Time text, Tid text, MID text, Card_Brand text" +
            ", ONOFF text, Card_Type text, Trace_No text, Batch_No text, App_Cd text, Reff_No text)";
    public static final String SQL_DELETE = "drop table if exists tbTransData";

    public TransDataDB(String id, String amount, String tip, String cardNo, String expDate, String cardHolderName, String posEntryMode, String date, String time, String TID, String MID, String cardBrand, String ONOFF, String cardType, String traceNo, String batchNo, String appCD, String reffNo) {
        this.id = id;
        this.amount = amount;
        this.tip = tip;
        this.cardNo = cardNo;
        this.expDate = expDate;
        this.cardHolderName = cardHolderName;
        this.posEntryMode = posEntryMode;
        this.date = date;
        this.time = time;
        this.TID = TID;
        this.MID = MID;
        this.cardBrand = cardBrand;
        this.ONOFF = ONOFF;
        this.cardType = cardType;
        this.traceNo = traceNo;
        this.batchNo = batchNo;
        this.appCD = appCD;
        this.reffNo = reffNo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getExpDate() {
        return expDate;
    }

    public void setExpDate(String expDate) {
        this.expDate = expDate;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public String getPosEntryMode() {
        return posEntryMode;
    }

    public void setPosEntryMode(String posEntryMode) {
        this.posEntryMode = posEntryMode;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTID() {
        return TID;
    }

    public void setTID(String TID) {
        this.TID = TID;
    }

    public String getMID() {
        return MID;
    }

    public void setMID(String MID) {
        this.MID = MID;
    }

    public String getCardBrand() {
        return cardBrand;
    }

    public void setCardBrand(String cardBrand) {
        this.cardBrand = cardBrand;
    }

    public String getONOFF() {
        return ONOFF;
    }

    public void setONOFF(String ONOFF) {
        this.ONOFF = ONOFF;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getTraceNo() {
        return traceNo;
    }

    public void setTraceNo(String traceNo) {
        this.traceNo = traceNo;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public String getAppCD() {
        return appCD;
    }

    public void setAppCD(String appCD) {
        this.appCD = appCD;
    }

    public String getReffNo() {
        return reffNo;
    }

    public void setReffNo(String reffNo) {
        this.reffNo = reffNo;
    }
}
