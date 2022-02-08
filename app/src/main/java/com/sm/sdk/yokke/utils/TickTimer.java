package com.sm.sdk.yokke.utils;

import android.os.CountDownTimer;

public class TickTimer {
    public static final int DEFAULT_TIMEOUT = 60;

    public interface OnTickTimerListener {
        void onTick(long leftTime);

        void onFinish();
    }

    private Timer timer;
    private OnTickTimerListener listener;

    private class Timer extends CountDownTimer {

        private OnTickTimerListener listener;

        Timer(long timeout, long tickInterval) {
            super(timeout * 1000, tickInterval * 1000);
        }

        void setTimeCountListener(OnTickTimerListener listener) {
            this.listener = listener;
        }

        @Override
        public void onFinish() {
            if (listener != null)
                listener.onFinish();
        }

        @Override
        public void onTick(long millisUntilFinished) {
            if (listener != null)
                listener.onTick(millisUntilFinished / 1000);
        }
    }

    public TickTimer(OnTickTimerListener listener) {
        this.listener = listener;
    }

    public void start() {
        if (timer != null) {
            timer.cancel();
        }
        updateTimer(DEFAULT_TIMEOUT);
        timer.start();
    }

    public void start(int timeout) {
        if (timer != null) {
            timer.cancel();
        }
        updateTimer(timeout);
        timer.start();
    }

    public void stop() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void updateTimer(int timeout) {
        timer = new Timer(timeout, 1);
        timer.setTimeCountListener(listener);
    }
}
