package org.openpaper.paint.drawing.brush;

import org.openpaper.paint.drawing.Point;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * Draws a point with the given paint. This can act as an erase if you provide
 * it the right paint.
 * 
 * @author erwinj
 * 
 */
public class PointBrush extends Brush {

    private Paint paint = new Paint();

    public PointBrush() {
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(5f);
    }

    public PointBrush(int color) {
        paint.setAntiAlias(true);
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(5f);
    }

    public PointBrush(Paint paint) {
        this.paint = paint;
    }

    @Override
    public Rect addPoint(Canvas c, Point p) {
        if (p == null)
            return null;

        this.paint.setColor(getColor());
        c.drawPoint(p.x, p.y, paint);
        int strokeWidth = (int) (paint.getStrokeWidth() / 2);
        return new Rect((int) p.x - strokeWidth, (int) p.y - strokeWidth,
                (int) p.x + strokeWidth, (int) p.y + strokeWidth);
    }

    @Override
    public void clear() {
    }

    @Override
    public String getName() {
        return "Point";
    }
}
