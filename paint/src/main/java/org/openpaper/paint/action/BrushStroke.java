package org.openpaper.paint.action;

import org.openpaper.paint.drawing.DrawingView;
import org.openpaper.paint.drawing.brush.Brush;

public class BrushStroke extends PaintAction {

    Brush brush;

    public BrushStroke(Class<?> clz, int color) {
        if (!Brush.class.isAssignableFrom(clz)) {
            throw new IllegalArgumentException("Clz is not a Brush!");
        }
        try {
            brush = (Brush) clz.newInstance();
            brush.setColor(color);
        } catch (Exception e) {
            throw new IllegalArgumentException("Clz is not a Brush!");
        }
    }

    public BrushStroke(Brush b) {
        this.brush = b;
    }

    @Override
    void execute(DrawingView dv) {
        dv.setBrush(brush);
    }

    @Override
    public boolean isBrush() {
        return true;
    }
}
