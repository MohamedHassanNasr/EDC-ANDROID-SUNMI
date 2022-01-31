package com.sm.sdk.yokke.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.sm.sdk.yokke.R;

public class MenuOptActivity extends AppCompatActivity {
    CardView btnPrintQr, btnLast, btnAny, btnRefund;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_opt);
        initView();
    }

    private void initView() {
        btnPrintQr = findViewById(R.id.btn_printQris);
        btnLast = findViewById(R.id.btn_lastTrx);
        btnAny = findViewById(R.id.btn_anyTrx);
        btnRefund = findViewById(R.id.btn_refundQris);

        btnPrintQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MenuOptActivity.this, QrisActivity.class);
                startActivity(i);
            }
        });

        btnLast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnAny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MenuOptActivity.this, AnyTrxQrActivity.class);
                startActivity(i);
            }
        });

        btnRefund.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MenuOptActivity.this, RefundQrisActivity.class);
                startActivity(i);
            }
        });
    }

}