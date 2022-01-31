package com.sm.sdk.yokke.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.sm.sdk.yokke.R;

public class AnyTrxQrActivity extends AppCompatActivity {
    private static int LAST_TRX = 1;
    TextView tvIdMenu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_any_trx_qr);
        initView();
    }

    private void initView() {

    }
}