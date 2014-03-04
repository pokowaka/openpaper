package org.openpaper.paint.drawing;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

@SuppressLint("UseSparseArrays")
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class ColorChooser extends LinearLayout {

    public interface ColorChooserListener {
        void onColorSelected(ColorChooser cc);
    }

    private static final String[] DEFAULT_PALETTE = new String[] { "#000000",
            "#b1ada2", "#ffffff", "#ab7f58", "#6d5a52", "#eca81f", "#4f7c81",
            "#91a170", "#b78faa", };

    int colors[];
    int current;
    HashMap<Integer, GradientDrawable> colorSet = new HashMap<Integer, GradientDrawable>();
    ColorChooserListener listener;

    private int strokeWidth = 5;

    public ColorChooser(Context context) {
        super(context);
        initialize();
    }

    public ColorChooser(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    ImageButton addColorButton(final int aColor) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(1);
        drawable.setColor(aColor);
        // I know this doesn't make sense.. not setting the stroke results
        // in weirdness.
        drawable.setStroke(strokeWidth, aColor);

        colorSet.put(aColor, drawable);

        ImageButton colorButton = new ImageButton(getContext());
        colorButton.setBackground(drawable);
        colorButton.setPadding(20, 20, 20, 20);
        colorButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onColorSelected(aColor);
            }
        });

        return colorButton;
    }

    void initialize() {
        // Load up the default colors..
        colors = new int[DEFAULT_PALETTE.length];
        for (int i = 0; i < DEFAULT_PALETTE.length; i++) {
            colors[i] = Color.parseColor(DEFAULT_PALETTE[i]);
        }

        LinearLayout.LayoutParams lllp = new LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        for (int i = 0; i < colors.length; i++) {
            ImageButton btn = addColorButton(colors[i]);
            addView(btn, lllp);
        }

        onColorSelected(colors[0]);
    }

    private void onColorSelected(int color) {
        updateSelectedColor(color);
        current = color;

        if (listener != null) {
            listener.onColorSelected(this);
        }
    }

    private void updateSelectedColor(int color) {
        for (int aColor : colorSet.keySet()) {
            colorSet.get(aColor).setStroke(strokeWidth, aColor);
        }
        colorSet.get(color).setStroke(strokeWidth, Color.parseColor("#878787"));
    }

    public int getSelectedColor() {
        return current;
    }

    public void setColorListener(ColorChooserListener listener) {
        this.listener = listener;
    }

}
