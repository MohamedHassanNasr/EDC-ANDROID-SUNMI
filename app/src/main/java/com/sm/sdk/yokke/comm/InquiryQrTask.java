package com.sm.sdk.yokke.comm;

import android.util.Log;

import com.sm.sdk.yokke.activities.MtiApplication;
import com.sm.sdk.yokke.models.QrTransData;
import com.sm.sdk.yokke.models.transData.TransData;

import java.util.List;

public class InquiryQrTask {
    private static String TAG = "InquiryTask";

    public InquiryQrTask(){

    }

    public static void deleteQrDataDb() {
        MtiApplication.getQrDataDHelper().deleteAllQrDataDb();

    }

    public static void initQrData(TransData transData) {

        QrTransData qrTransData = new QrTransData();
        qrTransData.setMerchantTransId(transData.getMerchantTransId());
        qrTransData.setMid(transData.getMID());
        qrTransData.setTid(transData.getTID());
        qrTransData.setQRreffno(transData.getReffNo());
        MtiApplication.getQrDataDHelper().insertQrtransData(qrTransData);


    }

    private static QrTransData getQrTransDataFromDb() {
        List<QrTransData> qrTransData;
        qrTransData = MtiApplication.getQrDataDHelper().selectAllQRDataDb();
        if(qrTransData.size() > 1) {
            Log.e(TAG,"Error reversal data is more than 1");
            return null;
        }

        if(qrTransData != null) {
            for(QrTransData rd : qrTransData) {
                return rd;
            }
        }
        return null;
    }

    /**
     * @return true jika ada data reversal di database
     */
    private static QrTransData getQrTransData() {
        QrTransData qrTransData;
        //check database reversal
        qrTransData = getQrTransDataFromDb();
        if(qrTransData == null) {
            return null;
        }
        else {
            return qrTransData;
        }
    }
}
