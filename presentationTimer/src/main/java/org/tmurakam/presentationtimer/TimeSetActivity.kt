
package org.tmurakam.presentationtimer;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import com.ikovac.timepickerwithseconds.view.TimePicker;

/**
 * 時刻設定 Activity
 */
public class TimeSetActivity extends Activity {
    private int mKind;

    private TimePicker mTimePicker;

    private CheckBox mCheckIsEndTime;

    private Prefs mPrefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setTheme(R.style.MyDialog);
        setContentView(R.layout.time_set);

        mTimePicker = (TimePicker)findViewById(R.id.TimePicker);
        mCheckIsEndTime = (CheckBox)findViewById(R.id.CheckUseAsEndTime);

        mKind = getIntent().getIntExtra("kind", 1);

        mPrefs = new Prefs(this);
        int time = mPrefs.getBellTime(mKind);
        int hour = time / 3600;
        int min = (time / 60) % 60;
        int sec = time % 60;

        mTimePicker.setIs24HourView(true);
        mTimePicker.setCurrentHour(hour);
        mTimePicker.setCurrentMinute(min);
        mTimePicker.setCurrentSecond(sec);

        mCheckIsEndTime.setChecked(mKind == mPrefs.getCountDownTarget());
    }

    public void onClickOk(View v) {
        int hour = mTimePicker.getCurrentHour();
        int min = mTimePicker.getCurrentMinute();
        int sec = mTimePicker.getCurrentSeconds();

        mPrefs.setBellTime(mKind, hour * 3600 + min * 60 + sec);
        if (mCheckIsEndTime.isChecked()) {
            mPrefs.setCountDownTarget(mKind);
        }
        finish();
    }

    public void onClickCancel(View v) {
        finish();
    }
}
