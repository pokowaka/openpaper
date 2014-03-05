package org.openpaper.paint.drawing.brush;

import java.util.LinkedList;
import java.util.Queue;

import org.openpaper.paint.drawing.Point;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class BezierBrush extends Brush {

    private int strokeWidth = 20;

    private float velocityFilter = 0.2f;

    private Paint paint = new Paint(Paint.DITHER_FLAG);

    private Point lastPoint;

    private float lastVelocity;
    private float lastWidth;


    public BezierBrush() {
    }

    public BezierBrush(float velocity, int stroke, int color) {
        this.velocityFilter = velocity;
        this.strokeWidth = stroke;
        init(color);
    }

    private void init(int color) {
        paint.setAntiAlias(true);
        paint.setColor(color);
        paint.setAlpha(0x80);
        paint.setDither(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(1);
    }

    @Override
    public Rect addPoint(Canvas c, Point newPoint) {

        // Okay, the user lifted their pen.
        if (newPoint == null) {
            this.pointQueue.clear();
            return null;
        }

        this.pointQueue.offer(newPoint);
        float velocity = lastVelocity;

        // velocity can be 0 in case of very fast movements..
        if (newPoint.velocityFrom(lastPoint) > 0) {
            velocity = newPoint.velocityFrom(lastPoint);
            // A simple lowpass filter to mitigate velocity aberrations.
            velocity = (velocityFilter * velocity + (1 - velocityFilter)
                    * lastVelocity);
        } else {

        }

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

        this.paint.setColor(getColor());
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
        this.lastPoint = null;
        this.lastVelocity = 0;
        this.lastWidth = 0;
    }

    @Override
    public String getName() {
        return "Bezier";
    }

}
