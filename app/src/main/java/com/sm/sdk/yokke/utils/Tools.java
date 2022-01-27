package com.sm.sdk.yokke.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;
import android.view.View;

import com.sunmi.pay.hardware.aidl.bean.CardInfo;

import java.io.UnsupportedEncodingException;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.CRC32;

import sunmi.sunmiui.utils.LogUtil;

public class Tools {

    private static final String TAG = "Tools";
    private static final String UTF8 = "UTF-8";
    private static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};


    public static byte[] string2Bytes(String source) {
        byte[] result = new byte[0];
        try {
            if (source != null)
                result = source.getBytes(UTF8);
        } catch (UnsupportedEncodingException e) {
            //ignore the exception
            Log.w(TAG, "", e);
        }
        return result;
    }

    public static byte[] string2Bytes(String source, int checkLen) {
        byte[] result = new byte[0];
        if (source == null || source.length() != checkLen)
            return result;
        try {
            result = source.getBytes(UTF8);
        } catch (UnsupportedEncodingException e) {
            //ignore the exception
            Log.w(TAG, "", e);
        }
        return result;
    }

    public static String bcd2Str(byte[] b) {
        if (b == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (byte i : b) {
            sb.append(HEX_DIGITS[((i & 0xF0) >>> 4)]);
            sb.append(HEX_DIGITS[(i & 0xF)]);
        }

        return sb.toString();
    }

    public static String hexToAscii(String hexStr) {
        StringBuilder output = new StringBuilder("");

        for (int i = 0; i < hexStr.length(); i += 2) {
            String str = hexStr.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }

        return output.toString();
    }

    public static String bcd2Str(byte[] b, int length) {
        if (b == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder(length * 2);
        for (int i = 0; i < length; ++i) {
            sb.append(HEX_DIGITS[((b[i] & 0xF0) >>> 4)]);
            sb.append(HEX_DIGITS[(b[i] & 0xF)]);
        }

        return sb.toString();
    }

    private static int strByte2Int(byte b) {
        int j;
        if ((b >= 'a') && (b <= 'z')) {
            j = b - 'a' + 0x0A;
        } else {
            if ((b >= 'A') && (b <= 'Z'))
                j = b - 'A' + 0x0A;
            else
                j = b - '0';
        }
        return j;
    }

    public static String getPaddedNumber(long num, int digit) {
        NumberFormat nf = NumberFormat.getInstance();
        nf.setGroupingUsed(false);
        nf.setMaximumIntegerDigits(digit);
        nf.setMinimumIntegerDigits(digit);
        return nf.format(num);
    }

    public static byte[] str2Bcd(String asc) {
        String str = asc;
        if (str.length() % 2 != 0) {
            str = "0" + str;
        }
        int len = str.length();
        if (len >= 2) {
            len /= 2;
        }
        byte[] bbt = new byte[len];
        byte[] abt = str.getBytes();

        for (int p = 0; p < str.length() / 2; p++) {
            bbt[p] = (byte) ((strByte2Int(abt[(2 * p)]) << 4) + strByte2Int(abt[(2 * p + 1)]));
        }
        return bbt;
    }

    public static byte[] int2ByteArray(int i) {
        byte[] to = new byte[4];
        int offset = 0;
        to[offset] = (byte) (i >>> 24 & 0xFF);
        to[(offset + 1)] = (byte) (i >>> 16 & 0xFF);
        to[(offset + 2)] = (byte) (i >>> 8 & 0xFF);
        to[(offset + 3)] = (byte) (i & 0xFF);
        for (int j = 0; j < to.length; ++j) {
            if (to[j] != 0) {
                return Arrays.copyOfRange(to, j, to.length);
            }
        }
        return new byte[]{0x00};
    }

    /**
     * todo : error handling
     * @param hex
     * @return
     */
    public static String hexToString(String hex) {
        StringBuilder sb = new StringBuilder();
        char[] hexData = hex.toCharArray();
        for (int count = 0; count < hexData.length - 1; count += 2) {
            int firstDigit = Character.digit(hexData[count], 16);
            int lastDigit = Character.digit(hexData[count + 1], 16);
            int decimal = firstDigit * 16 + lastDigit;
            sb.append((char) decimal);
        }
        return sb.toString();
    }

    public static void int2ByteArray(int i, byte[] to, int offset) {
        to[offset] = (byte) (i >>> 24 & 0xFF);
        to[(offset + 1)] = (byte) (i >>> 16 & 0xFF);
        to[(offset + 2)] = (byte) (i >>> 8 & 0xFF);
        to[(offset + 3)] = (byte) (i & 0xFF);
    }

    public static void int2ByteArrayLittleEndian(int i, byte[] to, int offset) {
        to[offset] = (byte) (i & 0xFF);
        to[(offset + 1)] = (byte) (i >>> 8 & 0xFF);
        to[(offset + 2)] = (byte) (i >>> 16 & 0xFF);
        to[(offset + 3)] = (byte) (i >>> 24 & 0xFF);
    }

    public static void short2ByteArray(short s, byte[] to, int offset) {
        to[offset] = (byte) (s >>> 8 & 0xFF);
        to[(offset + 1)] = (byte) (s & 0xFF);
    }

    public static void short2ByteArrayLittleEndian(short s, byte[] to, int offset) {
        to[offset] = (byte) (s & 0xFF);
        to[(offset + 1)] = (byte) (s >>> 8 & 0xFF);
    }

    public static int byteArray2Int(byte[] from, int offset) {
        return from[offset] << 24 & 0xFF000000 | from[(offset + 1)] << 16 & 0xFF0000 |
                from[(offset + 2)] << 8 & 0xFF00 | from[(offset + 3)] & 0xFF;
    }

    public static int byteArray2IntLittleEndian(byte[] from, int offset) {
        return from[(offset + 3)] << 24 & 0xFF000000 | from[(offset + 2)] << 16 & 0xFF0000 |
                from[(offset + 1)] << 8 & 0xFF00 | from[offset] & 0xFF;
    }

    public static short byteArray2Short(byte[] from, int offset) {
        return (short) (from[offset] << 8 & 0xFF00 | from[(offset + 1)] & 0xFF);
    }

    public static short byteArray2ShortLittleEndian(byte[] from, int offset) {
        return (short) (from[(offset + 1)] << 8 & 0xFF00 | from[offset] & 0xFF);
    }

    public static long getCRC32(byte[] data) {
        CRC32 crc = new CRC32();
        crc.update(data);
        return crc.getValue();
    }

    public static int bytes2Int(byte[] buffer, int radix) {
        int result;
        try {
            result = Integer.parseInt(bytes2String(buffer), radix);
        } catch (NumberFormatException e) {
            //ignore the exception
            result = 0;
        }
        return result;
    }

    public static String bytes2String(byte[] source) {
        String result = "";
        try {
            if (source.length > 0)
                result = new String(source, UTF8);
        } catch (UnsupportedEncodingException e) {
            //ignore the exception
            Log.w(TAG, "", e);
            result = "";
        }
        return result;
    }

    public static int bytes2Int(byte[] buffer) {
        int result = 0;
        int len = buffer.length;

        if ((len <= 0) || (len > 4)) {
            return 0;
        }
        for (int i = 0; i < len; i++) {
            result += (byte2Int(buffer[i]) << 8 * (len - 1 - i));
        }

        return result;
    }

    public static int byte2Int(byte b) {
        return b & 0xFF;
    }

    public static <T extends Enum<T>> T getEnum(Class<T> clazz, int ordinal) {
        for (T t : clazz.getEnumConstants()) {
            if (t.ordinal() == ordinal) {
                return t;
            }
        }
        return null;
    }

    public static byte[] fillData(int dataLength, byte[] source, int offset) {
        byte[] result = new byte[dataLength];
        if (offset >= 0)
            System.arraycopy(source, 0, result, offset, source.length);
        return result;
    }

    public static byte[] fillData(int dataLength, byte[] source, int offset, byte fillByte) {
        byte[] result = new byte[dataLength];
        for (int i = 0; i < dataLength; i++) {
            result[i] = fillByte;
        }
        if (offset >= 0)
            System.arraycopy(source, 0, result, offset, source.length);
        return result;
    }

    public static boolean byte2Boolean(byte b) {
        return b != 0;
    }

    public static byte boolean2Byte(boolean b) {
        return (byte) (b ? 1 : 0);
    }

    public static String paddingLeft(String inputString, char ch, int maxLength) {

        if (inputString.length() >= maxLength)
        {
            return inputString;
        }
        StringBuilder sb = new StringBuilder();
        while (sb.length() < maxLength - inputString.length()) {
            sb.append(ch);
        }
        sb.append(inputString);
        return sb.toString();
    }

    public static String paddingRight(String inputString, char ch, int maxLength) {

        if (inputString.length() >= maxLength)
        {
            return inputString;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(inputString);
        while (sb.length() < maxLength - inputString.length()) {
            sb.append(ch);
        }

        return sb.toString();
    }

    /**
     * format card no with spaces
     *
     * @param cardNo the original card no
     * @return spaced card no, if card no is null return empty string
     */
    public static String separateWithSpace(String cardNo) {
        if (cardNo == null)
            return "";
        String finalCardNo = cardNo.substring(12,16);
        cardNo = "";
        for (int i = 1 ; i <= 12 ; i++){
            cardNo += "*";
        }
        finalCardNo = cardNo+finalCardNo;

        StringBuilder temp = new StringBuilder();
        int total = finalCardNo.length() / 4;
        for (int i = 0; i < total; i++) {
            temp.append(finalCardNo.substring(i * 4, i * 4 + 4));
            if (i != (total - 1)) {
                temp.append(" ");
            }
        }
        if (total * 4 < finalCardNo.length()) {
            temp.append(" ");
            temp.append(finalCardNo.substring(total * 4, finalCardNo.length()));
        }
        return temp.toString();
    }

    /**
     * Parse track2 data
     */
    public static CardInfo parseTrack2(String track2) {
        LogUtil.e(Constant.TAG, "track2:" + track2);
        String track_2 = stringFilter(track2);
        int index = track_2.indexOf("=");
        if (index == -1) {
            index = track_2.indexOf("D");
        }
        CardInfo cardInfo = new CardInfo();
        if (index == -1) {
            return cardInfo;
        }
        String cardNumber = "";
        if (track_2.length() > index) {
            cardNumber = track_2.substring(0, index);
        }
        String expiryDate = "";
        if (track_2.length() > index + 5) {
            expiryDate = track_2.substring(index + 1, index + 5);
        }
        String serviceCode = "";
//        if (track_2.length() > index + 8) {
//            serviceCode = track_2.substring(index + 5, index + 8);
//        }
        LogUtil.e(Constant.TAG, "cardNumber:" + cardNumber + " expireDate:" + expiryDate + " serviceCode:" + serviceCode);
        cardInfo.cardNo = cardNumber;
        cardInfo.expireDate = expiryDate;
        cardInfo.serviceCode = serviceCode;
        cardInfo.track2     =   track_2;
        return cardInfo;
    }
    /**
     * remove characters not number,=,D
     */
    static String stringFilter(String str) {
        String regEx = "[^0-9=D]";
        Pattern p = Pattern.compile(regEx);
        Matcher matcher = p.matcher(str);
        return matcher.replaceAll("").trim();
    }

    /** Create Bitmap by View */
    public static Bitmap createViewBitmap(View v) {
        long start = System.currentTimeMillis();
        Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(),
                Bitmap.Config.ARGB_8888); //创建一个和View大小一样的Bitmap
        Canvas canvas = new Canvas(bitmap);  //使用上面的Bitmap创建canvas
        v.draw(canvas);  //把View画到Bitmap上
        LogUtil.e("TAG", "createViewBitmap time:" + (System.currentTimeMillis() - start));
        return bitmap;
    }
}
