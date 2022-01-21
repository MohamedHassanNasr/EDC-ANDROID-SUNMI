package com.sm.sdk.yokkeedc.comm;

import android.os.AsyncTask;
import android.util.Log;

import com.sm.sdk.yokkeedc.transaction.TransData;
import com.sm.sdk.yokkeedc.transaction.sale.PackTrans;
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

import sunmi.sunmiui.utils.LogUtil;

public class CommProcess extends AsyncTask<Void, Void, Void> {
    protected static final String TAG = "Comm Process";

    protected String hostIp;
    protected int hostPort;
    private static CommProcess sInstance;
    private byte[] sendData = null;

    private static final int DEFAULT_TIMEOUT = 30 * 1000; // 默认超时时间30s

    private String serverIP = "172.16.54.24";            // 服务IP
    private int serverPort = 5088;             // 服务器端口

    private int connectTimeout = 20;    // 连接超时时间 单位秒
    private int socketTimeout = 10;     // 读超时时间 单位秒
    private int receiveDataLen = 21;    // 默认返回报文长度校验

    private Socket socket;              // Socket对象
    private InputStream inputStream;    // Socket输入流
    private OutputStream outputStream;  // Socket输出流


    @Override
    protected Void doInBackground(Void... voids) {

        LogUtil.e("TEST KIRIM", "Connect to " + serverIP + " : " + serverPort);
        buildMessage(TransData.getInstance());
        try {
            if (sendData.length <= 0 || sendData.length > 1024 * 3) {
                return null;
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
            socket.setSoTimeout(socketTimeout);
            socket.setSoLinger(true, 0);
            socket.connect(inetSocketAddress, connectTimeout);
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
            while (len > 0) {
                index += inputStream.read(recBuff, index, len);
                Thread.sleep(10);
                len = inputStream.available();
            }
            parseMessage(recBuff);
            Log.i(TAG, "RECEIVE from HOST: " + Tools.bcd2Str(recBuff));

            // 接收的总字节数
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
        return null;
    }

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



//    private CommProcess() {
//
//    }
//
//    public static CommProcess getInstance() {
//        if (sInstance == null) {
//            sInstance = new CommProcess();
//        }
//        return sInstance;
//    }


    private PackTrans transIso = new PackTrans();

    public void buildMessage(TransData transData) {

        byte[] req = transIso.pack(transData);
        Log.i(TAG, "REQ: " + Tools.bcd2Str(req));
        sendData = new byte[2 + req.length];
        sendData[0] = (byte) (req.length / 256);
        sendData[1] = (byte) (req.length % 256);
        System.arraycopy(req, 0, sendData, 2, req.length);
        Log.i(TAG, "SEND to HOST: " + Tools.bcd2Str(sendData));
    }

    public void parseMessage(byte[] resp) {
        transIso.unpack(resp);
    }


}
