package com.avalco.imagination.render.surface.utils;

import android.graphics.Canvas;
import android.graphics.Paint;

import androidx.annotation.NonNull;

public class SurfaceUtils {
    public static void drawText(@NonNull Canvas canvas, String text, int x, int y, @NonNull Paint paint){
        Paint.FontMetrics fontMetrics=paint.getFontMetrics();
        y= (int) (y-fontMetrics.ascent);
        switch (paint.getTextAlign()) {
            case RIGHT:
                x = (int) (x + paint.measureText(text));
                break;
            case CENTER:
                x = (int) (x + paint.measureText(text) / 2);
                break;
            case LEFT:
            default:
                break;
        }
        canvas.drawText(text,x,y,paint);
    }
}
