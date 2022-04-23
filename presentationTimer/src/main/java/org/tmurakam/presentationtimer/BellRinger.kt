package org.tmurakam.presentationtimer;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Vibrator;

/**
 * ベル音・バイブレータ再生
 */
public class BellRinger {
    /** 音声データ */
    private MediaPlayer[] mBells;

    /** バイブレータ */
    private Vibrator mVibrator;
    private long[][] mVibratorPattern;

    /**
     * コンストラクタ
     * @param context
     */
    public BellRinger(Context context) {
        mBells = new MediaPlayer[3];
        mBells[0] = MediaPlayer.create(context, R.raw.bell1);
        mBells[1] = MediaPlayer.create(context, R.raw.bell2);
        mBells[2] = MediaPlayer.create(context, R.raw.bell3);
        for (MediaPlayer p : mBells) {
            p.setVolume(1.0f, 1.0f);
        }

        mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        mVibratorPattern = new long[3][];
        mVibratorPattern[0] = new long[]{0, 500};
        mVibratorPattern[1] = new long[]{0, 500, 200, 500};
        mVibratorPattern[2] = new long[]{0, 500, 200, 500, 200, 500};
    }

    public void release() {
        for (MediaPlayer p : mBells) {
            p.release();
        }
    }

    /**
     * Bell鳴動
     * @param n ベル番号(0-2)
     */
    public void ringBell(int n) {
        mBells[n].seekTo(0);
        mBells[n].start();
    }

    /**
     * バイブレータ起動
     * @param n ベル番号(0-2)
     */
    public void vibrate(int n) {
        mVibrator.vibrate(mVibratorPattern[n], -1);
    }
}
