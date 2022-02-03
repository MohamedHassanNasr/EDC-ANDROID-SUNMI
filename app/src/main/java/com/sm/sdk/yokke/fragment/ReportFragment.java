package com.sm.sdk.yokke.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.sm.sdk.yokke.R;
import com.sm.sdk.yokke.transaction.CardBrand;
import com.sm.sdk.yokke.transaction.report.AmountTransInfo;
import com.sm.sdk.yokke.transaction.report.ReportData;
import com.sm.sdk.yokke.transaction.report.ReportTask;
import com.sm.sdk.yokke.utils.Tools;
import com.sm.sdk.yokke.utils.Utility;

import java.util.ArrayList;
import java.util.HashMap;

public class ReportFragment extends ReportTask {
    private static String TAG = "REPORT FRAGMENT";
    private ListView lvPrint, lvDisplay;
    private View viewDisplay;

    private static final String CARD_TYPE_TEXT = "cardType";
    private static final String CARD_INFO_TEXT = "cardInfo";
    private static final String CARD_BRAND_TEXT = "cardBrand";

    private static final String SALE_NAME_TEXT = "saleText";
    private static final String SALE_COUNTER_TEXT = "saleCounter";
    private static final String SALE_TOTAL_TEXT = "saleTotal";

    private static final String VOID_NAME_TEXT = "voidText";
    private static final String VOID_COUNTER_TEXT = "voidCounter";
    private static final String VOID_TOTAL_TEXT = "voidTotal";

    private static final String REFUND_NAME_TEXT = "refundText";
    private static final String REFUND_COUNTER_TEXT = "refundCounter";
    private static final String REFUND_TOTAL_TEXT = "refundTotal";

    private static final String TOTAL_ALL_TEXT = "total";
    private static final String TOTAL_ALL_AMOUNT = "totalAmount";

    public ReportFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_report, container, false);
        printAndDisplaySettlement(view);

        return view;
    }


    private void printAndDisplaySettlement(View view) {

        TextView tvSaleGrandTotal, tvVoidGrandTotal, tvRefundGrandTotal, tvTotal;
        TextView tvPrintSaleGrandTotal, tvPrintVoidGrandTotal, tvPrintRefundGrandTotal, tvPrintTotal;
        long saleAmount, voidAmount, refundAmount, totalAmount;

        AmountTransInfo amountTransInfo = countGrandTotalSettlement();
        printReportByCardType(view);
        printReportByCardBrand(view);
        printReportByOnUsOffUs(view);

        saleAmount = amountTransInfo.getSaleAmount();
        voidAmount = amountTransInfo.getVoidAmount();
        refundAmount = amountTransInfo.getRefundAmount();
        totalAmount = saleAmount - voidAmount - refundAmount;

        tvSaleGrandTotal = view.findViewById(R.id.tvSaleGrandTotalAmount_display);
        tvVoidGrandTotal = view.findViewById(R.id.tvVoidGrandTotalAmount_display);
        tvRefundGrandTotal = view.findViewById(R.id.tvRefundGrandTotalAmount_display);
        tvTotal = view.findViewById(R.id.tvGrandTotalAmount_display);

        tvSaleGrandTotal.setText(Utility.getAmountRp(saleAmount));
        tvVoidGrandTotal.setText("-" + Utility.getAmountRp(voidAmount));
        tvRefundGrandTotal.setText("-" + Utility.getAmountRp(refundAmount));
        tvTotal.setText(Utility.getAmountRp(totalAmount));

        tvPrintSaleGrandTotal = view.findViewById(R.id.tvSaleGrandTotalAmount);
        tvPrintVoidGrandTotal = view.findViewById(R.id.tvVoidGrandTotalAmount);
        tvPrintRefundGrandTotal = view.findViewById(R.id.tvRefundGrandTotalAmount);
        tvPrintTotal = view.findViewById(R.id.tvGrandTotalAmount);

        tvPrintSaleGrandTotal.setText(Utility.getAmountRp(saleAmount));
        tvPrintVoidGrandTotal.setText("-" +Utility.getAmountRp(voidAmount));
        tvPrintRefundGrandTotal.setText("-" +Utility.getAmountRp(refundAmount));
        tvPrintTotal.setText(Utility.getAmountRp(totalAmount));


    }

    private void printReportByCardType(View view) {
        ArrayList<HashMap<String, String>> transList = new ArrayList<>();
        long saleAmount = 0, saleCounter = 0, voidAmount = 0, voidCounter = 0;

        String[] texts = {CARD_TYPE_TEXT, SALE_NAME_TEXT,SALE_COUNTER_TEXT, SALE_TOTAL_TEXT,
                VOID_NAME_TEXT, VOID_COUNTER_TEXT, VOID_TOTAL_TEXT, REFUND_NAME_TEXT, REFUND_COUNTER_TEXT, REFUND_TOTAL_TEXT};

        int[] idsPrint = {R.id.tvCardType_print, R.id.tvSale, R.id.tvSaleCounter, R.id.tvTotalSaleAmount,
                R.id.tvVoid, R.id.tvVoidCounter, R.id.tvTotalVoidAmount, R.id.tvRefund, R.id.tvRefundCounter, R.id.tvTotalRefundAmount};

        int[] idsDisplay = {R.id.tvCardType_display, R.id.tvSale_display,
                R.id.tvSaleCounter_display, R.id.tvTotalSaleAmount_display,
                R.id.tvVoid_display, R.id.tvVoidCounter_display, R.id.tvTotalVoidAmount_display,
                R.id.tvRefund_display, R.id.tvRefundCounter_display, R.id.tvTotalRefundAmount_display};

        String[] cardType = {"DEBIT","CREDIT","NPG"};

        for(String ct : cardType) {
            saleAmount = 0; saleCounter = 0; voidAmount = 0; voidCounter = 0;
            for(ReportData stData : lstReportData) {
                if(ct.equals(stData.getCardType())) {
                    saleAmount += stData.getAmountInfo().getSaleAmount();
                    saleCounter += stData.getAmountInfo().getSaleCounter();
                    voidAmount += stData.getAmountInfo().getVoidAmount();
                    voidCounter += stData.getAmountInfo().getVoidCounter();
                }
            }
            HashMap<String, String> map = new HashMap<>();
            map.put(CARD_TYPE_TEXT, ct);
            map.put(SALE_NAME_TEXT, "SALE");
            map.put(SALE_COUNTER_TEXT, Tools.paddingLeft(String.valueOf(saleCounter), '0', 4));
            map.put(SALE_TOTAL_TEXT, Utility.getAmountRp(saleAmount));

            map.put(VOID_NAME_TEXT, "VOID");
            map.put(VOID_COUNTER_TEXT, Tools.paddingLeft(String.valueOf(voidCounter), '0', 4));
            map.put(VOID_TOTAL_TEXT, "-" + Utility.getAmountRp(voidAmount));

            map.put(REFUND_NAME_TEXT, "REFUND");
            map.put(REFUND_COUNTER_TEXT, "0000");
            map.put(REFUND_TOTAL_TEXT, "-RP0");

            transList.add(map);

        }

        lvPrint = view.findViewById(R.id.idAllTransCardTypePrint);

        ListAdapter adapterPrint = new SimpleAdapter(
                context, transList,
                R.layout.layout_settlement_cardtype_print, texts, idsPrint);

        lvPrint.setAdapter(adapterPrint);
        setListViewSize(lvPrint, 145);

        lvDisplay = view.findViewById(R.id.idAllTransCardTypeDisplay);

        ListAdapter adapterDisplay = new SimpleAdapter(
                context, transList,
                R.layout.layout_settlement_cardtype_display, texts, idsDisplay) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                viewDisplay = super.getView(position, convertView, parent);
                return viewDisplay;
            }
        };

        lvDisplay.setAdapter(adapterDisplay);

        setListViewSize(lvDisplay, 185);

    }


    private void printReportByCardBrand(View view) {
        ArrayList<HashMap<String, String>> transList = new ArrayList<>();
        long saleAmount = 0, saleCounter = 0, voidAmount = 0, voidCounter = 0;
        int transCounter =0;
        boolean cardTypeFlag = false;
        boolean cardTypeTextFlag;
        String[] cardType = {"DEBIT", "CREDIT"};
        String[] texts = {CARD_INFO_TEXT,CARD_BRAND_TEXT, SALE_NAME_TEXT,SALE_COUNTER_TEXT, SALE_TOTAL_TEXT,
                VOID_NAME_TEXT, VOID_COUNTER_TEXT, VOID_TOTAL_TEXT, REFUND_NAME_TEXT, REFUND_COUNTER_TEXT, REFUND_TOTAL_TEXT};

        int[] idsPrint = {R.id.tvCardInfoPrint1, R.id.tvCardInfoPrint2, R.id.tvSale, R.id.tvSaleCounter, R.id.tvTotalSaleAmount,
                R.id.tvVoid, R.id.tvVoidCounter, R.id.tvTotalVoidAmount, R.id.tvRefund, R.id.tvRefundCounter, R.id.tvTotalRefundAmount};

        int[] idsDisplay = {R.id.tvCardInfo1_display, R.id.tvCardInfo2_display, R.id.tvSale_display,
                R.id.tvSaleCounter_display, R.id.tvTotalSaleAmount_display,
                R.id.tvVoid_display, R.id.tvVoidCounter_display, R.id.tvTotalVoidAmount_display,
                R.id.tvRefund_display, R.id.tvRefundCounter_display, R.id.tvTotalRefundAmount_display};

        int i=0;
        //Counting for On Us
        for(String ct : cardType) {
            saleAmount = 0; saleCounter = 0; voidAmount = 0; voidCounter = 0;
            transCounter = 0;
            for(ReportData stData : lstReportData) {
                if(stData.isOnUs()) {
                    transCounter++;
                    saleAmount += stData.getAmountInfo().getSaleAmount();
                    saleCounter += stData.getAmountInfo().getSaleCounter();
                    voidAmount += stData.getAmountInfo().getVoidAmount();
                    voidCounter += stData.getAmountInfo().getVoidCounter();
                }
            }

            if(transCounter > 0) {
                HashMap<String, String> map = new HashMap<>();
                map.put(CARD_INFO_TEXT, "[ON US]");
                map.put(CARD_BRAND_TEXT, ct);
                map.put(SALE_NAME_TEXT, "SALE");
                map.put(SALE_COUNTER_TEXT, Tools.paddingLeft(String.valueOf(saleCounter), '0', 4));
                map.put(SALE_TOTAL_TEXT, Utility.getAmountRp(saleAmount) );

                map.put(VOID_NAME_TEXT, "VOID");
                map.put(VOID_COUNTER_TEXT, Tools.paddingLeft(String.valueOf(voidCounter), '0', 4));
                map.put(VOID_TOTAL_TEXT, "-" + Utility.getAmountRp(voidAmount) );

                map.put(REFUND_NAME_TEXT, "REFUND");
                map.put(REFUND_COUNTER_TEXT, "0000");
                map.put(REFUND_TOTAL_TEXT, "-RP0");
                transList.add(map);
            }
        }

        //counting for off us
        for(String ct : cardType) {
            cardTypeTextFlag = true;
            for (CardBrand cb : CardBrand.values()) {
                String cardBrand = cb.getBrandName();

                saleAmount = 0; saleCounter = 0; voidAmount = 0; voidCounter = 0;
                transCounter = 0;
                for(ReportData stData : lstReportData) {
                    cardTypeFlag = false;
                    if(ct.equals(stData.getCardType())) {
                        cardTypeFlag = true;
                    }

                    if(cardBrand.equals(stData.getCardBrand())
                            && !stData.isOnUs() && cardTypeFlag) {
                        transCounter++;
                        saleAmount += stData.getAmountInfo().getSaleAmount();
                        saleCounter += stData.getAmountInfo().getSaleCounter();
                        voidAmount += stData.getAmountInfo().getVoidAmount();
                        voidCounter += stData.getAmountInfo().getVoidCounter();
                    }
                }


                if(transCounter > 0) {
                    if(cardTypeTextFlag) {
                        HashMap<String, String> map1 = new HashMap<>();
                        map1.put(CARD_INFO_TEXT, "[OFFUS " + ct + "]");
                        transList.add(map1);
                        cardTypeTextFlag = false;
                    }

                    HashMap<String, String> map2 = new HashMap<>();
                    map2.put(CARD_BRAND_TEXT, cardBrand);
                    map2.put(SALE_NAME_TEXT, "SALE");
                    map2.put(SALE_COUNTER_TEXT, Tools.paddingLeft(String.valueOf(saleCounter), '0', 4));
                    map2.put(SALE_TOTAL_TEXT, Utility.getAmountRp(saleAmount) );

                    map2.put(VOID_NAME_TEXT, "VOID");
                    map2.put(VOID_COUNTER_TEXT, Tools.paddingLeft(String.valueOf(voidCounter), '0', 4));
                    map2.put(VOID_TOTAL_TEXT, "-" + Utility.getAmountRp(voidAmount) );
                    transList.add(map2);
                }
            }
        }

        lvPrint = view.findViewById(R.id.idAllTransCardBrandPrint);

        ListAdapter adapterPrint = new SimpleAdapter(
                context, transList,
                R.layout.layout_settlement_cardbrand_print, texts, idsPrint) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View viewPrint = super.getView(position, convertView, parent);

                for(int id : idsPrint) {
                    TextView display = (TextView) viewPrint.findViewById(id);
                    String displayText = display.getText().toString();
                    if(TextUtils.isEmpty(displayText)) {
                        display.setVisibility(View.GONE);
                    }
                    else {
                        display.setVisibility(View.VISIBLE);
                    }
                }

                return viewPrint;
            }
        };

        lvPrint.setAdapter(adapterPrint);
        setListViewSize(lvPrint, 25);

        lvDisplay = view.findViewById(R.id.idAllTransCardBrandDisplay);

        ListAdapter adapterDisplay = new SimpleAdapter(
                context, transList,
                R.layout.layout_settlement_cardbrand_display, texts, idsDisplay) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View viewDisplay = super.getView(position, convertView, parent);

                for(int id : idsDisplay) {
                    TextView display = (TextView) viewDisplay.findViewById(id);
                    String displayText = display.getText().toString();
                    if(TextUtils.isEmpty(displayText)) {
                        display.setVisibility(View.GONE);
                    }
                    else {
                        display.setVisibility(View.VISIBLE);
                    }
                }

                return viewDisplay;
            }
        };

        lvDisplay.setAdapter(adapterDisplay);
        setListViewSize(lvDisplay, 55);
    }

    private void printReportByOnUsOffUs(View view) {
        ArrayList<HashMap<String, String>> transList = new ArrayList<>();
        long saleAmount = 0, saleCounter = 0, voidAmount = 0, voidCounter = 0;
        int i;

        String[] texts = {CARD_TYPE_TEXT, SALE_NAME_TEXT, SALE_TOTAL_TEXT,
                VOID_NAME_TEXT,  VOID_TOTAL_TEXT, TOTAL_ALL_TEXT, TOTAL_ALL_AMOUNT};

        int[] idsPrint = {R.id.tvOnUsOrOffUs_print, R.id.tvSale, R.id.tvTotalSaleAmount,
                R.id.tvVoid, R.id.tvTotalVoidAmount, R.id.tvTotal, R.id.tvTotalAll};

        int[] idsDisplay = {R.id.tvOnUsOrOffUs_display, R.id.tvSale_display,
                R.id.tvTotalSaleAmount_display, R.id.tvVoid_display, R.id.tvTotalVoidAmount_display,
                R.id.tvTotal_display, R.id.tvTotalAll_display};

        String[] types = {"[ON US]","[OFF US]"};

        //i=0 for On Us, i=1 for Off us
        for(i=0; i<2; i++) {
            saleAmount = 0; saleCounter = 0; voidAmount = 0; voidCounter = 0;
            for(ReportData stData : lstReportData) {
                //counting for All On Us Trans
                if(i==0) {
                    if(stData.isOnUs()) {
                        saleAmount += stData.getAmountInfo().getSaleAmount();
                        saleCounter += stData.getAmountInfo().getSaleCounter();
                        voidAmount += stData.getAmountInfo().getVoidAmount();
                        voidCounter += stData.getAmountInfo().getVoidCounter();
                    }
                }
                //Counting for All Off Us Trans
                else if(i==1) {
                    if(!stData.isOnUs()) {
                        saleAmount += stData.getAmountInfo().getSaleAmount();
                        saleCounter += stData.getAmountInfo().getSaleCounter();
                        voidAmount += stData.getAmountInfo().getVoidAmount();
                        voidCounter += stData.getAmountInfo().getVoidCounter();
                    }
                }

            }
            HashMap<String, String> map = new HashMap<>();
            map.put(CARD_TYPE_TEXT, types[i]);
            map.put(SALE_NAME_TEXT, "SALE");
            map.put(SALE_TOTAL_TEXT, Utility.getAmountRp(saleAmount));

            map.put(VOID_NAME_TEXT, "VOID");
            map.put(VOID_TOTAL_TEXT, "-" + Utility.getAmountRp(voidAmount));

            map.put(TOTAL_ALL_TEXT, "TOTAL");
            map.put(TOTAL_ALL_AMOUNT, Utility.getAmountRp(saleAmount - voidAmount));
            transList.add(map);

        }

        lvPrint = view.findViewById(R.id.idAllTransOnUsOffUsPrint);
        setListViewSize(lvPrint, 0);

        ListAdapter adapterPrint = new SimpleAdapter(
                context, transList,
                R.layout.layout_settlement_onus_offus_print, texts, idsPrint);

        lvPrint.setAdapter(adapterPrint);

        lvDisplay = view.findViewById(R.id.idAllTransOnUsOffUsDisplay);

        ListAdapter adapterDisplay = new SimpleAdapter(
                context, transList,
                R.layout.layout_settlement_onus_offus_display, texts, idsDisplay);

        lvDisplay.setAdapter(adapterDisplay);
        setListViewSize(lvDisplay, 130);

    }

    private void setListViewSize(ListView myListView, int n) {
        ListAdapter myListAdapter = myListView.getAdapter();
        if (myListAdapter == null) {
            //do nothing return null
            return;
        }
        //set listAdapter in loop for getting final size
        int totalHeight = 0;
        for (int size = 0; size < myListAdapter.getCount(); size++) {
            View listItem = myListAdapter.getView(size, null, myListView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        //setting listview item in adapter
        ViewGroup.LayoutParams params = myListView.getLayoutParams();
        params.height = totalHeight - n;
        myListView.setLayoutParams(params);
        // print height of adapter on log
        Log.i("height of listItem:", String.valueOf(totalHeight));
    }
}