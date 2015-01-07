
package org.tmurakam.presentationtimer;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * プリファレンス
 */
public class Prefs {
    private SharedPreferences mPrefs;

    /**
     * デフォルトのベル鳴動時刻 (分)
     */
    private final static int[] DEFAULT_BELL_TIMES = {
            13, 15, 20
    };

    public Prefs(Context context) {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
    }

    /**
     * ベル鳴動時刻取得
     * @param kind ベル番号(1-3)
     * @return
     */
    public int getBellTime(int kind) {
        assert (1 <= kind && kind <= 3);
        int defValue = DEFAULT_BELL_TIMES[kind - 1] * 60;
        return mPrefs.getInt("bell" + kind + "Time", defValue);
    }

    /**
     * ベル鳴動時刻設定
     * @param kind ベル番号(1-3)
     * @param secs
     */
    public void setBellTime(int kind, int secs) {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putInt("bell" + kind + "Time", secs);
        editor.apply();
    }

    /**
     * プレゼン終了時刻ベル番号取得
     * @return
     */
    public int getCountDownTarget() {
        return mPrefs.getInt("countDownTarget", 2);
    }

    /**
     * プレゼン終了時刻ベル番号設定
     * @param kind
     */
    public void setCountDownTarget(int kind) {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putInt("countDownTarget", kind);
        editor.apply();
    }

    /**
     * バイブレーションオプション
     */
    public boolean getVibration() {
        return mPrefs.getBoolean("vibration", true);
    }

    public void setVibration(boolean on) {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putBoolean("vibration", on);
        editor.apply();
    }
}
