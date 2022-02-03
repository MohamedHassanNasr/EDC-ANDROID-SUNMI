package com.sm.sdk.yokke.comm;

import android.content.Context;
import android.util.Log;

import com.sm.sdk.yokke.isopacker.PackIso8583;
import com.sm.sdk.yokke.models.transData.TransData;
import com.sm.sdk.yokke.transaction.sale.PackTrans;
import com.sm.sdk.yokke.utils.Tools;

import java.util.HashMap;

public class BaseTask {

    private static final String TAG = "BaseTask";
    private static Context context;

    public BaseTask() {
    }

    public static byte[] buildMessage(TransData transData, PackIso8583 packager, boolean isReversal) {
        byte[] sendData;
        byte[] req = packager.pack(transData,isReversal);
        Log.i(TAG, "REQ: " + Tools.bcd2Str(req));
        sendData = new byte[2 + req.length];
        sendData[0] = (byte) (req.length / 256);
        sendData[1] = (byte) (req.length % 256);
        System.arraycopy(req, 0, sendData, 2, req.length);
        Log.i(TAG, "SEND to HOST: " + Tools.bcd2Str(sendData));
        return sendData;
    }

    public static HashMap<String, byte[]> parseMessage(byte[] output) {
        PackTrans transIso = new PackTrans();
        return transIso.unpack(output);
    }
}
