package org.tmurakam.presentationtimer

import android.app.ActionBar
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageButton
import androidx.annotation.RequiresApi
//import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import org.tmurakam.presentationtimer.TimerLogic.TimerCallback
import org.tmurakam.presentationtimer.databinding.MainBinding

/**
 * メインアクティビティ
 */
class MainActivity : Activity(), TimerCallback {
    companion object {
        private val TAG = MainActivity::class.java.simpleName
        private const val KEY_IS_COUNTDOWN = "isCountDown"

        private const val COLOR_T1 = Color.YELLOW
        private val COLOR_T2 = Color.parseColor("#ffff33cc")
        private const val COLOR_T3 = Color.RED
    }

    /** View binding */
    private lateinit var binding: MainBinding

    /** 表示モード:カウントダウンモードなら真  */
    private var mIsCountDown = false

    /** プリファレンス　  */
    private lateinit var mPrefs: Prefs

    /** タイマロジック  */
    private val mTimerLogic = TimerLogic(this)

    /** タイマハンドラ  */
    private val mHandler = Handler(Looper.myLooper()!!)

    private lateinit var mBellRinger: BellRinger

    /** ActionBar  */
    private var mActionBar: ActionBar? = null

    /** Called when the activity is first created.  */
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mPrefs = Prefs(this)
        mBellRinger = BellRinger(this)

        // Firebase Crashlytics
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)

        // Firebase Analytics
        //FirebaseAnalytics.getInstance(this)

        mActionBar = actionBar
        mActionBar?.hide()

        // View Binding
        binding = MainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // ステータスバーを消す
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

        // 音量ボタンで、Media ボリュームが変わるようにする
        volumeControlStream = AudioManager.STREAM_MUSIC

        // イベントハンドラ設定
        binding.startStop.setOnClickListener { v -> onClickStartStop(v) }
        binding.bell.setOnClickListener { v -> onClickBell(v) }
        binding.reset.setOnClickListener { v -> onClickReset(v) }
        binding.config.setOnClickListener { v -> onClickConfig(v) }
        binding.timeView.setOnClickListener { onClickTime(it) }

        // scaled density 取得
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        binding.timeView.density = metrics.scaledDensity
        Log.d(TAG, "Density = " + metrics.scaledDensity)

        savedInstanceState?.let { restoreInstanceState(it) }
        updateTimeLabel()
        updateUiStates()
    }

    public override fun onSaveInstanceState(st: Bundle) {
        st.putBoolean(KEY_IS_COUNTDOWN, mIsCountDown)
        mTimerLogic.onSaveInstanceState(st)
    }

    private fun restoreInstanceState(st: Bundle) {
        mIsCountDown = st.getBoolean(KEY_IS_COUNTDOWN)
        mTimerLogic.restoreInstanceState(st)
    }

    public override fun onResume() {
        super.onResume()
        updateTimeLabel()
    }

    public override fun onDestroy() {
        mTimerLogic.stopTimer()
        mBellRinger.release()
        super.onDestroy()
    }

    /**
     * Start or stop timer (toggle)
     */
    private fun onClickStartStop(v: View?) {
        mTimerLogic.toggleTimer()

        /*
        if (mTimerLogic.isTimerWorking) {
            // ナビゲーションバーを隠す
            if (Build.VERSION.SDK_INT >= 11) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            }
        }
        */

        updateUiStates()
    }

    /**
     * Reset timer value
     */
    private fun onClickReset(v: View?) {
        mTimerLogic.reset()
        updateTimeLabel()
    }

    /**
     * Ring bell manually
     */
    private fun onClickBell(v: View?) {
        mBellRinger.ringBell(0)
    }

    /**
     * Toggle count down mode
     */
    private fun onClickTime(v: View?) {
        mIsCountDown = !mIsCountDown
        updateTimeLabel()
    }

    /**
     * Timer handler : called for each 1 second.
     */
    override fun onTimerUpdate() {
        mHandler.post { onTimerOnMainThread() }
    }

    private fun onTimerOnMainThread() {
        val currentTime = mTimerLogic.currentTime()

        for (i in 0..2) {
            if (currentTime == mPrefs.getBellTime(i + 1)) {
                mBellRinger.ringBell(i)
                if (mPrefs.vibration) {
                    mBellRinger.vibrate(i)
                }
                break
            }
        }

        updateTimeLabel()
    }

    /**
     * Update button states
     */
    private fun updateUiStates() {
        if (!mTimerLogic.isTimerWorking) {
            binding.startStop.setText(R.string.start)
            binding.reset.isEnabled = true
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            binding.startStop.setText(R.string.pause)
            binding.reset.isEnabled = false
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    /**
     * Update time label
     */
    private fun updateTimeLabel() {
        val currentTime = mTimerLogic.currentTime()

        var t: Int
        if (!mIsCountDown) {
            t = currentTime
        } else {
            val target = mPrefs.getBellTime(mPrefs.countDownTarget)
            t = target - currentTime
            if (t < 0) t = -t
        }
        binding.timeView.text = timeText(t)

        val col: Int = when {
            currentTime >= mPrefs.getBellTime(3) -> {
                COLOR_T3
            }
            currentTime >= mPrefs.getBellTime(2) -> {
                COLOR_T2
            }
            currentTime >= mPrefs.getBellTime(1) -> {
                COLOR_T1
            }
            else -> {
                Color.WHITE // 0xffffffff
            }
        }
        binding.timeView.setTextColor(col)
    }

    private fun timeText(n: Int): String {
        val min = n / 60
        val sec = n % 60
        return String.format("%02d:%02d", min, sec)
    }

    private fun onClickConfig(v: View?) {
        val intent = Intent(this, PrefActivity::class.java)
        startActivity(intent)
    }
}