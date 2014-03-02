package org.openpaper.paint;

import org.openpaper.paint.action.ActionQueue;
import org.openpaper.paint.action.BrushSelector;
import org.openpaper.paint.drawing.ColorChooser;
import org.openpaper.paint.drawing.ColorChooser.ColorChooserListener;
import org.openpaper.paint.drawing.brush.Brush;
import org.openpaper.paint.drawing.brush.PencilBrush;
import org.openpaper.paint.drawing.brush.PointBrush;
import org.openpaper.paint.drawing.DrawingView;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class paint extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_paint);
        final DrawingView drawingView = (DrawingView) findViewById(R.id.drawingView);
        final ColorChooser cc = (ColorChooser) findViewById(R.id.colorChooser1);

        final Button undo = (Button) findViewById(R.id.undo);
        final Button redo = (Button) findViewById(R.id.redo);

        final Button eraser = (Button) findViewById(R.id.eraser);
        final Button ink = (Button) findViewById(R.id.ink);
        final ActionQueue ac = drawingView.getActionQueue();

        undo.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View paramView) {
                ac.undo(100);
            }
        });

        redo.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View paramView) {
                ac.redo(100);
            }
        });

        eraser.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View paramView) {
                ac.addAction(new BrushSelector(PointBrush.class, cc
                        .getSelectedColor()));
            }
        });

        ink.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View paramView) {
                ac.addAction(new BrushSelector(PencilBrush.class, cc
                        .getSelectedColor()));
            }
        });

        cc.setColorListener(new ColorChooserListener() {
            @Override
            public void onColorSelected(ColorChooser cc) {
                Brush s = drawingView.getBrush();
                BrushSelector b = new BrushSelector(s.getClass(), cc
                        .getSelectedColor());
                ac.addAction(b);
            }
        });
    }
}
