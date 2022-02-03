package com.sm.sdk.yokke.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sm.sdk.yokke.R;
import com.sm.sdk.yokke.comm.CommTask;
import com.sm.sdk.yokke.comm.InquiryQrTask;
import com.sm.sdk.yokke.models.transData.TransData;
import com.sm.sdk.yokke.utils.Constant;
import com.sm.sdk.yokke.utils.ResponseCode;
import com.sm.sdk.yokke.utils.TransConstant;

public class AnyTrxQrActivity extends AppCompatActivity {
    private static int LAST_TRX = 1;
    TextView tvIdMenu;
    EditText etReffNo;
    Button btn_continue;
    private TransData transData;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_any_trx_qr);
        transData = new TransData();
        initView();
    }

    private void initView() {
        etReffNo = findViewById(R.id.etReffNo);
        btn_continue = findViewById(R.id.btn_continue);
        transData.setTransactionType(TransConstant.TRANS_TYPE_ANY_TRANS_QRIS);
        transData.setProcCode(TransConstant.PROCODE_INQUIRY_QRIS);

        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transData.setReffNo(etReffNo.getText().toString());
                progressDialog = new ProgressDialog(AnyTrxQrActivity.this);
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
                                    InquiryQrTask.deleteQrDataDb();
                                    InquiryQrTask.initQrData(transData);

                                    MtiApplication.getInstance().runOnUiThread(() ->{
                                        Toast.makeText(getApplicationContext(), "SUCCESS ", Toast.LENGTH_SHORT).show();
                                    });
                                }
                                else{
                                    MtiApplication.getInstance().runOnUiThread(() ->{
                                        Toast.makeText(getApplicationContext(), "FAILED, Respon: "+retmessage, Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(AnyTrxQrActivity.this, MainActivity.class);
                                        startActivity(intent);
                                    });
                                }
                            }
                            else if(result == Constant.RTN_COMM_REVERSAL_COMPLETE) {
                                Toast.makeText(AnyTrxQrActivity.this, "Reversal Completed", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(AnyTrxQrActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                            else{
                                returnFailed();
                            }
                        }
                        progressDialog.dismiss();
                    }
                }).start();
            }
        });
    }

    public void returnFailed(){
        MtiApplication.getInstance().runOnUiThread(() ->{
            Toast.makeText(getApplicationContext(), "TIME OUT ", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(AnyTrxQrActivity.this, MainActivity.class);
            startActivity(intent);

        });
    }
}