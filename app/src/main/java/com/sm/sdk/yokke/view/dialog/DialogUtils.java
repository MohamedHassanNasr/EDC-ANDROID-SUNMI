package com.sm.sdk.yokke.view.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.KeyEvent;

import com.sm.sdk.yokke.R;
import com.sm.sdk.yokke.activities.MainActivity;
import com.sm.sdk.yokke.activities.MtiApplication;
import com.sm.sdk.yokke.view.CustomAlertDialog;

public class DialogUtils {

    private DialogUtils() {
        //do nothing
    }
    public static void showErrMessage(final Context context, final String title, final String msg,
                                      final DialogInterface.OnDismissListener listener, final int timeout) {
        if (context == null) {
            return;
        }
        MtiApplication.getInstance().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CustomAlertDialog dialog = new CustomAlertDialog(context, CustomAlertDialog.ERROR_TYPE, true, timeout);
                dialog.setTitleText(title);
                dialog.setContentText(msg);
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
                dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        return keyCode == KeyEvent.KEYCODE_BACK;
                    }
                });
                dialog.setOnDismissListener(listener);
                //Device.beepErr(); --beepError
            }
        });
    }

    public static void showSuccMessage(final Context context, final String title,
                                       final DialogInterface.OnDismissListener listener, final int timeout) {
        if (context == null) {
            return;
        }
        MtiApplication.getInstance().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CustomAlertDialog dialog = new CustomAlertDialog(context, CustomAlertDialog.SUCCESS_TYPE, true, timeout);
                dialog.showContentText(false);
                dialog.setTitleText(context.getString(R.string.dialog_trans_succ_liff, title));
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
                dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        return keyCode == KeyEvent.KEYCODE_BACK;
                    }
                });
                dialog.setOnDismissListener(listener);
                //Device.beepOk(); beepOk
            }
        });
    }

    public static void showExitAppDialog(final Context context) {
        showConfirmDialog(context, context.getString(R.string.exit_app), null, new CustomAlertDialog.OnCustomClickListener() {
            @Override
            public void onClick(CustomAlertDialog alertDialog) {
                alertDialog.dismiss();
                //Device.enableStatusBar(true);
                //Device.enableHomeRecentKey(true);
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra(MainActivity.TAG_EXIT, true);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                context.startActivity(intent);
            }
        });
    }
    public static void showConfirmDialog(final Context context, final String content,
                                         final CustomAlertDialog.OnCustomClickListener cancelClickListener, final CustomAlertDialog.OnCustomClickListener confirmClickListener) {
        final CustomAlertDialog dialog = new CustomAlertDialog(context, CustomAlertDialog.NORMAL_TYPE);

        final CustomAlertDialog.OnCustomClickListener clickListener = new CustomAlertDialog.OnCustomClickListener() {
            @Override
            public void onClick(CustomAlertDialog alertDialog) {
                alertDialog.dismiss();
            }
        };

        dialog.setCancelClickListener(cancelClickListener == null ? clickListener : cancelClickListener);
        dialog.setConfirmClickListener(confirmClickListener == null ? clickListener : confirmClickListener);
        dialog.show();
        dialog.setNormalText(content);
        dialog.showCancelButton(true);
        dialog.showConfirmButton(true);
    }

}
