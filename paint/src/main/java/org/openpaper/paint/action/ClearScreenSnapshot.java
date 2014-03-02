package org.openpaper.paint.action;

import org.openpaper.paint.drawing.DrawingView;

public class ClearScreenSnapshot extends PaintAction {

    private BrushSelector brush;

    public ClearScreenSnapshot(DrawingView dv) {
        brush = new BrushSelector(dv.getBrush().getClass(), dv.getBrush()
                .getColor());
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
