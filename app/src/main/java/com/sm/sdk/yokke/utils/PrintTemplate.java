package com.sm.sdk.yokke.utils;

import android.graphics.Bitmap;
import android.os.RemoteException;

import com.sm.sdk.yokke.activities.MtiApplication;

public class PrintTemplate {
    public static void printLogo(int platform) throws Exception {
        //setHeight(0X16);
        Bitmap bitmap = PrintUtil.getReceiptTitleIcon(platform);
        MtiApplication.app.sunmiPrinterService.setAlignment(1, null);
        MtiApplication.app.sunmiPrinterService.printBitmap(bitmap, null);

        printLine(2);
    }

    /**
     * Set font line height
     */
    public static void setHeight(int height) throws RemoteException {
        byte[] returnText = new byte[3];
        returnText[0] = 0x1B;
        returnText[1] = 0x33;
        returnText[2] = (byte) height;
        MtiApplication.app.sunmiPrinterService.sendRAWData(returnText, null);
    }

    public static void printLine(int line) throws Exception {
        MtiApplication.app.sunmiPrinterService.lineWrap(line, null);
    }

    public static void printQRCode(String data) {
        try {
            if (MtiApplication.app.sunmiPrinterService == null) {
                //Toast.makeText(, "Print not supported", Toast.LENGTH_LONG).show();
                return;
            }
            MtiApplication.app.sunmiPrinterService.enterPrinterBuffer(true);
            MtiApplication.app.sunmiPrinterService.setAlignment(1, null);
            MtiApplication.app.sunmiPrinterService.printQRCode(data,5,1, null);
            MtiApplication.app.sunmiPrinterService.exitPrinterBuffer(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void printTID(String data, int alignment, int fontSize){
        try {
            MtiApplication.app.sunmiPrinterService.setAlignment(alignment,null);
            MtiApplication.app.sunmiPrinterService.setFontSize(fontSize,null);
            MtiApplication.app.sunmiPrinterService.printText(data,null);
            MtiApplication.app.sunmiPrinterService.exitPrinterBuffer(true);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static void printMID(String data, int alignment, int fontSize){
        try {
            MtiApplication.app.sunmiPrinterService.setAlignment(alignment,null);
            MtiApplication.app.sunmiPrinterService.setFontSize(fontSize,null);
            MtiApplication.app.sunmiPrinterService.printText(data,null);
            MtiApplication.app.sunmiPrinterService.exitPrinterBuffer(true);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }
}
