package org.openpaper.paint.action;

import org.openpaper.paint.drawing.DrawingView;
import org.openpaper.paint.drawing.brush.Stroke;

import android.graphics.Rect;

public class StrokeAction extends PaintAction {

    private Stroke stroke;

    public StrokeAction(Stroke aStroke) {
        this.stroke = aStroke;
    }

    @Override
    void execute(DrawingView dv) {
        Rect r = dv.getBrush().drawStroke(dv.getCanvas(), stroke);
        if (r != null)
            dv.invalidate(r);
    }
}
