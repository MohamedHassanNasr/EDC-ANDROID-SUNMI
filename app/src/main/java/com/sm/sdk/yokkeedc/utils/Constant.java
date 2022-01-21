package com.sm.sdk.yokkeedc.utils;

public class Constant {
    public static final String TAG = "Constant";

    public static final int LANGUAGE_AUTO = 0;
    public static final int LANGUAGE_ZH_CN = 1;
    public static final int LANGUAGE_EN_US = 2;
    public static final int LANGUAGE_JA_JP = 3;

    public static final int SCAN_MODEL_NONE = 100;
    public static final int SCAN_MODEL_P2Lite = 101;

    public static final String SCAN_MODEL_NONE_VALUE = "NONE";
    public static final String SCAN_MODEL_P2Lite_VALUE = "P2Lite";

    /**
     * RESPONSE CODE
     */
    public static final String ANSWER_CODE_MAC = "B6";
    public static final String ANSWER_CODE_SCRIPT = "SE";
    public static final String ANSWER_CODE_DENIAL = "DE";
    public static final String ANSWER_CODE_FAILED = "FE";
    public static final String ANSWER_CODE_NETWORK = "NE";
    public static final String ANSWER_CODE_TIMEOUT = "TE";

    /**
     * Comm Return
     */
    public static final int RTN_COMM_MSG_PARSE_ERROR = -2010;
    public static final int RTN_COMM_SEND_ERROR	     = -2011;
    public static final int RTN_COMM_TRY_RTRAN       = -2012;
    public static final int RTN_COMM_MANUAL_DISCONNECT = -2013;
    public static final int RTN_COMM_ALREADY_DISCONNECT	=	-2014;
    public static final int RTN_COMM_MAC_COMP_ERROR		=	-2015;
    public static final int RTN_COMM_NET_FAIL			=	-2016;
    public static final int RTN_COMM_NET_ETH_UNLINK		=	-2017;
    public static final int RTN_COMM_REVERSAL_COMPLETE	=	-2018;
    public static final int RTN_COMM_USER_CANCEL		=	-2019;
    public static final int RTN_COMM_TIMEOUT			=	-2020;
    public static final int RTN_COMM_MANDATORY_ERROR	=	-2021;
    public static final int RTN_COMM_SUCCESS = 1;
    public static final int RTN_COMM_ERROR = 0;


}
