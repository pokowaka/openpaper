package org.openpaper.paint.action;

import junit.framework.AssertionFailedError;

import org.openpaper.paint.drawing.DrawingView;
import org.openpaper.paint.drawing.brush.Brush;

public class ClearScreenSnapshot extends PaintAction {

    private BrushStroke brush;

    public ClearScreenSnapshot(DrawingView dv) {
        try {
            brush = new BrushStroke((Brush) dv.getBrush().clone());
        } catch (CloneNotSupportedException e) {
            throw new AssertionFailedError(
                    "Every brush should implement the clone method");
        }
    }

    @Override
    void execute(DrawingView dv) {
        dv.clear();
        brush.execute(dv);
    }

    @Override
    boolean isSnapshot() {
        return true;
    }

}
