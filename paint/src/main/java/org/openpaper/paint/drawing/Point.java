package org.openpaper.paint.drawing;

public class Point {
    public final float x;
    public final float y;
    public final long time;

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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (time ^ (time >>> 32));
        result = prime * result + Float.floatToIntBits(x);
        result = prime * result + Float.floatToIntBits(y);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Point other = (Point) obj;
        if (Float.floatToIntBits(x) != Float.floatToIntBits(other.x))
            return false;
        if (Float.floatToIntBits(y) != Float.floatToIntBits(other.y))
            return false;
        return true;
    }

}