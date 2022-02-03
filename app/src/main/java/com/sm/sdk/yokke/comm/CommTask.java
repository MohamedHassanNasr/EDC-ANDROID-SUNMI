package com.sm.sdk.yokke.comm;

import android.os.AsyncTask;
import android.util.Log;

import com.sm.sdk.yokke.isopacker.PackIso8583;
import com.sm.sdk.yokke.models.transData.TransData;
import com.sm.sdk.yokke.models.transData.TransactionResult;
import com.sm.sdk.yokke.transaction.processor.MessageProcessor;
import com.sm.sdk.yokke.transaction.sale.PackTrans;
import com.sm.sdk.yokke.transaction.validator.MessageValidator;
import com.sm.sdk.yokke.utils.Constant;
import com.sm.sdk.yokke.utils.Tools;
import com.sm.sdk.yokke.utils.TransConstant;

import java.util.HashMap;

public class CommTask extends BaseTask{
    private static final String TAG = "CommTask";
    private static TransData transData;
    private static int transStatus;

    public CommTask(TransData transData) {
        this.transData = transData;
    }

    public CommTask(byte[] message) {

    }

    private static TransactionResult tempTransResult;

    private static byte[] sendData;
    private static int result;

    public static int getTransStatus() {
        return transStatus;
    }

    public static void setTransStatus(int transStatus) {
        CommTask.transStatus = transStatus;
    }

    public static int startCommTask() {

        //final int[] taskStatus = new int[1];

        /**
         * melakukan cek ke db reversal apakah ada transaksi yang belum di reversal
         * jika ada, maka akan kirim reversal transaksi lama terlebih dahulu kemudian lanjut
         * ke proses transaksi yang baru
         */
        if(ReversalTask.startReversalTask() == Constant.RTN_COMM_SUCCESS) {
            ReversalTask.deleteReversalDataDb();
        }
        PackTrans packTrans = new PackTrans();
        sendData = buildMessage(transData, packTrans, false);
        if (sendData.length <= 0 || sendData.length > 1024 * 3) {
            //return 0;
        }

//        ReversalTask.initReversalData(transData, packTrans);
        setTransStatus(Constant.RTN_COMM_ERROR);
        AsyncTask task = new OnlineProcess(sendData, new AsyncResponse() {
            @Override
            public void processFinish(TransactionResult transResult) {
                int result;
                byte[] recvMsg = transResult.getRecvMessage();
                tempTransResult = transResult;

                if(recvMsg != null && transResult.getTransStatus() == Constant.RTN_COMM_SUCCESS) {
                    HashMap<String, byte[]> receiveDataMap = parseMessage(recvMsg);
                    result = MessageValidator.validateMessage(receiveDataMap, transData.getTransactionType());
                    if (result == Constant.RTN_COMM_SUCCESS) {
                        MessageProcessor.parseMessage(receiveDataMap, transData);
                        ReversalTask.deleteReversalDataDb();
                        setTransStatus(Constant.RTN_COMM_SUCCESS);
                    }
                }
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
        if(tempTransResult.getTransStatus() == Constant.RTN_COMM_TIMEOUT)
        {
            result = ReversalTask.startReversalTask();
            if (result == Constant.RTN_COMM_SUCCESS) {
                ReversalTask.deleteReversalDataDb();
                setTransStatus(Constant.RTN_COMM_REVERSAL_COMPLETE);
            } else {
                setTransStatus(Constant.RTN_COMM_ERROR);
            }
        }

        return getTransStatus();

    }
}