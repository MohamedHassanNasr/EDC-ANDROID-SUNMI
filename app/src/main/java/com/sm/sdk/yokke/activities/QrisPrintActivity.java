package com.sm.sdk.yokke.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.sm.sdk.yokke.R;
import com.sm.sdk.yokke.comm.CommTask;
import com.sm.sdk.yokke.models.BatchRecord;
import com.sm.sdk.yokke.models.transData.TransData;
import com.sm.sdk.yokke.utils.Constant;
import com.sm.sdk.yokke.utils.ResponseCode;
import com.sm.sdk.yokke.utils.Tools;
import com.sm.sdk.yokke.utils.TransConstant;
import com.sm.sdk.yokke.utils.Utility;
import com.sunmi.peripheral.printer.InnerResultCallbcak;

import java.util.Hashtable;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import sunmi.sunmiui.utils.LogUtil;

public class QrisPrintActivity extends AppCompatActivity {

    public static String QRIS_TRANS = "qris_trans";
    private long duration;
    private String response;
    Bitmap bitmap ;
    TransData transData;
    private TextView tvTimeQr, tvIdQris;
    private ImageView ivQris_display, ivQris;
    private LinearLayout llTimeQr;
    private CardView btnPrintQr;
    private int counter = 1;
    private int result = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qris_print);
        initView();

    }

    private void initView(){
        llTimeQr            = findViewById(R.id.lltimeQr);
        ivQris_display      = findViewById(R.id.ivQris_display);
        ivQris              = findViewById(R.id.ivQris);
        btnPrintQr          = findViewById(R.id.btnPrintQr);
        tvIdQris            = findViewById(R.id.tvIdQris);
        tvTimeQr            = findViewById(R.id.tvTimeQr);
        timerQr();

        TransData transData = getIntent().getParcelableExtra(QRIS_TRANS);
        bitmap = createCodeBitmap(transData.getGenerateQR(), 300, 300);
        ivQris.setImageBitmap(bitmap);
        ivQris_display.setImageBitmap(bitmap);

        transData.setTransactionType(TransConstant.TRANS_TYPE_INQUIRY_QRIS);
        transData.setProcCode(TransConstant.PROCODE_INQUIRY_QRIS);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (result != 1 || counter <= 5){
                    try {
                        Thread.sleep(20000);
                        CommTask task = new CommTask(transData);
                        result = task.startCommTask();
                        response = String.valueOf(transData.getResponseCode());
                        LogUtil.i("TAG","CLICK BUTTON PRINT QR ---"+response+"---");
                        if (result == Constant.RTN_COMM_SUCCESS && ("00").equals(response)) {
                            BatchRecord batchRecord = new BatchRecord(transData);
                            batchRecord.setUseYN("Y");
                            Utility.saveTransactionToDb(batchRecord);
                            result = 1;
                        }
                        counter++;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        counter++;

    }

    private void timerQr()
    {
        //timer QRIS
        duration = TimeUnit.MINUTES.toMillis(2); //durasi menitnya
        new CountDownTimer(duration, 1000){ // lama detiknya
            @Override
            public void onTick(long l) {
                String sDuration = String.format(Locale.ENGLISH, "%02d : %02d"
                        , TimeUnit.MILLISECONDS.toMinutes(l)
                        , TimeUnit.MILLISECONDS.toSeconds(l) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(l)));
                tvTimeQr.setText(sDuration);
            }
            @Override
            public void onFinish() {
                Toast toast = Toast.makeText(QrisPrintActivity.this,"QRIS berakhir",Toast.LENGTH_LONG);
                toast.show();
                Intent i = new Intent(QrisPrintActivity.this, MainActivity.class);
                startActivity(i);
            }
        }.start();
    }

    public static Bitmap createCodeBitmap(String content, int width, int height) {
        return createCodeBitmap(content, width, height, "UTF-8", "H", "0", 0xff000000, 0xffffffff);
    }
    public static Bitmap createCodeBitmap(String content, int width, int height, String characterSet, String errorLevel, String margin, int blackColor, int whiteColor) {
        try {

            Hashtable<EncodeHintType, String> hashtable = new Hashtable<>();
            hashtable.put(EncodeHintType.CHARACTER_SET, characterSet);
            hashtable.put(EncodeHintType.ERROR_CORRECTION, errorLevel);
            hashtable.put(EncodeHintType.MARGIN, margin);
            BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hashtable);

            int[] pixels = new int[width * height];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {

                    boolean bool = bitMatrix.get(x, y);
                    if (bool) {
                        pixels[y * width + x] = blackColor; // 黑色色块像素设置
                    } else {
                        pixels[y * width + x] = whiteColor; // 白色色块像素设置
                    }
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void printBitmap() {
        try {
            if (MtiApplication.app.sunmiPrinterService == null) {
                //showToast("Print not supported");
                return;
            }
            View content = findViewById(R.id.print_content_qr);
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

//    public void returnFailed(){
//        ResponseCode Rspcode = transData.getResponseCode();
//        String retCode = Rspcode.getCode();
//        String retmessage = Rspcode.getMessage();
//        MtiApplication.getInstance().runOnUiThread(() -> {
//            Toast.makeText(getApplicationContext(), "FAILED, Respon: " + retmessage, Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(QrisPrintActivity.this, MainActivity.class);
//            startActivity(intent);
//        });
//    }

}
