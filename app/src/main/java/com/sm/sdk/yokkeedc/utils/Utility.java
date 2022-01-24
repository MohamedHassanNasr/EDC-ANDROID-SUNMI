package com.sm.sdk.yokkeedc.utils;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.sm.sdk.yokkeedc.MtiApplication;
import com.sm.sdk.yokkeedc.transaction.BatchRecord;
import com.sm.sdk.yokkeedc.transaction.TransParam;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public final class Utility {
    private Utility() {
        throw new AssertionError("Create instance of Utility is forbidden.");
    }

    /** Bundle对象转换成字符串 */
    public static String bundle2String(Bundle bundle) {
        return bundle2String(bundle, 1);
    }

    /**
     * 根据key排序后将Bundle内容拼接成字符串
     *
     * @param bundle 要处理的bundle
     * @param order  排序规则，0-不排序，1-升序，2-降序
     * @return 拼接后的字符串
     */
    public static String bundle2String(Bundle bundle, int order) {
        if (bundle == null || bundle.keySet().isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        List<String> list = new ArrayList<>(bundle.keySet());
        if (order == 1) { //升序
            Collections.sort(list, String::compareTo);
        } else if (order == 2) {//降序
            Collections.sort(list, (o1, o2) -> o2.compareTo(o1));
        }
        for (String key : list) {
            sb.append(key);
            sb.append(":");
            sb.append(bundle.get(key));
            sb.append("\n");
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    /** 将null转换成空串 */
    public static String null2String(String str) {
        return str == null ? "" : str;
    }

    public static String formatStr(String format, Object... params) {
        return String.format(Locale.getDefault(), format, params);
    }

    /** 显示Toast */
    public static void showToast(final String msg) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> Toast.makeText(MtiApplication.app, msg, Toast.LENGTH_SHORT).show());
    }

    /** 显示Toast */
    public static void showToast(int resId) {
        showToast(MtiApplication.app.getString(resId));
    }

    /**
     * get account from track2
     *
     * @param track2 input track2 data
     * @return account no
     */
    public static String getPan(String track2) {
        if (track2 == null)
            return null;

        int len = track2.indexOf('=');
        if (len < 0) {
            len = track2.indexOf('D');
            if (len < 0)
                return null;
        }

        if ((len < 13) || (len > 19))
            return null;
        return track2.substring(0, len);
    }

    /**
     * get expiry data from track2
     *
     * @param track2 input track2 data
     * @return expiry data
     */
    public static String getExpDate(String track2) {
        if (track2 == null)
            return null;

        int index = track2.indexOf('=');
        if (index < 0) {
            index = track2.indexOf('D');
            if (index < 0)
                return null;
        }

        if (index + 5 > track2.length())
            return null;
        return track2.substring(index + 1, index + 5);
    }

    /**
     * get card holder name from track1
     *
     * @param track1 input track1 data
     * @return card holder name
     */
    public static String getHolderName(String track1) {
        if (track1 == null) {
            return null;
        }

        int index1 = track1.indexOf('^');
        if (index1 < 0) {
            return null;
        }

        int index2 = track1.lastIndexOf('^');
        if (index2 < 0) {
            return null;
        }
        return track1.substring(index1 + 1, index2);
    }

    public static String getDateFromBit13(byte[] date) {
        if (date != null) {
            return Tools.hexToString(Tools.bcd2Str(date));
        }

        return "";
    }

    public static String getTimeFromBit12(byte[] time) {
        if (time != null) {
            return Tools.hexToString(Tools.bcd2Str(time));
        }

        return "";
    }

    public enum CardBrand {
        MASTER_CARD("M","MASTER"),
        VISA("V","VISA"),
        JCB("J","JCB"),
        CUP("C","UNIONPAY"),
        DFS("D","DFS"),
        LOCAL("E","LOCAL"),
        ARTAJASA("1","ARTAJASA"),
        RINTIS("2","RINTIS"),
        ALTO("3","ALTO"),
        LINK("4","LINK");

        private String symbol;
        private String brand;

        private CardBrand(String symbol, String brand) {
            this.symbol = symbol;
            this.brand = brand;
        }

        public String getBrandName() {
            return brand;
        }

        public String getSymbol() {
            return symbol;
        }
    }

    public static String getCardBrandNameFromBit44(String symbol) {
        String cardBrand;
        if (symbol != null || symbol.equals("")) {
            for (CardBrand cb : CardBrand.values()) {
                if(cb.getSymbol().equalsIgnoreCase(symbol)) {
                    return cb.getBrandName().toString();
                }
            }
        }
        return "";
    }

    public static String getOnUsOrOfUsTypeFromBit44(String str) {
        if (str != null) {
            if (str.equalsIgnoreCase("1")) {
                return "On Us";
            }
            else if (str.equalsIgnoreCase("2")) {
                return "Off Us";
            }
        }
        return "";
    }

    public static String getCardTypeFromBit44(String str) {
        if(str != null) {
            if(str.equalsIgnoreCase("1")) {
                return "CREDIT";
            }
            else if(str.equalsIgnoreCase("2")) {
                return "DEBIT";
            }
            else if(str.equalsIgnoreCase("3")) {
                return "PREPAID";
            }
        }
        return "";
    }

    public static String getInvoiceNum() {
        String strInvoiceNum = "";
        String defaultInvoiceNum = "000001";
        boolean isSaved = false;

        TransParam transParam = MtiApplication.getTransParamDBHelper().findTransParamByParamName(TransParam.INVOICE_NUM);
        if (transParam != null) {
            strInvoiceNum = transParam.getParamVal();
            if(strInvoiceNum != null) {
                int iInvoiceNum = Integer.parseInt(strInvoiceNum);
                if (iInvoiceNum > 999999 || iInvoiceNum < 0)
                {
                    iInvoiceNum = 0;
                }
                iInvoiceNum++;

                String invoiceNum = Tools.paddingLeft(String.valueOf(iInvoiceNum), '0', 6);
//                TransParam newTransParam = new TransParam();
                transParam.setParamVal(invoiceNum);
                updateInvoiceNum(transParam);
                return invoiceNum;
            }
        }
        else {
            TransParam newTransParam = new TransParam();
            newTransParam.setParamName(TransParam.INVOICE_NUM);
            newTransParam.setParamVal(defaultInvoiceNum);
            isSaved = MtiApplication.getTransParamDBHelper().insertTransParam(newTransParam);
            if(isSaved) {
                return defaultInvoiceNum;
            }
            else {
                return "";
            }
        }
        return "";
    }

    public static void updateInvoiceNum(TransParam transParam) {
        MtiApplication.getTransParamDBHelper().updateTransParam(transParam);
    }

    public static String getSTAN() {
        String strTraceNum;
        String defaultTraceNum = "000001";
        boolean isSaved;

        TransParam transParam = MtiApplication.getTransParamDBHelper().findTransParamByParamName(TransParam.TRACE_AUDIT_NUM);
        if (transParam != null) {
            strTraceNum = transParam.getParamVal();
            if(strTraceNum != null) {
                int iTraceNum = Integer.parseInt(strTraceNum);
                if (iTraceNum > 999999 || iTraceNum < 0)
                {
                    iTraceNum = 0;
                }
                iTraceNum++;

                String TraceNum = Tools.paddingLeft(String.valueOf(iTraceNum), '0', 6);
                transParam.setParamVal(TraceNum);
                updateSTAN(transParam);
                return TraceNum;
            }
        }
        else {
            TransParam newTransParam = new TransParam();
            newTransParam.setParamName(TransParam.TRACE_AUDIT_NUM);
            newTransParam.setParamVal(defaultTraceNum);
            isSaved = MtiApplication.getTransParamDBHelper().insertTransParam(newTransParam);
            if(isSaved) {
                return defaultTraceNum;
            }
            else {
                return "";
            }
        }
        return "";
    }

    public static void updateSTAN(TransParam transParam) {
        MtiApplication.getTransParamDBHelper().updateTransParam(transParam);
    }

    public static void saveTransactionToDb(BatchRecord batchRecord) {
        MtiApplication.getBatchRecordDBHelper().insertBatchRecordDb(batchRecord);
    }

    public static BatchRecord getTransactionByTraceNo(String traceNo, String useYN) {
        return MtiApplication.getBatchRecordDBHelper().findBatchRecordByTraceNo(traceNo, useYN);
    }

    public static boolean updateTraceYN(String traceNo, String useYN){
        return MtiApplication.getBatchRecordDBHelper().updateFlagAfterVoidSuccess(traceNo,useYN);
    }

}
