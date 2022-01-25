package com.sm.sdk.yokkeedc.models;

public class QrisData {
    /*QRIS*/
    private String carmerchanttransid;
    private String carQRHostCode;
    private String careffno;
    private String camercPAN;
    private String caQRCODELen;
    private String GenerateQR;

    public QrisData() {
    }

    public String getCarmerchanttransid() {
        return carmerchanttransid;
    }

    public void setCarmerchanttransid(String carmerchanttransid) {
        this.carmerchanttransid = carmerchanttransid;
    }

    public String getCarQRHostCode() {
        return carQRHostCode;
    }

    public void setCarQRHostCode(String carQRHostCode) {
        this.carQRHostCode = carQRHostCode;
    }

    public String getCareffno() {
        return careffno;
    }

    public void setCareffno(String careffno) {
        this.careffno = careffno;
    }

    public String getCamercPAN() {
        return camercPAN;
    }

    public void setCamercPAN(String camercPAN) {
        this.camercPAN = camercPAN;
    }

    public String getCaQRCODELen() {
        return caQRCODELen;
    }

    public void setCaQRCODELen(String caQRCODELen) {
        this.caQRCODELen = caQRCODELen;
    }

    public String getGenerateQR() {
        return GenerateQR;
    }

    public void setGenerateQR(String generateQR) {
        GenerateQR = generateQR;
    }
}
