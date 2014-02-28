package org.openpaper.paint.drawing;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class Bezier {

    private Point startPoint;
    private Point endPoint;
    private Point control1;
    private Point control2;
    private int drawSteps;

    public Rect getBoundingBox() {
        int minx = (int) Math.floor(Math.min(
                Math.min(startPoint.x, endPoint.x),
                Math.min(control1.x, control2.x)));
        int miny = (int) Math.floor(Math.min(
                Math.min(startPoint.y, endPoint.y),
                Math.min(control1.y, control2.y)));
        int maxx = (int) Math.ceil(Math.max(Math.max(startPoint.x, endPoint.x),
                Math.max(control1.x, control2.x)));
        int maxy = (int) Math.ceil(Math.max(Math.max(startPoint.y, endPoint.y),
                Math.max(control1.y, control2.y)));
        return new Rect(minx, miny, maxx, maxy);
    }

    public Bezier(Point startPoint, Point control1, Point control2,
            Point endPoint) {
        super();
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.control1 = control1;
        this.control2 = control2;
        
        
        Rect bb = getBoundingBox();
        float dx = bb.right - bb.left;
        float dy = bb.bottom - bb.top;
        drawSteps = (int) (2 * Math.sqrt(dx*dx + dy * dy)); 
    }

    public void draw(Canvas canvas, Paint paint) {
        draw(canvas, paint, paint.getStrokeWidth(), paint.getStrokeWidth());
    }

    public Point calculatePoint(float t) {
        float tt = t * t;
        float ttt = tt * t;
        float u = 1 - t;
        float uu = u * u;
        float uuu = uu * u;

        float x = uuu * startPoint.x;
        x += 3 * uu * t * control1.x;
        x += 3 * u * tt * control2.x;
        x += ttt * endPoint.x;

        float y = uuu * startPoint.y;
        y += 3 * uu * t * control1.y;
        y += 3 * u * tt * control2.y;
        y += ttt * endPoint.y;

        return new Point(x, y);
    }

    /** Draws a variable-width Bezier curve. */
    public void draw(Canvas canvas, Paint paint, float startWidth,
            float endWidth) {
        float originalWidth = paint.getStrokeWidth();
        float widthDelta = endWidth - startWidth;

        for (int i = 0; i < drawSteps; i++) {
            // Calculate the Bezier (x, y) coordinate for this step.
            float t = ((float) i) / drawSteps;
            Point pt = calculatePoint(t);
            // Set the incremental stroke width and draw.
            paint.setStrokeWidth(startWidth + t * t * t * widthDelta);
            canvas.drawPoint(pt.x, pt.y, paint);
            ;
        }

        paint.setStrokeWidth(originalWidth);
    }
}
