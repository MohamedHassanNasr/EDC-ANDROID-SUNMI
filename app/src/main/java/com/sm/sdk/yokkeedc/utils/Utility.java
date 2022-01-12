package com.sm.sdk.yokkeedc.utils;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.sm.sdk.yokkeedc.MtiApplication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public final class Utility {
    private Utility() {
        throw new AssertionError("Create instance of Utility is forbidden.");
    }

    /** Bundle对象转换成字符串 */
    public static String bundle2String(Bundle bundle) {
        return bundle2String(bundle, 1);
    }

    /**
     * 根据key排序后将Bundle内容拼接成字符串
     *
     * @param bundle 要处理的bundle
     * @param order  排序规则，0-不排序，1-升序，2-降序
     * @return 拼接后的字符串
     */
    public static String bundle2String(Bundle bundle, int order) {
        if (bundle == null || bundle.keySet().isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        List<String> list = new ArrayList<>(bundle.keySet());
        if (order == 1) { //升序
            Collections.sort(list, String::compareTo);
        } else if (order == 2) {//降序
            Collections.sort(list, (o1, o2) -> o2.compareTo(o1));
        }
        for (String key : list) {
            sb.append(key);
            sb.append(":");
            sb.append(bundle.get(key));
            sb.append("\n");
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    /** 将null转换成空串 */
    public static String null2String(String str) {
        return str == null ? "" : str;
    }

    public static String formatStr(String format, Object... params) {
        return String.format(Locale.getDefault(), format, params);
    }

    /** 显示Toast */
    public static void showToast(final String msg) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> Toast.makeText(MtiApplication.app, msg, Toast.LENGTH_SHORT).show());
    }

    /** 显示Toast */
    public static void showToast(int resId) {
        showToast(MtiApplication.app.getString(resId));
    }

    /**
     * get account from track2
     *
     * @param track2 input track2 data
     * @return account no
     */
    public static String getPan(String track2) {
        if (track2 == null)
            return null;

        int len = track2.indexOf('=');
        if (len < 0) {
            len = track2.indexOf('D');
            if (len < 0)
                return null;
        }

        if ((len < 13) || (len > 19))
            return null;
        return track2.substring(0, len);
    }

    /**
     * get expiry data from track2
     *
     * @param track2 input track2 data
     * @return expiry data
     */
    public static String getExpDate(String track2) {
        if (track2 == null)
            return null;

        int index = track2.indexOf('=');
        if (index < 0) {
            index = track2.indexOf('D');
            if (index < 0)
                return null;
        }

        if (index + 5 > track2.length())
            return null;
        return track2.substring(index + 1, index + 5);
    }

    /**
     * get card holder name from track1
     *
     * @param track1 input track1 data
     * @return card holder name
     */
    public static String getHolderName(String track1) {
        if (track1 == null) {
            return null;
        }

        int index1 = track1.indexOf('^');
        if (index1 < 0) {
            return null;
        }

        int index2 = track1.lastIndexOf('^');
        if (index2 < 0) {
            return null;
        }
        return track1.substring(index1 + 1, index2);
    }



}
