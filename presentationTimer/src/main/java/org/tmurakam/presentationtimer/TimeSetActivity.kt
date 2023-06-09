package org.tmurakam.presentationtimer

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import com.ikovac.timepickerwithseconds.view.TimePicker
import org.tmurakam.presentationtimer.databinding.TimeSetBinding

/**
 * 時刻設定 Activity
 */
class TimeSetActivity : Activity() {
    private var mKind = 0
    private lateinit var binding: TimeSetBinding
    private lateinit var mPrefs: Prefs

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mPrefs = Prefs(this)

        binding = TimeSetBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        mKind = intent.getIntExtra("kind", 1)

        val time = mPrefs.getBellTime(mKind)
        val hour = time / 3600
        val min = time / 60 % 60
        val sec = time % 60

        val timePicker = binding.TimePicker
        timePicker.setIs24HourView(true)
        timePicker.currentHour = hour
        timePicker.currentMinute = min
        timePicker.setCurrentSecond(sec)

        binding.CheckUseAsEndTime.isChecked = mKind == mPrefs.countDownTarget
    }

    fun onClickOk(v: View?) {
        val timePicker = binding.TimePicker
        val hour = timePicker.currentHour
        val min = timePicker.currentMinute
        val sec = timePicker.currentSeconds

        mPrefs.setBellTime(mKind, hour * 3600 + min * 60 + sec)
        if (binding.CheckUseAsEndTime.isChecked) {
            mPrefs.countDownTarget = mKind
        }
        finish()
    }

    fun onClickCancel(v: View?) {
        finish()
    }
}