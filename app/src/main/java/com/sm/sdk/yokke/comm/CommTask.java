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

import java.util.HashMap;

public class CommTask {
    private static final String TAG = "CommTask";
    private static TransData transData;
    private static int transStatus;

    public CommTask(TransData transData) {
        this.transData = transData;
    }

    public CommTask(byte[] message) {

    }

    public static int getTransStatus() {
        return transStatus;
    }

    public static void setTransStatus(int transStatus) {
        CommTask.transStatus = transStatus;
    }

    public static int startCommTask() {
        byte[] sendData;

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
        sendData = buildMessage(transData, packTrans);
        if (sendData.length <= 0 || sendData.length > 1024 * 3) {
            //return 0;
        }

        ReversalTask.initReversalData(transData, packTrans);
        setTransStatus(Constant.RTN_COMM_ERROR);
        AsyncTask task = new OnlineProcess(sendData, new AsyncResponse() {
            @Override
            public void processFinish(TransactionResult transResult) {
                int result;
                byte[] recvMsg = transResult.getRecvMessage();

                if(recvMsg != null && transResult.getTransStatus() == Constant.RTN_COMM_SUCCESS) {
                    HashMap<String, byte[]> receiveDataMap = parseMessage(recvMsg);
                    result = MessageValidator.validateMessage(receiveDataMap, transData.getTransactionType());
                    if (result == Constant.RTN_COMM_SUCCESS) {
                        MessageProcessor.parseMessage(receiveDataMap, transData);
                        ReversalTask.deleteReversalDataDb();
                        setTransStatus(Constant.RTN_COMM_SUCCESS);
                    }
                } else if(transResult.getTransStatus() == Constant.RTN_COMM_TIMEOUT) {
                    result = ReversalTask.startReversalTask();
                    if(result == Constant.RTN_COMM_SUCCESS) {
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

        return getTransStatus();

    }

    public static byte[] buildMessage(TransData transData, PackIso8583 packager) {
        byte[] sendData;
        byte[] req = packager.pack(transData, false);
        Log.i(TAG, "REQ: " + Tools.bcd2Str(req));
        sendData = new byte[2 + req.length];
        sendData[0] = (byte) (req.length / 256);
        sendData[1] = (byte) (req.length % 256);
        System.arraycopy(req, 0, sendData, 2, req.length);
        Log.i(TAG, "SEND to HOST: " + Tools.bcd2Str(sendData));
        return sendData;
    }

    private static HashMap<String, byte[]> parseMessage(byte[] output) {
        PackTrans transIso = new PackTrans();
        return transIso.unpack(output);
    }
}