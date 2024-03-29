package org.tmurakam.presentationtimer

import android.content.Context
import android.content.res.Resources
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import androidx.appcompat.widget.AppCompatTextView

// http://stackoverflow.com/questions/2617266/how-to-adjust-text-font-size-to-fit-textview
@Deprecated("Replaced with AppCompatTextView", ReplaceWith("AppCompatTextView"))
class FontFitTextView : AppCompatTextView {
    companion object {
        private val TAG = FontFitTextView::class.java.simpleName
    }

    private var testPaint: Paint = Paint()

    // Getters and Setters
    private var minTextSize = 0f
    private var maxTextSize = 0f

    constructor(context: Context?) : super(context!!) {
        initialize()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {
        initialize()
    }

    private fun initialize() {
        Log.d(TAG, "FontFitTextView: initialize")
        testPaint.set(this.paint)
        // max size defaults to the initially specified text size unless it is
        // too small
        /*
        maxTextSize = this.textSize
        if (maxTextSize < 11f) {
            maxTextSize = 20f
        }
        */
        maxTextSize = 900f
        minTextSize = 10f
    }

    override fun onTextChanged(text: CharSequence, start: Int, before: Int, after: Int) {
        refitText(text.toString(), this.width, this.height)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        if (w != oldw || h != oldh) {
            refitText(this.text.toString(), w, h)
        }
    }

    /*
     * Re size the font so the specified text fits in the text box assuming the
     * text box is the specified width.
     */
    private fun refitText(text: String, width: Int, height: Int) {
        if (width <= 0 || height <= 0) return

        // density 取得
        //val scaledDensity = Resources.getSystem().displayMetrics.scaledDensity
        val density = Resources.getSystem().displayMetrics.density

        // 表示可能領域サイズを取得 (px -> dp単位に変換)
        var availableWidth = ((width - this.paddingLeft - this.paddingRight) / density).toInt()
        var availableHeight = ((height - this.paddingTop - this.paddingBottom) / density).toInt()

        // 安全のため、微妙にマージンを取る
        availableWidth -= 2
        availableHeight -= 2

        var max = maxTextSize
        var min = minTextSize
        val textBounds = Rect()

        // binary search an optimal font size
        while (true) {
            val trySize = (max + min) / 2

            testPaint.textSize = trySize
            val textWidth = testPaint.measureText(text, 0, text.length)
            testPaint.getTextBounds(text, 0, text.length, textBounds)

            if (textWidth < availableWidth && textBounds.height() < availableHeight) {
                min = trySize
            } else {
                max = trySize
            }

            if (max - min < 1.0) {
                Log.d(TAG, "font size = $min")
                this.textSize = min
                break
            }
        }
    }
}