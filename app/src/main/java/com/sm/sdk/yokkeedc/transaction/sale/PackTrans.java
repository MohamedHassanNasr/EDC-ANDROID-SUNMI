package com.sm.sdk.yokkeedc.transaction.sale;

import androidx.annotation.NonNull;

import com.pax.dal.exceptions.PedDevException;
import com.pax.gl.pack.exception.Iso8583Exception;
import com.sm.sdk.yokkeedc.isopacker.PackIso8583;
import com.sm.sdk.yokkeedc.transaction.TransData;

public class PackTrans extends PackIso8583 {

    public PackTrans() {

    }

    @NonNull
    @Override
    public byte[] pack(TransData transData) {

        switch(transData.getTransactionType()) {
            case "SALE":
                setSaleData(transData);
                break;
            case "VOID":
                break;
            case "SETTLEMENT":
                break;
        }
        byte[] packData = pack();
        return packData;
    }

    protected void setFinancialData(TransData transData) {
        try {
            entity.setFieldValue("4", transData.getAmount()); //amount
            entity.setFieldValue("41", "73003495"); //tid
            entity.setFieldValue("42", "000071000243621"); //mid
        } catch (Iso8583Exception e) {
            e.printStackTrace();
        }

    }

    protected void setMandatoryData(TransData transData) {
        //widya set temp data
        try {
            entity.setFieldValue("h", "6000910085"); //tpdu
            entity.setFieldValue("m","0200");//mti
            entity.setFieldValue("3","000000"); //procode
            entity.setFieldValue("11","000001"); //stan
            entity.setFieldValue("24","107"); //nii

        } catch (Iso8583Exception e) {
            e.printStackTrace();
        }


    }

    protected void setSaleData(TransData transData) {
        setFinancialData(transData);
        setMandatoryData(transData);
        try {
            entity.setFieldValue("35", transData.getTrack2Data()); //track2data
            entity.setFieldValue("55", transData.getICCData());//icc data
            entity.setFieldValue("22", transData.getPosEntryMode());//pos entry mode
        } catch (Iso8583Exception e) {
            e.printStackTrace();
        }



    }



    private void updateBitMap(byte[] bitMap, byte[] senFileds){
        for(byte item : senFileds){
            if(item <= 0) return ;
            byte bit = bitMap[(item -1)/8];
            byte bitNo = (byte)(0x80 >> ((item-1)%8));
            if((bit & bitNo) == 0){
                continue;
            }
            bitMap[(item -1)/8] = (byte)(bit & (~bitNo));
        }
    }
}
