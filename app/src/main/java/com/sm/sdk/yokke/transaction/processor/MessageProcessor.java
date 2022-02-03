package com.sm.sdk.yokke.transaction.processor;

import com.sm.sdk.yokke.activities.MtiApplication;
import com.sm.sdk.yokke.models.transData.TransData;
import com.sm.sdk.yokke.utils.FieldConstant;
import com.sm.sdk.yokke.utils.Tools;
import com.sm.sdk.yokke.utils.TransConstant;
import com.sm.sdk.yokke.utils.Utility;

import java.util.HashMap;

public class MessageProcessor {
    /**
     *
     * @param map : dari iso message
     * @param transData : see TransConstant
     *
     * @return null if ....
     */
    public static void parseMessage(HashMap<String,byte[]> map, TransData transData) {
        String transType = transData.getTransactionType();
        if(TransConstant.TRANS_TYPE_SALE.equals(transType)) {
            getSaleData(map, transData);
        }
        if(TransConstant.TRANS_TYPE_VOID.equals(transType)) {
            getvoid(map, transData);
        }
        if (TransConstant.TRANS_TYPE_GENERATE_QRIS.equals(transType)){
            getGenerateQris(map,transData);
        }
        if(TransConstant.TRANS_TYPE_ANY_TRANS_QRIS.equals(transType)) {
            getQRIS(map, transData);
        }
        if(TransConstant.TRANS_TYPE_INQUIRY_QRIS.equals(transType)) {
            getinquiryqris(map, transData);
        }
        if (TransConstant.TRANS_TYPE_REFUND_QRIS.equals(transType)){
            getRefundQris(map,transData);
        }
    }

    private static void getSaleData(HashMap<String, byte[]> map, TransData transData) {

        transData.setReffNo(Tools.hexToString(Tools.bcd2Str(map.get("37"))));
        if (map.containsKey("38")) {
            transData.setApprCode(Tools.hexToString(Tools.bcd2Str(map.get("38"))));
        }
        transData.setMID(Tools.hexToString(Tools.bcd2Str(map.get("42"))));
        //  transData.setResponseCode(Tools.hexToString(Tools.bcd2Str(map.get("39"))));
        parseAndSaveDataFromBit44(map, transData);
        getParseDefault(map, transData);

        //return transData;
    }

    private static void getvoid(HashMap<String, byte[]> map, TransData transData) {

        transData.setReffNo(Tools.hexToString(Tools.bcd2Str(map.get("37"))));
        if (map.containsKey("38")) {
            transData.setApprCode(Tools.hexToString(Tools.bcd2Str(map.get("38"))));
        }
        transData.setMID(Tools.hexToString(Tools.bcd2Str(map.get("42"))));
        //     transData.setResponseCode(Tools.hexToString(Tools.bcd2Str(map.get("39"))));
        parseAndSaveDataFromBit44(map, transData);
        getParseDefault(map, transData);

        //return transData;
    }

    private static void getGenerateQris(HashMap<String, byte[]> map, TransData transData) {

        if(map.containsKey("48")) {
            String nmid = Tools.hexToString(Tools.bcd2Str(map.get("48")));
            StringBuilder sb = new StringBuilder(nmid);
            nmid = sb.substring(4,nmid.length());
            transData.setNMID(nmid);
        }
        parseGenerateQris(map, transData);
        parseAndSaveDataFromBit44(map, transData);
        getParseDefault(map, transData);

    }


    private static void parseGenerateQris(HashMap<String, byte[]> map, TransData transData){
        String bit59, merchantTransid,qrHostCode,reffNo,mercPan,qrCodeLen,generateQR;
        bit59 = Tools.hexToString(Tools.bcd2Str(map.get(FieldConstant.BIT_RESERVED_NATIONAL_BIT59)));

        StringBuilder sb = new StringBuilder(bit59);
        merchantTransid = sb.substring(0,15);
        qrHostCode = sb.substring(15,17);
        reffNo = sb.substring(17,37);
        mercPan = sb.substring(39,58);
        qrCodeLen = sb.substring(58,61);
        generateQR = sb.substring(61,bit59.length());

        transData.setMerchantTransId(merchantTransid);
        transData.setQrHostCode(qrHostCode);
        transData.setReffNo(reffNo);
        transData.setMercPan(mercPan);
        transData.setGenerateQR(generateQR);
        transData.setQrCodeLen(qrCodeLen);
    }

    private static void getQRIS(HashMap<String, byte[]> map, TransData transData) {

        if (map.containsKey("4")) {
            transData.setAmount(Tools.hexToString(Tools.bcd2Str(map.get("4"))));
        }
        if (map.containsKey("48")) {
            transData.setNMID(Tools.hexToString(Tools.bcd2Str(map.get("48"))));
        }
        parseAndSaveDataFromBit44(map, transData);
        getParseDefault(map, transData);
        parseQris(map, transData);
        //return transData;
    }


    private static void parseAndSaveDataFromBit44(HashMap<String, byte[]> map, TransData transData) {
        String bit44, cardBrandSymbol, cardType, cardBrand, onOffSymbol, OnOff, bankCode, usageType,cardTypeBit44;

        bit44 = Tools.bcd2Str(map.get(FieldConstant.BIT_ADDITIONAL_RESPONSE_DATA));
        StringBuilder sb = new StringBuilder(bit44);
        usageType = sb.substring(0,2);
        bankCode = sb.substring(2,6);
        cardTypeBit44 = Tools.hexToAscii(sb.substring(6,12));

        transData.setCardTypeBit44(cardTypeBit44);

        StringBuilder getTypeCard = new StringBuilder(cardTypeBit44);
        cardBrand = Utility.getCardBrandNameFromBit44(getTypeCard.substring(0,1));
        OnOff = Utility.getOnUsOrOfUsTypeFromBit44(getTypeCard.substring(1,2));
        cardType = Utility.getCardTypeFromBit44(getTypeCard.substring(2,3));


//        String edcFlag = sb.substring(12,16);
//        if(TransConstant.TRANS_TYPE_INQUIRY_QRIS.equals(transData.getTransactionType())) {
//            String promotionID = Tools.hexToAscii(sb.substring(16, 28));
//            String memberbankTID = Tools.hexToAscii(sb.substring(28, 44));
//            String memberbankMID = Tools.hexToAscii(sb.substring(44, 74));
//
//            transData.setMemberBankMid(memberbankMID);
//            transData.setMemberBankTid(memberbankTID);
//
//        }

        transData.setONOFF(OnOff);
        transData.setCardBrand(cardBrand);
        transData.setCardType(cardType);
        transData.setBankCode(bankCode);

    }

    private static void parseQris(HashMap<String, byte[]> map, TransData transData){
        String bit59, merchantTransId,qrKindCode,reffNo,payByChannel,mercPan,custPan,custName,apprCode,reffId,acqBankCode;
        String issName,memberBankMid,memberBankTid,paymentType,feeAmt,tipAmt,totalAmt,appsUserId,merchantAddr,transDate,transTime;
        if (map.containsKey("59")) {
            bit59 = Tools.hexToString(Tools.bcd2Str(map.get(FieldConstant.BIT_RESERVED_NATIONAL_BIT59)));
            StringBuilder sb = new StringBuilder(bit59);
            merchantTransId = sb.substring(0,15);
            qrKindCode = sb.substring(15,17);
            reffNo = sb.substring(17,37);
            payByChannel = sb.substring(37,39);
            mercPan = sb.substring(39,58);
            custPan = sb.substring(58,77);
            custName = sb.substring(77,107);
            apprCode = sb.substring(107,113);
            reffId = sb.substring(113,133);
            acqBankCode = sb.substring(133,136);
            issName = sb.substring(136,146);
            memberBankMid = sb.substring(146,161);
            memberBankTid = sb.substring(161,169);
            paymentType = sb.substring(169,171);
            feeAmt = sb.substring(171,186);
            tipAmt = sb.substring(186,201);
            totalAmt = sb.substring(201,216);
            appsUserId = sb.substring(216,256);
            merchantAddr = sb.substring(256,296);
            transDate = sb.substring(296,300);
            transTime = sb.substring(300,306);


            transData.setMerchantTransId(merchantTransId);
            transData.setQrKindCode(qrKindCode);
            transData.setReffNo(reffNo);
            transData.setPayByChannel(payByChannel);
            transData.setMercPan(mercPan);
            transData.setCustName(custName);
            transData.setCustPan(custPan);
            transData.setApprCode(apprCode);
            transData.setReffId(reffId);
            transData.setAcqBankCode(acqBankCode);
            transData.setIssName(issName);
            transData.setMemberBankMid(memberBankMid);
            transData.setMemberBankTid(memberBankTid);
            transData.setPaymentType(paymentType);
            transData.setFeeAmt(feeAmt);
            transData.setTipAmt(tipAmt);
            transData.setTotalAmt(totalAmt);
            transData.setAppsUserId(appsUserId);
            transData.setMerchantAddr(merchantAddr);
            transData.setDate(transDate);
            transData.setTime(transTime);
        }
    }

    private static void getParseDefault(HashMap<String, byte[]> map, TransData transData){
        transData.setTime(Utility.getTimeFromBit12(map.get("12")));
        transData.setDate(Utility.getDateFromBit13(map.get("13")));
        transData.setTID(Tools.hexToString(Tools.bcd2Str(map.get("41"))));
        transData.setResponseCode(MtiApplication.getRspCode().parse(Tools.hexToString(Tools.bcd2Str(map.get("39")))));
    }

    private static void getinquiryqris(HashMap<String, byte[]> map, TransData transData) {

        transData.setResponseCode(MtiApplication.getRspCode().parse(Tools.hexToString(Tools.bcd2Str(map.get("39")))));
//        transData.setNMID(Tools.hexToString(Tools.bcd2Str(map.get("48"))));
//        parseqris(map, transData);
//        parseAndSaveDataFromBit44(map, transData);
//        getParseDefault(map, transData);
        //return transData;
    }

    private static void getRefundQris(HashMap<String, byte[]> map, TransData transData) {

        //  transData.setResponseCode(Tools.hexToString(Tools.bcd2Str(map.get("39"))));
        if (map.containsKey("48")){
            transData.setNMID(Tools.hexToString(Tools.bcd2Str(map.get("48"))));
        }
        //parseqris(map, transData);
        //parseAndSaveDataFromBit44(map, transData);
        getParseDefault(map, transData);
        //return transData;
    }
}
