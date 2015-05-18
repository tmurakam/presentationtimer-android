
package org.tmurakam.presentationtimer;

import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.view.Menu;
import android.view.MenuItem;

/**
 * 設定Activity
 */
public class PrefActivity extends PreferenceActivity {
    private Prefs mPrefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.pref);

        mPrefs = new Prefs(this);

        PreferenceScreen ps;
        Intent intent;

        for (int i = 1; i <= 3; i++) {
            ps = (PreferenceScreen) findPreference("_" + i + "bell");
            assert (ps != null);
            intent = new Intent(this, TimeSetActivity.class);
            intent.putExtra("kind", i);
            ps.setIntent(intent);
        }

        CheckBoxPreference cp = (CheckBoxPreference)findPreference("vibration");
        cp.setChecked(mPrefs.getVibration());

        cp.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                mPrefs.setVibration((Boolean)newValue);
                return true;
            }
        });

        updateUi();

    }

    @Override
    public void onResume() {
        super.onResume();
        updateUi();
    }

    private void updateUi() {
        PreferenceScreen ps;

        for (int i = 1; i <= 3; i++) {
            ps = (PreferenceScreen)findPreference("_" + i + "bell");
            assert (ps != null);

            int time = mPrefs.getBellTime(i);
            int hour = time / 3600;
            int min = (time / 60) % 60;
            
            String s = "";
            if (hour > 0) {
                s += hour;
                s += getResources().getString(R.string.hours);
                s += " ";
            }
            s += min;
            s += getResources().getString(R.string.minutes);

            if (i == mPrefs.getCountDownTarget()) {
                s += ", ";
                s += getResources().getString(R.string.end_time);
            }
            ps.setSummary(s);
        }
    }

    // --- Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pref_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;

        switch (item.getItemId()) {
            case R.id.menu_help:
                intent = new Intent(this, InfoActivity.class);
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
