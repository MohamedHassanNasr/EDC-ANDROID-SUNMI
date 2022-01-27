package com.sm.sdk.yokke.transaction.validator;

import com.sm.sdk.yokke.utils.Constant;
import com.sm.sdk.yokke.utils.TransConstant;

import java.util.HashMap;

public class MessageValidator {
    private static String[] SALE_RESPONSE_MANDATORY = {"37","39"};
    private static String[] SALE_RESPONSE_BIT38 = {"38"};
    public static final int checkBitMandatory(HashMap<String, byte[]> map, String[] mandatoryFields) {
        int result = Constant.RTN_COMM_SUCCESS;
        for (String f : mandatoryFields) {

            if(map.get(f) == null) {
                return Constant.RTN_COMM_ERROR;
            }

        }
        return result;
    }

    public static final int validateMessage(HashMap<String, byte[]> map, String transType) {
        int result = Constant.RTN_COMM_SUCCESS;
        if(TransConstant.TRANS_TYPE_SALE.equals(transType)) {
            result = checkBitMandatory(map, SALE_RESPONSE_MANDATORY);
            if (result == Constant.RTN_COMM_SUCCESS) {
                result = checkBitMandatory(map, new String[]{"38"});
            }
        }

        return result;
    }
}
