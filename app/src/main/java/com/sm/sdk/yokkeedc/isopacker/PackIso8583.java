package com.sm.sdk.yokkeedc.isopacker;

import android.util.Log;

import androidx.annotation.NonNull;

import com.pax.gl.pack.IIso8583;
import com.pax.gl.pack.exception.Iso8583Exception;
import com.sm.sdk.yokkeedc.MtiApplication;
import com.sm.sdk.yokkeedc.transaction.TransData;

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

    public int unpack(byte[] rsp) {

        HashMap<String, byte[]> map;
        try {
            map = iso8583.unpack(rsp, true);
            entity.dump();

        } catch (Iso8583Exception e) {
            Log.e(TAG, "", e);
            return 1;
        }

        byte[] header = map.get("h");
        return 0;
    }

    @NonNull
    //    public byte[] pack(@NonNull TransData transData) {
    public abstract byte[] pack(TransData transData);
}
