package com.sm.sdk.yokke.comm;

import android.os.AsyncTask;
import android.util.Log;

import com.sm.sdk.yokke.activities.MtiApplication;
import com.sm.sdk.yokke.isopacker.PackIso8583;
import com.sm.sdk.yokke.models.ReversalData;
import com.sm.sdk.yokke.models.transData.TransData;
import com.sm.sdk.yokke.models.transData.TransactionResult;
import com.sm.sdk.yokke.utils.Constant;
import com.sm.sdk.yokke.utils.Tools;

import java.util.List;

public class ReversalTask extends BaseTask{
    private static String TAG = "ReversalTask";

    private ReversalTask() {
    }

    public static int startReversalTask() {
        byte[] reversalMessage;
        final int[] result = new int[1];
        reversalMessage = getReversalData();
        if(reversalMessage != null) {

            AsyncTask task = new OnlineProcess(reversalMessage, new AsyncResponse() {
                @Override
                public void processFinish(TransactionResult transactionResult) {
                    result[0] = transactionResult.getTransStatus();
                }

            });
            task.execute();
            synchronized (task){
                try {
                    task.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        else {
            result[0] = Constant.RTN_COMM_ERROR;
        }
        return result[0];
    }

    public static void initReversalData(TransData transData, PackIso8583 packager) {
        byte[] reversalMessage;

        reversalMessage = buildMessage(transData, packager, true);
        ReversalData data = new ReversalData(reversalMessage);

        MtiApplication.getReversalDataDBHelper().insertReversalDataDb(data);
    }

    private static ReversalData getReversalDataFromDb() {
        List<ReversalData> lstReversal;
        lstReversal = MtiApplication.getReversalDataDBHelper().selectAllReversalDataDb();
        if(lstReversal.size() > 1) {
            Log.e(TAG,"Error reversal data is more than 1");
            return null;
        }

        if(lstReversal != null) {
            for(ReversalData rd : lstReversal) {
                return rd;
            }
        }
        return null;
    }

    /**
     * @return true jika ada data reversal di database
     */
    private static byte[] getReversalData() {
        ReversalData reversalData;
        //check database reversal
        reversalData = getReversalDataFromDb();
        if(reversalData == null) {
            return null;
        }
        else {
            return reversalData.getMessage();
        }
    }

    public static void deleteReversalDataDb() {
        MtiApplication.getReversalDataDBHelper().deleteAllReversalDataDb();
    }
}
