package com.sm.sdk.yokke.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.sm.sdk.yokke.R;
import com.sm.sdk.yokke.fragment.TransactionFragment;
import com.sm.sdk.yokke.models.transData.TransData;
import com.sm.sdk.yokke.utils.Tools;
import com.sm.sdk.yokke.utils.TransConstant;
import com.sm.sdk.yokke.utils.Utility;
import com.sunmi.peripheral.printer.InnerResultCallbcak;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Locale;

import sunmi.sunmiui.utils.LogUtil;

public class PrintQrActivity extends AppCompatActivity {
    public static String EXTRA_TRANS = "extra_trans";
    TextView tvTid, tvMid, tvDate, tvMercPan, tvNameIssuer, tvReffNo, tvTime, tvTextNameCust, tvTextCustPan,
            tvTextReffId, tvTextTip, tvTextNominal, tvTotalAmount, tvNameAcq, tvQrisPay;

    TextView tvTid_print, tvMid_print, tvDate_print, tvMercPan_print, tvNameIssuer_print, tvReffNo_print, tvTime_print, tvTextNameCust_print, tvTextCustPan_print,
            tvTextReffId_print, tvTextTip_print, tvTextNominal_print, tvTotalAmount_print, tvNameAcq_print, tvQrisPay_print;

    Button btnPrint, btnClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_qr);
        initView();
    }

    private void initView(){
        tvTid        = findViewById(R.id.tvTid);
        tvMid        = findViewById(R.id.tvMid);
        tvDate       = findViewById(R.id.tvDate);
        tvMercPan    = findViewById(R.id.tvMercPan);
        tvNameIssuer = findViewById(R.id.tvNameIssuer);
        tvReffNo     = findViewById(R.id.tvReffNo);
        tvTime       = findViewById(R.id.tvTime);
        tvTextNameCust  = findViewById(R.id.tvTextNameCust);
        tvTextCustPan   = findViewById(R.id.tvTextCustPan);
        tvTextReffId    = findViewById(R.id.tvTextReffId);
        tvTextTip       = findViewById(R.id.tvTextTip);
        tvTextNominal   = findViewById(R.id.tvTextNominal);
        tvTotalAmount   = findViewById(R.id.tvTotalAmount);
        tvNameAcq       = findViewById(R.id.tvNameAcq);
        tvQrisPay       = findViewById(R.id.tvQrisPay);

        //Print
        tvTid_print        = findViewById(R.id.tvTid_print);
        tvMid_print        = findViewById(R.id.tvMid_print );
        tvDate_print       = findViewById(R.id.tvDate_print );
        tvMercPan_print    = findViewById(R.id.tvMercPan_print );
        tvNameIssuer_print  = findViewById(R.id.tvNameIssuer_print );
        tvReffNo_print      = findViewById(R.id.tvReffNo_print );
        tvTime_print        = findViewById(R.id.tvTime_print );
        tvTextNameCust_print   = findViewById(R.id.tvTextNameCust_print );
        tvTextCustPan_print    = findViewById(R.id.tvTextCustPan_print );
        tvTextReffId_print     = findViewById(R.id.tvTextReffId_print );
        tvTextTip_print        = findViewById(R.id.tvTextTip_print );
        tvTextNominal_print    = findViewById(R.id.tvTextNominal_print );
        tvTotalAmount_print    = findViewById(R.id.tvTotalAmount_print );
        tvNameAcq_print        = findViewById(R.id.tvNameAcq_print );
        tvQrisPay_print        = findViewById(R.id.tvQrisPay_print);

        btnPrint        = findViewById(R.id.btnPrint);
        btnClose        = findViewById(R.id.btnClose);

        setDisplayData();

        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                printBitmap();
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PrintQrActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }

    private void setDisplayData(){
        TransData transData = getIntent().getParcelableExtra(EXTRA_TRANS);
        tvTid.setText("TID:"+transData.getMemberBankTid());

        tvMid.setText("MID:"+transData.getMemberBankMid());

        if (TransConstant.TRANS_TYPE_REFUND_QRIS.equals(transData.getTransactionType())){
            tvQrisPay_print.setText("QRIS REFUND");
            tvQrisPay.setText("QRIS REFUND");
        }

        String AcqName = Utility.getAcquirerName(transData.getAcqBankCode());
        AcqName = "Nama Acquirer:"+AcqName;
        tvNameAcq.setText(AcqName);

        String date = transData.getDate();
        int year = Calendar.getInstance().get(Calendar.YEAR);
        String dateTime = "DATE:"+date.substring(2,4) + "/" + date.substring(0,2) + "/" + year;

        tvDate.setText(dateTime);

        tvMercPan.setText("Merc PAN:"+transData.getMercPan());

        String time = transData.getTime();
        time        = "TIME:"+time.substring(0,2) + ":" + time.substring(2,4) + ":" + time.substring(4,6);
        tvTime.setText(time);

        tvReffNo.setText("Reff No:"+transData.getReffNo());

        tvTextNameCust.setText(transData.getCustName());

        tvTextCustPan.setText(transData.getCustPan());

        tvTextReffId.setText(transData.getReffId());

        String amount = transData.getAmount();
        int amountIDR = Integer.parseInt(amount);
        String amountNow = NumberFormat.getCurrencyInstance(new Locale("id","ID")).format(amountIDR);

        tvTotalAmount.setText(amountNow);
        tvTextNominal.setText(amountNow);


        //Print
        tvTid_print.setText("TID:"+transData.getMemberBankTid());
        tvMid_print.setText("MID:"+transData.getMemberBankMid());
        tvNameAcq_print.setText(AcqName);
        tvDate_print.setText(dateTime);
        tvMercPan_print.setText("Merc PAN:"+transData.getMercPan());
        tvTime_print.setText(time);
        tvReffNo_print.setText("Reff No:"+transData.getReffNo());
        tvTextNameCust_print.setText(transData.getCustName());
        tvTextCustPan_print.setText(transData.getCustPan());
        tvTextReffId_print.setText(transData.getReffId());
        tvTotalAmount_print.setText(amountNow);
        tvTextNominal_print.setText(amountNow);

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
