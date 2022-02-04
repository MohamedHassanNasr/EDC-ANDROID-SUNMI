package com.sm.sdk.yokke.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.sm.sdk.yokke.comm.InquiryQrTask;
import com.sm.sdk.yokke.models.BatchRecord;
import com.sm.sdk.yokke.models.QrTransData;
import com.sm.sdk.yokke.models.transData.TransData;
import com.sm.sdk.yokke.utils.Constant;
import com.sm.sdk.yokke.utils.CurrencyConverter;
import com.sm.sdk.yokke.utils.PrintTemplate;
import com.sm.sdk.yokke.utils.PrintUtil;
import com.sm.sdk.yokke.utils.ResponseCode;
import com.sm.sdk.yokke.utils.Tools;
import com.sm.sdk.yokke.utils.TransConstant;
import com.sm.sdk.yokke.utils.Utility;
import com.sm.sdk.yokke.view.DigitKeyboard;
import com.sm.sdk.yokke.view.EditorActionListener;
import com.sm.sdk.yokke.view.EnterAmountTextWatcher;
import com.sunmi.peripheral.printer.InnerResultCallbcak;

import java.util.Hashtable;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import sunmi.sunmiui.utils.LogUtil;

public class QrisActivity extends AppCompatActivity {
    private DigitKeyboard mDigitKeyboard;
    private View digitKeyboard;
    private Button btnContinue;
    private TextView tvEnterAmount, tvTimeQr, tvIdQris, tvTime, tvReffNo;
    private EditText etAmountQr;
    private ImageView ivQris_display, ivQris;
    private LinearLayout llTimeQr;
    private CardView btnPrintQr;
    private long duration;
    private long parseLong = 0 ;
    public String amount;
    public String tempAmount            = "";
    private int result_code = 0;
    Bitmap bitmap ;
    private ProgressDialog progressDialog;
    TransData transData;
    private String qrCode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qris);
        transData = new TransData();
        transData.setTransactionType(TransConstant.TRANS_TYPE_GENERATE_QRIS);
        transData.setProcCode(TransConstant.PROCODE_QRIS);
        initView();

    }
    private void initView() {
        mDigitKeyboard      = new DigitKeyboard(QrisActivity.this);
        etAmountQr          = findViewById(R.id.etAmountQr);
        mDigitKeyboard.attachTo(etAmountQr);
        btnContinue         = findViewById(R.id.btn_continue);
        tvEnterAmount       = findViewById(R.id.tvEnterAmount);
        llTimeQr            = findViewById(R.id.lltimeQr);
        ivQris_display      = findViewById(R.id.ivQris_display);
        ivQris              = findViewById(R.id.ivQris);
        btnPrintQr          = findViewById(R.id.btnPrintQr);
        tvIdQris            = findViewById(R.id.tvIdQris);
        digitKeyboard       = findViewById(R.id.view_keyboard);
        tvTimeQr            = findViewById(R.id.tvTimeQr);
        tvTime              = findViewById(R.id.tvTime);
        tvReffNo            = findViewById(R.id.tvReffNo);
        btnContinue.setVisibility(View.INVISIBLE);
        EditorActionListener editorActionListener = new EditorActionListener() {
            @Override
            protected void onKeyOk(TextView view) {
                amount = String.valueOf(CurrencyConverter.parse(etAmountQr.getText().toString()));
                tempAmount = amount;
                parseLong = Long.parseLong(amount);
                if (parseLong > 0){
                    digitKeyboard.setVisibility(View.INVISIBLE);
                    btnContinue.setVisibility(View.VISIBLE);
                }else if (parseLong <= 0){
                    Toast toast = Toast.makeText(QrisActivity.this, R.string.card_cost_hint, Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        };

        EnterAmountTextWatcher enterAmountTextWatcher = new EnterAmountTextWatcher();
        etAmountQr.setOnEditorActionListener(editorActionListener);
        etAmountQr.addTextChangedListener(enterAmountTextWatcher);

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amount = String.valueOf(CurrencyConverter.parse(etAmountQr.getText().toString()));
                transData.setAmount(amount);

                progressDialog = new ProgressDialog(QrisActivity.this);
                progressDialog.setMessage("Connect to server ...");
                progressDialog.setTitle("Send Receive Message");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.show();
                progressDialog.setCancelable(false);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            CommTask task = new CommTask(transData);
                            result_code = task.startCommTask();
                            //String rcCode = transData.getResponseCode();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        finally {
                            MtiApplication.getInstance().runOnUiThread(() ->{
                                if(result_code == Constant.RTN_COMM_SUCCESS){
                                    qrCode = transData.getGenerateQR();
                                    InquiryQrTask.deleteQrDataDb();
                                    InquiryQrTask.initQrData(transData);

                                    bitmap = createCodeBitmap(transData.getGenerateQR(), 300, 300);
                                    ivQris.setImageBitmap(bitmap);
                                    ivQris_display.setImageBitmap(bitmap);
                                    String time = transData.getTime();
                                    time        = "TIME:"+time.substring(0,2) + ":" + time.substring(2,4) + ":" + time.substring(4,6);
                                    tvTime.setText(time);
                                    String rrefNo = "REFF NO:"+transData.getReffNo();
                                    tvReffNo.setText(rrefNo);

                                    transData = new TransData();
                                    actionAfterInputAmount();
                                }
                            });
                        }
                        progressDialog.dismiss();
                    }
                }).start();
            }
        });

        btnPrintQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    printBitmap(R.id.print_content);
                    MtiApplication.app.sunmiPrinterService.setAlignment(1, null);
                    PrintTemplate.printQRCode(qrCode);
                    PrintTemplate.printString("NMID:ID02003233686\n",24);
                    printBitmap(R.id.print_content_line);
                    MtiApplication.app.sunmiPrinterService.lineWrap(4, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }
    private void actionAfterInputAmount(){
        etAmountQr.setVisibility(View.GONE);
        tvEnterAmount.setVisibility(View.GONE);
        btnContinue.setVisibility(View.GONE);
        llTimeQr.setVisibility(View.VISIBLE);
        ivQris_display.setVisibility(View.VISIBLE);
        btnPrintQr.setVisibility(View.VISIBLE);
        tvIdQris.setText("Print QRIS");
        digitKeyboard.setVisibility(View.GONE);
        timerQr();
    }

    private void timerQr()
    {
        //timer QRIS
        duration = TimeUnit.MINUTES.toMillis(1); //durasi menitnya
        new CountDownTimer(duration, 1000){ // lama detiknya
            @Override
            public void onTick(long l) {
                String sDuration = String.format(Locale.ENGLISH, "%02d : %02d"
                        , TimeUnit.MILLISECONDS.toMinutes(l)
                        , TimeUnit.MILLISECONDS.toSeconds(l) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(l)));
                tvTimeQr.setText(sDuration);
                String retCode;
                ResponseCode Rspcode = transData.getResponseCode();
                if (Rspcode == null){
                    retCode = null;
                }
                else {
                    retCode = Rspcode.getCode();
                }
                if ( TimeUnit.MILLISECONDS.toSeconds(l) % 20 ==0) {
                    if (!"00".equals(retCode)) {
                        inquiryStatusQr();
                    }
                    if("00".equals(retCode)){
                        cancel();
                        //Toast.makeText(getApplicationContext(), "SUCCESS CHECK KE HOST", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFinish() {

                ResponseCode Rspcode = transData.getResponseCode();
                String retmessage = Rspcode.getMessage();
                Toast.makeText(getApplicationContext(), "FAILED, Respon: "+retmessage, Toast.LENGTH_LONG).show();
                Intent i = new Intent(QrisActivity.this, MainActivity.class);
                startActivity(i);
            }
        }.start();
    }

    public void inquiryStatusQr(){
        int hideNav = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(hideNav);

        /*TES INQUIRY */
        QrTransData qrTransData;
        qrTransData = InquiryQrTask.getQrTransDataFromDb();

        transData = new TransData();
        transData.setMerchantTransId(qrTransData.getMerchantTransId());
        transData.setReffNo(qrTransData.getQRreffno());
        transData.setMID(qrTransData.getMid());
        transData.setTID(qrTransData.getTid());
        transData.setAmount(qrTransData.getAmount());

        transData.setTransactionType(TransConstant.TRANS_TYPE_INQUIRY_QRIS);
        transData.setProcCode(TransConstant.PROCODE_INQUIRY_QRIS);
        progressDialog = new ProgressDialog(QrisActivity.this);
        progressDialog.setMessage("Connect to server ...");
        progressDialog.setTitle("Send Receive Message");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        progressDialog.setCancelable(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                int result = 0;
                try {
                    CommTask task = new CommTask(transData);
                    result = task.startCommTask();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                finally {
                    if (result == Constant.RTN_COMM_SUCCESS) {
                        ResponseCode Rspcode = transData.getResponseCode();
                        String retCode = Rspcode.getCode();
                        String retmessage = Rspcode.getMessage();
                        if("00".equals(retCode)){
//                            InquiryQrTask.deleteQrDataDb();
                            BatchRecord batchRecord = new BatchRecord(transData);
                            batchRecord.setUseYN("Y");
                            Utility.saveTransactionToDb(batchRecord);
                            Intent intent = new Intent(QrisActivity.this, PrintQrActivity.class);
                            intent.putExtra(PrintQrActivity.EXTRA_TRANS,transData);
                            startActivity(intent);
                        }
                    }
                    else{
                        returnFailed();
                    }
                }
                progressDialog.dismiss();
            }
        }).start();
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

    private void printBitmap(int layout) {
        try {
            if (MtiApplication.app.sunmiPrinterService == null) {
                //showToast("Print not supported");
                return;
            }
            MtiApplication.app.sunmiPrinterService.setAlignment(1, null);
            View content = findViewById(layout);
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
            MtiApplication.app.sunmiPrinterService.lineWrap(1, null);
            MtiApplication.app.sunmiPrinterService.exitPrinterBuffer(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void returnFailed(){
        ResponseCode Rspcode = transData.getResponseCode();
        String retCode = Rspcode.getCode();
        String retmessage = Rspcode.getMessage();
        MtiApplication.getInstance().runOnUiThread(() -> {
            Toast.makeText(getApplicationContext(), "FAILED, Respon: " + retmessage, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(QrisActivity.this, MainActivity.class);
            startActivity(intent);
        });

    }

}
