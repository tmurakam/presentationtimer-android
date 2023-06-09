package org.tmurakam.presentationtimer

import android.content.Intent
import android.os.Bundle
import android.preference.CheckBoxPreference
import android.preference.Preference.OnPreferenceChangeListener
import android.preference.PreferenceActivity
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
//import com.google.android.gms.ads.AdRequest
//import com.google.android.gms.ads.AdSize
//import com.google.android.gms.ads.AdView

/**
 * 設定Activity
 */
class PrefActivity : PreferenceActivity() {
    companion object {
        //private const val AD_UNIT_ID = "ca-app-pub-4621925249922081/5594984304"
    }

    private lateinit var mPrefs: Prefs

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mPrefs = Prefs(this)

        addPreferencesFromResource(R.xml.pref)

        var intent: Intent

        for (i in 1..3) {
            val ps = findPreference("_" + i + "bell")

            intent = Intent(this, TimeSetActivity::class.java)
            intent.putExtra("kind", i)
            ps.intent = intent
        }

        val cp = findPreference("vibration") as CheckBoxPreference
        cp.isChecked = mPrefs.vibration

        cp.onPreferenceChangeListener = OnPreferenceChangeListener { preference, newValue ->
            mPrefs.vibration = (newValue as Boolean)
            true
        }

        updateUi()
        //addAds()
    }

    /*
    private fun addAds() {
        // add AdMob
        val adView = AdView(this)
        adView.adUnitId = AD_UNIT_ID
        adView.setAdSize(AdSize.SMART_BANNER);

        val adLayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        adView.layoutParams = adLayoutParams

        // 広告表示位置は画面下部
        val layout = LinearLayout(this)
        layout.addView(adView)
        layout.gravity = Gravity.BOTTOM

        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        addContentView(layout, layoutParams)

        // load ad
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }
    */

    public override fun onResume() {
        super.onResume()
        updateUi()
    }

    private fun updateUi() {
        for (i in 1..3) {
            val ps = findPreference("_" + i + "bell")

            val time = mPrefs.getBellTime(i)
            val hour = time / 3600
            val min = time / 60 % 60
            val sec = time % 60

            var s = ""
            if (hour > 0) {
                s += hour
                s += resources.getString(R.string.hours)
                s += " "
            }
            if (min > 0) {
                s += min
                s += resources.getString(R.string.minutes)
                s += " "
            }
            if (sec > 0) {
                s += sec
                s += resources.getString(R.string.seconds)
            }

            if (i == mPrefs.countDownTarget) {
                s += ", "
                s += resources.getString(R.string.end_time)
            }
            ps.summary = s
        }
    }

    // --- Menu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.pref_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val intent: Intent

        when (item.itemId) {
            R.id.menu_help -> {
                intent = Intent(this, InfoActivity::class.java)
                startActivity(intent)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }
}