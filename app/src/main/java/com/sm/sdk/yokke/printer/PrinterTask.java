package com.sm.sdk.yokke.printer;

import android.graphics.Bitmap;
import android.os.RemoteException;
import android.view.View;

import com.sm.sdk.yokke.activities.MtiApplication;
import com.sm.sdk.yokke.utils.Tools;
import com.sunmi.peripheral.printer.InnerResultCallbcak;

import sunmi.sunmiui.utils.LogUtil;

public class PrinterTask {

    public PrinterTask() {
    }

    public void printBitmap(View content) {
        try {
            if (MtiApplication.app.sunmiPrinterService == null) {
                //showToast("Print not supported");
                return;
            }

            Bitmap bitmap = Tools.createViewBitmap(content);
//            bitmap = getBinaryzationBitmap(bitmap);
            MtiApplication.app.sunmiPrinterService.enterPrinterBuffer(true);
            MtiApplication.app.sunmiPrinterService.printBitmap(bitmap, new InnerResultCallbcak() {
                @Override
                public void onRunResult(boolean isSuccess) throws RemoteException {
                    LogUtil.e("TAG", "onRunResult-->isSuccess:" + isSuccess);
                }

                @Override
                public void onReturnString(String result) throws RemoteException {
                    LogUtil.e("TAG", "onReturnString-->result:" + result);
                }

                @Override
                public void onRaiseException(int code, String msg) throws RemoteException {
                    LogUtil.e("TAG", "onRaiseException-->code:" + code + ",msg:" + msg);
                }

                @Override
                public void onPrintResult(int code, String msg) throws RemoteException {
                    LogUtil.e("TAG", "onPrintResult-->code:" + code + ",msg:" + msg);
                }
            });
            MtiApplication.app.sunmiPrinterService.lineWrap(4, null);
            MtiApplication.app.sunmiPrinterService.exitPrinterBuffer(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
