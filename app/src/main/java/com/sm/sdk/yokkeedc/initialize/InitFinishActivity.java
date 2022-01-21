package com.sm.sdk.yokkeedc.initialize;

import androidx.appcompat.app.AppCompatActivity;

import com.sm.sdk.yokkeedc.MtiApplication;
import com.sm.sdk.yokkeedc.R;
import com.sm.sdk.yokkeedc.utils.Constant;
import com.sunmi.peripheral.printer.InnerResultCallbcak;
import com.sunmi.peripheral.printer.SunmiPrinterService;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.widget.TextView;

import java.util.LinkedHashMap;
import android.os.Handler;

import sunmi.sunmiui.utils.LogUtil;

public class InitFinishActivity extends AppCompatActivity {

    public static final String TAG = Constant.TAG;

    private TextView tvMerhcantName;
    private TextView tvMerchantAddr1;
    private TextView tvMerchantAddr2;
    private TextView tvMerchantAddr3;
    private TextView tvTID;
    private TextView tvMID;
    private TextView tvAID;

    private SunmiPrinterService sunmiPrinterService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init_finish);
        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                vdInitViewAndReceiptFormat(InitFinishActivity.this);
                printBitmap();
            }
        }, 2000);


    }

    private void vdInitViewAndReceiptFormat(Context context) {
        String strMerchantNM, strMerchantAddr1, strMerchantAddr2, strMerchantAddr3, strTID, strMID;
        LinkedHashMap<String, String> mapMerchantInfo = new LinkedHashMap<>();
//        Utilities util = new Utilities();
//
//        mapMerchantInfo = util.getMerchantInfo(context);
//        tvMerhcantName = (TextView) findViewById(R.id.text_merchant_name);
//        tvMerchantAddr1 = (TextView) findViewById(R.id.text_merchant_addr_1);
//        tvMerchantAddr2 = (TextView) findViewById(R.id.text_merchant_addr_2);
//        tvMerchantAddr3 = (TextView) findViewById(R.id.text_merchant_addr_3);
//        tvTID = (TextView) findViewById(R.id.text_tid_value);
//        tvMID = (TextView) findViewById(R.id.text_mid_value);
//
//        String AidList;
//
//        LinkedHashMap<String, String> mapAidList = new LinkedHashMap<>();
//        Utilities util2 = new Utilities();




        tvAID = (TextView) findViewById(R.id.aidTex);

        strMerchantNM = mapMerchantInfo.get("Site name").toString();
        strMerchantAddr1 = mapMerchantInfo.get("Address 1").toString();
        strMerchantAddr2 = mapMerchantInfo.get("Address 2").toString();
        strMerchantAddr3 = mapMerchantInfo.get("Address 3").toString();
        strTID = mapMerchantInfo.get("TID").toString();
        strMID = mapMerchantInfo.get("MID").toString();

        tvMerhcantName.setText(strMerchantNM);
        tvMerchantAddr1.setText(strMerchantAddr1);
        tvMerchantAddr2.setText(strMerchantAddr2);
        tvMerchantAddr3.setText(strMerchantAddr3);
        tvTID.setText(strTID);
        tvMID.setText(strMID);
    }

//    private static Bitmap bitmap;

    private void printBitmap() {
        try {
            if (MtiApplication.app.sunmiPrinterService == null) {
//                showToast("Print not supported");
                return;
            }
            View content = (View) findViewById(R.id.print_content);
            Bitmap bitmap = createViewBitmap(content);

//            content.post(new Runnable() {
//                @Override
//                public void run() {
//                    bitmap = createViewBitmap(content);
//                }
//            });

//            bitmap = getBinaryzationBitmap(bitmap);
            MtiApplication.app.sunmiPrinterService.enterPrinterBuffer(true);
            MtiApplication.app.sunmiPrinterService.printBitmap(bitmap, new InnerResultCallbcak() {
                @Override
                public void onRunResult(boolean isSuccess) throws RemoteException {
                    LogUtil.e(TAG, "onRunResult-->isSuccess:" + isSuccess);
                }

                @Override
                public void onReturnString(String result) throws RemoteException {
                    LogUtil.e(TAG, "onReturnString-->result:" + result);
                }

                @Override
                public void onRaiseException(int code, String msg) throws RemoteException {
                    LogUtil.e(TAG, "onRaiseException-->code:" + code + ",msg:" + msg);
                }

                @Override
                public void onPrintResult(int code, String msg) throws RemoteException {
                    LogUtil.e(TAG, "onPrintResult-->code:" + code + ",msg:" + msg);
                }
            });
            MtiApplication.app.sunmiPrinterService.lineWrap(4, null);
            MtiApplication.app.sunmiPrinterService.exitPrinterBuffer(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Create Bitmap by View */
    private Bitmap createViewBitmap(View v) {
        long start = System.currentTimeMillis();
        Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(),
                Bitmap.Config.ARGB_8888); //创建一个和View大小一样的Bitmap
        Canvas canvas = new Canvas(bitmap);  //使用上面的Bitmap创建canvas
        v.draw(canvas);  //把View画到Bitmap上
        LogUtil.e(TAG, "createViewBitmap time:" + (System.currentTimeMillis() - start));
        return bitmap;
    }

    /** Bitmap Binaryzation */
    private Bitmap getBinaryzationBitmap(Bitmap bm) {
        long start = System.currentTimeMillis();
        Bitmap bitmap = null;
        // 获取图片的宽和高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 创建二值化图像
        bitmap = bm.copy(Bitmap.Config.ARGB_8888, true);
        // 遍历原始图像像素,并进行二值化处理
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                // 得到当前的像素值
                int pixel = bitmap.getPixel(i, j);
                // 得到Alpha通道的值
                int alpha = pixel & 0xFF000000;
                // 得到Red的值
                int red = (pixel & 0x00FF0000) >> 16;
                // 得到Green的值
                int green = (pixel & 0x0000FF00) >> 8;
                // 得到Blue的值
                int blue = pixel & 0x000000FF;
                // 通过加权平均算法,计算出最佳像素值
                int gray = (int) ((float) red * 0.3 + (float) green * 0.59 + (float) blue * 0.11);
                // 对图像设置黑白图
                if (gray <= 95) {
                    gray = 0;
                } else {
                    gray = 255;
                }
                // 得到新的像素值
                int newPiexl = alpha | (gray << 16) | (gray << 8) | gray;
                // 赋予新图像的像素
                bitmap.setPixel(i, j, newPiexl);
            }
        }
        LogUtil.e(TAG, "getBinaryzationBitmap time:" + (System.currentTimeMillis() - start));
        return bitmap;
    }

    /** Create Bitmap by View */
    public Bitmap createBitmapFromView(View view) {
        Bitmap bitmap = null;
        //开启view缓存bitmap
        view.setDrawingCacheEnabled(true);
        //设置view缓存Bitmap质量
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        //获取缓存的bitmap
        Bitmap cache = view.getDrawingCache();
        if (cache != null && !cache.isRecycled()) {
            bitmap = Bitmap.createBitmap(cache);
        }
        //销毁view缓存bitmap
        view.destroyDrawingCache();
        //关闭view缓存bitmap
        view.setDrawingCacheEnabled(false);
        return bitmap;
    }
}