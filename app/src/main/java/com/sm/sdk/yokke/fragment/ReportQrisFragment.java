package com.sm.sdk.yokke.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.sm.sdk.yokke.R;
import com.sm.sdk.yokke.models.BatchRecord;
import com.sm.sdk.yokke.models.transData.TransData;
import com.sm.sdk.yokke.transaction.report.AmountTransInfo;
import com.sm.sdk.yokke.transaction.report.ReportData;
import com.sm.sdk.yokke.transaction.report.ReportTask;
import com.sm.sdk.yokke.utils.Utility;

import java.util.List;

import sunmi.sunmiui.utils.LogUtil;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class ReportQrisFragment extends ReportTask {

    private static String TAG = "ReportQrisFragment";
    TextView tvSaleQrisCounterDisp, tvSaleQrisAmountDisp, tvRefundQrisCounterDisp, tvRefundQrisAmountDisp, tvTidDisp, tvMidDisp, tvBatchDisplay;
    TextView tvSaleQrisCounterPrint, tvSaleQrisAmountPrint, tvRefundQrisCounterPrint, tvRefundQrisAmountPrint, tvTidPrint, tvMidPrint, tvBatch;
    TransData transData;
    public ReportQrisFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    Handler handler = new Handler();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report_qris, container, false);

        initView(view);
        printAndDisplaySettlementQr(view);

        return view;
    }

    private void initView(View view) {
        tvSaleQrisCounterDisp = view.findViewById(R.id.tvSaleQrCounter_display);
        tvSaleQrisAmountDisp = view.findViewById(R.id.tvTotalSaleQrAmount_display);
        tvRefundQrisCounterDisp = view.findViewById(R.id.tvRefundQrCounter_display);
        tvRefundQrisAmountDisp = view.findViewById(R.id.tvTotalRefundQrAmount_display);
        tvTidDisp = view.findViewById(R.id.tvTid_display);
        tvMidDisp = view.findViewById(R.id.tvMid_display);
        tvBatchDisplay = view.findViewById(R.id.tvBatchDisplay);

        tvSaleQrisCounterPrint = view.findViewById(R.id.tvSaleQrCounter_print);
        tvSaleQrisAmountPrint = view.findViewById(R.id.tvTotalSaleQrAmount_print);
        tvRefundQrisCounterPrint = view.findViewById(R.id.tvRefundQrCounter_print);
        tvRefundQrisAmountPrint = view.findViewById(R.id.tvTotalRefundQrAmount_print);
        tvTidPrint = view.findViewById(R.id.tvTid);
        tvMidPrint = view.findViewById(R.id.tvMid);
        tvBatch = view.findViewById(R.id.tvBatch);

    }

    private void printAndDisplaySettlementQr(View view) {
        List<ReportData> lstReportData = getListReportData();
        AmountTransInfo amountTransInfo = checkQrisDomestikTrans(lstReportData);
        long saleCounter, saleAmount, refundAmount, refundCounter;
        String tid, mid;
        tid           = "TID:"+ReportData.getTid();
        tvTidPrint.setText(tid);
        tvTidDisp.setText(tid);
        tvBatchDisplay.setText(Utility.getBatchNum());
        tvBatch.setText(Utility.getBatchNum());

        if(amountTransInfo != null) {

            saleAmount = amountTransInfo.getQrisSaleAmount();
            saleCounter = amountTransInfo.getQrisSaleCounter();
            refundAmount = amountTransInfo.getQrisRefundAmount();
            refundCounter = amountTransInfo.getQrisRefundCounter();

            //mid           = "MID:"+transData.getMID();

            tvSaleQrisCounterDisp.setText(Utility.setPaddingDataFromLongToString(saleCounter,4));
            tvSaleQrisAmountDisp.setText(Utility.getAmountRp(saleAmount));
            tvRefundQrisCounterDisp.setText(Utility.setPaddingDataFromLongToString(refundCounter,4));
            tvRefundQrisAmountDisp.setText(Utility.getAmountRp(refundAmount));

            //LogUtil.i("TAG","--"+mid);
            //tvMidDisp.setText(mid);


            tvSaleQrisCounterPrint.setText(Utility.setPaddingDataFromLongToString(saleCounter,4));
            tvSaleQrisAmountPrint.setText(Utility.getAmountRp(saleAmount));
            tvRefundQrisCounterPrint.setText(Utility.setPaddingDataFromLongToString(refundCounter,4));
            tvRefundQrisAmountPrint.setText(Utility.getAmountRp(refundAmount));

            //tvMidPrint.setText(mid);

        }
    }




}