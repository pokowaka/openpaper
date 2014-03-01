package org.openpaper.paint.drawing;

import org.openpaper.paint.util.Bounds;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

public class Bezier {

    private static final String TAG = "org.openpaper.paint.drawing.Bezier";

    private Point p0;
    private Point p1;
    private Point p2;
    private Point p3;
    private int drawSteps;

    private Rect bb;

    public Rect getBoundingBox() {
        return bb;
    }

    public Bezier(Point startPoint, Point control1, Point control2,
            Point endPoint) {
        super();
        this.p0 = startPoint;
        this.p3 = endPoint;
        this.p1 = control1;
        this.p2 = control2;

        // Now let's approximate the bounding box..
        this.bb = new Rect(Integer.MAX_VALUE, Integer.MAX_VALUE,
                Integer.MIN_VALUE, Integer.MIN_VALUE);

        final int APPROX = 128;
        for (int i = 0; i < APPROX; i++) {
            Bounds.extendRect(calculatePoint(i / APPROX), bb);
        }

        Bounds.extendRect(p0, bb);
        Bounds.extendRect(p1, bb);
        Bounds.extendRect(p2, bb);
        Bounds.extendRect(p3, bb);

        // Poor approximation of how many points we will draw...
        float dx = bb.right - bb.left;
        float dy = bb.bottom - bb.top;
        drawSteps = (int) (2 * Math.sqrt(dx * dx + dy * dy));
    }

    public Point calculatePoint(float t) {
        float tt = t * t;
        float ttt = tt * t;
        float u = 1 - t;
        float uu = u * u;
        float uuu = uu * u;

        float x = uuu * p0.x;
        x += 3 * uu * t * p1.x;
        x += 3 * u * tt * p2.x;
        x += ttt * p3.x;

        float y = uuu * p0.y;
        y += 3 * uu * t * p1.y;
        y += 3 * u * tt * p2.y;
        y += ttt * p3.y;

        return new Point(x, y);
    }

    /** Draws a variable-width Bezier curve. */
    public void draw(Canvas canvas, Paint paint, float startWidth,
            float endWidth) {
        float originalWidth = paint.getStrokeWidth();
        float widthDelta = (endWidth - startWidth) / drawSteps;

        for (int i = 0; i < drawSteps; i++) {
            // Calculate the Bezier (x, y) coordinate for this step.
            float t = ((float) i) / drawSteps;
            Point pt = calculatePoint(t);

            // Set the incremental stroke width and draw.
            paint.setStrokeWidth(startWidth + t * widthDelta);
            canvas.drawPoint(pt.x, pt.y, paint);

        }
        paint.setStrokeWidth(originalWidth);
    }
}
