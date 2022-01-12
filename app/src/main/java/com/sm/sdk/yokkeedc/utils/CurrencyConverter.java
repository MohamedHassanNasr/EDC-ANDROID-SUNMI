package com.sm.sdk.yokkeedc.utils;

import android.util.Log;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

public class CurrencyConverter {
    private static final String TAG = "CurrencyConv";
    private static final List<Locale> locales = new ArrayList<>();

    private static Locale defLocale = new Locale("in","ID");


    private CurrencyConverter() {
        //do nothing
    }


    public static Locale getDefCurrency() {
        return defLocale;
    }

    /**
     * @param amount
     * @return
     */
    public static String convert(long amount) {
        return convert(amount, defLocale);
    }

    /**
     * @param amount
     * @param locale
     * @return
     */
    public static String convert(long amount, Locale locale) {
        Currency currency = Currency.getInstance(locale);
        NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);
        formatter.setMinimumFractionDigits(currency.getDefaultFractionDigits());
        formatter.setMaximumFractionDigits(currency.getDefaultFractionDigits());
        long newAmount = amount < 0 ? -amount : amount; // AET-58
        String prefix = amount < 0 ? "-" : "";
        try {
            double amt = Double.valueOf(newAmount) / (Math.pow(10, currency.getDefaultFractionDigits()));
            return prefix + formatter.format(amt);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "", e);
        }
        return "";
    }

    public static Long parse(String formatterAmount) {
        return parse(formatterAmount, defLocale);
    }

    public static Long parse(String formatterAmount, Locale locale) {
        Currency currency = Currency.getInstance(locale);
        NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);
        formatter.setMinimumFractionDigits(currency.getDefaultFractionDigits());
        formatter.setMaximumFractionDigits(currency.getDefaultFractionDigits());
        try {
            Number num = formatter.parse(formatterAmount);

            return Math.round(num.doubleValue() * Math.pow(10, currency.getDefaultFractionDigits()));
        } catch (ParseException | NumberFormatException e) {
            Log.e(TAG, "", e);
        }
        return 0L;
    }

//    public static byte[] getCurrencyCode() {
//        Currency currency = Currency.getInstance(defLocale);
//        String currencyCode = currency.getCurrencyCode();
//        LogUtils.i(TAG, "currency symbol:" + currency.getSymbol() + ", Currency code:" + currencyCode + ",fraction:" + currency.getDefaultFractionDigits());
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            LogUtils.d(TAG, ",numeric code:" + currency.getNumericCode());
//            return ConvertHelper.getConvert().intToByteArray(currency.getNumericCode(), IConvert.EEndian.LITTLE_ENDIAN);
//        }
//        return new byte[]{0x08, 0x40};
//    }


    public static int getCurrencyFraction() {
        Currency currency = Currency.getInstance(defLocale);
        return currency.getDefaultFractionDigits();
    }
}
