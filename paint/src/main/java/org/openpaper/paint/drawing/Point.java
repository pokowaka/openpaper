package org.openpaper.paint.drawing;

/**
 * A simple point with an x,y coordinate and a timestamp of when it was created.
 * 
 * @author erwinj
 * 
 */
public class Point {
    public final float x;
    public final float y;
    public final long time;
    private float pressure;

    public float distanceTo(Point other) {
        if (other == null)
            return 0;
        float dx = other.x - x;
        float dy = other.y - y;
        return (float) Math.sqrt((dx * dx) + (dy * dy));
    }

    public float velocityFrom(Point start) {
        if (start == null || this.time == start.time)
            return 0;
        return distanceTo(start) / (this.time - start.time);
    }

    public Point(float x, float y) {
        super();
        this.x = x;
        this.y = y;
        this.time = System.currentTimeMillis();
    }

    public Point(float x, float y, float pressure) {
        this(x, y);
        this.pressure = pressure;
    }

    public float getPressure() {
        return pressure;
    }
}