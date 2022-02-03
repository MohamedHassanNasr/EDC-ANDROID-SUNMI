package com.sm.sdk.yokke.transaction.sale;

import androidx.annotation.NonNull;

import com.pax.gl.pack.exception.Iso8583Exception;
import com.sm.sdk.yokke.isopacker.PackIso8583;
import com.sm.sdk.yokke.models.transData.TransData;
import com.sm.sdk.yokke.utils.FieldConstant;
import com.sm.sdk.yokke.utils.Tools;
import com.sm.sdk.yokke.utils.TransConstant;
import com.sm.sdk.yokke.utils.Utility;

public class PackTrans extends PackIso8583 {

    public PackTrans() {

    }

    @NonNull
    @Override
    public byte[] pack(TransData transData, boolean isReversal) {
        if(isReversal)
        {
            setReversalData(transData);
        }
        else {
            switch(transData.getTransactionType()) {
                case TransConstant.TRANS_TYPE_SALE:
                    setSaleData(transData);
                    break;
                case TransConstant.TRANS_TYPE_VOID:
                    setVoidData(transData);
                    break;
                case TransConstant.TRANS_TYPE_SETTLEMENT:
                    break;
                case TransConstant.TRANS_TYPE_GENERATE_QRIS:
                    setGenerateQrData(transData);
                    break;
                case TransConstant.TRANS_TYPE_INQUIRY_QRIS:
                    setInquiryStatusQr(transData);
                    break;
                case TransConstant.TRANS_TYPE_REFUND_QRIS:
                    setRefundQr(transData);
                    break;
                case TransConstant.TRANS_TYPE_ANY_TRANS_QRIS:
                    setAnyQrisTrans(transData);
                    break;
            }
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

    public void setReversalData(TransData transData) {
        try {
            entity.setFieldValue(FieldConstant.MESSAGE_TYPE, TransConstant.MESSAGE_TYPE_REVERSAL);
            entity.setFieldValue(FieldConstant.BIT_RESERVED_PRIVATE_BIT63, "RR0201");
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

    public void setGenerateQrData(TransData transData) {
        setFinancialData(transData);
        setMandatoryData(transData);
        String bit59;
        StringBuilder sb = new StringBuilder();
        sb.append("20220125");
        sb.append(Utility.getInvoiceNum());
        sb.append("015");
        sb.append("01");

        /* paymentByChannel(2): 01(EDC Request) */
        sb.append("01");
        sb.append("                    ");

        /* qrResponseType(2): 02(TLV EMVCO STRING) */
        sb.append("02");

        bit59 = sb.toString();

        try {
            entity.setFieldValue(FieldConstant.BIT_CURRENCY_CODE_TRANSACTION, "0360");
            entity.setFieldValue(FieldConstant.BIT_ADDITIONAL_AMOUNTS,transData.getTip());
            //entity.setFieldValue(FieldConstant.BIT_RESERVED_NATIONAL_BIT57, "0360");
            entity.setFieldValue(FieldConstant.BIT_RESERVED_NATIONAL_BIT59,bit59 );

        } catch (Iso8583Exception e) {
            e.printStackTrace();
        }
    }

    public void setInquiryStatusQr(TransData transData) {
        setFinancialData(transData);
        setMandatoryData(transData);
        String bit59;
        StringBuilder sb = new StringBuilder();
        String merchantTransid = transData.getMerchantTransId();
        if(merchantTransid == null) {
            sb.append("              ");
        }
        else{
            sb.append(merchantTransid);
        }
        sb.append("01");


        String reffNo = transData.getReffNo();
        if(reffNo == null) {
            sb.append("                 ");
        }
        else{
            sb.append(reffNo);
        }
        sb.append("01");

        bit59 = sb.toString();

        try {
            entity.setFieldValue(FieldConstant.BIT_CURRENCY_CODE_TRANSACTION, "0360");
            entity.setFieldValue(FieldConstant.BIT_ADDITIONAL_AMOUNTS,"000000000000");
            // entity.setFieldValue(FieldConstant.BIT_ADDITIONAL_DATA_NATIONAL, "1");
            //  entity.setFieldValue(FieldConstant.BIT_RESERVED_NATIONAL_BIT57, "4D544930303733303033343935FA0005730034950000460000000000000000000031313030303803D748CA2DFDD5BF");
            entity.setFieldValue(FieldConstant.BIT_RESERVED_NATIONAL_BIT59,bit59 );

        } catch (Iso8583Exception e) {
            e.printStackTrace();
        }
    }

    public void setRefundQr(TransData transData) {
        setFinancialData(transData);
        setMandatoryData(transData);
        try {
            entity.setFieldValue(FieldConstant.BIT_AMOUNT_TRANSACTION,"000000000000");
            entity.setFieldValue(FieldConstant.BIT_CURRENCY_CODE_TRANSACTION,"0360");
            entity.setFieldValue(FieldConstant.BIT_ADDITIONAL_AMOUNTS,"000000000000");
            //entity.setFieldValue(FieldConstant.BIT_RESERVED_NATIONAL_BIT57, transData.getApprCode());

            String bit59;
            StringBuilder sb = new StringBuilder();
            String merchantTransid = transData.getMerchantTransId();
            if(merchantTransid == null) {
                sb.append("                 ");
            }
            else{
                sb.append(merchantTransid);
                sb.append("01");
            }


            String reffNo = transData.getReffNo();
            if(reffNo == null) {
                sb.append("                 ");
            }
            else{
                sb.append(Tools.paddingRight(reffNo,' ',20));
//                sb.append(reffNo);
//                sb.append("        ");
            }
            sb.append("01");

            bit59 = sb.toString();
            entity.setFieldValue(FieldConstant.BIT_RESERVED_NATIONAL_BIT59,bit59 );//Invoice No

        } catch (Iso8583Exception e) {
            e.printStackTrace();
        }
    }

    public void setAnyQrisTrans(TransData transData) {
        setFinancialData(transData);

        setMandatoryData(transData);
        String bit59;
        StringBuilder sb = new StringBuilder();
        String merchantTransid = transData.getMerchantTransId();
        if(merchantTransid == null) {
            sb.append("                 ");
        }
        else{
            sb.append(merchantTransid);
            sb.append("01");
        }
        String reffNo = transData.getReffNo();
        sb.append(Tools.paddingRight(reffNo,' ',20));
        sb.append("01");

        bit59 = sb.toString();
        try {
            entity.setFieldValue(FieldConstant.BIT_CURRENCY_CODE_TRANSACTION, "0360");
            entity.setFieldValue(FieldConstant.BIT_ADDITIONAL_AMOUNTS,"000000000000");
            // entity.setFieldValue(FieldConstant.BIT_ADDITIONAL_DATA_NATIONAL, "1");
            //  entity.setFieldValue(FieldConstant.BIT_RESERVED_NATIONAL_BIT57, "4D544930303733303033343935FA0005730034950000460000000000000000000031313030303803D748CA2DFDD5BF");
            entity.setFieldValue(FieldConstant.BIT_RESERVED_NATIONAL_BIT59,bit59 );

        } catch (Iso8583Exception e) {
            e.printStackTrace();
        }
    }

    private void setSettlementData(TransData transData) {
        setFinancialData(transData);
        setMandatoryData(transData);
        try {
            entity.setFieldValue(FieldConstant.BIT_ADDITIONAL_DATA_NATIONAL, "1");
            entity.setFieldValue(FieldConstant.BIT_RESERVED_NATIONAL_BIT60, Utility.getBatchNum()); //batch number
            entity.setFieldValue(FieldConstant.BIT_RESERVED_PRIVATE_BIT63, transData.getBit63());
        } catch (Iso8583Exception e) {
            e.printStackTrace();
        }
    }

}
