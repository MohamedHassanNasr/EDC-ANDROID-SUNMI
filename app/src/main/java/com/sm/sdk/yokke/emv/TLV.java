package com.sm.sdk.yokke.emv;

import com.sm.sdk.yokke.utils.ByteUtil;

public final class TLV {

    private final String tag;
    private final int length;
    private final String value;

    public TLV(String tag, String value) {
        this.tag = null2UpperCaseString(tag);
        this.value = null2UpperCaseString(value);
        this.length = ByteUtil.hexStr2Bytes(value).length;
    }

    public TLV(String tag, int length, String value) {
        this.tag = null2UpperCaseString(tag);
        this.length = length;
        this.value = null2UpperCaseString(value);
    }

    public String getTag() {
        return tag;
    }

    public int getLength() {
        return length;
    }

    public String getValue() {
        return value;
    }

    public String recoverToHexStr() {
        return com.sm.sdk.yokke.emv.TLVUtil.revertToHexStr(this);
    }

    public byte[] recoverToBytes() {
        return com.sm.sdk.yokke.emv.TLVUtil.revertToBytes(this);
    }

    @Override
    public String toString() {
        return "tag=[" + tag + "]," + "length=[" + length + "]," + "value=[" + value + "]";
    }

    private String null2UpperCaseString(String src) {
        return src == null ? "" : src.toUpperCase();
    }


}
