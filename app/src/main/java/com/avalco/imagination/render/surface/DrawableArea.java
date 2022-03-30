package com.avalco.imagination.render.surface;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import androidx.annotation.CallSuper;
import androidx.annotation.Nullable;

import com.avalco.imagination.render.surface.interfaces.DrawMethod;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Avalco
 */
public abstract class DrawableArea implements DrawMethod {
    RectF area;
    ConcurrentLinkedQueue<DrawableArea> drawableAreas;
   protected Paint paint;
   private DrawMethod drawMethod;
    protected abstract void initPaint();

    public DrawableArea(RectF area, @Nullable ConcurrentLinkedQueue<DrawableArea> drawableAreas) {
        this.area = area;
        this.drawableAreas = drawableAreas;
        drawMethod=this;
        initPaint();
    }

    public final RectF getAreaF() {
        return area;
    }
    @Nullable
    public final Rect getArea() {
        if (area!=null){
            return new Rect((int)(area.left),(int)(area.top),(int)(area.right),(int)(area.bottom));
        }
        else {
            return null;
        }
    }
    public final void setArea(RectF area) {
        this.area = area;
    }


    public final void draw(Canvas canvas){
       drawMethod.drawBackground(canvas);
       drawNext(canvas);
       drawMethod.drawFront(canvas);
    }

    private void drawNext(Canvas canvas) {
        if (drawableAreas!=null){
//            int l=drawableAreas.size();
            for (DrawableArea d : drawableAreas) {
                d.draw(canvas);
            }
           /* for (int i=0;i<l;i++){
                try {
                    drawableAreas.get(i).draw(canvas);
                }
                catch (Exception e){
                    //
                    LogUtil.logD("DrawableArea","ConCurrentException :"+e.getMessage());
                }
            }*/
        }
    }

    public ConcurrentLinkedQueue<DrawableArea> getDrawableAreas() {
        return drawableAreas;
    }
    public void setDrawableAreas(ConcurrentLinkedQueue<DrawableArea> drawableAreas) {
        this.drawableAreas = drawableAreas;
    }
    public final void addDrawableArea(DrawableArea drawableArea){
        if (drawableAreas==null){
            drawableAreas=new ConcurrentLinkedQueue<>();
        }
        drawableAreas.offer(drawableArea);
    }
    public final void removeDrawableArea(DrawableArea drawableArea){
        if (drawableAreas!=null){
            drawableAreas.remove(drawableArea);
        }
    }

    public void setDrawable(DrawMethod selfDrawable) {
        this.drawMethod = selfDrawable;
    }
    @CallSuper
    public  void clear(){
        if (drawableAreas!=null){
            drawableAreas.clear();
        }
    }
}
