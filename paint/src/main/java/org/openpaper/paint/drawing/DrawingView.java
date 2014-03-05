package org.openpaper.paint.drawing;

import org.openpaper.paint.action.ActionQueue;
import org.openpaper.paint.action.StrokeAction;
import org.openpaper.paint.drawing.brush.Brush;
import org.openpaper.paint.drawing.brush.PointBrush;
import org.openpaper.paint.drawing.brush.Stroke;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * A DrawingView is a view that tracks touch events.
 * 
 * @author erwinj
 * 
 */
public class DrawingView extends View {
    private static final String TAG = "org.openpaper.paint.drawing.DrawingView";

    private Bitmap bitmap = null;
    private Canvas bitmapCanvas = null;
    private Brush brush = new PointBrush();
    Stroke stroke = new Stroke();
    private ActionQueue actionQueue = new ActionQueue(this);

    public DrawingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public DrawingView(Context context) {
        super(context);
    }

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void addPoint(Point newPoint) {
        if (bitmap == null) {
            initBitmap();
        }
        // Draw an invalidate.
        Rect dirty = brush.addPoint(bitmapCanvas, newPoint);
        if (dirty != null) {
            invalidate();
        }
    }

    public void clear() {
        brush.clear();
        if (bitmapCanvas != null)
            bitmapCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (bitmap != null)
            canvas.drawBitmap(bitmap, 0, 0, null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            stroke = new Stroke();
            addPoint(null);
            return true;

        case MotionEvent.ACTION_MOVE:
        case MotionEvent.ACTION_UP:
            // When the hardware tracks events faster than they are delivered,
            // the
            // event will contain a history of those skipped points.
            int historySize = event.getHistorySize();
            for (int i = 0; i < historySize; i++) {
                float historicalX = event.getHistoricalX(i);
                float historicalY = event.getHistoricalY(i);
                float pressure = event.getHistoricalPressure(i);
                long time = event.getHistoricalEventTime(i);

                Point newPoint = new Point(historicalX, historicalY, pressure,
                        time);
                stroke.add(newPoint);
                addPoint(newPoint);
            }

            // After replaying history, connect the line to the touch point.
            Point newPoint = new Point(event.getX(), event.getY(),
                    event.getPressure(), event.getEventTime());
            stroke.add(newPoint);
            addPoint(newPoint);

            if (event.getAction() == MotionEvent.ACTION_UP) {
                this.actionQueue.addAction(new StrokeAction(stroke));
            }
            break;

        default:
            Log.d(TAG, "Ignored touch event: " + event.toString());
            return false;
        }

        return true;
    }

    public Brush getBrush() {
        return this.brush;
    }

    public void setBrush(Brush aBrush) {
        this.brush = aBrush;
    }

    public Bitmap getOffscreenBuffer() {
        if (this.bitmap == null) {
            initBitmap();
        }
        return this.bitmap;
    }

    private void initBitmap() {
        bitmap = Bitmap.createBitmap(getWidth(), getHeight(),
                Bitmap.Config.ARGB_8888);
        bitmapCanvas = new Canvas(bitmap);
    }

    public ActionQueue getActionQueue() {
        return actionQueue;
    }

    public Canvas getCanvas() {
        return bitmapCanvas;
    }
}