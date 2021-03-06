package com.avalco.imagination.render.image;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;

import androidx.annotation.Nullable;

import com.avalco.imagination.render.surface.DrawableArea;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Avalco
 */
public class ImageDrawableArea extends DrawableArea {
    Bitmap image;
    float left;
    float top;

    @Override
    protected void initPaint() {
         left=getAreaF()!=null?getAreaF().left:0;
         top=getAreaF()!=null?getAreaF().top:0;
    }

    public ImageDrawableArea(RectF area, @Nullable ConcurrentLinkedQueue<DrawableArea> drawableAreas) {
        super(area, drawableAreas);
    }

    @Override
    public void drawBackground(Canvas canvas) {
        if (image!=null){
            canvas.drawBitmap(image,left,top,null);
        }
    }

    @Override
    public void drawFront(Canvas canvas) {

    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}
