package com.sm.sdk.yokke.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.sm.sdk.yokke.R;
import com.sm.sdk.yokke.comm.CommTask;
import com.sm.sdk.yokke.comm.InquiryQrTask;
import com.sm.sdk.yokke.models.BatchRecord;
import com.sm.sdk.yokke.models.QrTransData;
import com.sm.sdk.yokke.models.transData.TransData;
import com.sm.sdk.yokke.utils.Constant;
import com.sm.sdk.yokke.utils.ResponseCode;
import com.sm.sdk.yokke.utils.TransConstant;
import com.sm.sdk.yokke.utils.Utility;

import sunmi.sunmiui.utils.LogUtil;

public class MenuOptActivity extends AppCompatActivity {
    CardView btnPrintQr, btnLast, btnAny, btnRefund;
    TransData transData;
    ProgressDialog progressDialog;
    QrTransData qrTransData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_opt);
        initView();
    }

    private void initView() {
        btnPrintQr = findViewById(R.id.btn_printQris);
        btnLast = findViewById(R.id.btn_lastTrx);
        btnAny = findViewById(R.id.btn_anyTrx);
        btnRefund = findViewById(R.id.btn_refundQris);

        btnPrintQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MenuOptActivity.this, QrisActivity.class);
                startActivity(i);
            }
        });

        btnLast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qrTransData = InquiryQrTask.getQrTransDataFromDb();
                if(qrTransData == null){
                    Toast.makeText(getApplicationContext(),"NOT FOUND",Toast.LENGTH_LONG).show();
                    return;
                }
                else {
                    Toast.makeText(getApplicationContext(),"FOUND",Toast.LENGTH_LONG).show();
                    inquiryStatusQr();
                }
            }
        });

        btnAny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MenuOptActivity.this, AnyTrxQrActivity.class);
                startActivity(i);
            }
        });

        btnRefund.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MenuOptActivity.this, RefundQrisActivity.class);
                startActivity(i);
            }
        });
    }

    public void inquiryStatusQr(){

        /*TES INQUIRY */
        transData = new TransData();
        transData.setMerchantTransId(qrTransData.getMerchantTransId());
        transData.setReffNo(qrTransData.getQRreffno());
        transData.setMID(qrTransData.getMid());
        transData.setTID(qrTransData.getTid());
        transData.setAmount(qrTransData.getAmount());

        transData.setTransactionType(TransConstant.TRANS_TYPE_INQUIRY_QRIS);
        transData.setProcCode(TransConstant.PROCODE_INQUIRY_QRIS);
        progressDialog = new ProgressDialog(MenuOptActivity.this);
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
//                            InquiryQrTask.initQrData(transData);
                            BatchRecord findreffid = Utility.getRefIDByRefNo(transData.getReffId());
                            if (findreffid == null){
                                BatchRecord batchRecord = new BatchRecord(transData);
                                batchRecord.setUseYN("Y");
                                Utility.saveTransactionToDb(batchRecord);
                            }
                            Intent intent = new Intent(MenuOptActivity.this, PrintQrActivity.class);
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

    public void returnFailed(){
        ResponseCode Rspcode = transData.getResponseCode();
        String retCode = Rspcode.getCode();
        String retmessage = Rspcode.getMessage();
        MtiApplication.getInstance().runOnUiThread(() -> {
            Toast.makeText(getApplicationContext(), "FAILED, Respon: " + retmessage, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MenuOptActivity.this, MainActivity.class);
            startActivity(intent);
        });

    }

}