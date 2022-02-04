package com.sm.sdk.yokke.transaction.report;


import com.sm.sdk.yokke.models.BatchRecord;
import com.sm.sdk.yokke.utils.TransConstant;
import com.sm.sdk.yokke.utils.Utility;

public class ReportData {

    //private final int CARD_BRAND_MAX = 8;
    private static AmountTransInfo amountInfo;
    private static String cardBrand;
    private static String cardType;
    private static String bit44;
    private static String bankCode;
    private static String tid;
    private static boolean isOnUs;
    private static boolean qrDomestikTransFlag; //true jika transaksi adalah QR


    public ReportData(BatchRecord batch) {
        this.amountInfo = new AmountTransInfo();
        this.bit44 = batch.getCardTypeBit44();
        this.bankCode = batch.getBankCode();
        this.qrDomestikTransFlag = checkQrDomestikTrans(batch);
        this.tid = batch.getTID();
        if(batch.getCardTypeBit44() != null) {
            setDataByBit44(bit44);
        }
    }

    private static void setDataByBit44(String bit44) {
        String cardBrandBit44 = bit44.substring(0,1);
        String onUsOrOffUs = bit44.substring(1,2);
        String cardTypeBit44 = bit44.substring(2,3);

        cardType = Utility.getCardTypeFromBit44(cardTypeBit44);
        cardBrand = Utility.getCardBrandNameFromBit44(cardBrandBit44);
        isOnUs =  "On Us".equals(Utility.getOnUsOrOfUsTypeFromBit44(onUsOrOffUs));
    }

    private static boolean checkQrDomestikTrans(BatchRecord batch) {
        return TransConstant.TRANS_TYPE_INQUIRY_QRIS.equals(batch.getTransactionType()) ||
                TransConstant.TRANS_TYPE_ANY_TRANS_QRIS.equals(batch.getTransactionType()) ||
                TransConstant.TRANS_TYPE_REFUND_QRIS.equals(batch.getTransactionType());
    }

    public static AmountTransInfo getAmountInfo() {
        return amountInfo;
    }

    public static void setAmountInfo(AmountTransInfo amountInfo) {
        ReportData.amountInfo = amountInfo;
    }

    public static String getCardBrand() {
        return cardBrand;
    }

    public static void setCardBrand(String cardBrand) {
        ReportData.cardBrand = cardBrand;
    }

    public static String getCardType() {
        return cardType;
    }

    public static void setCardType(String cardType) {
        ReportData.cardType = cardType;
    }

    public static boolean isOnUs() {
        return isOnUs;
    }

    public static void setIsOnUs(boolean isOnUs) {
        ReportData.isOnUs = isOnUs;
    }

    public static String getBit44() {
        return bit44;
    }

    public static void setBit44(String bit44) {
        ReportData.bit44 = bit44;
    }

    public static String getBankCode() {
        return bankCode;
    }

    public static void setBankCode(String bankCode) {
        ReportData.bankCode = bankCode;
    }

    public static boolean isQrDomestikTransFlag() {
        return qrDomestikTransFlag;
    }

    public static void setQrDomestikTransFlag(boolean qrDomestikTransFlag) {
        ReportData.qrDomestikTransFlag = qrDomestikTransFlag;
    }

    public static String getTid() {
        return tid;
    }

    public static void setTid(String tid) {
        ReportData.tid = tid;
    }

}
