package org.tmurakam.presentationtimer

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import com.ikovac.timepickerwithseconds.view.TimePicker

/**
 * 時刻設定 Activity
 */
class TimeSetActivity : Activity() {
    private var mKind = 0
    private lateinit var mTimePicker: TimePicker
    private lateinit var mCheckIsEndTime: CheckBox
    private lateinit var mPrefs: Prefs

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mPrefs = Prefs(this)

        //setTheme(R.style.MyDialog);
        setContentView(R.layout.time_set)

        mTimePicker = findViewById(R.id.TimePicker)
        mCheckIsEndTime = findViewById(R.id.CheckUseAsEndTime)

        mKind = intent.getIntExtra("kind", 1)

        val time = mPrefs.getBellTime(mKind)
        val hour = time / 3600
        val min = time / 60 % 60
        val sec = time % 60

        mTimePicker.setIs24HourView(true)
        mTimePicker.currentHour = hour
        mTimePicker.currentMinute = min
        mTimePicker.setCurrentSecond(sec)

        mCheckIsEndTime.isChecked = mKind == mPrefs.countDownTarget
    }

    fun onClickOk(v: View?) {
        val hour = mTimePicker.currentHour
        val min = mTimePicker.currentMinute
        val sec = mTimePicker.currentSeconds

        mPrefs.setBellTime(mKind, hour * 3600 + min * 60 + sec)
        if (mCheckIsEndTime.isChecked) {
            mPrefs.countDownTarget = mKind
        }
        finish()
    }

    fun onClickCancel(v: View?) {
        finish()
    }
}