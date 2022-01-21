package com.sm.sdk.yokkeedc.transaction;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * TransData digunakan untuk menyimpan semua variabel yang ada pada flow transaksi
 * data digunakan untuk build ISO 8583, save ke database, print format
 * @Author rivaldi
 */

public class TransData implements Parcelable {
    
    public String transactionType;
    
    public String amount;
    
    public String tip;
    
    public String cardNo;
    
    public String expDate;
    
    public String cardHolderName;

    // enterMode : INSERT, SWIPE, WAVE, QR
    
    public EnterMode enterMode;

    
    public String posEntryMode;
    
    public String date;
    
    public String time;
    
    public String TID;
    
    public String MID;
    
    public String cardBrand;
    
    public String ONOFF;
    
    public String cardType;

    public String track2Data;
    public String traceNo;
    
    public String batchNo;
    
    public String apprCode;
    
    public String reffNo;

    public String cardSeqNo;
    public String ResponseCode;
    public String iccData;
    public String procCode;
    public String dateTime;
    public String invoiceNo;

    private static TransData instance;
    /**
     * field name
     */
    public static final String TRACE_NO_FIELD = "trace_no";

    public TransData() {

    }

    protected TransData(Parcel in) {
        transactionType = in.readString();
        amount = in.readString();
        tip = in.readString();
        cardNo = in.readString();
        expDate = in.readString();
        cardHolderName = in.readString();
        posEntryMode = in.readString();
        date = in.readString();
        time = in.readString();
        TID = in.readString();
        MID = in.readString();
        cardBrand = in.readString();
        ONOFF = in.readString();
        cardType = in.readString();
        track2Data = in.readString();
        traceNo = in.readString();
        batchNo = in.readString();
        apprCode = in.readString();
        reffNo = in.readString();
        cardSeqNo = in.readString();
        ResponseCode = in.readString();
        iccData = in.readString();
        procCode = in.readString();
        dateTime = in.readString();
        invoiceNo = in.readString();
    }

    public static final Creator<TransData> CREATOR = new Creator<TransData>() {
        @Override
        public TransData createFromParcel(Parcel in) {
            return new TransData(in);
        }

        @Override
        public TransData[] newArray(int size) {
            return new TransData[size];
        }
    };

    public static TransData getInstance() {
        if (instance == null) {
            instance = new TransData();
        }
        return instance;
    }

    public static void deleteTransData() {
        instance = null;
    }

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(transactionType);
        dest.writeString(amount);
        dest.writeString(tip);
        dest.writeString(cardNo);
        dest.writeString(expDate);
        dest.writeString(cardHolderName);
        dest.writeString(posEntryMode);
        dest.writeString(date);
        dest.writeString(time);
        dest.writeString(TID);
        dest.writeString(MID);
        dest.writeString(cardBrand);
        dest.writeString(ONOFF);
        dest.writeString(cardType);
        dest.writeString(track2Data);
        dest.writeString(traceNo);
        dest.writeString(batchNo);
        dest.writeString(apprCode);
        dest.writeString(reffNo);
        dest.writeString(cardSeqNo);
        dest.writeString(ResponseCode);
        dest.writeString(iccData);
        dest.writeString(procCode);
        dest.writeString(dateTime);
        dest.writeString(invoiceNo);
    }

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

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
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

    public String getTrack2Data() {
        return track2Data;
    }

    public void setTrack2Data(String track2Data) {
        this.track2Data = track2Data;
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

    public String getCardSeqNo() {
        return cardSeqNo;
    }

    public void setCardSeqNo(String cardSeqNo) {
        this.cardSeqNo = cardSeqNo;
    }

    public String getResponseCode() {
        return ResponseCode;
    }

    public void setResponseCode(String ResponseCode) {
        this.ResponseCode = ResponseCode;
    }
}
