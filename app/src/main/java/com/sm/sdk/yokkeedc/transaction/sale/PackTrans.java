package com.sm.sdk.yokkeedc.transaction.sale;

import androidx.annotation.NonNull;

import com.pax.gl.pack.exception.Iso8583Exception;
import com.sm.sdk.yokkeedc.isopacker.PackIso8583;
import com.sm.sdk.yokkeedc.transaction.TransData;
import com.sm.sdk.yokkeedc.utils.FieldConstant;
import com.sm.sdk.yokkeedc.utils.Tools;
import com.sm.sdk.yokkeedc.utils.TransConstant;
import com.sm.sdk.yokkeedc.utils.Utility;

import java.util.HashMap;

public class PackTrans extends PackIso8583 {

    public PackTrans() {

    }

    @NonNull
    @Override
    public byte[] pack(TransData transData) {

        switch(transData.getTransactionType()) {
            case TransConstant.TRANS_TYPE_SALE:
                setSaleData(transData);
                break;
            case TransConstant.TRANS_TYPE_VOID:
                setVoidData(transData);
                break;
            case TransConstant.TRANS_TYPE_SETTLEMENT:
                break;
        }
        byte[] packData = pack();
        return packData;
    }



    protected void setSaleData(TransData transData) {
        setFinancialData(transData);
        setMandatoryData(transData);
        try {
            entity.setFieldValue(FieldConstant.BIT_PAN, transData.getCardNo());
            entity.setFieldValue(FieldConstant.BIT_DATE_EXPIRATION,transData.getExpDate());
            entity.setFieldValue(FieldConstant.BIT_TRACK_2_DATA, transData.getTrack2Data()); //track2data
            if(transData.getEnterMode() == TransData.EnterMode.INSERT)
            {
                entity.setFieldValue(FieldConstant.BIT_APPLICATION_PAN_SEQUENCE_NUMBER, transData.getCardSeqNo());//card seq no
                entity.setFieldValue(FieldConstant.BIT_EMV_DATA, Tools.str2Bcd(transData.getICCData()));//icc data
            }
            entity.setFieldValue(FieldConstant.BIT_POINT_OF_SERVICE_ENTRY_MODE, "052");//pos entry mode
            entity.setFieldValue(FieldConstant.BIT_ADDITIONAL_DATA_NATIONAL, "1");

            String strInvoiceNo = Utility.getInvoiceNum();
            entity.setFieldValue(FieldConstant.BIT_RESERVED_PRIVATE_BIT62,strInvoiceNo );//Invoice No
            transData.setTraceNo(strInvoiceNo);
        } catch (Iso8583Exception e) {
            e.printStackTrace();
        }
    }

    public void setVoidData(TransData transData) {
        setFinancialData(transData);
        setMandatoryData(transData);
        try {
            entity.setFieldValue(FieldConstant.BIT_PAN, transData.getCardNo());
            entity.setFieldValue(FieldConstant.BIT_DATE_EXPIRATION,transData.getExpDate());
            entity.setFieldValue(FieldConstant.BIT_TIME_LOCAL_TRANSACTION,transData.getTime());
            entity.setFieldValue(FieldConstant.BIT_DATE_LOCAL_TRANSACTION,transData.getDate());
            entity.setFieldValue(FieldConstant.BIT_POINT_OF_SERVICE_ENTRY_MODE, "052");//pos entry mode
            entity.setFieldValue(FieldConstant.BIT_ADDITIONAL_DATA_NATIONAL, "1");
            entity.setFieldValue(FieldConstant.BIT_POINT_OF_SERVICE_CONDITION_CODE,"00");
            entity.setFieldValue(FieldConstant.BIT_RETRIEVAL_REFERENCE_NUMBER,transData.getReffNo());
            entity.setFieldValue(FieldConstant.BIT_AUTHORIZATION_IDENTIFICATION_RESPONSE, transData.getApprCode());

            entity.setFieldValue(FieldConstant.BIT_RESERVED_PRIVATE_BIT62,transData.getInvoiceNo() );//Invoice No

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
