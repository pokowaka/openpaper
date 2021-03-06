/**
 * 
 * https://github.com/RaghavSood/AndroidCircularSeekBar
 * 
 * @author Raghav Sood
 * @auther ErwinJ Minor changes.
 * @version 2
 * @date 26 January, 2013
 */
package org.openpaper.paint.drawing;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * The Class CircularSeekBar.
 */
public class CircularSeekBar extends View {

    /**
     * The angle at which the progress bar should display 100% (note that 360 ==
     * 0, so we cannot use 360).
     */
    private static final int ANGLE_AT_100PERCENT = 359;

    private static final String REWIND = "Rewind";

    private static final String TAG = "org.openpaper.paint.drawing.CircularSeekBar";

    /** The listener to listen for changes */
    private OnSeekChangeListener mListener;

    /** The color of the progress ring */
    private Paint circleColor;

    /** The progress circle ring background */
    private Paint circleRing;

    /** The angle of progress */
    private int angle = ANGLE_AT_100PERCENT;

    /** The start angle (12 O'clock */
    private int startAngle = 270;

    /** The width of the progress ring */
    private int barWidth = 5;

    /** The width of the view */
    private int width;

    /** The height of the view */
    private int height;

    /** The maximum progress amount */
    private int maxProgress = 100;

    /** The current progress */
    private int progress;

    /** The radius of the inner circle */
    private float innerRadius;

    /** The radius of the outer circle */
    private float outerRadius;

    /** The circle's center X coordinate */
    private float cx;

    /** The circle's center Y coordinate */
    private float cy;

    /** The left bound for the circle RectF */
    private float left;

    /** The right bound for the circle RectF */
    private float right;

    /** The top bound for the circle RectF */
    private float top;

    /** The bottom bound for the circle RectF */
    private float bottom;

    /**
     * The adjustment factor. This adds an adjustment of the specified size to
     * both sides of the progress bar, allowing touch events to be processed
     * more realistically
     */
    private float adjustmentFactor = 100;

    /** The rectangle containing our circles and arcs. */
    private RectF rect = new RectF();

    private double degold;

    private Paint textPaint;

    private Rect textBounds = new Rect();

    {
        mListener = new OnSeekChangeListener() {

            @Override
            public void onProgressChange(CircularSeekBar view, int newProgress) {

            }
        };

        circleColor = new Paint();
        circleRing = new Paint();
        textPaint = new Paint(Paint.LINEAR_TEXT_FLAG);

        circleColor.setColor(Color.parseColor("#ff33b5e5")); // Set default
                                                             // progress
                                                             // color to holo
                                                             // blue.
        circleRing.setColor(Color.GRAY);// Set default background color to Gray
        textPaint.setColor(Color.BLACK);

        circleColor.setAntiAlias(true);
        textPaint.setAntiAlias(true);
        circleRing.setAntiAlias(true);

        circleColor.setAlpha(0x80);
        circleRing.setAlpha(0x80);

        circleColor.setStrokeWidth(5);
        circleRing.setStrokeWidth(5);

        // innerColor.setAlpha(0x80);

        circleColor.setStyle(Paint.Style.FILL);
    }

    /**
     * Instantiates a new circular seek bar.
     * 
     * @param context
     *            the context
     * @param attrs
     *            the attrs
     * @param defStyle
     *            the def style
     */
    public CircularSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Instantiates a new circular seek bar.
     * 
     * @param context
     *            the context
     * @param attrs
     *            the attrs
     */
    public CircularSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Instantiates a new circular seek bar.
     * 
     * @param context
     *            the context
     */
    public CircularSeekBar(Context context) {
        super(context);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.view.View#onMeasure(int, int)
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        width = getWidth(); // Get View Width
        height = getHeight();// Get View Height

        // Choose the smaller between width and
        // height to make asquare
        int size = (width > height) ? height : width;

        cx = width / 2; // Center X for circle
        cy = height / 2; // Center Y for circle
        outerRadius = size / 2; // Radius of the outer circle

        innerRadius = outerRadius - barWidth; // Radius of the inner circle

        left = cx - outerRadius; // Calculate left bound of our rect
        right = cx + outerRadius;// Calculate right bound of our rect
        top = cy - outerRadius;// Calculate top bound of our rect
        bottom = cy + outerRadius;// Calculate bottom bound of our rect

        textPaint.setTextSize(innerRadius / 3);
        textPaint.getTextBounds(REWIND, 0, REWIND.length(), textBounds);

        rect.set(left, top, right, bottom); // assign size to rect
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.view.View#onDraw(android.graphics.Canvas)
     */
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(cx, cy, outerRadius, circleRing);
        canvas.drawArc(rect, startAngle, angle, true, circleColor);
        canvas.drawText(REWIND, cx - textBounds.width() / 2,
                cy + textBounds.height() / 2, textPaint);
        super.onDraw(canvas);
    }

    /**
     * Get the angle.
     * 
     * @return the angle
     */
    public int getAngle() {
        return angle;
    }

    /**
     * Set the angle.
     * 
     * @param angle
     *            the new angle
     */
    private void setAngle(int angle) {
        this.angle = Math.abs(angle % 360);
        float donePercent = (((float) this.angle) / ANGLE_AT_100PERCENT) * 100;
        float progress = (donePercent / 100) * getMaxProgress();
        setInternalProgress(Math.round(progress));
    }

    /**
     * Sets the seek bar change listener.
     * 
     * @param listener
     *            the new seek bar change listener
     */
    public void setSeekBarChangeListener(OnSeekChangeListener listener) {
        mListener = listener;
    }

    /**
     * Gets the seek bar change listener.
     * 
     * @return the seek bar change listener
     */
    public OnSeekChangeListener getSeekBarChangeListener() {
        return mListener;
    }

    /**
     * Gets the bar width.
     * 
     * @return the bar width
     */
    public int getBarWidth() {
        return barWidth;
    }

    /**
     * Sets the bar width.
     * 
     * @param barWidth
     *            the new bar width
     */
    public void setBarWidth(int barWidth) {
        this.barWidth = barWidth;
    }

    /**
     * The listener interface for receiving onSeekChange events. The class that
     * is interested in processing a onSeekChange event implements this
     * interface, and the object created with that class is registered with a
     * component using the component's
     * <code>setSeekBarChangeListener(OnSeekChangeListener)<code> method. When
     * the onSeekChange event occurs, that object's appropriate
     * method is invoked.
     * 
     * @see OnSeekChangeEvent
     */
    public interface OnSeekChangeListener {

        /**
         * On progress change.
         * 
         * @param view
         *            the view
         * @param newProgress
         *            the new progress
         */
        public void onProgressChange(CircularSeekBar view, int newProgress);
    }

    /**
     * Gets the max progress.
     * 
     * @return the max progress
     */
    public int getMaxProgress() {
        return maxProgress;
    }

    /**
     * Sets the max progress.
     * 
     * @param maxProgress
     *            the new max progress
     */
    public void setMaxProgress(int maxProgress) {
        this.maxProgress = maxProgress;

        if (this.getVisibility() != View.VISIBLE)
            this.degold = 0;
    }

    /**
     * Gets the progress.
     * 
     * @return the progress
     */
    public int getProgress() {
        return progress;
    }

    private void setInternalProgress(int progress) {
        if (this.progress != progress) {
            this.progress = progress;
            mListener.onProgressChange(this, this.getProgress());
        }
    }

    /**
     * Sets the progress.
     * 
     * @param progress
     *            the new progress
     */
    public void setProgress(int progress) {
        if (progress < 0 || progress > this.maxProgress)
            return;

        if (this.progress != progress) {
            this.progress = progress;
            float newPercent = (((float) this.progress / (float) this.maxProgress) * 100f);
            this.angle = (int) ((newPercent / 100) * ANGLE_AT_100PERCENT);
            mListener.onProgressChange(this, this.progress);
            invalidate();
        }
    }

    /**
     * Sets the ring background color.
     * 
     * @param color
     *            the new ring background color
     */
    public void setRingBackgroundColor(int color) {
        circleRing.setColor(color);
    }

    /**
     * Sets the progress color.
     * 
     * @param color
     *            the new progress color
     */
    public void setProgressColor(int color) {
        circleColor.setColor(color);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.view.View#onTouchEvent(android.view.MotionEvent)
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            degold = Math
                    .atan2(y - cy - this.getTop(), x - cx - this.getLeft())
                    + Math.PI;
            break;
        case MotionEvent.ACTION_MOVE:
            double degree = Math.atan2(y - cy - this.getTop(),
                    x - cx - this.getLeft())
                    + Math.PI;
            if (degree < degold && progress > 0) {
                // We moved left
                setProgress(progress - 1);
            }
            if (degree > degold && progress < maxProgress) {
                setProgress(progress + 1);
            }

            degold = degree;
            this.invalidate();
            break;
        }
        return true;
    }

    /**
     * Gets the adjustment factor.
     * 
     * @return the adjustment factor
     */
    public float getAdjustmentFactor() {
        return adjustmentFactor;
    }

    /**
     * Sets the adjustment factor.
     * 
     * @param adjustmentFactor
     *            the new adjustment factor
     */
    public void setAdjustmentFactor(float adjustmentFactor) {
        this.adjustmentFactor = adjustmentFactor;
    }
}