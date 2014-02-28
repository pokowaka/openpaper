package org.openpaper.paint.drawing;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Stack;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
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
    
    private Bitmap snapshot = null;
    private Canvas snapshotCanvas = null;
    
    private DrawStrategy drawStrategy = new PointDrawStrategy();

    private Stack<Point> history = new Stack<Point>();
    private Stack<Point> redo = new Stack<Point>();

    private PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void addPoint(Point newPoint) {
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(getWidth(), getHeight(),
                    Bitmap.Config.ARGB_8888);
            bitmapCanvas = new Canvas(bitmap);
        }

        // Add to the undo history and notify observers
        int size = history.size();
        history.push(newPoint);
        pcs.firePropertyChange("history", size, history.size());

        // Draw an invalidate.
        Rect dirty = drawStrategy.addPoint(bitmapCanvas, newPoint);
        if (dirty != null)
            invalidate(dirty);
    }

    public void clear() {
        bitmap = Bitmap.createBitmap(getWidth(), getHeight(),
                Bitmap.Config.ARGB_8888);
        bitmapCanvas = new Canvas(bitmap);
        drawStrategy.clear();
        postInvalidate();
    }

    public int getUndoHistory() {
        return history.size();
    }

    public int getRedoHistory() {
        return redo.size();
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
            redo.clear();
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
                addPoint(new Point(historicalX, historicalY, pressure));
            }

            // After replaying history, connect the line to the touch point.
            addPoint(new Point(event.getX(), event.getY(), event.getPressure()));
            break;

        default:
            Log.d(TAG, "Ignored touch event: " + event.toString());
            return false;
        }

        return true;
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        this.pcs.addPropertyChangeListener(pcl);
    }

    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        this.pcs.removePropertyChangeListener(pcl);
    }

    public void setDrawStrategy(DrawStrategy strategy) {
        this.drawStrategy = strategy;
    }

    public void redo(int points) {
        while (points > 0 && !redo.empty()) {
            points--;
            addPoint(redo.pop());
        }
    }

    public void undo(int points) {
        // TODO: We might want to make intermediate snapshots...
        // to increase performance..
        while (points > 0 && !history.empty()) {
            points--;
            redo.push(history.pop());
            pcs.firePropertyChange("history", history.size(),
                    history.size() - 1);
        }

        // now let's repaint from the beginning of time..
        this.clear();
        Rect invalid = new Rect(Integer.MAX_VALUE, Integer.MAX_VALUE,
                Integer.MIN_VALUE, Integer.MIN_VALUE);

        // Now repaint from the beginning of time..
        for (Point p : history) {
            Rect dirty = drawStrategy.addPoint(bitmapCanvas, p);

            // Extend the dirty rectangle.
            if (dirty != null) {
                invalid.left = Math.min(dirty.left, invalid.left);
                invalid.right = Math.max(dirty.right, invalid.right);
                invalid.top = Math.min(dirty.top, invalid.top);
                invalid.bottom = Math.max(dirty.bottom, invalid.bottom);
            }
        }
        postInvalidate(invalid.left, invalid.top, invalid.right, invalid.bottom);
    }
}