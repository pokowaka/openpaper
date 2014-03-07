package org.openpaper.paint.drawing.brush;

import java.util.ArrayList;

import org.openpaper.paint.drawing.Point;

public class Stroke extends ArrayList<Point> {

    private static final long serialVersionUID = 1L;
    private int maxSize;

    public Stroke(int maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    public void add(int index, Point object) {
        if (this.size() >= maxSize)
            return;

        super.add(index, object);
    }

    @Override
    public boolean add(Point object) {
        if (this.size() >= maxSize)
            return false;
        return super.add(object);
    }

}
