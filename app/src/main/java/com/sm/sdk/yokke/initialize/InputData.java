package com.sm.sdk.yokke.initialize;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class InputData {
    public static final JSONObject jResult = new JSONObject();
    private static final JSONArray jArrAIDList = new JSONArray();
    private static final JSONObject jObjEMVTag = new JSONObject();
    private static final JSONObject jObjPaywave = new JSONObject();
    private static final JSONObject jObjPaypass = new JSONObject();
    private static final JSONObject jObjJSpeedy = new JSONObject();

    /** GROUP ID 10 DATA **/
    private static final String arrParamCode10[] = new String[30];
    private static final String[] arrParamVal10 = {"71000243338", "71234000", "TOKO HANDPHONE DUATIGA", "OLLALA",
            "26420210011" , "HAPPY STORE 123", "JALAN RAYA KELAPA",
            "JAKARTA SELATAN", "20211208080428", "7", "9002000",
            "1400", "1400", "1400", "6212", "1400", "0", null, null, "30",
            "0", "3", "4", null, null, "1", "09999912381", "9"};

    /** GROUP ID 30 DATA **/
    private static final String[] arrParamCode30 = {"3001", "3002", "3003", "3004", "3005"};
    private static final String[] arrParamVal30 = {"1","1","0","0","1"};

    /** GROUP ID 50 DATA **/
    private static final String[] arrAID =
            {
                    "A0000000031010", "A0000000032010",
                    "A0000000041010", "A0000000651010",
                    "A0000003330101", "A000000333010101",
                    "A000000333010102", "A000000333010103",
                    "A000000333010106", "A0000006021010"
            };

    private static final String[] arrEMVTag = {"5F2A","9F09","9F1B","1F07","1F08","1F09","1F15","1F04",
            "1F05","1F03","9F35","9F15","9F33","1F03","1F04","1F05",
            "1F07","1F08","1F09","1F15","5F2A","9F09","9F1B","9F33",
            "9F35","9F3C","9F3D","9F40","9F3C","9F3D","5F2A","9F09",
            "9F1B","1F07","1F08","1F09","1F15","1F04","1F05","1F03",
            "9F35","9F15","9F33","5F36","9F40","9F3C","9F3D","5F2A",
            "5F36","9F40","1F03","1F04","1F05","1F07","1F08","1F09",
            "1F15","5F2A","5F36","9F09","9F15","9F1B","9F33","9F35",
            "9F3C","9F3D","9F40","1F03","1F04","1F05","1F07","1F08",
            "1F09","1F15","9F09","9F1B","9F15","9F33","9F40","9F3C",
            "9F3D","9F35","5F36","5F2A","1F03","1F04","1F05","1F07",
            "1F08","1F09","1F15","9F09","9F1B","9F15","9F33","9F40",
            "9F3C","9F3D","9F35","5F36","5F2A","9F09","9F1B","1F09",
            "1F15","1F03","1F04","1F05","1F07","1F08","9F35","9F15",
            "9F33","5F36","9F40","9F3C","9F3D","5F2A","9F09","9F1B",
            "1F07","1F08","1F09","1F15","1F04","1F05","1F03","9F35",
            "9F15","9F33","5F36","9F40","9F3C","9F3D","9F1B","5F2A",
            "9F09","1F07","1F08","1F09","1F15","1F04","1F05","1F03",
            "9F35","9F15","9F33","5F36","9F40","9F3C","9F3D","5F2A",
            "9F09","9F1B","1F07","1F08","1F09","1F15","1F04","1F05",
            "1F03","9F35","9F15","9F33","5F36","9F40","9F3C","9F3D",
            "5F2A","9F09","9F1B","1F07","1F08","1F09","1F15","1F04",
            "1F05","1F03","9F35","9F15","9F33","5F36","9F40","9F3C",
            "9F3D"};

    private static final String[] arrEMVTagVal = {"360", "200", "0", "0", "0", "0", "9F3704",
            "10000000", "D84004F800", "D84000A800", "22", "742", "E070C8",
            "DC4000A800", "10000000", "DC4004F800", "0", "0", "0", "9F3704",
            "360", "0", "0", "E070C8", "22", "0", "0", "F000F0A001", "0", "0",
            "360", "200", "0", "0", "0", "0", "9F3704", "10000000", "FC60ACF800",
            "FC6024A800", "22", "742", "E070C8", "0", "F000F0A001", "0", "0",
            "360", "0", "F000F0A001", "F850ACA000", "0", "F850ACF800", "0", "0",
            "0", "9F3704", "360", "0", "0", "742", "0", "E070C8", "22", "0", "0",
            "F000F0A001", "DC4000A800", "10000000", "DC4004F800", "0", "0", "0",
            "9F3704", "008C", "0", "742", "E078C8", "F000F0A001", "0", "0", "22",
            "0", "360", "DC4000A800", "10000000", "DC4004F800", "0", "0", "0", "9F3704",
            "008C", "0", "742", "E078C8", "F000F0A001", "0", "0", "22", "0", "360",
            "2", "0", "99", "9F3704", "FC50BCA000", "0", "FC50BCF800", "0", "99",
            "22", "742", "E070C8", "0", "F000F0A001", "0", "0", "360", "200", "0",
            "0", "0", "0", "9F3704", "360", "0", "0", "742", "0", "E070C8", "22", "0", "0",
            "F000F0A001", "DC4000A800", "10000000", "DC4004F800", "0", "0", "0", "9F3704",
            "008C", "0", "742", "E078C8", "F000F0A001", "0", "0", "22", "0", "360", "DC4000A800",
            "10000000", "DC4004F800", "0", "0", "0", "9F3704", "008C", "0", "742", "E078C8",
            "F000F0A001", "0", "0", "22", "0", "360", "2", "0", "99", "9F3704", "FC50BCA000", "0",
            "FC50BCF800", "0", "99", "22", "742", "E070C8", "0", "F000F0A001", "0", "0", "360",
            "200", "0", "0", "0", "0", "9F3704", "10000000", "D84004F800", "D84000A800", "22",
            "742", "E070C8", "0", "F000F0A001", "0", "0", "0", "360", "200", "0", "0", "0",
            "9F3704", "10000000", "D84004F800", "D84000A800", "22", "742", "E070C8", "0",
            "F000F0A001", "0", "0", "360", "200", "0", "0", "0", "0", "9F3704", "10000000",
            "D84004F800", "D84000A800", "22", "742", "E070C8", "0", "F000F0A001", "0", "0",
            "360", "200", "0", "0", "0", "0", "9F3704", "10000000", "D84004F800", "D84000A800",
            "22", "742", "E070C9", "0", "F000F0A001", "0", "0"};

    private static final String CAPK_VAL = "BC853E6B5365E89E7EE9317C94B02D0ABB0DBD91C05A224A2554AA29ED9FCB9D86EB9CCBB322A57811F86188AAC7351C72BD9EF196C5A01ACEF7A4EB0D2AD63D9E6AC2E7836547CB1595C68BCBAFD0F6728760F3A7CA7B97301B7E0220184EFC4F653008D93CE098C0D93B45201096D1ADFF4CF1F9FC02AF759DA27CD6DFD6D789B099F16F378B6100334E63F3D35F3251A5EC78693731F5233519CDB380F5AB8C0F02728E91D469ABD0EAE0D93B1CC66CE127B29C7D77441A49D09FCA5D6D9762FC74C31BB506C8BAE3C79AD6C2578775B95956B5370D1D0519E37906B384736233251E8F09AD79DFBE2C6ABFADAC8E4D8624318C27DAF1";

    private static final String[] arrPaywaveParamTag = {"5F2A","9F09","9F1A","9F35","9F66","DF00","DF01","DF02"};
    private static final String[] arrPaywaveParamVal = {"360","008C","360","21","36004000","0","100000100","0"};
    private static final String[] arrPaypassParamTag = {"9F09","9F15","9F1D","9F35","EF01","EF02","EF03","EF04","EF05","EF06","EF07","EF08","EF09"};
    private static final String[] arrPaypassParamVal = {"2","9311","6CFC800000000000","22","9F6A04","0","0","0","100000100","8","0","0","0"};
    private static final String[] arrJSpeedyParamTag = {"5F2A","9F1A","9F35","FF00","FF01","FF02","FF03"};
    private static final String[] arrJSpeedyParamVal = {"360","360","21","500000000","100000000","100000000","0"};

    public InputData() {

    }

    public static void setData() throws JSONException {
        int i;

        for(i = 1; i<=28; i++) {
            arrParamCode10[i] = Integer.toString(1000 + i);
            jResult.put(arrParamCode10[i], arrParamVal10[i-1]);
        }

        for(i = 0; i<arrParamCode30.length; i++) {
            jResult.put(arrParamCode30[i], arrParamVal30[i]);
        }

        for(i = 0; i<arrAID.length; i++) {
            jArrAIDList.put(arrAID[i]);
        }

        jResult.put("5001", jArrAIDList);
        jResult.put("5002", CAPK_VAL);

        for(i = 0; i<arrEMVTag.length; i++) {
            jObjEMVTag.put(arrEMVTag[i],arrEMVTagVal[i]);
        }

        jResult.put("5003",jObjEMVTag);

        for(i=0; i<arrPaywaveParamTag.length; i++){
            jObjPaywave.put(arrPaywaveParamTag[i], arrPaywaveParamVal[i]);
        }

        jResult.put("5004", jObjPaywave);

        for(i=0; i<arrPaypassParamTag.length; i++){
            jObjPaywave.put(arrPaypassParamTag[i], arrPaypassParamVal[i]);
        }

        jResult.put("5005", jObjPaypass);

        for(i=0; i<arrJSpeedyParamTag.length; i++){
            jObjPaywave.put(arrJSpeedyParamTag[i], arrJSpeedyParamVal[i]);
        }

        jResult.put("5006", jObjJSpeedy);

    }
}
