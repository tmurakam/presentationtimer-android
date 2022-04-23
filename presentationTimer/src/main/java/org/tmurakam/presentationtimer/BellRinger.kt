package org.tmurakam.presentationtimer

import android.content.Context
import android.media.MediaPlayer
import android.os.Vibrator

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

        mVibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
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
        mVibrator.vibrate(mVibratorPattern[n], -1)
    }
}