package com.sm.sdk.yokkeedc.transaction;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.sm.sdk.yokkeedc.R;
import com.sm.sdk.yokkeedc.comm.CommProcess;
import com.sm.sdk.yokkeedc.database.BatchRecordDB;
import com.sm.sdk.yokkeedc.utils.TransConstant;
import com.sm.sdk.yokkeedc.utils.Utility;

public class VoidActivity extends AppCompatActivity {
    Button btnOkTrace;
    EditText etTraceNo;

    TransData transData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_void);
        initView();
        btnOkTrace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String trace = etTraceNo.getText().toString();
                getDataFromDB(trace);
            }
        });
    }

    private void initView(){
        btnOkTrace = findViewById(R.id.btn_okTrace);
        etTraceNo  = findViewById(R.id.etTrace);
    }

    private void getDataFromDB(String traceNo){
        BatchRecord batchRecord = Utility.getTransactionByTraceNo(traceNo);
        Log.i("TAG","TEST");
    }

}