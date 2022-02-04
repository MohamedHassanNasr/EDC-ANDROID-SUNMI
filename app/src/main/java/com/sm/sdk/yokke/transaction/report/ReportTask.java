package com.sm.sdk.yokke.transaction.report;

import android.content.Context;
import android.util.Log;

import androidx.fragment.app.Fragment;

import com.sm.sdk.yokke.activities.MtiApplication;
import com.sm.sdk.yokke.models.BatchRecord;
import com.sm.sdk.yokke.utils.TransConstant;

import java.util.List;

public class ReportTask extends Fragment{
    private static final String TAG = "REPORT TASK";
    protected static List<ReportData> lstReportData;

    protected static Context context;
//    private static AmountTransInfo amountTransInfo;

    public ReportTask(Context context, List<ReportData> lstReportData) {
        this.context = context;
        this.lstReportData = lstReportData;
//        this.amountTransInfo = amountTransInfo;
    }

    public ReportTask() {

    }

    /**
     * fungsi ini untuk menghitung data dari batch, kemudian disimpan ke dalam model ReportData
     * model ReportData kemudian disimpan dalam list lstSettlementData
     * @return true jika ada data di dalam batch
     */
    public boolean countDataFromBatch() {
        long amount, counter;
        List<BatchRecord> lstBatch = MtiApplication.getBatchRecordDBHelper().getAllBatchRecord();

        if(lstBatch == null || lstBatch.size() == 0) {
            Log.e(TAG,"No Record Found");
            return false;
        }
        Log.e(TAG, "Batch Data Total(s) : " + String.valueOf(lstBatch.size()));
        for (BatchRecord batch : lstBatch) {
            amount = 0; counter = 0;
            ReportData reportData = getSettlementData(batch);
            AmountTransInfo amountTransInfo = reportData.getAmountInfo();
            switch (batch.getTransactionType()) {

                case TransConstant.TRANS_TYPE_SALE:
                    amount = reportData.getAmountInfo().getSaleAmount() + Long.parseLong(batch.getAmount());
                    counter = reportData.getAmountInfo().getSaleCounter() + 1;
                    amountTransInfo.setSaleAmount(amount);
                    amountTransInfo.setSaleCounter(counter);
                    reportData.setAmountInfo(amountTransInfo);
                    break;

                case TransConstant.TRANS_TYPE_VOID:
                    amount = reportData.getAmountInfo().getVoidAmount() + Long.parseLong(batch.getAmount());
                    counter = reportData.getAmountInfo().getVoidCounter() + 1;
                    amountTransInfo.setSaleAmount(amount);
                    amountTransInfo.setSaleCounter(counter);
                    reportData.setAmountInfo(amountTransInfo);
                    break;

                case TransConstant.TRANS_TYPE_INQUIRY_QRIS:
                case TransConstant.TRANS_TYPE_ANY_TRANS_QRIS:
                    amount = reportData.getAmountInfo().getQrisSaleAmount() + Long.parseLong(batch.getAmount());
                    counter = reportData.getAmountInfo().getQrisSaleCounter() + 1;
                    amountTransInfo.setQrisSaleAmount(amount);
                    amountTransInfo.setQrisSaleCounter(counter);
                    reportData.setAmountInfo(amountTransInfo);
                    break;

                case TransConstant.TRANS_TYPE_REFUND_QRIS:
                    amount = reportData.getAmountInfo().getQrisRefundAmount() + Long.parseLong(batch.getAmount());
                    counter = reportData.getAmountInfo().getQrisRefundCounter() + 1;
                    amountTransInfo.setQrisSaleAmount(amount);
                    amountTransInfo.setQrisSaleCounter(counter);
                    reportData.setAmountInfo(amountTransInfo);
                    break;

                default:
                    Log.e(TAG, "Transaction Type null");
                    break;
            }
            updateSettlementData(reportData);
        }

        return true;
    }

    public AmountTransInfo countGrandTotalSettlement() {
        long saleAmount = 0, voidAmount = 0, refundAmount = 0, totalAmount = 0;
        AmountTransInfo amountTransInfo = new AmountTransInfo();

        for(ReportData stData : lstReportData) {
            saleAmount += stData.getAmountInfo().getSaleAmount();
            voidAmount += stData.getAmountInfo().getVoidAmount();
            refundAmount += stData.getAmountInfo().getRefundAmount();
        }
        amountTransInfo.setSaleAmount(saleAmount);
        amountTransInfo.setVoidAmount(voidAmount);
        amountTransInfo.setRefundAmount(refundAmount);
        return amountTransInfo;
    }

    private ReportData getSettlementData(BatchRecord batch) {
        if(lstReportData.size() == 0) {
            ReportData reportData = new ReportData(batch);
            lstReportData.add(reportData);
            Log.i(TAG, "Return new Settlement Data");
            return reportData;
        }
        for (ReportData stData : lstReportData) {
            String bit441 = stData.getBit44();
            if(stData.isQrDomestikTransFlag() && stData.getBit44() == null) {
                return stData;
            }
            else if(stData.getBit44() != null) {
                String bit44 = batch.getCardTypeBit44();
                if(bit44.equals(stData.getBit44())) {
                    Log.i(TAG, "Return Existing Settlement Data");
                    return stData;
                }
            }

            else {
                ReportData reportData = new ReportData(batch);
                lstReportData.add(reportData);
                Log.i(TAG, "Return new Settlement Data");
                return reportData;
            }
        }

        return null;
    }

    private void updateSettlementData(ReportData reportData) {
        int index = 0;
        for(ReportData sd : lstReportData) {
            if(sd.getBit44() != null) {
                if(reportData.getBit44().equals(sd.getBit44())) {
                    index = lstReportData.indexOf(sd);
                    lstReportData.set(index, reportData);
                    break;
                }
            }
            else {
                if(sd.isQrDomestikTransFlag()) {
                    index = lstReportData.indexOf(sd);
                    lstReportData.set(index, reportData);
                    break;
                }
            }

        }
    }

    public AmountTransInfo checkQrisDomestikTrans(List<ReportData> lstReportData) {
        for(ReportData data : lstReportData) {
            if(data.isQrDomestikTransFlag()) {
                return data.getAmountInfo();
            }
            else {
                return null;
            }
        }
        return null;
    }

    public List<ReportData> getListReportData() {
        return lstReportData;
    }

}
