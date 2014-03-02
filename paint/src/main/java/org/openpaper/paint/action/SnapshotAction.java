package org.openpaper.paint.action;

import org.openpaper.paint.drawing.DrawingView;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;

/**
 * Creates a snapshot of the drawingview.
 * 
 * @author erwinj
 * 
 */
public class SnapshotAction extends PaintAction {

    private Bitmap snapshot;
    private BrushSelector brush;

    public SnapshotAction(DrawingView dv) {
        Bitmap bitmap = dv.getOffscreenBuffer();
        snapshot = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), null, true);
        brush = new BrushSelector(dv.getBrush().getClass(), dv.getBrush()
                .getColor());
    }

    @Override
    void execute(DrawingView dv) {
        Canvas c = new Canvas(dv.getOffscreenBuffer());
        c.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        c.drawBitmap(snapshot, 0, 0, null);

        brush.execute(dv);
        dv.invalidate();
    }

    @Override
    boolean isSnapshot() {
        return true;
    }
}
