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
    private TextView tvEnterAmount, tvTimeQr, tvIdQris;
    private EditText etAmountQr;
    private ImageView ivQris_display, ivQris;
    private LinearLayout llTimeQr;
    private CardView btnPrintQr;
    private long parseLong = 0 ;
    public String amount;
    public String tempAmount            = "";
    private int result_code = 0;
    private ProgressDialog progressDialog;
    TransData transData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qris);
        transData = new TransData();
        transData.setTransactionType(TransConstant.TRANS_TYPE_GENERATE_QRIS);
        transData.setProcCode(TransConstant.PROCODO_QRIS);
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
                                    //InquiryQrTask.initQrData(transData);
                                    Intent intent = new Intent(QrisActivity.this, QrisPrintActivity.class);
                                    intent.putExtra(QrisPrintActivity.QRIS_TRANS,transData);
                                    startActivity(intent);
                                }
                            });
                        }
                        progressDialog.dismiss();
                    }
                }).start();
            }
        });

    }

}
