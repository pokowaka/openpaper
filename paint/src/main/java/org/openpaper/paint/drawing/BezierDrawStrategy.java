package org.openpaper.paint.drawing;

import java.util.LinkedList;
import java.util.Queue;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class BezierDrawStrategy implements DrawStrategy {

    private int strokeWidth = 20;

    private float velocityFilter = 0.2f;

    private Paint paint = new Paint();

    private Point lastPoint;

    private float lastVelocity;
    private float lastWidth;

    private Queue<Point> pointQueue = new LinkedList<Point>();

    public BezierDrawStrategy() {
    }

    public BezierDrawStrategy(float velocity, int stroke, int color) {
        this.velocityFilter = velocity;
        this.strokeWidth = stroke;
        init(color);
    }

    private void init(int color) {
        paint.setAntiAlias(true);
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(strokeWidth);
    }

    @Override
    public Rect addPoint(Canvas c, Point newPoint) {

        // Okay, the user lifted their pen.
        if (newPoint == null) {
            this.pointQueue.clear();
            return null;
        }

        this.pointQueue.offer(newPoint);
        int velocity = (int) newPoint.velocityFrom(lastPoint);

        // A simple lowpass filter to mitigate velocity aberrations.
        velocity = (int) (velocityFilter * velocity + (1 - velocityFilter)
                * lastVelocity);

        float newWidth = strokeWidth - velocity;

        Rect invalid = null;
        if (this.pointQueue.size() >= 4) {

            // The Bezier's width starts out as last curve's final width, and
            // gradually changes to the stroke width just calculated. The new
            // width calculation is based on the velocity between the Bezier's
            // start and end points.

            // Note we keep the last element in the queue, so that the next
            // curve can be added properly.
            Bezier curve = new Bezier(this.pointQueue.poll(),
                    this.pointQueue.poll(), this.pointQueue.poll(),
                    this.pointQueue.peek());
            invalid = addBezier(c, curve, lastWidth, newWidth);
            lastPoint = newPoint;
            lastWidth = newWidth;
        }

        lastVelocity = velocity;

        return invalid;
    }

    private Rect addBezier(Canvas canvas, Bezier curve, float startWidth,
            float endWidth) {
        curve.draw(canvas, paint, startWidth, endWidth);

        // Invalidate the curve, make sure the bounding box is thick enough to
        // contain the full width of the brush.
        int width = (int) Math.ceil(Math.max(startWidth, endWidth));
        Rect bound = curve.getBoundingBox();
        bound.left -= width;
        bound.right += width;
        bound.top -= width;
        bound.bottom += width;
        return bound;
    }

    @Override
    public void clear() {
        this.pointQueue.clear();
    }

    @Override
    public void setColor(int color) {
        this.paint.setColor(color);
    }
}
