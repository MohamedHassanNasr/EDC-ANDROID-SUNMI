package com.sm.sdk.yokke.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.sm.sdk.yokke.models.transData.TransData;

import java.io.Serializable;

@DatabaseTable(tableName = "batch_record")
public class BatchRecord implements Serializable {
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField
    private String transactionType;
    @DatabaseField
    private String amount;
    @DatabaseField
    private String tip;
    @DatabaseField
    private String cardNo;
    @DatabaseField
    private String expDate;
    @DatabaseField
    private String cardHolderName;

    // enterMode : INSERT, SWIPE, WAVE, QR
    @DatabaseField
    private TransData.EnterMode enterMode;

    @DatabaseField
    private String posEntryMode;
    @DatabaseField
    private String date;
    @DatabaseField
    private String time;
    @DatabaseField
    private String TID;
    @DatabaseField
    private String MID;
    @DatabaseField
    private String cardBrand;
    @DatabaseField
    private String ONOFF;
    @DatabaseField
    private String cardType;

    private String track2Data;
    @DatabaseField(columnName = TRACE_NO_FIELD)
    private String traceNo;
    @DatabaseField
    private String batchNo;
    @DatabaseField
    private String apprCode;
    @DatabaseField
    private String reffNo;
    @DatabaseField
    private String invoiceNo;
    @DatabaseField(columnName = USE_YN_FIELD)
    private String useYN;
    @DatabaseField
    private String bankCode;
    @DatabaseField
    private String cardTypeBit44;
    @DatabaseField(columnName = REF_ID_FIELD)
    private String reffId;

    /**
     * field name
     */
    public static final String TRACE_NO_FIELD = "trace_no";
    public static final String USE_YN_FIELD   = "use_yn";
    public static final String REF_ID_FIELD   = "ref_id";

    public BatchRecord() {

    }

    public BatchRecord(TransData transData) {
        this.transactionType = transData.getTransactionType();
        this.amount = transData.getAmount();
        this.tip = transData.getTip();
        this.cardNo = transData.getCardNo();
        this.expDate = transData.getExpDate();
        this.cardHolderName = transData.getCardHolderName();
        this.enterMode = transData.getEnterMode();
        this.posEntryMode = transData.getPosEntryMode();
        this.date = transData.getDate();
        this.time = transData.getTime();
        this.TID = transData.getTID();
        this.MID = transData.getMID();
        this.cardBrand = transData.getCardBrand();
        this.ONOFF = transData.getONOFF();
        this.cardType = transData.getCardType();
        this.track2Data = transData.getTrack2Data();
        this.traceNo = transData.getTraceNo();
        this.batchNo = transData.getBatchNo();
        this.apprCode = transData.getApprCode();
        this.reffNo = transData.getReffNo();
        this.bankCode = transData.getBankCode();
        this.cardTypeBit44 = transData.getCardTypeBit44();
        this.reffId = transData.getReffId();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public TransData.EnterMode getEnterMode() {
        return enterMode;
    }

    public void setEnterMode(TransData.EnterMode enterMode) {
        this.enterMode = enterMode;
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

    public String getApprCode() {
        return apprCode;
    }

    public void setApprCode(String apprCode) {
        this.apprCode = apprCode;
    }

    public String getReffNo() {
        return reffNo;
    }

    public void setReffNo(String reffNo) {
        this.reffNo = reffNo;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public String getUseYN() {
        return useYN;
    }

    public void setUseYN(String useYN) {
        this.useYN = useYN;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getCardTypeBit44() {
        return cardTypeBit44;
    }

    public void setCardTypeBit44(String cardTypeBit44) {
        this.cardTypeBit44 = cardTypeBit44;
    }

    public String getReffId() {
        return reffId;
    }

    public void setReffId(String reffId) {
        this.reffId = reffId;
    }
}
