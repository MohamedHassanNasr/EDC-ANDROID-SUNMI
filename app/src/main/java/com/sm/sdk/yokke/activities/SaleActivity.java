package com.sm.sdk.yokke.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.sm.sdk.yokke.R;
import com.sm.sdk.yokke.card.wrapper.CheckCardCallbackV2Wrapper;
import com.sm.sdk.yokke.comm.CommTask;
import com.sm.sdk.yokke.emv.TLV;
import com.sm.sdk.yokke.emv.TLVUtil;
import com.sm.sdk.yokke.models.BatchRecord;
import com.sm.sdk.yokke.models.transData.TransData;
import com.sm.sdk.yokke.utils.ByteUtil;
import com.sm.sdk.yokke.utils.Constant;
import com.sm.sdk.yokke.utils.CurrencyConverter;
import com.sm.sdk.yokke.utils.ResponseCode;
import com.sm.sdk.yokke.utils.Tools;
import com.sm.sdk.yokke.utils.TransConstant;
import com.sm.sdk.yokke.utils.Utility;
import com.sm.sdk.yokke.view.DigitKeyboard;
import com.sm.sdk.yokke.view.EditorActionListener;
import com.sm.sdk.yokke.view.EnterAmountTextWatcher;
import com.sunmi.pay.hardware.aidl.bean.CardInfo;
import com.sunmi.pay.hardware.aidlv2.AidlConstantsV2;
import com.sunmi.pay.hardware.aidlv2.AidlErrorCodeV2;
import com.sunmi.pay.hardware.aidlv2.bean.EMVCandidateV2;
import com.sunmi.pay.hardware.aidlv2.bean.PinPadConfigV2;
import com.sunmi.pay.hardware.aidlv2.bean.PinPadTextConfigV2;
import com.sunmi.pay.hardware.aidlv2.emv.EMVListenerV2;
import com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2;
import com.sunmi.pay.hardware.aidlv2.pinpad.PinPadListenerV2;
import com.sunmi.pay.hardware.aidlv2.pinpad.PinPadOptV2;
import com.sunmi.pay.hardware.aidlv2.readcard.CheckCardCallbackV2;
import com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2;
import com.sunmi.pay.hardware.aidlv2.system.BasicOptV2;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import sunmi.sunmiui.utils.LogUtil;

public class SaleActivity extends AppCompatActivity{

    //AppCompatActivity -> dari android
    //BaseAppCompatActivity -> dari sunmi

    /**
     * variable from sdk sunmi
     */
    private final BasicOptV2 basicOptV2 = MtiApplication.app.basicOptV2;

    private EMVOptV2 mEMVOptV2;
    private PinPadOptV2 mPinPadOptV2;
    private ReadCardOptV2 mReadCardOptV2;
    private int mCardType;  // card type
    private String mCardNo; // card number
    private int mPinType;   // 0-online pin, 1-offline pin
    private String mCertInfo;
    private int mSelectIndex;
    private int mAppSelect = 0;
    private int mProcessStep;
    private AlertDialog mAppSelectDialog;
    private Map<String, String> configMap;
    private static final int EMV_APP_SELECT = 1;
    private static final int EMV_FINAL_APP_SELECT = 2;
    private static final int EMV_CONFIRM_CARD_NO = 3;
    private static final int EMV_CERT_VERIFY = 4;
    private static final int EMV_SHOW_PIN_PAD = 5;
    private static final int EMV_ONLINE_PROCESS = 6;
    private static final int EMV_SIGNATURE = 7;
    private static final int EMV_TRANS_SUCCESS = 888;
    private static final int EMV_TRANS_FAIL = 999;
    private static final int REMOVE_CARD = 1000;
    private static final int PIN_CLICK_NUMBER = 50;
    private static final int PIN_CLICK_PIN = 51;
    private static final int PIN_CLICK_CONFIRM = 52;
    private static final int PIN_CLICK_CANCEL = 53;
    private static final int PIN_ERROR = 54;

    private final Map<String, Long> timeMap = new LinkedHashMap<>();
    private final Handler handler = new Handler();
    private ProgressDialog progressDialog;

    /**
     * Variable Made In
     */

    TransData transData;

    Button btnContinue, btnConfirm, btnCancel, btnConfirmPin;
    private EditText etAmount, pinInputText;
    TextView pleaseDip, tapYourCard, tvCardNo, tvExpDate, enterAmount, tvAmount, tvTotalAmount, tvEnterPin;
    private View digitKeyboard;
    private DigitKeyboard mDigitKeyboard;

    private String tempCardNo            = "";
    private String tempExpDate           = "";
    private String tempAmount            = "";
    private String tempCardHolderName    = "";
    private String cardType              = "";
    private String tempTag55             = "";
    private String tempTrack2Data        = "";
    private String tempCardSeqNo         = "";
    private String amount = "";
    private long parseLong = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        transData = new TransData();
        transData.setTransactionType(TransConstant.TRANS_TYPE_SALE);
        transData.setProcCode(TransConstant.PROCODE_SALE);
        setContentView(R.layout.activity_sale);
        initView();
        try {
            basicOptV2.setNavigationBarVisibility(0);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }





    /**
     * Function Activity After Input Amount
     */
    private void actionAfterInputAmount(){
        etAmount.setEnabled(false);
        pleaseDip.setVisibility(View.VISIBLE);
        tapYourCard.setVisibility(View.VISIBLE);
        btnContinue.setVisibility(View.GONE);
    }

    /**
     * Initialize All Variable before Main Process
     */

    private void initView() {
        mEMVOptV2       = MtiApplication.app.emvOptV2;
        mPinPadOptV2    = MtiApplication.app.pinPadOptV2;
        mReadCardOptV2  = MtiApplication.app.readCardOptV2;

        mDigitKeyboard  = new DigitKeyboard(SaleActivity.this);
        etAmount        = findViewById(R.id.etAmount);
        mDigitKeyboard.attachTo(etAmount);

        btnContinue     = findViewById(R.id.btn_continue);
        digitKeyboard   = findViewById(R.id.view_keyboard);
        pleaseDip       = findViewById(R.id.pleaseDip);
        tapYourCard     = findViewById(R.id.tapYourCard);
        tvCardNo        = findViewById(R.id.tvCardNo);
        tvExpDate       = findViewById(R.id.tvExpDate);
        btnConfirm      = findViewById(R.id.btnConfirm);
        btnCancel       = findViewById(R.id.btnCancel);
        enterAmount     = findViewById(R.id.tvEnterAmount);
        tvAmount        = findViewById(R.id.tvAmount);
        tvTotalAmount   = findViewById(R.id.tvTotalAmount);
        tvEnterPin      = findViewById(R.id.tvEnterPin);
        pinInputText    = findViewById(R.id.pin_input_text);
        btnConfirmPin   = findViewById(R.id.btn_confirm_pin);

        transData.setCardNo("");
        transData.setExpDate("");
        transData.setCardHolderName("");


        EditorActionListener editorActionListener = new EditorActionListener() {
            @Override
            protected void onKeyOk(TextView view) {
                amount = String.valueOf(CurrencyConverter.parse(etAmount.getText().toString()));
                parseLong = Long.parseLong(amount);
                if (parseLong > 0){
                    digitKeyboard.setVisibility(View.INVISIBLE);
                    enterAmount.setText("Amount :");
                }else if (parseLong <= 0){
                    Toast toast = Toast.makeText(SaleActivity.this, R.string.card_cost_hint, Toast.LENGTH_LONG);
                    toast.show();
                }

            }
        };

        EnterAmountTextWatcher enterAmountTextWatcher = new EnterAmountTextWatcher();
        etAmount.setOnEditorActionListener(editorActionListener);
        etAmount.addTextChangedListener(enterAmountTextWatcher);

        /**
         * Action When Press Button
         */

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionAfterInputAmount();
                emvProcess();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mEMVOptV2.abortTransactProcess();
                    checkAndRemoveCard();
                    Intent cancel = new Intent(SaleActivity.this, MainActivity.class);
                    startActivity(cancel);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterAmount.setVisibility(View.GONE);
                etAmount.setVisibility(View.GONE);
                tvTotalAmount.setVisibility(View.VISIBLE);
                tvAmount.setVisibility(View.VISIBLE);
                tvAmount.setText(etAmount.getText().toString());
                pinInputText.setVisibility(View.GONE);
                tvEnterPin.setVisibility(View.GONE);
                btnCancel.setVisibility(View.GONE);
                btnConfirm.setVisibility(View.GONE);
                btnConfirmPin.setVisibility(View.GONE);

                transData.setAmount(tempAmount);
                transData.setCardHolderName(tempCardHolderName);
                transData.setCardNo(tempCardNo);
                transData.setExpDate(tempExpDate);

                transData.setTrack2Data(tempTrack2Data);
                transData.setCardSeqNo(Tools.paddingLeft(tempCardSeqNo,'0',4));
                importCardNoStatus(0);
            }
        });

    }

    /**
     * Start EMV Process
     */

    private void emvProcess(){
        try {
            JSONObject object = new JSONObject();
            object.put("maxOnlineTime", 150);
            String value = object.toString();
            MtiApplication.getInstance().basicOptV2.setSysParam(AidlConstantsV2.SysParam.RESERVED, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mProcessStep == 0) {
            tempAmount    = amount;
            try {
                // Before check card, initialize emv process(clear all TLV)
                timeMap.clear();
                timeMap.put("start", System.currentTimeMillis());
                timeMap.put("initEmvProcess", System.currentTimeMillis());
                mEMVOptV2.initEmvProcess();
                initEmvTlvData();
                LogUtil.i("TAG", String.valueOf(parseLong));
                checkCard();

            } catch (Exception e) {
                e.printStackTrace();
                Toast toast = Toast.makeText(SaleActivity.this, R.string.card_cost_hint, Toast.LENGTH_LONG);
                toast.show();
            }
        }
        else if (mProcessStep == EMV_CONFIRM_CARD_NO) {
            importCardNoStatus(0);
        } else if (mProcessStep == EMV_CERT_VERIFY) {
            importCertStatus(0);
        }
    }

    @Override
    public void onBackPressed() {
        if (mProcessStep == EMV_APP_SELECT) {
            importAppSelect(-1);
        } else if (mProcessStep == EMV_FINAL_APP_SELECT) {
            importFinalAppSelectStatus(-1);
        }
        super.onBackPressed();
    }

    /**
     * Notify emv process the Application select result
     *
     * @param selectIndex the index of selected App, start from 0
     */
    private void importAppSelect(int selectIndex) {
        LogUtil.e(Constant.TAG, "importAppSelect selectIndex:" + selectIndex);
        try {
            mEMVOptV2.importAppSelect(selectIndex);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Notify emv process the final Application select result
     *
     * @param status 0:success, other value:failed
     */
    private void importFinalAppSelectStatus(int status) {
        try {
            LogUtil.e(Constant.TAG, "importFinalAppSelectStatus status:" + status);
            mEMVOptV2.importAppFinalSelectStatus(status);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Notify emv process the card number confirm status
     *
     * @param status 0:success, other value:failed
     */
    private void importCardNoStatus(int status) {
        LogUtil.e(Constant.TAG, "importCardNoStatus status:" + status);
        if (cardType.equals("Dip")){
            try {
                mEMVOptV2.importCardNoStatus(status);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if (cardType.equals("Swipe")){
            mProcessStep = EMV_SHOW_PIN_PAD;
            mHandler.obtainMessage(EMV_SHOW_PIN_PAD).sendToTarget();
        }
    }

    /**
     * Notify emv process the certification verify status
     *
     * @param status 0:success, other value:failed
     */
    private void importCertStatus(int status) {
        LogUtil.e(Constant.TAG, "importCertStatus status:" + status);
        try {
            mEMVOptV2.importCertStatus(status);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Notify emv process the PIN input result
     *
     * @param inputResult 0:success,1:input PIN canceled,2:input PIN skipped,3:PINPAD problem,4:input PIN timeout
     */
    private void importPinInputStatus(int inputResult) {
        LogUtil.e(Constant.TAG, "importPinInputStatus:" + inputResult);
        try {
            mEMVOptV2.importPinInputStatus(mPinType, inputResult);
            /**
             * when user click cancel , then the screen return to TransactionActivity
             */
            if (inputResult == 1){
                checkAndRemoveCard();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void importOnlineProcessStatus(int status) {
        LogUtil.e(Constant.TAG, "importOnlineProcessStatus status:" + status);
        try {
            String[] tags = {"71", "72", "91", "8A", "89"};
            String[] values = {"", "", "", "", ""};
            byte[] out = new byte[1024];
            int len = mEMVOptV2.importOnlineProcStatus(status, tags, values, out);
            if (len < 0) {
                LogUtil.e(Constant.TAG, "importOnlineProcessStatus error,code:" + len);
            } else {
                byte[] bytes = Arrays.copyOf(out, len);
                String hexStr = ByteUtil.bytes2HexStr(bytes);
                LogUtil.e(Constant.TAG, "importOnlineProcessStatus outData:" + hexStr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Import signature result to emv process
     *
     * @param status 0:success, other value:failed
     */
    private void importSignatureStatus(int status) {
        LogUtil.e(Constant.TAG, "importSignatureStatus status:" + status);
        try {
            mEMVOptV2.importSignatureStatus(status);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Set tlv essential tlv data
     */
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
     * Start check card
     */
    private void checkCard() {
        try {
            timeMap.put("checkCard", System.currentTimeMillis());
            int cardType = AidlConstantsV2.CardType.MAGNETIC.getValue() | AidlConstantsV2.CardType.NFC.getValue() | AidlConstantsV2.CardType.IC.getValue();
            LogUtil.i("tag","process before");
            mReadCardOptV2.checkCard(cardType, mCheckCardCallback, 60);
            LogUtil.i("tag","process after");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Check card callback when EMV Process
     */
    private CheckCardCallbackV2 mCheckCardCallback = new CheckCardCallbackV2Wrapper() {

        @Override
        public void findMagCard(Bundle bundle) throws RemoteException {
            timeMap.put("findMagCard", System.currentTimeMillis());
            LogUtil.e(Constant.TAG, "findMagCard:" + bundle);
            mCardType = AidlConstantsV2.CardType.MAGNETIC.getValue();
            transData.setEnterMode(TransData.EnterMode.SWIPE);
            handleResult(bundle);
        }

        @Override
        public void findICCard(String atr) throws RemoteException {
            timeMap.put("findICCard", System.currentTimeMillis());
            LogUtil.e(Constant.TAG, "findICCard:" + atr);
            //IC card Beep buzzer when check card success
            MtiApplication.app.basicOptV2.buzzerOnDevice(1, 2750, 200, 0);
            mCardType = AidlConstantsV2.CardType.IC.getValue();
            transData.setEnterMode(TransData.EnterMode.INSERT);
            transactProcess();
        }

        @Override
        public void findRFCard(String uuid) throws RemoteException {
            timeMap.put("findRFCard", System.currentTimeMillis());
            LogUtil.e(Constant.TAG, "findRFCard:" + uuid);
            mCardType = AidlConstantsV2.CardType.NFC.getValue();
            transactProcess();
        }

        @Override
        public void onError(int code, String message) throws RemoteException {
            timeMap.put("checkcard_onError", System.currentTimeMillis());
            String error = "onError:" + message + " -- " + code;
            LogUtil.e(Constant.TAG, error);
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            tryAgain("Please Use Card Correctly");
            overridePendingTransition(0,0);
            timeMap.clear();
        }

        @Override
        public void onErrorEx(Bundle info) throws RemoteException {
            super.onErrorEx(info);
        }
    };

    /**
     * Function for get Magnetic Card Info
     */
    private void handleResult(Bundle bundle) {
        if (isFinishing()) {
            return;
        }
        handler.post(() -> {
            if (bundle == null) {
                return;
            }
            String track1 = Utility.null2String(bundle.getString("TRACK1"));
            String track2 = Utility.null2String(bundle.getString("TRACK2"));
            String track3 = Utility.null2String(bundle.getString("TRACK3"));

            mCardNo                 = Utility.getPan(track2);
            tempCardNo              = mCardNo;
            tempExpDate             = Utility.getExpDate(track2);
            tempCardHolderName      = Utility.getHolderName(track1);
            tempTrack2Data          = track2;

            int code1 = bundle.getInt("track1ErrorCode");
            int code2 = bundle.getInt("track2ErrorCode");
            int code3 = bundle.getInt("track3ErrorCode");
            LogUtil.e("TAG", String.format(Locale.getDefault(),
                    "track1ErrorCode:%d,track1:%s\ntrack2ErrorCode:%d,track2:%s\ntrack3ErrorCode:%d,track3:%s",
                    code1, track1, code2, track2, code3, track3));
            if ((code1 != 0 && code1 != -1) || (code2 != 0 && code2 != -1) || (code3 != 0 && code3 != -1)) {
                cardType = "Swipe";
                mProcessStep = EMV_CONFIRM_CARD_NO;
                mHandler.obtainMessage(EMV_CONFIRM_CARD_NO).sendToTarget();
            } else {
                //showResult(true, track1, track2, track3);
            }
            if (!isFinishing()) {
                handler.postDelayed(this::checkCard, 500);
            }
        });
    }

    /**
     * Start emv transact process
     */
    private void transactProcess() {
        timeMap.put("transactProcess", System.currentTimeMillis());
        timeMap.put("onTransactionStart", System.currentTimeMillis());
        LogUtil.e(Constant.TAG, "transactProcess");
        try {
            Bundle bundle = new Bundle();
            bundle.putString("amount", String.valueOf(CurrencyConverter.parse(etAmount.getText().toString())));
            bundle.putString("transType", "00");
           if (mCardType == AidlConstantsV2.CardType.NFC.getValue()) {
               bundle.putInt("flowType", AidlConstantsV2.EMV.FlowType.TYPE_NFC_SPEEDUP);
           } else {
               bundle.putInt("flowType", AidlConstantsV2.EMV.FlowType.TYPE_EMV_STANDARD);
           }
           bundle.putInt("cardType", mCardType);
           mEMVOptV2.transactProcessEx(bundle, mEMVListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * EMV process callback
     */
    private final EMVListenerV2 mEMVListener = new EMVListenerV2.Stub() {
        @Override
        public void onWaitAppSelect(List<EMVCandidateV2> appNameList, boolean isFirstSelect) throws RemoteException {
            timeMap.put("onWaitAppSelect", System.currentTimeMillis());
            LogUtil.e(Constant.TAG, "onWaitAppSelect isFirstSelect:" + isFirstSelect);
            mProcessStep = EMV_APP_SELECT;
            String[] candidateNames = getCandidateNames(appNameList);
            mHandler.obtainMessage(EMV_APP_SELECT, candidateNames).sendToTarget();
        }

        @Override
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
            mHandler.obtainMessage(EMV_FINAL_APP_SELECT, tag9F06Value).sendToTarget();
            timeMap.put("onAppFinalSelect_end", System.currentTimeMillis());
        }

        @Override
        public void onConfirmCardNo(String cardNo) throws RemoteException {
            timeMap.put("onConfirmCardNo", System.currentTimeMillis());
            LogUtil.e(Constant.TAG, "onConfirmCardNo cardNo:" + cardNo);
            mCardNo = cardNo;
            cardType     = "Dip" ;
            mProcessStep = EMV_CONFIRM_CARD_NO;
            mHandler.obtainMessage(EMV_CONFIRM_CARD_NO).sendToTarget();
        }

        @Override
        public void onRequestShowPinPad(int pinType, int remainTime) throws RemoteException {
            timeMap.put("onRequestShowPinPad", System.currentTimeMillis());
            if (!timeMap.containsKey("onTransactionEnd")) {
                timeMap.put("onTransactionEnd", System.currentTimeMillis());
            }
            LogUtil.e(Constant.TAG, "onRequestShowPinPad pinType:" + pinType + " remainTime:" + remainTime);
            mPinType = pinType;
            if (mCardNo == null) {
                mCardNo = getCardNo();
            }
            mProcessStep = EMV_SHOW_PIN_PAD;
            mHandler.obtainMessage(EMV_SHOW_PIN_PAD).sendToTarget();
        }

        /**
         * Notify  client to do signature
         */
        @Override
        public void onRequestSignature() throws RemoteException {
            timeMap.put("onRequestSignature", System.currentTimeMillis());
            if (!timeMap.containsKey("onTransactionEnd")) {
                timeMap.put("onTransactionEnd", System.currentTimeMillis());
            }
            LogUtil.e(Constant.TAG, "onRequestSignature");
            mProcessStep = EMV_SIGNATURE;
            mHandler.obtainMessage(EMV_SIGNATURE).sendToTarget();
        }

        /**
         * Notify client to do certificate verification
         *
         * @param certType The certificate type, refer to AidlConstants.CertType
         * @param certInfo The certificate info
         */
        @Override
        public void onCertVerify(int certType, String certInfo) throws RemoteException {
            timeMap.put("onCertVerify", System.currentTimeMillis());
            if (!timeMap.containsKey("onTransactionEnd")) {
                timeMap.put("onTransactionEnd", System.currentTimeMillis());
            }
            LogUtil.e(Constant.TAG, "onCertVerify certType:" + certType + " certInfo:" + certInfo);
            mCertInfo = certInfo;
            mProcessStep = EMV_CERT_VERIFY;
            mHandler.obtainMessage(EMV_CERT_VERIFY).sendToTarget();
        }

        /**
         * Notify client to do online process
         */
        @Override
        public void onOnlineProc() throws RemoteException {
            timeMap.put("onOnlineProc", System.currentTimeMillis());
            if (!timeMap.containsKey("onTransactionEnd")) {
                timeMap.put("onTransactionEnd", System.currentTimeMillis());
            }
            LogUtil.e(Constant.TAG, "onOnlineProcess");
            mProcessStep = EMV_ONLINE_PROCESS;
            mHandler.obtainMessage(EMV_ONLINE_PROCESS).sendToTarget();
        }

        /**
         * Notify client EMV kernel and card data exchange finished, client can remove card
         */
        @Override
        public void onCardDataExchangeComplete() throws RemoteException {
            timeMap.put("onCardDataExchangeComplete", System.currentTimeMillis());
            if (!timeMap.containsKey("onTransactionEnd")) {
                timeMap.put("onTransactionEnd", System.currentTimeMillis());
            }
            LogUtil.e(Constant.TAG, "onCardDataExchangeComplete");
            if (mCardType == AidlConstantsV2.CardType.NFC.getValue()) {
                //NFC card Beep buzzer to notify remove card
                MtiApplication.app.basicOptV2.buzzerOnDevice(1, 2750, 200, 0);
            }
        }

        /**
         * Notify client EMV process ended
         *
         * @param code The transaction result code, 0-success, 1-offline approval, 2-offline denial,
         *             4-try again, other value-error code
         * @param desc The corresponding message of this code
         */
        @Override
        public void onTransResult(int code, String desc) throws RemoteException {
            timeMap.put("onTransResult", System.currentTimeMillis());
            if (!timeMap.containsKey("onTransactionEnd")) {
                timeMap.put("onTransactionEnd", System.currentTimeMillis());
            }
            showStepTimestamp();
            if (mCardNo == null) {
                tryAgain("Card Number is Null, \n Please Try Again");
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                //mCardNo = getCardNo();
                //getCardInfo();
            }
            LogUtil.e(Constant.TAG, "onTransResult code:" + code + " desc:" + desc);
            LogUtil.e(Constant.TAG, "***************************************************************");
            LogUtil.e(Constant.TAG, "****************************End Process************************");
            LogUtil.e(Constant.TAG, "***************************************************************");
            if (code == 0) {
                mHandler.obtainMessage(EMV_TRANS_SUCCESS, code, code, desc).sendToTarget();
            } else if (code == 4) {
                tryAgain("Please Try Again");
            } else {
                mHandler.obtainMessage(EMV_TRANS_FAIL, code, code, desc).sendToTarget();
            }
        }

        /**
         * Notify client the confirmation code verified(See phone)
         */
        @Override
        public void onConfirmationCodeVerified() throws RemoteException {
            timeMap.put("onConfirmationCodeVerified", System.currentTimeMillis());
            if (!timeMap.containsKey("onTransactionEnd")) {
                timeMap.put("onTransactionEnd", System.currentTimeMillis());
            }
            showStepTimestamp();
            LogUtil.e(Constant.TAG, "onConfirmationCodeVerified");

            byte[] outData = new byte[512];
            int len = mEMVOptV2.getTlv(AidlConstantsV2.EMV.TLVOpCode.OP_PAYPASS, "DF8129", outData);
            if (len > 0) {
                byte[] data = new byte[len];
                System.arraycopy(outData, 0, data, 0, len);
                String hexStr = ByteUtil.bytes2HexStr(data);
                LogUtil.e(Constant.TAG, "DF8129: " + hexStr);
            }
            // card off
            mReadCardOptV2.cardOff(mCardType);
            runOnUiThread(() -> new AlertDialog.Builder(SaleActivity.this)
                    .setTitle("See Phone")
                    .setMessage("execute See Phone flow")
                    .setPositiveButton("OK", (dia, which) -> {
                                dia.dismiss();
                                // Restart transaction procedure.
                                try {
                                    mEMVOptV2.initEmvProcess();
                                    checkCard();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                    ).show()
            );
        }

        /**
         * Notify client to exchange data
         * <br/> This method only used for Russia MIR
         *
         * @param cardNo The card number
         */
        @Override
        public void onRequestDataExchange(String cardNo) throws RemoteException {
            timeMap.put("onRequestDataExchange", System.currentTimeMillis());
            LogUtil.e(Constant.TAG, "onRequestDataExchange,cardNo:" + cardNo);
            mEMVOptV2.importDataExchangeStatus(0);
        }
    };

    /** getCard number */
    private String getCardNo() {
        LogUtil.e(Constant.TAG, "getCardNo");
        try {
            String[] tagList = {"57", "5A"};
            byte[] outData = new byte[256];
            int len = mEMVOptV2.getTlvList(AidlConstantsV2.EMV.TLVOpCode.OP_NORMAL, tagList, outData);
            if (len <= 0) {
                LogUtil.e(Constant.TAG, "getCardNo error,code:" + len);
                return "";
            }
            byte[] bytes = Arrays.copyOf(outData, len);
            Map<String, TLV> tlvMap = TLVUtil.buildTLVMap(bytes);
            if (!TextUtils.isEmpty(Objects.requireNonNull(tlvMap.get("57")).getValue())) {
                TLV tlv57 = tlvMap.get("57");
                CardInfo cardInfo = Tools.parseTrack2(tlv57.getValue());
                return cardInfo.cardNo;
            }
            if (!TextUtils.isEmpty(Objects.requireNonNull(tlvMap.get("5A")).getValue())) {
                return Objects.requireNonNull(tlvMap.get("5A")).getValue();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return "";
    }
    /*
        Function Get Expired Date for ICC Card
     */
    public String getExpDate(){
        try {
            String[] tagList = {"57", "5A"};
            byte[] outData = new byte[256];
            int len = mEMVOptV2.getTlvList(AidlConstantsV2.EMV.TLVOpCode.OP_NORMAL, tagList, outData);
            byte[] bytes = Arrays.copyOf(outData, len);
            Map<String, TLV> tlvMap = TLVUtil.buildTLVMap(bytes);
            TLV tlv57 = tlvMap.get("57");
            CardInfo cardInfo = Tools.parseTrack2(tlv57.getValue());
            return cardInfo.expireDate;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return "";
    }
    /*
        Function Get Card Holder Name for ICC Card
     */
    public String getCardHolderName(){
        try {
            byte[] out = new byte[64];
            String[] tags = {
                    "5F20"
            };
            int len = mEMVOptV2.getTlvList(AidlConstantsV2.EMV.TLVOpCode.OP_NORMAL, tags, out);
            if (len > 0) {
                byte[] bytesOut = Arrays.copyOf(out, len);
                String hexStr = ByteUtil.bytes2HexStr(bytesOut);
                Map<String, TLV> map = TLVUtil.buildTLVMap(hexStr);
                TLV tlv5F20 = map.get("5F20"); // cardholder name
                String cardholder = "";
                if (tlv5F20 != null && tlv5F20.getValue() != null) {
                    String value = tlv5F20.getValue();
                    byte[] bytes = ByteUtil.hexStr2Bytes(value);
                    cardholder = new String(bytes);
                }
                LogUtil.i("TAG",cardholder);
                return cardholder;

            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return "";
    }

    private String getCardInfo() {
        LogUtil.e(Constant.TAG, "getCardNo");
        try {
            String[] tagList = {"57", "5A"};
            byte[] outData = new byte[256];
            int len = mEMVOptV2.getTlvList(AidlConstantsV2.EMV.TLVOpCode.OP_NORMAL, tagList, outData);
            if (len <= 0) {
                LogUtil.e(Constant.TAG, "getCardNo error,code:" + len);
                return null;
            }
            byte[] bytes = Arrays.copyOf(outData, len);
            Map<String, TLV> tlvMap = TLVUtil.buildTLVMap(bytes);
            if (!TextUtils.isEmpty(Objects.requireNonNull(tlvMap.get("57")).getValue())) {
                TLV tlv57 = tlvMap.get("57");
                CardInfo cardInfo = Tools.parseTrack2(tlv57.getValue());

                String trackDua = cardInfo.track2;
                LogUtil.i("TAG",trackDua);
                return cardInfo.track2;
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * Start show PinPad
     */
    private void initPinPad() {
        LogUtil.e(Constant.TAG, "initPinPad");
        try {
            PinPadConfigV2 pinPadConfig = new PinPadConfigV2();
            PinPadTextConfigV2 textConfigV2 = new PinPadTextConfigV2();

            textConfigV2.confirm = "INPUT PIN";
            textConfigV2.inputPin = "INPUT PIN";
            MtiApplication.app.pinPadOptV2.setPinPadText(textConfigV2);

            pinPadConfig.setPinPadType(0);
            pinPadConfig.setPinType(mPinType);
            pinPadConfig.setOrderNumKey(true);
            byte[] panBytes = mCardNo.substring(mCardNo.length() - 13, mCardNo.length() - 1).getBytes("US-ASCII");
            pinPadConfig.setPan(panBytes);
            pinPadConfig.setTimeout(60 * 1000); // input password timeout
            pinPadConfig.setPinKeyIndex(12);    // pik index
            pinPadConfig.setMaxInput(6);
            pinPadConfig.setMinInput(0);
            pinPadConfig.setKeySystem(0);
            pinPadConfig.setAlgorithmType(0);
            mPinPadOptV2.initPinPad(pinPadConfig, mPinPadListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initPinPadMag() {
        LogUtil.e(Constant.TAG, "initPinPadMag");
        try {
            PinPadConfigV2 pinPadConfig = new PinPadConfigV2();
            pinPadConfig.setPinPadType(0);
            pinPadConfig.setPinType(0);
            pinPadConfig.setOrderNumKey(true);
            byte[] panBytes = mCardNo.substring(mCardNo.length() - 13, mCardNo.length() - 1).getBytes("US-ASCII");
            pinPadConfig.setPan(panBytes);
            pinPadConfig.setTimeout(60 * 1000); // input password timeout
            pinPadConfig.setPinKeyIndex(12);    // pik index
            pinPadConfig.setMaxInput(12);
            pinPadConfig.setMinInput(0);
            mPinPadOptV2.initPinPad(pinPadConfig, mPinPadListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Input pin callback
     */
    private PinPadListenerV2 mPinPadListener = new PinPadListenerV2.Stub() {

        @Override
        public void onPinLength(int len) {
            timeMap.put("onPinLength", System.currentTimeMillis());
            LogUtil.e(Constant.TAG, "onPinLength:" + len);
            mHandler.obtainMessage(PIN_CLICK_NUMBER, len).sendToTarget();
        }

        @Override
        public void onConfirm(int i, byte[] pinBlock) {
            timeMap.put("onConfirm", System.currentTimeMillis());
            if (pinBlock != null) {
                String hexStr = ByteUtil.bytes2HexStr(pinBlock);
                LogUtil.e(Constant.TAG, "onConfirm pin block:" + hexStr);
                mHandler.obtainMessage(PIN_CLICK_PIN, pinBlock).sendToTarget();
            } else {
                mHandler.obtainMessage(PIN_CLICK_CONFIRM).sendToTarget();
            }
        }

        @Override
        public void onCancel() {
            timeMap.put("onCancel", System.currentTimeMillis());
            LogUtil.e(Constant.TAG, "onCancel");
            mHandler.obtainMessage(PIN_CLICK_CANCEL).sendToTarget();
        }

        @Override
        public void onError(int code) {
            timeMap.put("onError", System.currentTimeMillis());
            LogUtil.e(Constant.TAG, "onError:" + code);
            String msg = AidlErrorCodeV2.valueOf(code).getMsg();
            mHandler.obtainMessage(PIN_ERROR, code, code, msg).sendToTarget();
        }
    };

    /**
     * Mock a POSP to do some data exchange(online process), we don't have a really POSP,
     * client should connect to a really POSP at this step.
     */
    private void mockRequestToServer() {
        try {
            if (AidlConstantsV2.CardType.MAGNETIC.getValue() != mCardType) {
                tempTag55           = getTlvData();
                transData.setICCData(tempTag55);
                progressDialog = new ProgressDialog(SaleActivity.this);
                progressDialog.setMessage("Connect to server ...");
                progressDialog.setTitle("Send Receive Message");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.show();
                progressDialog.setCancelable(false);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            CommTask task = new CommTask(transData);
                            int result = task.startCommTask();
                            //String rcCode = transData.getResponseCode();
                            //if (rcCode == null || !rcCode.equals("00")){
                            if(result == Constant.RTN_COMM_SUCCESS){
                                importOnlineProcessStatus(0);
                                BatchRecord batchRecord = new BatchRecord(transData);
                                batchRecord.setUseYN("N");
                                Utility.saveTransactionToDb(batchRecord);
                                Intent intent = new Intent(SaleActivity.this, PrintActivity.class);
                                intent.putExtra(PrintActivity.EXTRA_TRANS,transData);
                                startActivity(intent);
                            }
                            else if(result == Constant.RTN_COMM_REVERSAL_COMPLETE) {
                            MtiApplication.getInstance().runOnUiThread(() ->{
                                Toast.makeText(SaleActivity.this, "Reversal Tran Completed", Toast.LENGTH_SHORT).show();
//                                    try {
//                                        MtiApplication.app.basicOptV2.buzzerOnDevice(1, 2750, 200, 0);
//                                    } catch (RemoteException e) {
//                                        e.printStackTrace();
//                                    }
                                Intent intent = new Intent(SaleActivity.this, MainActivity.class);
                                startActivity(intent);
                                });
                            }
                            else{
                                returnFailed();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        progressDialog.dismiss();
                    }
                }).start();
                //new CommProcess(this::afterCommProcess, transData).execute();
            }
            else if (AidlConstantsV2.CardType.MAGNETIC.getValue() == mCardType){
                progressDialog = new ProgressDialog(SaleActivity.this);
                progressDialog.setMessage("Connect to server ...");
                progressDialog.setTitle("Send Receive Message");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.show();
                progressDialog.setCancelable(false);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            CommTask task = new CommTask(transData);
                            int taskStatus = task.startCommTask();
                            //String rcCode = transData.getResponseCode();
                            //if (rcCode == null || !rcCode.equals("00")){
                            if(taskStatus != Constant.RTN_COMM_SUCCESS){
                                returnFailedMag();
                            }else{
                                importOnlineProcessStatus(0);
                                BatchRecord batchRecord = new BatchRecord(transData);
                                Utility.saveTransactionToDb(batchRecord);
                                Intent intent = new Intent(SaleActivity.this, PrintActivity.class);
                                intent.putExtra(PrintActivity.EXTRA_TRANS,transData);
                                startActivity(intent);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        progressDialog.dismiss();
                    }
                }).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
            importOnlineProcessStatus(-1);
        } finally {
            //dismissLoadingDialog();
        }
    }

    public void returnFailed(){
        importOnlineProcessStatus(-1);
        ResponseCode Rspcode = transData.getResponseCode();
        String retCode = Rspcode.getCode();
        String retmessage = Rspcode.getMessage();
        MtiApplication.getInstance().runOnUiThread(() -> {
                    Toast.makeText(getApplicationContext(), "FAILED, Respon: " + retmessage, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SaleActivity.this, MainActivity.class);
                    startActivity(intent);
                });
        checkAndRemoveCard();

    }

    public void returnFailedMag(){
        importOnlineProcessStatus(-1);
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        tryAgain("Transaction Failed");

    }

    /**
     * Read we interested tlv data
     */
    private String getTlvData() {
        String hexStr = "";
        try {
            String[] tagList = {
                    "9F27", "9F26", "95", "9F34", "9F02", "9F03", "5F2A", "9A",
                    "9F37", "82", "84", "9F10", "9F36", "9C", "9F1A", "9F33",
                    "9F35", "9F1E",
            };
            byte[] outData = new byte[2048];
            Map<String, TLV> map = new TreeMap<>();
            int tlvOpCode;
            tlvOpCode = AidlConstantsV2.EMV.TLVOpCode.OP_NORMAL;
            int len = mEMVOptV2.getTlvList(tlvOpCode, tagList, outData);
            if (len > 0) {
                byte[] bytes = Arrays.copyOf(outData, len);
                hexStr = ByteUtil.bytes2HexStr(bytes);
                Map<String, TLV> tlvMap = TLVUtil.buildTLVMap(hexStr);
                map.putAll(tlvMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hexStr;
    }

    /**
     * Create Candidate names
     */
    private String[] getCandidateNames(List<EMVCandidateV2> candiList) {
        if (candiList == null || candiList.size() == 0) return new String[0];
        String[] result = new String[candiList.size()];
        for (int i = 0; i < candiList.size(); i++) {
            EMVCandidateV2 candi = candiList.get(i);
            String name = candi.appPreName;
            name = TextUtils.isEmpty(name) ? candi.appLabel : name;
            name = TextUtils.isEmpty(name) ? candi.appName : name;
            name = TextUtils.isEmpty(name) ? "" : name;
            result[i] = name;
            LogUtil.e(Constant.TAG, "EMVCandidateV2: " + name);
        }
        return result;
    }

    /**
     * Show timestamp and spent time at each step
     */
    private void showStepTimestamp() {
        StringBuilder sb = new StringBuilder("\n");
        long start = timeMap.get("start");
        for (Map.Entry<String, Long> entry : timeMap.entrySet()) {
            sb.append(entry.getKey());
            sb.append(":");
            sb.append(entry.getValue());
            sb.append(", spendTime:");
            sb.append(entry.getValue() - start);
            sb.append("\n");
        }
        Long transStart = timeMap.get("onTransactionStart");
        Long transEnd = timeMap.get("onTransactionEnd");
        transStart = transStart == null ? 0 : transStart;
        transEnd = transEnd == null ? 0 : transEnd;
        String msg = "EMV transaction spend time:" + (transEnd - transStart);
        sb.append(msg);
        LogUtil.e(Constant.TAG, sb.toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelCheckCard();
    }

    private void cancelCheckCard() {
        try {
            mReadCardOptV2.cardOff(AidlConstantsV2.CardType.NFC.getValue());
            mReadCardOptV2.cancelCheckCard();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void tryAgain(String Message) {
        try {
            runOnUiThread(
                    new Runnable() {
                      public void run() {
                          Toast.makeText(SaleActivity.this, Message, Toast.LENGTH_SHORT).show();
                      }
                    }
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Check and notify remove card */
    private void checkAndRemoveCard() {
        try {
            int status = mReadCardOptV2.getCardExistStatus(mCardType);
            if (status < 0) {
                LogUtil.e(Constant.TAG, "getCardExistStatus error, code:" + status);
                return;
            }
            if (status == AidlConstantsV2.CardExistStatus.CARD_ABSENT) {
                //dismissLoadingDialog();
            } else if (status == AidlConstantsV2.CardExistStatus.CARD_PRESENT) {
                //showLoadingDialog(R.string.emv_remove_card);
                MtiApplication.app.basicOptV2.buzzerOnDevice(1, 2750, 200, 0);
                mHandler.sendEmptyMessageDelayed(REMOVE_CARD, 1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case EMV_FINAL_APP_SELECT:
                    importFinalAppSelectStatus(0);
                    break;
                case EMV_APP_SELECT:
                    //dismissLoadingDialog();
                    String[] candiNames = (String[]) msg.obj;
                    mAppSelectDialog = new AlertDialog.Builder(SaleActivity.this)
                            .setTitle(R.string.emv_app_select)
                            .setNegativeButton(R.string.cancel, (dialog, which) -> {
                                        importAppSelect(-1);
                                    }
                            )
                            .setPositiveButton(R.string.ok, (dialog, which) -> {
                                        //showLoadingDialog(R.string.handling);
                                        importAppSelect(mSelectIndex);
                                    }
                            )
                            .setSingleChoiceItems(candiNames, 0, (dialog, which) -> {
                                        mSelectIndex = which;
                                        LogUtil.e(Constant.TAG, "singleChoiceItems which:" + which);
                                    }
                            ).create();
                    mSelectIndex = 0;
                    mAppSelectDialog.show();
                    break;
                case EMV_CONFIRM_CARD_NO:
                    confirmPAN(mCardNo);
                    //dismissLoadingDialog();
                    break;
                case EMV_CERT_VERIFY:
                    //dismissLoadingDialog();
                    break;
                case EMV_SHOW_PIN_PAD:
                    //dismissLoadingDialog();
                    if (cardType.equals("Dip")){
                        initPinPad();
                    }
                    else if (cardType.equals("Swipe")){
                        initPinPadMag();
                    }
                    break;
                case EMV_ONLINE_PROCESS:
                    mockRequestToServer();
                    break;
                case EMV_SIGNATURE:
                    importSignatureStatus(0);
                    break;
                case PIN_CLICK_NUMBER:
                    break;
                case PIN_CLICK_PIN:
                    if (cardType.equals("Dip")){
                        importPinInputStatus(0);
                    }
                    else if (cardType.equals("Swipe")){
                        mockRequestToServer();
                    }
                    break;
                case PIN_CLICK_CONFIRM:
                    if (cardType.equals("Dip")){
                        importPinInputStatus(2);
                    }
                    else if (cardType.equals("Swipe")){
                        mockRequestToServer();
                    }
                    break;
                case PIN_CLICK_CANCEL:
                    tryAgain("user cancel");
                    importPinInputStatus(1);
                    break;
                case PIN_ERROR:
                    importPinInputStatus(3);
                    break;
                case EMV_TRANS_FAIL:
                    tryAgain("Failed Please Try Again");
                    Intent intent = new Intent(SaleActivity.this, MainActivity.class);
                    startActivity(intent);
                    break;
                case EMV_TRANS_SUCCESS:
                    checkAndRemoveCard();
                    break;
                case REMOVE_CARD:
                    checkAndRemoveCard();
                    break;
            }
        }
    };

    private void confirmPAN(String cardNo){
        String expDate = tempExpDate;
        String cardHolderName = "";
        String cardSeqno = "";

        if (cardType.equals("Dip")){
            expDate = getExpDate();
            tempExpDate         =   expDate;

            cardHolderName = getCardHolderName();
            LogUtil.i("TAG",cardHolderName);

            tempCardSeqNo       = getCardSeqNo();
            tempCardNo          = cardNo;
            tempCardHolderName  = cardHolderName;

            tempTrack2Data      = getCardInfo();

        }
        expDate = expDate.substring(2, 4) + "/" +expDate.substring(0, 2);

        btnConfirm.setVisibility(View.VISIBLE);
        btnCancel.setVisibility(View.VISIBLE);
        tvExpDate.setText(expDate);
        tvCardNo.setText(Tools.separateWithSpace(cardNo));
        pleaseDip.setVisibility(View.GONE);
        tapYourCard.setVisibility(View.GONE);
    }

    /**
     * Get Card Seq No From Card (dip only)
     * @return card sequence no, if no data return empty string
     */

    public String getCardSeqNo(){
        try {
            byte[] out = new byte[64];
            String[] tags = {
                    "5F34"
            };
            int len = mEMVOptV2.getTlvList(AidlConstantsV2.EMV.TLVOpCode.OP_NORMAL, tags, out);
            if (len > 0) {
                byte[] bytesOut = Arrays.copyOf(out, len);
                String hexStr = ByteUtil.bytes2HexStr(bytesOut);
                Map<String, TLV> map = TLVUtil.buildTLVMap(hexStr);
                TLV tlv5F34 = map.get("5F34"); // cardholder name
                String cardSeqNo = "";
                if (tlv5F34 != null && tlv5F34.getValue() != null) {
                    String value = tlv5F34.getValue();
                    //byte[] bytes = ByteUtil.hexStr2Bytes(value);
                    cardSeqNo = value;
                }
                LogUtil.i("TAG",cardSeqNo);
                return cardSeqNo;

            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return "";
    }
}
