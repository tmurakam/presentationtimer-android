package org.tmurakam.presentationtimer

import android.content.Context
import android.preference.PreferenceManager

/**
 * プリファレンス
 */
class Prefs(context: Context) {
    private val mPrefs = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)

    /**
     * デフォルトのベル鳴動時刻 (分)
     */
    private val DEFAULT_BELL_TIMES = intArrayOf(13, 15, 20)

    /**
     * ベル鳴動時刻取得
     * @param kind ベル番号(1-3)
     * @return
     */
    fun getBellTime(kind: Int): Int {
        assert(kind in 1..3)
        val defValue = DEFAULT_BELL_TIMES[kind - 1] * 60
        return mPrefs.getInt("bell" + kind + "Time", defValue)
    }

    /**
     * ベル鳴動時刻設定
     * @param kind ベル番号(1-3)
     * @param secs
     */
    fun setBellTime(kind: Int, secs: Int) {
        val editor = mPrefs.edit()
        editor.putInt("bell" + kind + "Time", secs)
        editor.apply()
    }

    /**
     * プレゼン終了時刻ベル番号取得
     */
    var countDownTarget: Int
        get() = mPrefs.getInt("countDownTarget", 2)
        set(kind) {
            val editor = mPrefs.edit()
            editor.putInt("countDownTarget", kind)
            editor.apply()
        }

    /**
     * バイブレーションオプション
     */
    var vibration: Boolean
        get() = mPrefs.getBoolean("vibration", true)
        set(on) {
            val editor = mPrefs.edit()
            editor.putBoolean("vibration", on)
            editor.apply()
        }
}