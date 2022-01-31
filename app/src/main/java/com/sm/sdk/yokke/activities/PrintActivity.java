package com.sm.sdk.yokke.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sm.sdk.yokke.R;
import com.sm.sdk.yokke.models.transData.TransData;
import com.sm.sdk.yokke.utils.Tools;
import com.sm.sdk.yokke.utils.TransConstant;
import com.sunmi.peripheral.printer.InnerResultCallbcak;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Locale;

import sunmi.sunmiui.utils.LogUtil;

public class PrintActivity extends AppCompatActivity {

    TransData transData;
    TextView tvNumCard, tvExpDate, tvAmount, tvReffNo, tvAppr, tvCardType, tvTime, tvDate, tvTraceNo, tvStatus;
    TextView tvCardTypeDisplay, tvNumCard_display, tvSearchCard_display, tvExpDate_display, tvStatus_display;
    TextView tvBatch_display, tvTrace_display, tvDate_display, tvTime_display, tvReffNo_display, tvAppr_display;
    TextView tvAmount_display;
    TextView printStatus;
    Button btnPrint, btnCancel;
    Boolean statusReceipt = true;
    int index = 0;

    public static String EXTRA_TRANS = "extra_trans";
    Handler handler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print);
        initView();
        setDisplayData();
        printReceipt();
        printReceiptCancel();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (index == 0){
                    printBitmap();
                    statusReceipt = false;
                    index++;
                }
            }
        }, 500);
        printStatus.setText("Print For Bank?");
    }

    private void printReceiptCancel() {
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (index == 2){
                    Intent intent = new Intent(PrintActivity.this, TransactionActivity.class);
                    startActivity(intent);
                }
                else{
                    printStatus.setText("Print For Merchant?");
                    index++;
                }
            }
        });
    }

    private void printReceipt() {
        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusReceipt = true;
                if (statusReceipt){
                    if (index == 1){
                        printStatus.setText("Print For Merchant?");
                        printBitmap();
                        statusReceipt = false;
                        index++;
                    }
                    else if (index == 2){
                        printBitmap();
                        statusReceipt = false;
                        Intent intent = new Intent(PrintActivity.this, TransactionActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });
    }

    private void initView(){
        /**
         * Find ID For Struck
         */
        btnPrint    = findViewById(R.id.btnPrint);
        btnCancel   = findViewById(R.id.btnClose);
        printStatus = findViewById(R.id.printStatus);

        tvExpDate   = findViewById(R.id.tvExpDate);
        tvNumCard   = findViewById(R.id.tvNumCard);
        tvAmount    = findViewById(R.id.tvAmount);
        tvAppr      = findViewById(R.id.tvAppr);
        tvReffNo    = findViewById(R.id.tvReffNo);
        tvCardType  = findViewById(R.id.tvCardType);
        tvTime      = findViewById(R.id.tvTime);
        tvDate      = findViewById(R.id.tvDate);
        tvTraceNo   = findViewById(R.id.tvTrace);
        tvStatus    = findViewById(R.id.tvStatus);

        /**
         * Find ID For Display Data
         */
        tvCardTypeDisplay       = findViewById(R.id.tvCardType_display);
        tvNumCard_display       = findViewById(R.id.tvNumCard_display);
        tvSearchCard_display    = findViewById(R.id.tvSearchCard_display);
        tvExpDate_display       = findViewById(R.id.tvExpDate_display);
        tvStatus_display        = findViewById(R.id.tvStatus_display);
        tvBatch_display         = findViewById(R.id.tvBatch_display);
        tvTrace_display         = findViewById(R.id.tvTrace_display);
        tvDate_display          = findViewById(R.id.tvDate_display);
        tvTime_display          = findViewById(R.id.tvTime_display);
        tvReffNo_display        = findViewById(R.id.tvReffNo_display);
        tvAppr_display          = findViewById(R.id.tvAppr_display);
        tvAmount_display        = findViewById(R.id.tvAmount_display);
    }

    private void setDisplayData(){
        TransData transData = getIntent().getParcelableExtra(EXTRA_TRANS);
        tvNumCard.setText(Tools.separateWithSpace(transData.getCardNo()));
        tvNumCard_display.setText(Tools.separateWithSpace(transData.getCardNo()));

        tvStatus.setText(transData.getTransactionType());
        tvStatus_display.setText(transData.getTransactionType());

        String date = transData.getDate();
        String time = transData.getTime();
        time        = "TIME:"+time.substring(0,2) + ":" + time.substring(2,4) + ":" + time.substring(4,6);

        int year = Calendar.getInstance().get(Calendar.YEAR);

        String dateTime = "DATE:"+date.substring(2,4) + "/" + date.substring(0,2) + "/" + year;

        tvTime.setText(time);
        tvTime_display.setText(time);

        tvDate.setText(dateTime);
        tvDate_display.setText(dateTime);

        String cardType = "CARD TYPE : " + transData.getCardBrand() + " " + transData.getONOFF() + " " + transData.getCardType();
        tvCardType.setText(cardType);
        tvCardTypeDisplay.setText(cardType);

        String cardExpDate = transData.getExpDate();
        cardExpDate = cardExpDate.substring(2, 4) + "/" +cardExpDate.substring(0, 2);
        tvExpDate.setText(cardExpDate);
        tvExpDate_display.setText(cardExpDate);

        int amountIDR = Integer.parseInt(transData.getAmount());
        String amountNow = NumberFormat.getCurrencyInstance(new Locale("id","ID")).format(amountIDR);

        if (transData.getTransactionType().equals(TransConstant.TRANS_TYPE_VOID)){
            tvAmount.setText("- "+amountNow);
            tvAmount_display.setText("- "+amountNow);
        }else{
            tvAmount.setText(amountNow);
            tvAmount_display.setText(amountNow);
        }


        String apprCd;
        apprCd = "APPR CODE : " + transData.getApprCode();
        tvAppr.setText(apprCd);
        tvAppr_display.setText(apprCd);

        String refNo;
        refNo = "REF NO : " + transData.getReffNo();
        tvReffNo.setText(refNo);
        tvReffNo_display.setText(refNo);

        String traceNo;
        traceNo = "TRACE NO: " + transData.getTraceNo();
        tvTraceNo.setText(traceNo);
        tvTrace_display.setText(traceNo);
    }

    private void printBitmap() {
        try {
            if (MtiApplication.app.sunmiPrinterService == null) {
                //showToast("Print not supported");
                return;
            }
            View content = findViewById(R.id.print_content);
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
