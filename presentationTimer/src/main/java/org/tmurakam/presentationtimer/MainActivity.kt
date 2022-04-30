
package org.tmurakam.presentationtimer;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

/**
 * メインアクティビティ
 */
public class MainActivity extends Activity implements TimerLogic.TimerCallback {
    private final static String TAG = MainActivity.class.getSimpleName();

    private final static String KEY_IS_COUNTDOWN = "isCountDown";

    /** 表示モード:カウントダウンモードなら真 */
    private boolean mIsCountDown = false;

    /** プリファレンス　 */
    private Prefs mPrefs;

    /** タイマロジック */
    private TimerLogic mTimerLogic = new TimerLogic(this);

    /** タイマハンドラ */
    private Handler mHandler = new Handler();

    private BellRinger mBellRinger;

    /** 現在時間表示ビュー */
    private FontFitTextView mTextView;

    /** ボタン */
    private Button mStartStopButton, mResetButton;

    /** ActionBar */
    private ActionBar mActionBar;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Firebase Crashlytics
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);

        // Firebase Analytics
        FirebaseAnalytics.getInstance(this);

        mActionBar = getActionBar();
        mActionBar.hide();
        setContentView(R.layout.main);

        // ステータスバーを消す
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 音量ボタンで、Media ボリュームが変わるようにする
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        mTextView = (FontFitTextView) findViewById(R.id.timeView);
        mStartStopButton = (Button) findViewById(R.id.startStop);
        mResetButton = (Button) findViewById(R.id.reset);

        // scaled density 取得
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mTextView.setDensity(metrics.scaledDensity);
        Log.d(TAG, "Density = " + metrics.scaledDensity);

        mPrefs = new Prefs(this);

        mBellRinger = new BellRinger(this);

        if (savedInstanceState != null) {
            restoreInstanceState(savedInstanceState);
        }
        updateTimeLabel();
        updateUiStates();
    }

    @Override
    public void onSaveInstanceState(Bundle st) {
        st.putBoolean(KEY_IS_COUNTDOWN, mIsCountDown);
        mTimerLogic.onSaveInstanceState(st);
    }

    private void restoreInstanceState(Bundle st) {
        mIsCountDown = st.getBoolean(KEY_IS_COUNTDOWN);
        mTimerLogic.restoreInstanceState(st);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateTimeLabel();
    }

    @Override
    public void onDestroy() {
        mTimerLogic.stopTimer();
        mBellRinger.release();

        super.onDestroy();
    }
    
    /**
     * Start or stop timer (toggle)
     */
    public void onClickStartStop(View v) {
        mTimerLogic.toggleTimer();

        if (mTimerLogic.isTimerWorking()) {
            // ナビゲーションバーを隠す
            /*
            if (Build.VERSION.SDK_INT >= 11) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            }
            */
        }

        updateUiStates();
    }

    /**
     * Reset timer value
     */
    public void onClickReset(View v) {
        mTimerLogic.reset();
        updateTimeLabel();
    }

    /**
     * Ring bell manually
     */
    public void onClickBell(View v) {
        mBellRinger.ringBell(0);
    }

    /**
     * Toggle count down mode
     */
    public void onClickTime(View v) {
        mIsCountDown = !mIsCountDown;
        updateTimeLabel();
    }

    /**
     * Timer handler : called for each 1 second.
     */
    @Override
    public void onTimerUpdate() {
        mHandler.post(new Runnable() {
            public void run() {
                onTimerOnMainThread();
            }
        });
    }

    private void onTimerOnMainThread() {
        int currentTime = mTimerLogic.currentTime();

        for (int i = 0; i < 3; i++) {
            if (currentTime == mPrefs.getBellTime(i + 1)) {
                mBellRinger.ringBell(i);
                if (mPrefs.getVibration()) {
                    mBellRinger.vibrate(i);
                }
                break;
            }
        }

        updateTimeLabel();
    }

    /**
     * Update button states
     */
    private void updateUiStates() {
        if (!mTimerLogic.isTimerWorking()) {
            mStartStopButton.setText(R.string.start);
            mResetButton.setEnabled(true);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            mStartStopButton.setText(R.string.pause);
            mResetButton.setEnabled(false);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    /**
     * Update time label
     */
    private void updateTimeLabel() {
        int currentTime = mTimerLogic.currentTime();
        int t;

        if (!mIsCountDown) {
            t = currentTime;
        } else {
            int target = mPrefs.getBellTime(mPrefs.getCountDownTarget());
            t = target - currentTime;
            if (t < 0)
                t = -t;
        }

        mTextView.setText(timeText(t));

        int col;
        if (currentTime >= mPrefs.getBellTime(3)) {
            col = Color.RED; // 0xffff0000
        } else if (currentTime >= mPrefs.getBellTime(2)) {
            col = 0xffff33cc;
        } else if (currentTime >= mPrefs.getBellTime(1)) {
            col = Color.YELLOW; // 0xffffff00
        } else {
            col = Color.WHITE; // 0xffffffff
        }
        mTextView.setTextColor(col);
    }

    private String timeText(int n) {
        int min = n / 60;
        int sec = n % 60;
        String ts = String.format("%02d:%02d", min, sec);
        return ts;
    }

    public void onClickConfig(View v) {
        Intent intent = new Intent(this, PrefActivity.class);
        startActivity(intent);
    }
}
