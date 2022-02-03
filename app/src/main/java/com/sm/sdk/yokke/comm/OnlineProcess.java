package com.sm.sdk.yokke.comm;

import android.os.AsyncTask;
import android.util.Log;

import com.sm.sdk.yokke.models.transData.TransData;
import com.sm.sdk.yokke.models.transData.TransactionResult;
import com.sm.sdk.yokke.utils.Constant;
import com.sm.sdk.yokke.utils.Tools;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

import sunmi.sunmiui.utils.LogUtil;

public class OnlineProcess extends AsyncTask<Object, Object, TransactionResult> {
    //Object -> input type data doin
    protected static final String TAG = "Comm Process";

    protected String hostIp;
    protected int hostPort;
    private static OnlineProcess sInstance;
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
    private long maxRecvDataTimeout = 60000; //60 detik = 60000 milisekon

    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;

    private TransData transData;
    private byte[] message;

    public AsyncResponse delegate = null;


    //    public OnlineProcess(TransData transData) {
//       //setCallback(callback);
//       this.transData = transData;
//
//    }
    public OnlineProcess(byte[] message, AsyncResponse delegate) {
        this.message = message;
        this.delegate = delegate;
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
    protected TransactionResult doInBackground(Object... voids) {
//        TransData transData = getTransData();
        byte[] sendData;
        long currentTime, startTime;

        LogUtil.e("TEST KIRIM", "Connect to " + serverIP + " : " + serverPort);

        try {


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


//            sendData = buildMessage(transData);
//            if (sendData.length <= 0 || sendData.length > 1024 * 3) {
//                return 0;
//            }

            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
            sendData = getMessage();
            outputStream.write(sendData, 0, sendData.length);
            outputStream.flush();

            startTime = System.currentTimeMillis();
            while (inputStream.available() <= receiveDataLen) {
                Thread.sleep(50);
                currentTime = System.currentTimeMillis();
                if(currentTime - startTime >= maxRecvDataTimeout) {
                    closePOSP();
                    return new TransactionResult(Constant.RTN_COMM_TIMEOUT,null);
                }
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

            if(recvData != null) {
                System.arraycopy(recBuff, 2, recvData, 0, recvLength-2);
                Log.i(TAG, "RECEIVE from HOST: " + Tools.bcd2Str(recvData));
                closePOSP();
                return new TransactionResult(Constant.RTN_COMM_SUCCESS,recvData);
            }

//            recLen[0] = index;
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            Log.i(TAG, "ANSWER_CODE_TIMEOUT");
            return new TransactionResult(Constant.RTN_COMM_TIMEOUT,null);
//            throw new Exception(Constant.ANSWER_CODE_TIMEOUT);
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "ANSWER_CODE_NETWORK");
//            throw new Exception(Constant.ANSWER_CODE_NETWORK);
        }
        closePOSP();
        return new TransactionResult(Constant.RTN_COMM_ERROR,null);
    }

    public byte[] getMessage() {
        return message;
    }

    public void setMessage(byte[] message) {
        this.message = message;
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

    protected void onPostExecute(TransactionResult result) {
        synchronized (this){
            this.notify();
            delegate.processFinish(result);
        }
        //return 123;
    }




}
