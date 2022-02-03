package com.sm.sdk.yokke.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.sm.sdk.yokke.R;
import com.sm.sdk.yokke.models.transData.TransData;

public class PrintQrActivity extends AppCompatActivity {
    public static String EXTRA_TRANS = "extra_trans";
    TextView tvTid, tvMid, tvDate, tvMercPan, tvNameIssuer, tvReffNo, tvTime, tvTextNameCust, tvTextCustPan,
            tvTextReffId, tvTextTip, tvTextNominal, tvTotalAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_qr);
        initView();
    }

    private void initView(){
        tvTid        = findViewById(R.id.tvTid);
        tvMid        = findViewById(R.id.tvMid);
        tvDate       = findViewById(R.id.tvDate);
        tvMercPan    = findViewById(R.id.tvMercPan);
        tvNameIssuer = findViewById(R.id.tvNameIssuer);
        tvReffNo     = findViewById(R.id.tvReffNo);
        tvTime       = findViewById(R.id.tvTime);
        tvTextNameCust  = findViewById(R.id.tvTextNameCust);
        tvTextCustPan   = findViewById(R.id.tvTextCustPan);
        tvTextReffId    = findViewById(R.id.tvTextReffId);
        tvTextTip       = findViewById(R.id.tvTextTip);
        tvTextNominal   = findViewById(R.id.tvTextNominal);
        tvTotalAmount   = findViewById(R.id.tvTotalAmount);

        setDisplayData();
    }

    private void setDisplayData(){
        TransData transData = getIntent().getParcelableExtra(EXTRA_TRANS);
        tvTid.setText("TID:"+transData.getTID());

    }











}
