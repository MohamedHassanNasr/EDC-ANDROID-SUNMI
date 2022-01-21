package com.sm.sdk.yokkeedc.comm;

import android.os.AsyncTask;
import android.util.Log;

import com.sm.sdk.yokkeedc.transaction.TransData;
import com.sm.sdk.yokkeedc.transaction.sale.PackTrans;
import com.sm.sdk.yokkeedc.transaction.sale.processor.MessageProcessor;
import com.sm.sdk.yokkeedc.transaction.sale.validator.MessageValidator;
import com.sm.sdk.yokkeedc.utils.Constant;
import com.sm.sdk.yokkeedc.utils.Tools;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashMap;

import sunmi.sunmiui.utils.LogUtil;

public class CommProcess extends AsyncTask<Object, Object, Integer> {
    //Object -> input type data doin
    protected static final String TAG = "Comm Process";

    protected String hostIp;
    protected int hostPort;
    private static CommProcess sInstance;
    //private byte[] sendData;
    //private byte[] recvData;
    private Runnable callback;

    private static final int DEFAULT_TIMEOUT = 30 * 1000; // 默认超时时间30s

    /**
     * todo : ganti jangan static
     */
    private String serverIP = "172.16.54.24";            // temp data
    private int serverPort = 5088;             // temp data

    private int connectTimeout = 2000;
    private int socketTimeout = 1000;
    private int receiveDataLen = 21;

    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;

    private TransData transData;

    public CommProcess(TransData transData) {
       //setCallback(callback);
       this.transData = transData;

    }

    public Runnable getCallback() {
        return callback;
    }

    public void setCallback(Runnable callback) {
        this.callback = callback;
    }

    public TransData getTransData() {
        return transData;
    }

    public void setTransData(TransData transData) {
        this.transData = transData;
    }

    /**
     * dapat digunakan untuk connect ke domain (mis. API.yokke.co.id) atau ke suatu ip
     * @param voids
     * @return
     */
    @Override
    protected Integer doInBackground(Object... voids) {
        TransData transData = getTransData();
        byte[] sendData;

        LogUtil.e("TEST KIRIM", "Connect to " + serverIP + " : " + serverPort);
        sendData = buildMessage(transData);
        try {
            if (sendData.length <= 0 || sendData.length > 1024 * 3) {
                return 0;
            }

            InetSocketAddress inetSocketAddress;
            try {
                boolean bool = false;
                char[] charArray = serverIP.toCharArray();
                for (char c : charArray) {
                    bool = Character.isLowerCase(c) || Character.isUpperCase(c);
                    if (bool) break;
                }
                if (bool) {
                    InetAddress inetAddress = InetAddress.getByName(serverIP);
                    inetSocketAddress = new InetSocketAddress(inetAddress, serverPort);
                } else {
                    inetSocketAddress = new InetSocketAddress(serverIP, serverPort);
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception(Constant.ANSWER_CODE_FAILED);
            }

            socket = new Socket();
            socket.setSoTimeout(socketTimeout); //timeout terima balasan
            socket.setSoLinger(true, 0);
            socket.connect(inetSocketAddress, connectTimeout); //timeout connect ke server
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
            outputStream.write(sendData, 0, sendData.length);
            outputStream.flush();

            while (inputStream.available() <= receiveDataLen) {
                Thread.sleep(50);
            }

            int index = 0;
            byte[] recBuff = new byte[2048];
            int len = inputStream.available();
            int recvLength = len;
            while (len > 0) {
                index += inputStream.read(recBuff, index, len);
                Thread.sleep(10);
                len = inputStream.available();

            }
            byte[] recvData = new byte[recvLength-2];
            System.arraycopy(recBuff, 2, recvData, 0, recvLength-2);

            Log.i(TAG, "RECEIVE from HOST: " + Tools.bcd2Str(recvData));
//            return recvData;

            HashMap<String, byte[]> receiveDataMap = parseMessage(recvData);
            int result =  MessageValidator.validateMessage(receiveDataMap, transData.getTransactionType());
            if(result == Constant.RTN_COMM_SUCCESS) {
                MessageProcessor.parseMessage(receiveDataMap, transData);
                return result;
            }

            return result;

//            recLen[0] = index;
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            Log.i(TAG, "ANSWER_CODE_TIMEOUT");
//            throw new Exception(Constant.ANSWER_CODE_TIMEOUT);
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "ANSWER_CODE_NETWORK");
//            throw new Exception(Constant.ANSWER_CODE_NETWORK);
        } finally {
            closePOSP();
        }
        return 0;
    }

//    @Override
//    protected void onPostExecute(Integer recvData) {
//        getCallback().run();
//    }

    private void closePOSP() {
        close(inputStream);
        close(outputStream);
        close(socket);

        inputStream = null;
        outputStream = null;
        socket = null;
    }

    private void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private PackTrans transIso = new PackTrans();

    public byte[] buildMessage(TransData transData) {
        byte[] sendData;
        byte[] req = transIso.pack(transData);
        Log.i(TAG, "REQ: " + Tools.bcd2Str(req));
        sendData = new byte[2 + req.length];
        sendData[0] = (byte) (req.length / 256);
        sendData[1] = (byte) (req.length % 256);
        System.arraycopy(req, 0, sendData, 2, req.length);
        Log.i(TAG, "SEND to HOST: " + Tools.bcd2Str(sendData));
        return sendData;
    }

    private HashMap<String, byte[]> parseMessage(byte[] resp) {

        return transIso.unpack(resp);
    }

    protected void onPostExecute(Integer result) {
        synchronized (this){
            this.notify();
        }
    }


}
