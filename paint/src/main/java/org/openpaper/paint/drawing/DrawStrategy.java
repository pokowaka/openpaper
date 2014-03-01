package org.openpaper.paint.drawing;

import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * A Drawing strategy used in conjunction with the {@link DrawingView}. For
 * example you could implement an eraser, fancy curve fitters, etc.. etc...
 * 
 * @author erwinj
 * 
 */
public interface DrawStrategy extends Cloneable {

    /**
     * A new point has been added to canvas. This is called during touch events.
     * Note, you will get a addPoint with a null value when we detect a touch up
     * event. this usually indicated the end of a line.
     * 
     * @param c
     *            The canvas on which you can draw.
     * @param p
     *            The point to be added, or null if there are no new points
     *            coming in this segment.
     * @return The dirty rectangle.
     */
    Rect addPoint(Canvas c, Point p);

    void clear();

    /**
     * Sets the color that should be used by this drawing strategy.
     * 
     * @param color
     *            The color to be used while drawing.
     */
    void setColor(int color);

}
