package com.sm.sdk.yokkeedc.isopacker;

import android.content.Context;

import com.pax.gl.pack.IApdu;
import com.pax.gl.pack.IIso8583;
import com.pax.gl.pack.ITlv;
import com.pax.gl.pack.impl.PaxGLPacker;

public class PackerImp implements IPacker{
    private PaxGLPacker paxGLPacker;

    PackerImp(Context context) {
        paxGLPacker = PaxGLPacker.getInstance(context);
    }

    @Override
    public IApdu getApdu() {
        return paxGLPacker.getApdu();
    }

    @Override
    public IIso8583 getIso8583() {
        return paxGLPacker.getIso8583();
    }

    @Override
    public ITlv getTlv() {
        return paxGLPacker.getTlv();
    }
}
