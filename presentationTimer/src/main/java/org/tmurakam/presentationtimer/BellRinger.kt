package org.tmurakam.presentationtimer

import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/**
 * ベル音・バイブレータ再生
 */
class BellRinger(context: Context) {
    /** 音声データ  */
    private val mBells: Array<MediaPlayer> = arrayOf(
        MediaPlayer.create(context, R.raw.bell1),
        MediaPlayer.create(context, R.raw.bell2),
        MediaPlayer.create(context, R.raw.bell3),
    )

    /** バイブレータ  */
    private val mVibrator: Vibrator
    private val mVibratorPattern = arrayOf(
        longArrayOf(0, 500),
        longArrayOf(0, 500, 200, 500),
        longArrayOf(0, 500, 200, 500, 200, 500)
    )

    /**
     * コンストラクタ
     */
    init {
        for (p in mBells) {
            p.setVolume(1.0f, 1.0f)
        }

        mVibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    fun release() {
        for (p in mBells) {
            p.release()
        }
    }

    /**
     * Bell鳴動
     * @param n ベル番号(0-2)
     */
    fun ringBell(n: Int) {
        mBells[n].seekTo(0)
        mBells[n].start()
    }

    /**
     * バイブレータ起動
     * @param n ベル番号(0-2)
     */
    fun vibrate(n: Int) {
        val pattern = mVibratorPattern[n]

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mVibrator.vibrate(VibrationEffect.createWaveform(pattern, -1));
        } else {
            @Suppress("DEPRECATION")
            mVibrator.vibrate(pattern, -1)
        }
    }
}