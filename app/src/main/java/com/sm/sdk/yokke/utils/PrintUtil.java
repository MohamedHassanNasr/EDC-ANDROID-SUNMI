package com.sm.sdk.yokke.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.RemoteException;

import com.sm.sdk.yokke.R;
import com.sm.sdk.yokke.activities.MtiApplication;

public class PrintUtil {
    private static final int DEFAULT_SIZE = 24;


    public static Bitmap getReceiptTitleIcon(int platform) {
        Bitmap bitmap;
        switch (platform) {
            case 0:
                bitmap = BitmapFactory.decodeResource(MtiApplication.getInstance().getResources(), R.drawable.log_yoki);
                break;
            default:
                bitmap = BitmapFactory.decodeResource(MtiApplication.getInstance().getResources(), R.drawable.yokke_print);
                break;
        }
//        if (bitmap.getWidth() > 105) {
//            double val = 1.0 * bitmap.getHeight() * 105 / bitmap.getWidth();
//            int newHeight = (int) val;
//            bitmap = BitmapUtil.scale(bitmap, 105, newHeight);
//        }
        return bitmap;
    }

    public static void printSeparateLine() throws Exception {
        MtiApplication.app.sunmiPrinterService.setAlignment(0, null);
        MtiApplication.app.sunmiPrinterService.printTextWithFont("--------------------------------\n", "", DEFAULT_SIZE, null);
    }

    /**
     * Set font bold
     */
    public static void setBold(boolean isBold) throws RemoteException {
        byte[] returnText = new byte[3];
        returnText[0] = 0x1B;
        returnText[1] = 0x45;
        if (isBold) {
            returnText[2] = 0x01; // 表示加粗
        } else {
            returnText[2] = 0x00;
        }
        MtiApplication.app.sunmiPrinterService.sendRAWData(returnText, null);
    }
}
