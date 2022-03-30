package com.avalco.imagination.render.surface;

import android.graphics.Bitmap;
import android.graphics.Matrix;

/**
 * @author Avalco
 */
public class BitMapCache {
    private final Bitmap bitmap;
    public Matrix matrix;

    public BitMapCache(Bitmap bitmap) {
        this.bitmap=bitmap;
        matrix=new Matrix();
    }
    public Bitmap get(){
        return bitmap;
    }
}
