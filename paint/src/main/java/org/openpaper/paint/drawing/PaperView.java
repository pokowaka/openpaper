package org.openpaper.paint.drawing;

import org.openpaper.paint.action.ActionQueue;
import org.openpaper.paint.action.ActionQueue.ActionQueueChangeListener;
import org.openpaper.paint.drawing.CircularSeekBar.OnSeekChangeListener;
import org.openpaper.paint.drawing.brush.Brush;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * This combines the rewind (circular seekbar) with the canvas.
 * 
 * 
 * @author erwinj
 * 
 */
public class PaperView extends FrameLayout {

    private static final String TAG = "org.openpaper.paint.drawing.PaperView";

    private DrawingView dv;
    private CircularSeekBar csb;

    public PaperView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.init();
    }

    public PaperView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public PaperView(Context context) {
        super(context);
        this.init();
    }

    private void init() {
        /*
         * <org.openpaper.paint.drawing.DrawingView
         * android:id="@+id/drawingView" android:layout_width="match_parent"
         * android:layout_height="match_parent" android:background="#f5f4f0"
         * android:gravity="center" android:keepScreenOn="true" />
         * 
         * <org.openpaper.paint.drawing.CircularSeekBar
         * android:id="@+id/circularSeekBar1"
         * android:layout_width="wrap_content"
         * android:layout_height="wrap_content" />
         */

        Context context = getContext();
        FrameLayout.LayoutParams fllp = new LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT, Gravity.CENTER);

        dv = new DrawingView(context);
        dv.setBackgroundColor(Color.parseColor("#f5f4f0"));
        csb = new CircularSeekBar(context);

        FrameLayout.LayoutParams csblayout = new LayoutParams(300, 300,
                Gravity.CENTER);

        addView(dv, fllp);
        addView(csb, csblayout);

        csb.setVisibility(View.INVISIBLE);
        csb.setSeekBarChangeListener(new OnSeekChangeListener() {

            @Override
            public void onProgressChange(CircularSeekBar view, int newProgress) {
                ActionQueue aq = getActionQueue();
                int undo = aq.getUndoHistory();
                int redo = aq.getRedoHistory();

                Log.i(TAG, "Progress change: " + newProgress + ", undo: "
                        + undo + ", redo: " + redo);
                // Nothing is happening.
                if (newProgress == undo || newProgress >= undo + redo)
                    return;

                if (newProgress < undo) {
                    aq.undo(undo - newProgress);
                } else {
                    aq.redo(newProgress - undo);
                }
            }
        });
    }

    public ActionQueue getActionQueue() {
        return dv.getActionQueue();
    }

    public Brush getBrush() {
        return dv.getBrush();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getPointerCount() == 2) {
            if (csb.getVisibility() != View.VISIBLE) {
                int maxProgress = getActionQueue().getRedoHistory() + getActionQueue().getUndoHistory();
                csb.setMaxProgress(maxProgress);
                csb.setProgress(maxProgress);
                csb.setVisibility(View.VISIBLE);
            }

            return csb.onTouchEvent(event);
        }

        csb.setVisibility(View.INVISIBLE);
        return dv.onTouchEvent(event);
    }
}
