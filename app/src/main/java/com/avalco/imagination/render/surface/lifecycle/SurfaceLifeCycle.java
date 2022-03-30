package com.avalco.imagination.render.surface.lifecycle;

import android.view.SurfaceHolder;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;

/**
 * @author Avalco
 */
public interface SurfaceLifeCycle {
    /**
     * called immediately after the surface is first created
     * @param holder – the one hold the surface you will draw on
     */
    @CallSuper
    default void onSurfaceCreated(@NonNull SurfaceHolder holder) {
    }

    /**
     *called immediately after any structural changes (format or size) have been made to the surface.
     * @param holder – the one hold the surface you will draw on.
     * @param format - The new PixelFormat of the surface.
     * @param width – The new width of the surface.
     * @param height – The new height of the surface.
     */
    @CallSuper
    default void onSurfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {}

    /**
     * called immediately before a surface is being destroyed.
     * @param holder – the one hold the surface you will draw on
     */
    @CallSuper
    default void onSurfaceDestroyed(@NonNull SurfaceHolder holder) {
    }
    /**
     * called immediately before a surface is start render.
     */
    @CallSuper
    default void onSurfaceRenderStart() {
    }
}
