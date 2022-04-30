package org.tmurakam.presentationtimer

import android.os.Bundle
import java.util.*

/**
 * タイマロジック
 */
class TimerLogic(private val mTimerCallback: TimerCallback) {
    companion object {
        private const val KEY_CURRENT_TIME = "currentTime"
        private const val KEY_IS_TIMER_WORKING = "isTimerWorking"
        private const val KEY_SUSPENDED_TIME = "suspendedTime"
    }

    /**
     * タイマコールバック
     */
    interface TimerCallback {
        fun onTimerUpdate()
    }

    /** 現在時刻(秒)  */
    private var mCurrentTime = 0

    /** タイマ  */
    private var mTimer: Timer? = null

    fun currentTime(): Int {
        return mCurrentTime
    }

    val isTimerWorking: Boolean
        get() = mTimer != null

    fun toggleTimer() {
        if (isTimerWorking) {
            stopTimer()
        } else {
            startTimer()
        }
    }

    fun startTimer() {
        assert(mTimer == null)
        val timer = Timer(true)
        mTimer = timer

        val task: TimerTask = object : TimerTask() {
            override fun run() {
                mCurrentTime++
                mTimerCallback.onTimerUpdate()
            }
        }
        timer.schedule(task, 1000, 1000)
    }

    fun stopTimer() {
        if (mTimer != null) {
            mTimer?.cancel()
            mTimer?.purge()
        }
        mTimer = null
    }

    fun reset() {
        mCurrentTime = 0
    }

    fun onSaveInstanceState(st: Bundle) {
        st.putInt(KEY_CURRENT_TIME, mCurrentTime)
        st.putBoolean(KEY_IS_TIMER_WORKING, isTimerWorking)
        st.putLong(KEY_SUSPENDED_TIME, System.currentTimeMillis())
    }

    fun restoreInstanceState(st: Bundle) {
        mCurrentTime = st.getInt(KEY_CURRENT_TIME)
        val isTimeWorking = st.getBoolean(KEY_IS_TIMER_WORKING)
        if (isTimeWorking) {
            val suspendedTime = st.getLong(KEY_SUSPENDED_TIME)
            val elapsed = (System.currentTimeMillis() - suspendedTime) / 1000
            mCurrentTime += elapsed.toInt()
            startTimer()
        }
    }
}