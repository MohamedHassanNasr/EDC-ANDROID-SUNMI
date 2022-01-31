package com.sm.sdk.yokke.utils;

public class TransConstant {
    /**
     * Transaction Type
     */
    public static final String TRANS_TYPE_SALE = "SALE";
    public static final String TRANS_TYPE_VOID = "VOID SALE";
    public static final String TRANS_TYPE_SETTLEMENT = "SETTLEMENT";
    public static final String TRANS_TYPE_REVERSAL = "REVERSAL";
    public static final String TRANS_TYPE_GENERATE_QRIS = "GENERATE_QRIS";
    public static final String TRANS_TYPE_INQUIRY_QRIS = "INQUIRY_QRIS";
    public static final String TRANS_TYPE_REFUND_QRIS = "REFUND_QRIS";


    /**
     * Processing Code
     */
    public static final String PROCODE_SALE = "000000";
    public static final String PROCODO_VOID_SALE = "020000";
    public static final String PROCODO_QRIS = "010002";
    public static final String PROCODE_INQUIRY_QRIS = "011002";
    public static final String PROCODE_REFUND_QRIS = "013001";

    /**
     * Message Type Identifier
     */
    public static final String MESSAGE_TYPE_SALE = "0200";
    public static final String MESSAGE_TYPE_VOID_SALE = "0200";
    public static final String MESSAGE_TYPE_REVERSAL = "0400";
    public static final String MESSAGE_TYPE_QRIS = "0200";
    public static final String MESSAGE_TYPE_QRIS_INQUIRY = "0200";
    public static final String MESSAGE_TYPE_QRIS_REFUND = "0200";
}
