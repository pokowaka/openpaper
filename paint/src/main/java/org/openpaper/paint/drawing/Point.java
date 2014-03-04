package org.openpaper.paint.drawing;

import android.graphics.RectF;

/**
 * A simple point with an x,y coordinate and a timestamp of when it was created.
 * 
 * @author erwinj
 * 
 */
public class Point {
    private float pressure;
    public long time;
    public final float x;
    public final float y;

    public Point(float x, float y) {
        this(x, y, 0, 0);
    }

    public Point(float x, float y, float pressure) {
        this(x, y, pressure, 0);
    }

    public Point(float x, float y, float pressure, long time) {
        super();
        this.x = x;
        this.y = y;
        this.time = time;
        this.pressure = pressure;
    }

    public float distanceTo(Point other) {
        if (other == null)
            return 0;
        float dx = other.x - x;
        float dy = other.y - y;
        return (float) Math.sqrt((dx * dx) + (dy * dy));
    }

    public float dotProduct(Point other) {
        return x * other.x + y * other.y;
    }

    public float getPressure() {
        return pressure;
    }

    @Override
    public String toString() {
        return "Point [x=" + x + ", y=" + y + ", time=" + time + ", pressure="
                + pressure + "]";
    }

    public float velocityFrom(Point start) {
        if (start == null || this.time == start.time)
            return 0;
        return distanceTo(start) / (this.time - start.time);
    }

}