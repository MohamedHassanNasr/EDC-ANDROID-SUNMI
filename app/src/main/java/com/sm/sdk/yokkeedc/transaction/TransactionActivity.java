package com.sm.sdk.yokkeedc.transaction;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
//import com.sm.sdk.yokkeedc.BaseAppCompatActivity;
import com.sm.sdk.yokkeedc.MainActivity;
import com.sm.sdk.yokkeedc.R;
import com.sm.sdk.yokkeedc.initialize.InitializeActivity;
import com.sm.sdk.yokkeedc.transaction.sale.SaleActivity;
import com.sm.sdk.yokkeedc.utils.Constant;
import com.sunmi.pay.hardware.aidlv2.AidlConstantsV2;
import com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2;

import java.util.LinkedHashMap;
import java.util.Map;

import sunmi.sunmiui.utils.LogUtil;

public class TransactionActivity extends AppCompatActivity {

    private EMVOptV2 mEMVOptV2;
    private int mAppSelect = 0;
    private int mProcessStep;
    private static final int EMV_FINAL_APP_SELECT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_transaction);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_main:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.nav_transaction:
                        return true;
                    case R.id.nav_init:
                        startActivity(new Intent(getApplicationContext(), InitializeActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

        CardView btnSale = (CardView) findViewById(R.id.btn_sale);
        btnSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TransactionActivity.this, SaleActivity.class);
                startActivity(intent);
            }
        });

    }

    private final Map<String, Long> timeMap = new LinkedHashMap<>();

    //
//    /**
//     * Set tlv essential tlv data
//     */
    private void initEmvTlvData() {
        try {
            timeMap.put("initEmvTlvData", System.currentTimeMillis());

            // set PayPass(MasterCard) tlv data
            String[] tagsPayPass = {"DF8117", "DF8118", "DF8119", "DF811F", "DF811E", "DF812C",
                    "DF8123", "DF8124", "DF8125", "DF8126",
                    "DF811B", "DF811D", "DF8122", "DF8120", "DF8121"};
            String[] valuesPayPass = {"E0", "F8", "F8", "E8", "00", "00",
                    "000000000000", "000000100000", "999999999999", "000000100000",
                    "30", "02", "0000000000", "000000000000", "000000000000"};
            mEMVOptV2.setTlvList(AidlConstantsV2.EMV.TLVOpCode.OP_PAYPASS, tagsPayPass, valuesPayPass);

            // set AMEX(AmericanExpress) tlv data
            String[] tagsAE = {"9F6D", "9F6E", "9F33", "9F35", "DF8168", "DF8167", "DF8169", "DF8170"};
            String[] valuesAE = {"C0", "D8E00000", "E0E888", "22", "00", "00", "00", "60"};
            mEMVOptV2.setTlvList(AidlConstantsV2.EMV.TLVOpCode.OP_AE, tagsAE, valuesAE);

            String[] tagsJCB = {"9F53", "DF8161"};
            String[] valuesJCB = {"708000", "7F00"};
            mEMVOptV2.setTlvList(AidlConstantsV2.EMV.TLVOpCode.OP_JCB, tagsJCB, valuesJCB);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Notify client the final selected Application
     * <br/> For Contactless and flowType set as AidlConstants.FlowType.TYPE_NFC_SPEEDUP, this
     * method will not be called
     *
     * @param tag9F06Value The final selected Application id
     */
    //@Override
    public void onAppFinalSelect(String tag9F06Value) throws RemoteException {
        timeMap.put("onAppFinalSelect_start", System.currentTimeMillis());
        LogUtil.e(Constant.TAG, "onAppFinalSelect tag9F06Value:" + tag9F06Value);
        if (tag9F06Value != null && tag9F06Value.length() > 0) {
            boolean isUnionPay = tag9F06Value.startsWith("A000000333");
            boolean isVisa = tag9F06Value.startsWith("A000000003");
            boolean isMaster = tag9F06Value.startsWith("A000000004")
                    || tag9F06Value.startsWith("A000000005");
            boolean isAmericanExpress = tag9F06Value.startsWith("A000000025");
            boolean isJCB = tag9F06Value.startsWith("A000000065");
            boolean isRupay = tag9F06Value.startsWith("A000000524");
            boolean isPure = tag9F06Value.startsWith("D999999999")
                    || tag9F06Value.startsWith("D888888888")
                    || tag9F06Value.startsWith("D777777777")
                    || tag9F06Value.startsWith("D666666666")
                    || tag9F06Value.startsWith("A000000615");



            String paymentType = "unknown";
            if (isUnionPay) {
                paymentType = "UnionPay";
                mAppSelect = 0;
            } else if (isVisa) {
                paymentType = "Visa";
                mAppSelect = 1;
            } else if (isMaster) {
                paymentType = "MasterCard";
                mAppSelect = 2;
            } else if (isAmericanExpress) {
                paymentType = "AmericanExpress";
            } else if (isJCB) {
                paymentType = "JCB";
            } else if (isRupay) {
                paymentType = "Rupay";
            } else if (isPure) {
                paymentType = "Pure";
            }
            LogUtil.e(Constant.TAG, "detect " + paymentType + " card");
        }
        mProcessStep = EMV_FINAL_APP_SELECT;
        //mHandler.obtainMessage(EMV_FINAL_APP_SELECT, tag9F06Value).sendToTarget();
        timeMap.put("onAppFinalSelect_end", System.currentTimeMillis());
    }



}