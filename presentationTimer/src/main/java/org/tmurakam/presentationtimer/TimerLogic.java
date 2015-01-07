package org.tmurakam.presentationtimer;

import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

/**
 * タイマロジック
 */
public class TimerLogic {
    private final static String KEY_CURRENT_TIME = "currentTime";

    private final static String KEY_IS_TIMER_WORKING = "isTimerWorking";

    private final static String KEY_SUSPENDED_TIME = "suspendedTime";

    /**
     * タイマコールバック
     */
    public static interface TimerCallback {
        public void onTimerUpdate();
    }

    private TimerCallback mTimerCallback;

    /** 現在時刻(秒) */
    private int mCurrentTime = 0;

    /** タイマ */
    private Timer mTimer = null;

    public TimerLogic(TimerCallback callback) {
        mTimerCallback = callback;
    }

    public int currentTime() {
        return mCurrentTime;
    }

    public boolean isTimerWorking() {
        return mTimer != null;
    }

    public void toggleTimer() {
        if (isTimerWorking()) {
            stopTimer();
        } else {
            startTimer();
        }
    }

    public void startTimer() {
        assert (mTimer == null);

        mTimer = new Timer(true);
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                mCurrentTime++;
                mTimerCallback.onTimerUpdate();
            }
        };
        mTimer.schedule(task, 1000, 1000);
    }

    public void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer.purge();
        }
        mTimer = null;
    }

    public void reset() {
        mCurrentTime = 0;
    }

    public void onSaveInstanceState(Bundle st) {
        st.putInt(KEY_CURRENT_TIME, mCurrentTime);
        st.putBoolean(KEY_IS_TIMER_WORKING, isTimerWorking());

        st.putLong(KEY_SUSPENDED_TIME, System.currentTimeMillis());
    }

    public void restoreInstanceState(Bundle st) {
        mCurrentTime = st.getInt(KEY_CURRENT_TIME);

        boolean isTimeWorking = st.getBoolean(KEY_IS_TIMER_WORKING);
        if (isTimeWorking) {
            long suspendedTime = st.getLong(KEY_SUSPENDED_TIME);
            long elapsed = (System.currentTimeMillis() - suspendedTime) / 1000;

            mCurrentTime += elapsed;

            startTimer();
        }
    }
}
