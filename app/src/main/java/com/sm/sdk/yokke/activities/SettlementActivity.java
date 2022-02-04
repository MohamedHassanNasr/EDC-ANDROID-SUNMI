package com.sm.sdk.yokke.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.sm.sdk.yokke.R;
import com.sm.sdk.yokke.comm.CommTask;
import com.sm.sdk.yokke.comm.InquiryQrTask;
import com.sm.sdk.yokke.fragment.ReportFragment;
import com.sm.sdk.yokke.fragment.ReportQrisFragment;
import com.sm.sdk.yokke.models.transData.TransData;
import com.sm.sdk.yokke.printer.PrinterTask;
import com.sm.sdk.yokke.transaction.report.AmountTransInfo;
import com.sm.sdk.yokke.transaction.report.ReportData;
import com.sm.sdk.yokke.transaction.report.ReportTask;
import com.sm.sdk.yokke.utils.Constant;
import com.sm.sdk.yokke.utils.TransConstant;
import com.sm.sdk.yokke.utils.Utility;

import java.util.ArrayList;
import java.util.List;

public class SettlementActivity extends AppCompatActivity {

    private static String TAG = "SETTLEMENT ACTIVITY";
    private static ProgressDialog progressDialog;
    Button btnPrint, btnClose;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settlement);

        TransData transData = new TransData();
        transData.setTransactionType(TransConstant.TRANS_TYPE_SETTLEMENT);
        transData.setProcCode(TransConstant.PROCODE_SETTLEMENT);

        settlementProcess(transData);
    }


    private void settlementProcess(TransData transData) {

        AmountTransInfo amountTransInfo = null;
        boolean dataIsExist;

        List<ReportData> lstReportData = new ArrayList<>();

        ReportTask reportTask = new ReportTask(SettlementActivity.this, lstReportData);
        dataIsExist = reportTask.countDataFromBatch();

        if(dataIsExist) {
            String bit63 = setBit63Settlement(reportTask);
            transData.setBit63(bit63);
            progressDialog = new ProgressDialog(SettlementActivity.this);
            progressDialog.setMessage("Connect to server ...");
            progressDialog.setTitle("Send Receive Message");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();
            progressDialog.setCancelable(false);
            new Thread(new Runnable() {
                @Override
                public void run() {

                    CommTask task = new CommTask(transData);
                    int result = task.startCommTask();
                    if(result == Constant.RTN_COMM_SUCCESS) {
                        resultSuccessSettlement();
                    }
                    else {
                        MtiApplication.getInstance().runOnUiThread(() -> {
                            Toast.makeText(SettlementActivity.this, "Settlement Failed", Toast.LENGTH_LONG);
                            startActivity(new Intent(SettlementActivity.this, MainActivity.class));
                        });
                    }
                    progressDialog.dismiss();
                }

            }).start();


        }
        else {
            MtiApplication.getInstance().runOnUiThread(() -> {
                Toast.makeText(SettlementActivity.this, "No Record Found", Toast.LENGTH_LONG);
                startActivity(new Intent(SettlementActivity.this, MainActivity.class));
            });

            //return Constant.RTN_COMM_ERROR;
        }

    }

    private void resultSuccessSettlement() {
        MtiApplication.getInstance().runOnUiThread(() -> {
            printSettlement();
        });
        MtiApplication.getBatchRecordDBHelper().deleteAllBatchRecord();
        Utility.updateBatchNum();
        InquiryQrTask.deleteQrDataDb();

        btnClose = findViewById(R.id.btnCloseSettlement);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettlementActivity.this, MainActivity.class));
            }
        });

        btnPrint = findViewById(R.id.btnPrintSettlement);

        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        PrinterTask printer = new PrinterTask();
                        printer.printBitmap(findViewById(R.id.print_content_settlement));
                        printer.printBitmap(findViewById(R.id.print_content_settlement_qr));
                    }
                }, 500);
                startActivity(new Intent(SettlementActivity.this, MainActivity.class));
            }
        });
    }

    private void printSettlement() {
        Fragment reportFragment = new ReportFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_settlement, reportFragment).commit();
        Fragment reportFragmentQr = new ReportQrisFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_settlement_qr, reportFragmentQr).commit();

    }

    private static String setBit63Settlement(ReportTask reportTask) {
        List<ReportData> lstReportData = reportTask.getListReportData();

        String bit63 = "";
        StringBuilder sb = new StringBuilder(bit63);
        for(ReportData data : lstReportData) {
            String saleCounter = Utility.setPaddingDataFromLongToString(data.getAmountInfo().getSaleCounter(), 3);
            String saleAmount = Utility.setPaddingDataFromLongToString(data.getAmountInfo().getSaleAmount(), 10) + "00";
            String refundCounter = Utility.setPaddingDataFromLongToString(data.getAmountInfo().getRefundCounter(), 3);
            String refundAmount = Utility.setPaddingDataFromLongToString(data.getAmountInfo().getRefundAmount(), 10) + "00";

            sb.append(saleCounter);
            sb.append(saleAmount);
            sb.append(refundCounter);
            sb.append(refundAmount);
            sb.append(data.getBit44());
            sb.append(data.getBankCode().substring(1,4));

        }
        return sb.toString();
    }
}
