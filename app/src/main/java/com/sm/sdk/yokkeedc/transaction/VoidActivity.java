package com.sm.sdk.yokkeedc.transaction;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sm.sdk.yokkeedc.R;
import com.sm.sdk.yokkeedc.comm.CommProcess;
import com.sm.sdk.yokkeedc.database.BatchRecordDB;
import com.sm.sdk.yokkeedc.transaction.print.PrintActivity;
import com.sm.sdk.yokkeedc.transaction.sale.SaleActivity;
import com.sm.sdk.yokkeedc.utils.Tools;
import com.sm.sdk.yokkeedc.utils.TransConstant;
import com.sm.sdk.yokkeedc.utils.Utility;

public class VoidActivity extends AppCompatActivity {
    Button btnOkTrace, btnConfrim;
    EditText etTraceNo;
    TextView tvCardNo, tvExpDate, voidAmount;
    private ProgressDialog progressDialog;
    String trace, traceYN = "N";
    BatchRecord batchRecord;

    TransData transData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_void);
        initView();


        btnOkTrace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                trace = etTraceNo.getText().toString();
                batchRecord = Utility.getTransactionByTraceNo(trace, traceYN);
                Log.i("TAG","TEST");
                //batchRecord.getAmount();
                if (batchRecord != null){
                    String cardNo   = batchRecord.getCardNo();
                    String expDate  = batchRecord.getExpDate();
                    String amount   = batchRecord.getAmount();
                    tvCardNo.setText(Tools.separateWithSpace(cardNo));
                    tvExpDate.setText(expDate);
                    etTraceNo.setText(amount);
                    etTraceNo.setEnabled(false);
                    voidAmount.setVisibility(View.VISIBLE);
                    btnConfrim.setVisibility(View.VISIBLE);
                    btnOkTrace.setVisibility(View.GONE);
                }
                else{
                    etTraceNo.setText("");
                    Toast.makeText(getApplicationContext(), "Data Not Found or Already Void", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnConfrim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transData = new TransData();
                transData.setTransactionType(TransConstant.TRANS_TYPE_VOID);
                transData.setProcCode(TransConstant.PROCODO_VOID_SALE);
                transData.setCardType(batchRecord.getCardType());
                transData.setCardBrand(batchRecord.getCardBrand());
                transData.setONOFF(batchRecord.getONOFF());
                transData.setCardNo(batchRecord.getCardNo());
                transData.setExpDate(batchRecord.getExpDate());
                transData.setDate(batchRecord.getDate());
                transData.setTime(batchRecord.getTime());
                transData.setReffNo(batchRecord.getReffNo());
                transData.setApprCode(batchRecord.getApprCode());
                transData.setTraceNo(batchRecord.getTraceNo());
                transData.setInvoiceNo(batchRecord.getTraceNo());
                transData.setAmount(batchRecord.getAmount());

                progressDialog = new ProgressDialog(VoidActivity.this);
                progressDialog.setMessage("Connect to server ...");
                progressDialog.setTitle("Send Receive Message");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.show();
                progressDialog.setCancelable(false);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //Thread.sleep(3000);
                            AsyncTask task =  new CommProcess(transData);
                            task.execute();
                            synchronized (task){
                                task.wait();
                            }
                            String rcCode = "00";
                            if (rcCode == null || !rcCode.equals("00")){
                                startActivity(new Intent(getApplicationContext(), TransactionActivity.class));
                            }else{
                                traceYN = "Y";
                                Utility.updateTraceYN(trace,traceYN);
                                BatchRecord batchRecord = new BatchRecord(transData);
                                batchRecord.setUseYN("Y");
                                Utility.saveTransactionToDb(batchRecord);
                                Intent intent = new Intent(VoidActivity.this, PrintActivity.class);
                                intent.putExtra(PrintActivity.EXTRA_TRANS,transData);
                                startActivity(intent);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        progressDialog.dismiss();
                    }
                }).start();
            }
        });

    }

    private void initView(){
        btnOkTrace = findViewById(R.id.btn_okTrace);
        etTraceNo  = findViewById(R.id.etTrace);
        tvCardNo   = findViewById(R.id.tvCardNo);
        tvExpDate  = findViewById(R.id.tvExpDate);
        voidAmount = findViewById(R.id.voidAmount);
        btnConfrim = findViewById(R.id.btn_confirm_void);
    }
}