package com.sm.sdk.yokkeedc.transaction.qris;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.nfc.Tag;
import android.os.AsyncTask;
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
import com.sm.sdk.yokkeedc.MainActivity;
import com.sm.sdk.yokkeedc.MtiApplication;
import com.sm.sdk.yokkeedc.R;
import com.sm.sdk.yokkeedc.comm.CommProcess;
import com.sm.sdk.yokkeedc.transaction.TransData;
import com.sm.sdk.yokkeedc.transaction.print.PrintActivity;
import com.sm.sdk.yokkeedc.transaction.sale.SaleActivity;
import com.sm.sdk.yokkeedc.utils.CurrencyConverter;
import com.sm.sdk.yokkeedc.utils.Tools;
import com.sm.sdk.yokkeedc.utils.TransConstant;
import com.sm.sdk.yokkeedc.view.DigitKeyboard;
import com.sm.sdk.yokkeedc.view.EditorActionListener;
import com.sm.sdk.yokkeedc.view.EnterAmountTextWatcher;
import com.sunmi.peripheral.printer.InnerResultCallbcak;

import java.util.Hashtable;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import sunmi.sunmiui.utils.LogUtil;

public class QrisActivity extends AppCompatActivity {
    private DigitKeyboard mDigitKeyboard;
    private View digitKeyboard;
    private Button btnContinue;
    private TextView tvEnterAmount, tvTimeQr, tvIdQris;
    private EditText etAmountQr;
    private ImageView ivQris;
    private LinearLayout llTimeQr;
    private CardView btnPrintQr;
    private long duration;
    private long parseLong = 0 ;
    public String amount;
    public String tempAmount            = "";
    public final static int QRcodeWidth = 500 ;
    Bitmap bitmap ;
    private ProgressDialog progressDialog;
    TransData transData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qris);
        transData = new TransData();
        transData.setTransactionType(TransConstant.TRANS_TYPE_QRIS);
        transData.setProcCode(TransConstant.PROCODO_QRIS);
        initView();

    }
    private void initView() {
        mDigitKeyboard = new DigitKeyboard(QrisActivity.this);
        etAmountQr = findViewById(R.id.etAmountQr);
        mDigitKeyboard.attachTo(etAmountQr);
        btnContinue = findViewById(R.id.btn_continue);
        tvEnterAmount = findViewById(R.id.tvEnterAmount);
        llTimeQr = findViewById(R.id.lltimeQr);
        ivQris = findViewById(R.id.ivQris);
        btnPrintQr = findViewById(R.id.btnPrintQr);
        tvIdQris = findViewById(R.id.tvIdQris);
        digitKeyboard = findViewById(R.id.view_keyboard);
        tvTimeQr = findViewById(R.id.tvTimeQr);

        EditorActionListener editorActionListener = new EditorActionListener() {
            @Override
            protected void onKeyOk(TextView view) {
                amount = String.valueOf(CurrencyConverter.parse(etAmountQr.getText().toString()));
                tempAmount = amount;
                parseLong = Long.parseLong(amount);
                if (parseLong > 0){
                    digitKeyboard.setVisibility(View.INVISIBLE);
                }else if (parseLong <= 0){
                    Toast toast = Toast.makeText(QrisActivity.this, R.string.card_cost_hint, Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        };

        EnterAmountTextWatcher enterAmountTextWatcher = new EnterAmountTextWatcher();
        etAmountQr.setOnEditorActionListener(editorActionListener);
        etAmountQr.addTextChangedListener(enterAmountTextWatcher);

        String qrString = "00020101021226690021ID.CO.BANKMANDIRI.WWW01189360000800000000570211710002044420303UMI51440014ID.CO.QRIS.WWW0215ID10200325336860303UMI520430065303360540417005802ID5908FRONTEND6013Jakarta Barat61051181062280708730029035012120016175645630441F8";

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
                            AsyncTask task =  new CommProcess(transData);
                            task.execute();
                            synchronized (task){
                                task.wait();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        finally {
                            MtiApplication.getInstance().runOnUiThread(() ->{
                                if(transData.getResponseCode().equals("00")){
                                    bitmap = createCodeBitmap(transData.getGenerateQR(), 300, 300);
                                    ivQris.setImageBitmap(bitmap);
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
                printBitmap();
            }
        });

    }
    private void actionAfterInputAmount(){
        etAmountQr.setVisibility(View.GONE);
        tvEnterAmount.setVisibility(View.GONE);
        btnContinue.setVisibility(View.GONE);
        llTimeQr.setVisibility(View.VISIBLE);
        ivQris.setVisibility(View.VISIBLE);
        btnPrintQr.setVisibility(View.VISIBLE);
        tvIdQris.setText("Print QRIS");
        digitKeyboard.setVisibility(View.GONE);

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
                Toast toast = Toast.makeText(QrisActivity.this,"QRIS berakhir",Toast.LENGTH_LONG);
                toast.show();
                Intent i = new Intent(QrisActivity.this, MainActivity.class);
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
}