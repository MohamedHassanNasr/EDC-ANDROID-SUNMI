package com.sm.sdk.yokkeedc.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.sm.sdk.yokkeedc.R;

public class FloatView implements View.OnLongClickListener, View.OnTouchListener {
    enum Status {
        OPEN,
        CLOSE;
    }

    private Status status;

    private Activity context;

    private String logStr = "";
    private final String DEFAULT_TEXT = "Log";

    private static FloatView floatView;

    private WindowManager wm;
    private TextView textView;
    private WindowManager.LayoutParams params;
    private long startTime = 0;

    float lastX = 0, lastY = 0;
    int oldOffsetX = 0, oldOffsetY = 0;
    int tag = 0;

    private FloatView(Activity context) {
        this.context = context;
    }

    public static FloatView getInstance(Activity context) {
        if (floatView == null) {
            synchronized (FloatView.class) {
                if (floatView == null) {
                    floatView = new FloatView(context);
                }
            }
        }
        return floatView;
    }

    public void release() {
        floatView = null;
    }


    @SuppressLint("ClickableViewAccessibility")

    public void createFloatView(int paddingTop, int paddingRight) {
        // int w = 80;// 大小
        wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//        textView = (TextView) LayoutInflater.from(context).inflate(R.layout.floatview, null);
        textView.setMovementMethod(ScrollingMovementMethod.getInstance());
        params = new WindowManager.LayoutParams();
        params.type = WindowManager.LayoutParams.TYPE_BASE_APPLICATION;// 所有程序窗口的“基地”窗口，其他应用程序窗口都显示在它上面。
        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.format = PixelFormat.TRANSLUCENT;// 不设置这个弹出框的透明遮罩显示为黑色
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.TOP | Gravity.LEFT;
        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        // int screenHeight = c.getResources().getDisplayMetrics().heightPixels;
        params.x = screenWidth - paddingRight - 50;
        params.y = paddingTop;
        textView.setBackgroundColor(Color.GRAY);
        textView.setVisibility(View.VISIBLE);
        textView.setOnLongClickListener(this);
        textView.setOnTouchListener(this);
        status = Status.CLOSE;
        wm.addView(textView, params);
    }

    public void onFloatViewClick() {
        if (status == Status.CLOSE) {
            open();
        }
    }

    /**
     * 将悬浮View从WindowManager中移除，需要与createFloatView()成对出现
     */
    public void removeFloatView() {
        if (wm != null && textView != null) {
            wm.removeViewImmediate(textView);
            textView = null;
            wm = null;
        }
        status = Status.CLOSE;
    }

    /**
     * 隐藏悬浮View
     */
    public void hideFloatView() {
        if (wm != null && textView != null && textView.isShown()) {
            textView.setVisibility(View.GONE);
        }
        status = Status.CLOSE;
    }

    /**
     * 显示悬浮View
     */
    public void showFloatView() {
        if (wm != null && textView != null && !textView.isShown()) {
            textView.setVisibility(View.VISIBLE);
            status = Status.OPEN;
        }
    }

    private void setText() {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (status == Status.CLOSE || logStr.isEmpty()) {
                    textView.setText(DEFAULT_TEXT);
                } else {
                    textView.setText(logStr);
                }
                wm.updateViewLayout(textView, params);
            }
        });
    }

    public void open() {
        status = Status.OPEN;
        setText();
    }

    public void close() {
        status = Status.CLOSE;
        setText();
    }

    public void closeAndClear() {
        logStr = "";
        status = Status.CLOSE;
        setText();
    }

    public void update() {
        if (status == Status.OPEN) {
            setText();
        }
    }

    public static void appendLog(String log) {
        log = log.endsWith("\n") ? log : log + "\n";
        floatView.logStr += log;
        floatView.update();
    }

    public static void clearLog() {
        floatView.closeAndClear();
    }



    @Override
    public boolean onLongClick(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete log?");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                closeAndClear();
            }
        });
        builder.create().show();
        return true;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        final int action = event.getAction();
        float x = event.getX();
        float y = event.getY();
        if (tag == 0) {
            oldOffsetX = params.x; // 偏移量
            oldOffsetY = params.y; // 偏移量
        }
        if (action == MotionEvent.ACTION_DOWN) {
            lastX = x;
            lastY = y;
        } else if (action == MotionEvent.ACTION_MOVE) {
            params.x += (int) (x - lastX) / 3; // 减小偏移量,防止过度抖动
            params.y += (int) (y - lastY) / 3; // 减小偏移量,防止过度抖动
            tag = 1;
            wm.updateViewLayout(textView, params);
        } else if (action == MotionEvent.ACTION_UP) {
            int newOffsetX = params.x;
            int newOffsetY = params.y;
            // 只要按钮移动位置不是很大,就认为是点击事件
            if (Math.abs(oldOffsetX - newOffsetX) <= 20 && Math.abs(oldOffsetY - newOffsetY) <= 20) {
                onFloatViewClick();
            } else {
                tag = 0;
            }

            if ((System.currentTimeMillis() - startTime) < 500) {
                // doubleClick
                close();
            } else {
                startTime = System.currentTimeMillis();
            }
        }
        return false;
    }
}
