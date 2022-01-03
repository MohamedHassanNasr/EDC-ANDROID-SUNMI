package com.sm.sdk.yokkeedc.initialize;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sm.sdk.yokkeedc.MainActivity;
import com.sm.sdk.yokkeedc.MtiApplication;
import com.sm.sdk.yokkeedc.R;
import com.sm.sdk.yokkeedc.database.DatabaseHelper;
import com.sm.sdk.yokkeedc.models.EDCParam;
import com.sm.sdk.yokkeedc.transaction.TransactionActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InitializeActivity extends AppCompatActivity implements View.OnClickListener{

    private boolean initFlag = true;
    private final int DOWNLOAD_TYPE_PARAM = 1;
    private final int DOWNLOAD_TYPE_ACQ_IMG = 2;
    private final int DOWNLOAD_TYPE_MTI_IMG = 3;
    private final int DOWNLOAD_TYPE_PROMOTION = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initialize);
        initBottomNavigationView();

        findViewById(R.id.btnInit).setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        //Toast.makeText(getApplicationContext(), "Processing..", Toast.LENGTH_SHORT).show();
        if (!MtiApplication.app.isConnectPaySDK()) {
            MtiApplication.app.bindPaySDKService();

            //return;
        }

        vdInitProcess(InitializeActivity.this);
        if (!isInitializeProc()) {
//            Intent intent = new Intent(this,InitFinishActivity.class);
//            startActivity(intent);
            switch (v.getId())
            {
                case R.id.btnInit:
                    startActivity(new Intent(InitializeActivity.this, InitFinishActivity.class));
                    break;
            }
        }

    }

    private void initBottomNavigationView() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_init);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_main:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.nav_transaction:
                        startActivity(new Intent(getApplicationContext(), TransactionActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.nav_init:
                        return true;
                }
                return false;
            }
        });
    }

    private boolean isInitializeProc() {
        return initFlag;
    }

    private void vdInitProcess(Context context) {

        Map<String,String> map = new HashMap<>();

        if(isInitializeProc()) {
            vdClearRecordEDC(context);

            /* COMM PROCESS */

            iDownloadProc(DOWNLOAD_TYPE_PARAM, context);
//            iDownloadProc(DOWNLOAD_TYPE_ACQ_IMG, context);
//            iDownloadProc(DOWNLOAD_TYPE_MTI_IMG, context);
//            iDownloadProc(DOWNLOAD_TYPE_PROMOTION, context);

            initFlag = false; //Init process already success

        }

    }

    /**
     * Clear EDC param record inside the Table
     * @param context
     */
    private void vdClearRecordEDC(Context context) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        dbHelper.deleteEDCParam();
    }

    /**
     *
     * @param downloadType = DOWNLOAD_TYPE_PARAM, DOWNLOAD_TYPE_ACQ_IMG, DOWNLOAD_TYPE_MTI_IMG, DOWNLOAD_TYPE_PROMOTION
     * @return status of download process
     */
    private int iDownloadProc(int downloadType,Context context) {
        int iRet = 0;

        if(downloadType == DOWNLOAD_TYPE_PARAM) {
            paramDownloadAndSave(context);
        }
        else if(downloadType == DOWNLOAD_TYPE_ACQ_IMG) {
            //FOR GET ACQUIRE IMAGE
        }

        return iRet;
    }

    private void paramDownloadAndSave(Context context) {
        int i;
        String stParam;
        try {
            //Load JSON File from folder assets
            JSONObject obj = new JSONObject(loadJSONFile(context));
            JSONArray keys = obj.names();
            for(i=0; i<keys.length(); i++) {
                stParam = keys.getString(i);
                if(stParam.equalsIgnoreCase("merchant_info")) {
                    JSONObject objParam = obj.getJSONObject(stParam);
                    saveParameterToDB(context, objParam);
                }
                else if (stParam.equalsIgnoreCase("emv_tag")){
                    JSONObject objParam = obj.getJSONObject(stParam);
                    System.out.println(objParam);
                    System.out.println("testing emv tag---");


                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void saveParameterToDB(Context context, JSONObject obj) {
        int i;
        int size = 0;
        String paramName, paramVal;
        List<EDCParam> lstEdcParam = new ArrayList<>();
        DatabaseHelper dbHelper = new DatabaseHelper(context);

        try {
            size = obj.length();

            for(i=0; i<size; i++) {
                paramName = obj.names().getString(i);
                paramVal = obj.getString(paramName);
                EDCParam edcParam = new EDCParam(Integer.toString(i), paramName, paramVal);
                lstEdcParam.add(edcParam);
            }

            //Insert to Table tbEDCParam
            dbHelper.insertEDCParam(lstEdcParam);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private String loadJSONFile(Context context) {
        String json = null;
        try {
            InputStream is = context.getAssets().open("dataa.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}