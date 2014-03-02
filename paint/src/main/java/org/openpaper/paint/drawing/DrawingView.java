package org.openpaper.paint.drawing;

import org.openpaper.paint.action.ActionQueue;
import org.openpaper.paint.action.AddPointAction;
import org.openpaper.paint.drawing.brush.Brush;
import org.openpaper.paint.drawing.brush.PointBrush;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * A DrawingView is a view that tracks touch events. The touch events are
 * dispatched to a drawing strategy that is responsible for drawing these points
 * on the canvas.
 * 
 * The canvas is an offscreen buffer, so the draw strategy doesn't have to
 * implement repaints etc.
 * 
 * It also contains an undo buffer, so that users can undo a series of
 * operations. Undo's work by replaying the addition of points from the last
 * time a snapshot has been made.
 * 
 * @author erwinj
 * 
 */
@TargetApi(Build.VERSION_CODES.CUPCAKE)
public class DrawingView extends View {
    private static final String TAG = "com.rwin.randy.ui.SignatureView";

    private Bitmap bitmap = null;
    private Canvas bitmapCanvas = null;

    private Brush brush = new PointBrush();
    private ActionQueue actionQueue = new ActionQueue(this);

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
            actionQueue.addAction(new AddPointAction(null));
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

                actionQueue.addAction(new AddPointAction(new Point(
                        historicalX, historicalY, pressure, time)));
            }

            // After replaying history, connect the line to the touch point.
            actionQueue.addAction(new AddPointAction(new Point(event.getX(),
                    event.getY(), event.getPressure(), event.getEventTime())));
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
}