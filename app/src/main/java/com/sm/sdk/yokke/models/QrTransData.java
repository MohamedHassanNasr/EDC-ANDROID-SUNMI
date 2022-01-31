package com.sm.sdk.yokke.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "QR_Trans_Data")
public class QrTransData {
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField
    private String mid;
    @DatabaseField
    private String tid;
    @DatabaseField
    private String merchantTransId;
    @DatabaseField
    private String QRreffno;
    @DatabaseField
    private String amount;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getMerchantTransId() {
        return merchantTransId;
    }

    public void setMerchantTransId(String merchantTransId) {
        this.merchantTransId = merchantTransId;
    }

    public String getQRreffno() {
        return QRreffno;
    }

    public void setQRreffno(String QRreffno) {
        this.QRreffno = QRreffno;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
