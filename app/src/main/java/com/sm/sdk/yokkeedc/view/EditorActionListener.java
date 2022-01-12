package com.sm.sdk.yokkeedc.view;

import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.sm.sdk.yokkeedc.MtiApplication;

public abstract class EditorActionListener implements TextView.OnEditorActionListener{
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            MtiApplication.app.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onKeyOk(v);
                }
            });
        } else if (actionId == EditorInfo.IME_ACTION_NONE) {
//            MtiApplication.getApp().runOnUiThread(EditorActionListener.this::onKeyCancel);
            v.setText("");
        }
        return false;
    }

    protected abstract void onKeyOk(TextView v);

//    protected abstract void onKeyCancel();
}
