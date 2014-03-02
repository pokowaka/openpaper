package org.openpaper.paint.action;

import org.openpaper.paint.drawing.DrawingView;
import org.openpaper.paint.drawing.brush.Brush;

public class BrushSelector extends PaintAction {

    Brush ds;

    public BrushSelector(Class<?> clz, int color) {
        if (!Brush.class.isAssignableFrom(clz)) {
            throw new IllegalArgumentException("Clz is not a DrawStrategy!");
        }
        try {
            ds = (Brush) clz.newInstance();
            ds.setColor(color);
        } catch (Exception e) {
            throw new IllegalArgumentException("Clz is not a DrawStrategy!");
        }
    }

    @Override
    void execute(DrawingView dv) {
        dv.setBrush(ds);
    }
    
    @Override
    public boolean isBrush() {
        return true;
    }
}
