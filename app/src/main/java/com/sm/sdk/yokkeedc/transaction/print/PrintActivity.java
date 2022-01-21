package com.sm.sdk.yokkeedc.transaction.print;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sm.sdk.yokkeedc.MtiApplication;
import com.sm.sdk.yokkeedc.R;
import com.sm.sdk.yokkeedc.transaction.TransData;
import com.sm.sdk.yokkeedc.transaction.sale.SaleActivity;
import com.sm.sdk.yokkeedc.utils.CurrencyConverter;
import com.sunmi.peripheral.printer.InnerResultCallbcak;

import java.text.NumberFormat;
import java.time.Year;
import java.util.Calendar;
import java.util.Locale;

import sunmi.sunmiui.utils.LogUtil;

public class PrintActivity extends AppCompatActivity {

    TransData transData;
    TextView tvNumCard, tvExpDate, tvAmount, tvReffNo, tvAppr, tvCardType, tvTime, tvDate, tvTraceNo;
    Button btnPrint;

    public static String EXTRA_TRANS = "extra_trans";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_temp);
        initView();
        setDisplayData();

        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                printBitmap();
            }
        });

    }

    private void initView(){
        tvExpDate   = findViewById(R.id.tvExpDate);
        tvNumCard   = findViewById(R.id.tvNumCard);
        tvAmount    = findViewById(R.id.tvAmount);
        tvAppr      = findViewById(R.id.tvAppr);
        tvReffNo    = findViewById(R.id.tvReffNo);
        tvCardType  = findViewById(R.id.tvCardType);
        btnPrint    = findViewById(R.id.button_print_struct_temp);
        tvTime      = findViewById(R.id.tvTime);
        tvDate      = findViewById(R.id.tvDate);
        tvTraceNo   = findViewById(R.id.tvTrace);
    }

    private void setDisplayData(){
        TransData transData = getIntent().getParcelableExtra(EXTRA_TRANS);
        tvNumCard.setText(SaleActivity.separateWithSpace(transData.getCardNo()));

        String date = transData.getDate();
        String time = transData.getTime();
        time        = time.substring(0,2) + ":" + time.substring(2,4) + ":" + time.substring(4,6);

        int year = Calendar.getInstance().get(Calendar.YEAR);

        String dateTime = date.substring(2,4) + "/" + date.substring(0,2) + "/" + year;

        tvTime.setText("TIME:"+time);
        tvDate.setText("DATE:"+dateTime);

        String cardType = "CARD TYPE : " + transData.getCardBrand() + " " + transData.getONOFF() + " " + transData.getCardType();
        tvCardType.setText(cardType);

        String cardExpDate = transData.getExpDate();
        cardExpDate = cardExpDate.substring(2, 4) + "/" +cardExpDate.substring(0, 2);
        tvExpDate.setText(cardExpDate);

        int amountIDR = Integer.parseInt(transData.getAmount());
        String amountNow = NumberFormat.getCurrencyInstance(new Locale("id","ID")).format(amountIDR);
        tvAmount.setText(amountNow);

        String apprCd;
        apprCd = "APPR CODE : " + transData.getApprCode();
        tvAppr.setText(apprCd);

        String refNo;
        refNo = "REF NO : " + transData.getReffNo();
        tvReffNo.setText(refNo);

        String traceNo;
        traceNo = "TRACE NO: " + transData.getTraceNo();
        tvTraceNo.setText(traceNo);
        tvTraceNo.setText(traceNo);

    }

    private void printBitmap() {
        try {
            if (MtiApplication.app.sunmiPrinterService == null) {
                //showToast("Print not supported");
                return;
            }
            View content = findViewById(R.id.print_content);
            Bitmap bitmap = createViewBitmap(content);
//            bitmap = getBinaryzationBitmap(bitmap);
            MtiApplication.app.sunmiPrinterService.enterPrinterBuffer(true);
            MtiApplication.app.sunmiPrinterService.printBitmap(bitmap, new InnerResultCallbcak() {
                @Override
                public void onRunResult(boolean isSuccess) throws RemoteException {
                    LogUtil.e("TAG", "onRunResult-->isSuccess:" + isSuccess);
                }

                @Override
                public void onReturnString(String result) throws RemoteException {
                    LogUtil.e("TAG", "onReturnString-->result:" + result);
                }

                @Override
                public void onRaiseException(int code, String msg) throws RemoteException {
                    LogUtil.e("TAG", "onRaiseException-->code:" + code + ",msg:" + msg);
                }

                @Override
                public void onPrintResult(int code, String msg) throws RemoteException {
                    LogUtil.e("TAG", "onPrintResult-->code:" + code + ",msg:" + msg);
                }
            });
            MtiApplication.app.sunmiPrinterService.lineWrap(4, null);
            MtiApplication.app.sunmiPrinterService.exitPrinterBuffer(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Create Bitmap by View */
    private Bitmap createViewBitmap(View v) {
        long start = System.currentTimeMillis();
        Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(),
                Bitmap.Config.ARGB_8888); //创建一个和View大小一样的Bitmap
        Canvas canvas = new Canvas(bitmap);  //使用上面的Bitmap创建canvas
        v.draw(canvas);  //把View画到Bitmap上
        LogUtil.e("TAG", "createViewBitmap time:" + (System.currentTimeMillis() - start));
        return bitmap;
    }

}
