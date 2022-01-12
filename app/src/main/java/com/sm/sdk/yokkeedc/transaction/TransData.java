package com.sm.sdk.yokkeedc.transaction;

import java.util.Date;

/*
* Get All data when transaction, some data save to Database
*
* */

public class TransData {
    public String transactionType;
    public String dateTime;
    public String amount;
    public String tip;
    public String cardNo;               //From Card
    public String expDate;              //From Card
    public String cardHolderName;       //From Card

    // enterMode : INSERT, SWIPE, WAVE, QR
    public EnterMode enterMode;

    public String posEntryMode;
    public Date date;
    public String time;
    public String TID;
    public String MID;
    public String cardBrand;
    public String ONOFF;
    public String cardType;
    public String track2Data;           //Swipe or TAG 57
    public long traceNo;
    public String batchNo;
    public String iccData;
    public String procCode;
    public String apprCode;
    public String reffNo;

    public static final String PEM_MANUAL_NO_PIN = "0120";
    public static final String PEM_MANUAL_PIN = "0110";
    public static final String PEM_MS_NO_PIN = "0220";
    public static final String PEM_MS_PIN = "0210";
    public static final String PEM_SCAN_QR = "0310";
    public static final String PEM_IC_NO_PIN = "0520";
    public static final String PEM_IC_PIN = "0510";
    public static final String PEM_CTLS_NO_PIN = "0720";
    public static final String PEM_CTLS_PIN = "0710";
    public static final String PEM_FALLBACK_NO_PIN = "8020";
    public static final String PEM_FALLBACK_PIN = "8010";

    public enum EnterMode {
        MANUAL("MANUAL"),
        SWIPE("SWIPE"),
        INSERT("CHIP"),
        FALLBACK("FALLBACK"),
        CLSS("C"),
        QR("QR"),;

        private String str;

        private EnterMode(String str) {
            this.str = str;
        }

        @Override
        public String toString() {
            return str;
        }
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public EnterMode getEnterMode() {
        return enterMode;
    }

    public void setEnterMode(EnterMode enterMode) {
        this.enterMode = enterMode;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
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

    public String getTrack2Data() {
        return track2Data;
    }

    public void setTrack2Data(String track2Data) {
        this.track2Data = track2Data;
    }

    public long getTraceNo() {
        return traceNo;
    }

    public void setTraceNo(long traceNo) {
        this.traceNo = traceNo;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public String getICCData() {
        return iccData;
    }

    public void setICCData(String ICCData) {
        this.iccData = ICCData;
    }

    public String getProcCode() {
        return procCode;
    }

    public void setProcCode(String procCD) {
        procCode = procCD;
    }

    public String getApprCode() {
        return apprCode;
    }

    public void setApprCode(String appCD) {
        apprCode = appCD;
    }

    public String getReffNo() {
        return reffNo;
    }

    public void setReffNo(String reffNo) {
        this.reffNo = reffNo;
    }
}
