package org.openpaper.paint.drawing.brush;

import java.util.LinkedList;
import java.util.Queue;

import org.openpaper.paint.drawing.Point;
import org.openpaper.paint.util.Bounds;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;

/**
 * An implementation of
 * http://iwi.eldoc.ub.rug.nl/FILES/root/2008/ProcCAGVIMeraj
 * /2008ProcCAGVIMeraj.pdf
 * 
 * This kind of looks like a pencil.
 * 
 * @author erwinj
 * 
 */
public class PencilBrush extends Brush {

    final int displacement = 0;

    private Queue<Point> pointQueue = new LinkedList<Point>();
    private Paint paint = new Paint();

    public PencilBrush() {
        init(Color.BLACK);
    }

    public PencilBrush(int color) {
        init(color);
    }

    private void init(int color) {
        paint.setAntiAlias(true);
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(1f);
    }

    @Override
    public Rect addPoint(Canvas c, Point newPoint) {

        // Okay, the user lifted their pen.
        if (newPoint == null) {
            this.pointQueue.clear();
            return null;
        }

        this.pointQueue.offer(newPoint);

        Rect invalid = null;
        if (this.pointQueue.size() == 2) {
            Point a = pointQueue.poll();
            Point b = pointQueue.peek();
            drawLine(c, a, b);
            invalid = Bounds.getBounds(a, b);
        }

        return invalid;
    }

    private void drawLine(Canvas canvas, Point p0, Point p1) {
        this.paint.setColor(getColor());
        float distance = p0.distanceTo(p1);
        final float tfinal = 2;

        // Delta settings as per paper
        float delta = 0.5f;
        if (distance > 400) {
            delta = 0.2f;
        } else if (distance > 200) {
            delta = 0.3f;
        }

        Path p = new Path();
        p.moveTo(p0.x, p0.y);

        Queue<Point> pts = new LinkedList<Point>();
        for (float t = 0; t < tfinal; t += delta) {

            // Calculate the squiggle randomization..
            float tau = t / tfinal;
            float m = (15 * tau * tau * tau * tau)
                    - (6 * tau * tau * tau * tau * tau)
                    - (10 * tau * tau * tau);
            float x = p0.x + (p0.x - p1.x) * m + displacement;
            float y = p0.y + (p0.y - p1.y) * m + displacement;

            // Which we use to construct bezier curves.
            pts.offer(new Point(x, y));
            if (pts.size() > 2) {
                Point a0 = pts.poll();
                Point b1 = pts.poll();
                Point c2 = pts.poll();
                p.cubicTo(a0.x, a0.y, b1.x, b1.y, c2.x, c2.y);
            }
        }

        // Okay, we just draw the last left overs.
        while (pts.size() > 0) {
            Point a = pts.poll();
            p.lineTo(a.x, a.y);
        }

        // Make sure we leave no gap..
        p.lineTo(p1.x, p1.y);
        canvas.drawPath(p, paint);
    }

    @Override
    public void clear() {
        this.pointQueue.clear();
    }

    @Override
    public String getName() {
        return "Pencil";
    }

}
