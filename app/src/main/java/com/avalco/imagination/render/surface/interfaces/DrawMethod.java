package com.avalco.imagination.render.surface.interfaces;

import android.graphics.Canvas;

/**
 * @author Avalco
 */
public interface DrawMethod {
    /**
     *
     * @param canvas canvas you draw on
     */
    void drawBackground(Canvas canvas);
    /**
     *
     * @param canvas canvas you draw on
     */
    void drawFront(Canvas canvas);
}
