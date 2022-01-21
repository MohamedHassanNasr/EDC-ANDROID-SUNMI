package com.sm.sdk.yokkeedc.isopacker;

import com.pax.gl.pack.IApdu;
import com.pax.gl.pack.IIso8583;
import com.pax.gl.pack.ITlv;

public interface IPacker {
    IApdu getApdu();

    IIso8583 getIso8583();

    ITlv getTlv();
}
