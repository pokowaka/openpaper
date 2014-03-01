package org.openpaper.paint;

import org.openpaper.paint.drawing.BezierDrawStrategy;
import org.openpaper.paint.drawing.ColorChooser;
import org.openpaper.paint.drawing.ColorChooser.ColorChooserListener;
import org.openpaper.paint.drawing.DrawingView;
import org.openpaper.paint.drawing.PointDrawStrategy;
import org.openpaper.paint.util.SystemUiHider;

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
        final Button eraser = (Button) findViewById(R.id.eraser);
        final Button ink = (Button) findViewById(R.id.ink);

        eraser.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View paramView) {
                drawingView.setDrawStrategy(new PointDrawStrategy(cc
                        .getSelectedColor()));
            }
        });

        ink.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View paramView) {
                drawingView.setDrawStrategy(new BezierDrawStrategy(0.7f, 5, cc
                        .getSelectedColor()));
            }
        });

        cc.setColorListener(new ColorChooserListener() {
            @Override
            public void onColorSelected(ColorChooser cc) {
                drawingView.getStrategy().setColor(cc.getSelectedColor());
            }
        });
    }
}
