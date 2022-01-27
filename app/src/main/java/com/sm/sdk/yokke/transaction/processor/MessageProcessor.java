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
        if(TransConstant.TRANS_TYPE_GENERATE_QRIS.equals(transType)) {
            getqris(map, transData);
        }
        if(TransConstant.TRANS_TYPE_INQUIRY_QRIS.equals(transType)) {
            getinquiryqris(map, transData);
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


    private static void getqris(HashMap<String, byte[]> map, TransData transData) {

        //  transData.setResponseCode(Tools.hexToString(Tools.bcd2Str(map.get("39"))));
        transData.setNMID(Tools.hexToString(Tools.bcd2Str(map.get("48"))));
        parseqris(map, transData);
        parseAndSaveDataFromBit44(map, transData);
        getParseDefault(map, transData);
        //return transData;
    }


    private static void parseAndSaveDataFromBit44(HashMap<String, byte[]> map, TransData transData) {
        String bit44, cardBrandSymbol, cardType, cardBrand, onOffSymbol, OnOff, bankCode, usageType;
        bit44 = Tools.bcd2Str(map.get(FieldConstant.BIT_ADDITIONAL_RESPONSE_DATA));

        StringBuilder sb = new StringBuilder(bit44);
        usageType = sb.substring(0,2);
        bankCode = sb.substring(2,6);
        cardBrand = Utility.getCardBrandNameFromBit44(Tools.hexToAscii(sb.substring(6,8)));
        OnOff = Utility.getOnUsOrOfUsTypeFromBit44(Tools.hexToAscii(sb.substring(8,10)));
        cardType = Utility.getCardTypeFromBit44(Tools.hexToAscii(sb.substring(10,12)));

        transData.setCardBrand(cardBrand);
        transData.setONOFF(OnOff);
        transData.setCardType(cardType);
        transData.setBankCode(bankCode);

    }

    private static void parseqris(HashMap<String, byte[]> map, TransData transData){
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
}
