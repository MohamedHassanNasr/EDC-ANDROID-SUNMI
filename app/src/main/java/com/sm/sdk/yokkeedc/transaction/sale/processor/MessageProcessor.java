package com.sm.sdk.yokkeedc.transaction.sale.processor;

import com.sm.sdk.yokkeedc.models.QrisData;
import com.sm.sdk.yokkeedc.transaction.TransData;
import com.sm.sdk.yokkeedc.utils.FieldConstant;
import com.sm.sdk.yokkeedc.utils.Tools;
import com.sm.sdk.yokkeedc.utils.TransConstant;
import com.sm.sdk.yokkeedc.utils.Utility;

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
        }else if (TransConstant.TRANS_TYPE_QRIS.equals(transType)){
            getqris(map, transData);
        }
    }

    private static void getSaleData(HashMap<String, byte[]> map, TransData transData) {

        transData.setReffNo(Tools.hexToString(Tools.bcd2Str(map.get("37"))));
        transData.setApprCode(Tools.hexToString(Tools.bcd2Str(map.get("38"))));
        transData.setTime(Utility.getTimeFromBit12(map.get("12")));
        transData.setDate(Utility.getDateFromBit13(map.get("13")));
        transData.setTID(Tools.hexToString(Tools.bcd2Str(map.get("41"))));
        transData.setMID(Tools.hexToString(Tools.bcd2Str(map.get("42"))));
        transData.setResponseCode(Tools.hexToString(Tools.bcd2Str(map.get("39"))));
        parseAndSaveDataFromBit44(map, transData);

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
    }

    private static void getqris(HashMap<String, byte[]> map, TransData transData) {

        transData.setResponseCode(Tools.hexToString(Tools.bcd2Str(map.get("39"))));
        parseQris(map, transData);

        //return transData;
    }

    private static void parseQris(HashMap<String, byte[]> map, TransData transData){
        String bit59, carmerchanttransid,carQRHostCode,careffno,camercPAN,caQRCODELen,GenerateQR;
        bit59 = Tools.hexToString(Tools.bcd2Str(map.get(FieldConstant.BIT_RESERVED_NATIONAL_BIT59)));

        StringBuilder sb = new StringBuilder(bit59);
        carmerchanttransid = sb.substring(0,15);
        carQRHostCode = sb.substring(15,17);
        careffno = sb.substring(17,37);
        camercPAN = sb.substring(39,58);
        caQRCODELen = sb.substring(58,61);
        GenerateQR = sb.substring(61,bit59.length());

        transData.setCarmerchanttransid(carmerchanttransid);
        transData.setCarQRHostCode(carQRHostCode);
        transData.setCareffno(careffno);
        transData.setCamercPAN(camercPAN);
        transData.setGenerateQR(GenerateQR);
        transData.setCaQRCODELen(caQRCODELen);
    }


}
