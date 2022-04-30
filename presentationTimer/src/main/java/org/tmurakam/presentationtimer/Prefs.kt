package org.tmurakam.presentationtimer

import android.content.Context
import android.content.SharedPreferences

/**
 * プリファレンス
 */
class Prefs(context: Context) {
    companion object {
        /**
         * デフォルトのベル鳴動時刻 (分)
         */
        private val DEFAULT_BELL_TIMES = intArrayOf(13, 15, 20)
    }

    private val mSharedPrefs: SharedPreferences

    init {
        val c = context.applicationContext
        mSharedPrefs = c.getSharedPreferences(c.packageName + "_preferences", Context.MODE_PRIVATE)
    }

    /**
     * ベル鳴動時刻取得
     * @param kind ベル番号(1-3)
     * @return
     */
    fun getBellTime(kind: Int): Int {
        assert(kind in 1..3)
        val defValue = DEFAULT_BELL_TIMES[kind - 1] * 60
        return mSharedPrefs.getInt("bell" + kind + "Time", defValue)
    }

    /**
     * ベル鳴動時刻設定
     * @param kind ベル番号(1-3)
     * @param secs
     */
    fun setBellTime(kind: Int, secs: Int) {
        val editor = mSharedPrefs.edit()
        editor.putInt("bell" + kind + "Time", secs)
        editor.apply()
    }

    /**
     * プレゼン終了時刻ベル番号取得
     */
    var countDownTarget: Int
        get() = mSharedPrefs.getInt("countDownTarget", 2)
        set(kind) {
            val editor = mSharedPrefs.edit()
            editor.putInt("countDownTarget", kind)
            editor.apply()
        }

    /**
     * バイブレーションオプション
     */
    var vibration: Boolean
        get() = mSharedPrefs.getBoolean("vibration", true)
        set(on) {
            val editor = mSharedPrefs.edit()
            editor.putBoolean("vibration", on)
            editor.apply()
        }
}