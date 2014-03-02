package org.openpaper.paint.action;

import org.openpaper.paint.drawing.DrawingView;

public abstract class PaintAction {

    abstract void execute(DrawingView dv);

    boolean isSnapshot() {
        return false;
    }

    public boolean isBrush() {
        return false;
    }
}
