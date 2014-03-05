package org.openpaper.paint;

import java.util.ServiceLoader;

import org.openpaper.paint.action.ActionQueue;
import org.openpaper.paint.action.BrushStroke;
import org.openpaper.paint.drawing.ColorChooser;
import org.openpaper.paint.drawing.ColorChooser.ColorChooserListener;
import org.openpaper.paint.drawing.DrawingView;
import org.openpaper.paint.drawing.PaperView;
import org.openpaper.paint.drawing.brush.BezierBrush;
import org.openpaper.paint.drawing.brush.Brush;
import org.openpaper.paint.drawing.brush.PencilBrush;
import org.openpaper.paint.drawing.brush.PointBrush;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class Paint extends Activity {

    Class brushes[] = new Class[] { BezierBrush.class, PencilBrush.class,
            PointBrush.class };

    private void addBrushButtons(final ActionQueue ac, final ColorChooser cc) {
        final LinearLayout palette = (LinearLayout) findViewById(R.id.palette);
        LinearLayout.LayoutParams lllp = new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        for (final Class brush : brushes) {
            Brush b = null;
            try {
                b = (Brush) brush.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            Button brushButton = new Button(getApplicationContext());
            brushButton.setText(b.getName());
            brushButton.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    ac.execute(new BrushStroke(brush, cc.getSelectedColor()));
                }
            });
            brushButton.setLayoutParams(lllp);
            palette.addView(brushButton, 0);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_paint);
        final PaperView paperView = (PaperView) findViewById(R.id.paperView);
        final ColorChooser cc = (ColorChooser) findViewById(R.id.colorChooser);
        final ActionQueue ac = paperView.getActionQueue();

        addBrushButtons(ac, cc);

        cc.setColorListener(new ColorChooserListener() {
            @Override
            public void onColorSelected(ColorChooser cc) {
                Brush s = paperView.getBrush();
                BrushStroke b = new BrushStroke(s.getClass(), cc
                        .getSelectedColor());
                ac.execute(b);
            }
        });
    }
}
