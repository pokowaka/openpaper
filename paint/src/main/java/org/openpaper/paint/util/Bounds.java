package org.openpaper.paint.util;

import org.openpaper.paint.drawing.Point;

import android.graphics.Rect;
import android.graphics.RectF;

public class Bounds {

    public static RectF extendRectF(Point p, RectF r) {
        if (p.x < r.left) {
            r.left = p.x;
        }
        if (p.x > r.right) {
            r.right = p.x;
        }

        if (p.y < r.top) {
            r.top = p.y;
        }

        if (p.y > r.bottom) {
            r.bottom = p.y;
        }
        return r;
    }

    public static Rect extendRect(Point p, Rect r) {
        if (p.x < r.left) {
            r.left = (int) p.x;
        }
        if (p.x > r.right) {
            r.right = (int) p.x;
        }

        if (p.y < r.top) {
            r.top = (int) p.y;
        }

        if (p.y > r.bottom) {
            r.bottom = (int) p.y;
        }
        return r;
    }

    public static Rect getBounds(Point a, Point b) {
        Rect r = new Rect(Integer.MAX_VALUE, Integer.MAX_VALUE,
                Integer.MIN_VALUE, Integer.MIN_VALUE);
        return extendRect(b, extendRect(a, r));
    }

    public static Rect extendRect(Rect orig, Rect toExtend) {
        if (toExtend != null) {
            extendRect(new Point(toExtend.left, toExtend.top), orig);
            extendRect(new Point(toExtend.right, toExtend.bottom), orig);
        }
        return orig;
    }
}
