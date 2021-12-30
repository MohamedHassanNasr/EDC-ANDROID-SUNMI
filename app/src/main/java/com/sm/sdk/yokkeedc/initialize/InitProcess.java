package com.sm.sdk.yokkeedc.initialize;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.sm.sdk.yokkeedc.database.DatabaseHelper;
import com.sm.sdk.yokkeedc.models.EDCParam;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class InitProcess {
    private static JSONObject initData = new JSONObject();

    private static String EDC_PARAM_MID = "1001";
    private static String EDC_PARAM_TID = "1002";

    public static void set() throws JSONException {
        InputData.setData();
        initData = InputData.jResult;
    }

    public static void deleteObj() throws JSONException {
        while(InputData.jResult.length()>0) {
            InputData.jResult.remove(InputData.jResult.keys().next());
        }
        initData = InputData.jResult;
    }

    public static int initProc(Context context) {
        int iRet = 0, i;
        String keyObj, paramVal, strTID, strMID;
        EDCParam edcParam;
        List<EDCParam> lstEdcParam = new ArrayList<>();
        DatabaseHelper dbHelper = new DatabaseHelper(context);

        dbHelper.deleteEDCParam();

        try {
            set();
            JSONArray keys = initData.names();
            for(i=0; i<keys.length(); i++){
                keyObj = keys.getString(i);
                if(keyObj.substring(0,2).equals("10"))
                {
                    paramVal = initData.getString(keyObj);
                    edcParam = new EDCParam(Integer.toString(i), keyObj, paramVal);
                    lstEdcParam.add(edcParam);
                }
            }

            dbHelper.insertEDCParam(lstEdcParam);

            deleteObj();
            iRet = 1;


        } catch (JSONException e) {
            e.printStackTrace();
        }


        return iRet;
    }

}
