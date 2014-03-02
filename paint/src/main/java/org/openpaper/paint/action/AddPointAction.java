package org.openpaper.paint.action;

import org.openpaper.paint.drawing.DrawingView;
import org.openpaper.paint.drawing.Point;

public class AddPointAction extends PaintAction {

    private Point point;

    public AddPointAction(Point newPoint) {
        this.point = newPoint;
    }

    @Override
    void execute(DrawingView dv) {
        dv.addPoint(point);
    }
}
