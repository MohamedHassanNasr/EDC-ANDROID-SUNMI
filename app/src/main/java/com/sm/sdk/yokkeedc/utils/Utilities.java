package com.sm.sdk.yokkeedc.utils;

import android.content.Context;

import com.sm.sdk.yokkeedc.database.DatabaseHelper;
import com.sm.sdk.yokkeedc.models.EDCParam;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class Utilities {
    public Utilities() {

    }

    public static List<LinkedHashMap<String,String>> LST_EDC_PARAM = new ArrayList<>();

    public static LinkedHashMap<String, String> getMerchantInfo(Context context) {
        int i;
        String paramName, paramVal;
        LinkedHashMap<String, String> value = new LinkedHashMap<>();
        DatabaseHelper dbHelper = new DatabaseHelper(context);

        List<EDCParam> lstEDCParam = dbHelper.getEDCParam();

        for(i = 0; i<lstEDCParam.size(); i++) {
            EDCParam param = lstEDCParam.get(i);
            paramName = param.getParamCode();
            paramVal = param.getParamVal();
            value.put(paramName, paramVal);
        }

        return value;



    }
}
