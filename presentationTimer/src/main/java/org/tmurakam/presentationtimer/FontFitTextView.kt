
package org.tmurakam.presentationtimer;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import androidx.appcompat.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.Log;

// http://stackoverflow.com/questions/2617266/how-to-adjust-text-font-size-to-fit-textview

public class FontFitTextView extends AppCompatTextView {
    private final static String TAG = FontFitTextView.class.getSimpleName();

    private Paint testPaint;

    private float minTextSize;

    private float maxTextSize;

    private float density = 1;

    public FontFitTextView(Context context) {
        super(context);
        initialize();
    }

    public FontFitTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    private void initialize() {
        Log.d(TAG, "FontFitTextView: initialize");
        
        testPaint = new Paint();
        testPaint.set(this.getPaint());
        // max size defaults to the initially specified text size unless it is
        // too small
        /*maxTextSize = this.getTextSize();
        if (maxTextSize < 11) {
            maxTextSize = 20;
        }*/
        maxTextSize = 900;
        minTextSize = 10;
    }

    public void setDensity(float density) {
        this.density = density;
    }

    @Override
    protected void onTextChanged(final CharSequence text, final int start, final int before,
            final int after) {
        refitText(text.toString(), this.getWidth(), this.getHeight());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (w != oldw || h != oldh) {
            refitText(this.getText().toString(), w, h);
        }
    }

    /*
     * Re size the font so the specified text fits in the text box assuming the
     * text box is the specified width.
     */
    final private void refitText(String text, int width, int height) {
        if (width <= 0 || height <= 0)
            return;

        // 表示可能領域サイズを取得 (dp単位)
        int availableWidth = (int)((width - this.getPaddingLeft() - this.getPaddingRight()) / density);
        int availableHeight = (int)((height - this.getPaddingTop() - this.getPaddingBottom()) / density);

        float max = maxTextSize;
        float min = minTextSize;
        Rect textBounds = new Rect();

        // binary search an optimal font size
        while (true) {
            float trySize = (max + min) / 2;

            testPaint.setTextSize(trySize);
            float textWidth = testPaint.measureText(text, 0, text.length());
            testPaint.getTextBounds(text, 0, text.length(), textBounds);

            if (textWidth < availableWidth && textBounds.height() < availableHeight) {
                min = trySize;    
            } else {
                max = trySize;
            }

            if (max - min < 1.0) {
                Log.d(TAG, "font size = "+ min);
                this.setTextSize(min);
                break;
            }
        }
    }
    
    // Getters and Setters
    public float getMinTextSize() {
        return minTextSize;
    }

    public void setMinTextSize(int minTextSize) {
        this.minTextSize = minTextSize;
    }

    public float getMaxTextSize() {
        return maxTextSize;
    }

    public void setMaxTextSize(int minTextSize) {
        this.maxTextSize = minTextSize;
    }
}
