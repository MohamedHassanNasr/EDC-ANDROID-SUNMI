package com.sm.sdk.yokkeedc.isopacker;

import android.util.Log;

import androidx.annotation.NonNull;

import com.pax.gl.pack.IIso8583;
import com.pax.gl.pack.exception.Iso8583Exception;
import com.sm.sdk.yokkeedc.MtiApplication;
import com.sm.sdk.yokkeedc.transaction.TransData;
import com.sm.sdk.yokkeedc.utils.FieldConstant;
import com.sm.sdk.yokkeedc.utils.TransConstant;
import com.sm.sdk.yokkeedc.utils.Utility;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashMap;

public abstract class PackIso8583 {
    protected static final String TAG = "PackIso8583";

    private static PackIso8583 packIso8583;

    private IIso8583 iso8583;
    public IIso8583.IIso8583Entity entity;
//    protected PackListener listener;

    public PackIso8583() {
        initEntity();
    }

    private void initEntity() {
        iso8583 = MtiApplication.getPacker().getIso8583();
        try {
            entity = iso8583.getEntity();
            entity.loadTemplate(MtiApplication.app.getResources().getAssets().open("edc8583.xml"));
        } catch (Iso8583Exception | IOException | XmlPullParserException e) {
            Log.e(TAG, "", e);
        }
    }

    protected void loadTemplate() throws Iso8583Exception, IOException, XmlPullParserException {
        entity.loadTemplate(MtiApplication.app.getResources().getAssets().open("edc8583.xml"));
    }

    protected final void setBitData(String field, String value) throws Iso8583Exception {
        if (value != null && !value.isEmpty()) {
            entity.setFieldValue(field, value);
        }
    }

    protected final void setBitData(String field, byte[] value) throws Iso8583Exception {
        if (value != null && value.length > 0) {
            entity.setFieldValue(field, value);
        }
    }

    @NonNull
    protected byte[] pack() {
        try {
//            setBitData("64", new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00});
            // for debug entity.dump();
            byte[] packData = iso8583.pack();
            if (packData == null || packData.length == 0) {
                return "".getBytes();
            }

            int len = packData.length;

            return packData;
        } catch (Iso8583Exception e) {
            Log.e(TAG, "", e);
        }
        return "".getBytes();
    }

    public HashMap<String, byte[]> unpack(byte[] rsp) {

        HashMap<String, byte[]> map;
        try {
            map = iso8583.unpack(rsp, true);
            entity.dump();

        } catch (Iso8583Exception e) {
            Log.e(TAG, "", e);
            return null;
        }

        byte[] header = map.get("h");
        return map;
    }

    protected void setMandatoryData(TransData transData) {
        //widya set temp data
        try {
            entity.setFieldValue(FieldConstant.MESSAGE_HEADER, "6000910085"); //tpdu
            entity.setFieldValue(FieldConstant.MESSAGE_TYPE,"0200");//mti
            entity.setFieldValue(FieldConstant.BIT_PROCESSING_CODE,transData.getProcCode()); //procode
            entity.setFieldValue(FieldConstant.BIT_SYSTEM_TRACE_AUDIT_NUMBER, Utility.getTraceNum()); //stan
            entity.setFieldValue(FieldConstant.BIT_NETWORK_INTERNATIONAL_IDENTIFIER,"107"); //nii

        } catch (Iso8583Exception e) {
            e.printStackTrace();
        }


    }

    protected void setFinancialData(TransData transData) {
        try {

            entity.setFieldValue("4", transData.getAmount() + "00"); //amount
            entity.setFieldValue("41", "73003495"); //tid
            entity.setFieldValue("42", "000071000243621"); //mid
        } catch (Iso8583Exception e) {
            e.printStackTrace();
        }

    }

    @NonNull
    //    public byte[] pack(@NonNull TransData transData) {
    public abstract byte[] pack(TransData transData);
}
