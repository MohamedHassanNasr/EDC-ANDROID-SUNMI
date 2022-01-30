package com.sm.sdk.yokke.utils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import com.sm.sdk.yokke.R;
import com.sm.sdk.yokke.activities.MtiApplication;

public class ResponseCode implements Serializable {
    private static final long serialVersionUID = 1L;

    private String code;
    private String message;
    private HashMap<String, ResponseCode> map;
    private static ResponseCode rcCode;

    private static Map<String, Integer> responses = new HashMap<>();

    static {
        responses.put("00", R.string.response_00);
        responses.put("01", R.string.response_01);
        responses.put("03", R.string.response_03);
        responses.put("04", R.string.response_04);
        responses.put("05", R.string.response_05);
        responses.put("06", R.string.response_06);
        responses.put("07", R.string.response_07);
        responses.put("11", R.string.response_11);
        responses.put("12", R.string.response_12);
        responses.put("13", R.string.response_13);
        responses.put("14", R.string.response_14);
        responses.put("15", R.string.response_15);
        responses.put("19", R.string.response_19);
        responses.put("31", R.string.response_31);
        responses.put("43", R.string.response_43);
        responses.put("51", R.string.response_51);
        responses.put("52", R.string.response_52);
        responses.put("53", R.string.response_53);
        responses.put("54", R.string.response_54);
        responses.put("55", R.string.response_55);
        responses.put("56", R.string.response_56);
        responses.put("58", R.string.response_58);

        responses.put("61", R.string.response_61);
        responses.put("62", R.string.response_62);
        responses.put("67", R.string.response_67);
        responses.put("75", R.string.response_75);
        responses.put("76", R.string.response_76);
        responses.put("86", R.string.response_86);
        responses.put("87", R.string.response_87);
        responses.put("88", R.string.response_88);
        responses.put("89", R.string.response_89);
        responses.put("90", R.string.response_90);
        responses.put("92", R.string.response_92);
        responses.put("93", R.string.response_93);
        responses.put("96", R.string.response_96);

        responses.put("AG", R.string.response_AG);

        responses.put("P1", R.string.response_P1);
        responses.put("P2", R.string.response_P2);
        responses.put("P3", R.string.response_P3);
        responses.put("P4", R.string.response_P4);
        responses.put("P5", R.string.response_P5);
        responses.put("P6", R.string.response_P6);
        responses.put("P7", R.string.response_P7);
        responses.put("P8", R.string.response_P8);
        responses.put("P9", R.string.response_P9);

        responses.put("Q1", R.string.response_Q1);
        responses.put("Q2", R.string.response_Q2);
        responses.put("Q3", R.string.response_Q3);
        responses.put("Q4", R.string.response_Q4);
        responses.put("Q5", R.string.response_Q5);
        responses.put("Q6", R.string.response_Q6);
        responses.put("Q7", R.string.response_Q7);
        responses.put("Q8", R.string.response_Q8);
        responses.put("Q9", R.string.response_Q9);

        responses.put("B1", R.string.response_B1);
        responses.put("B2", R.string.response_B2);
        responses.put("B3", R.string.response_B3);
        responses.put("B4", R.string.response_B4);
        responses.put("B5", R.string.response_B5);
        responses.put("B6", R.string.response_B6);
        responses.put("B7", R.string.response_B7);
        responses.put("B8", R.string.response_B8);
        responses.put("B9", R.string.response_B9);

        responses.put("C1", R.string.response_C1);
        responses.put("C2", R.string.response_C2);
        responses.put("C3", R.string.response_C3);
        responses.put("C4", R.string.response_C4);
        responses.put("C5", R.string.response_C5);
        responses.put("C6", R.string.response_C6);
        responses.put("C7", R.string.response_C7);
        responses.put("C8", R.string.response_C8);
        responses.put("C9", R.string.response_C9);

        responses.put("D1", R.string.response_D1);
        responses.put("D2", R.string.response_D2);
        responses.put("R1", R.string.response_R1);
        responses.put("R2", R.string.response_R2);
    }

    private ResponseCode() {
        if (map == null)
            map = new HashMap<>();
    }

    private ResponseCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public static ResponseCode getInstance() {
        if (rcCode == null) {
            rcCode = new ResponseCode();
        }
        return rcCode;
    }

    /**
     * init方法必须调用， 一般放在应用启动的时候
     */
    public void init() {
        for (String i : responses.keySet()) {
            String msg = findResponse(i);
            ResponseCode rspCode = new ResponseCode(i, msg);
            map.put(i, rspCode);
        }
    }

    public ResponseCode parse(String code) {
        ResponseCode rc = map.get(code);
        if (rc == null)
            return new ResponseCode(code, Utility.getString(R.string.err_undefine_info));
        return rc;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private static String findResponse(final String code) {
        Integer id = responses.get(code);
        if (id == null) {
            id = R.string.response_unknown;
        }
        return MtiApplication.getInstance().getString(id);
    }

    @Override
    public String toString() {
        return this.getCode() + "\n" + this.getMessage();
    }
}
