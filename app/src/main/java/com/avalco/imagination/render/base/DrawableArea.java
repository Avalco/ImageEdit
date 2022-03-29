package com.avalco.imagination.render.base;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import androidx.annotation.Nullable;

import com.avalco.imagination.render.base.interfaces.DrawMethod;
import com.avalco.imagination.utils.LogUtil;

import java.util.LinkedList;

/**
 * @author Avalco
 */
public abstract class DrawableArea implements DrawMethod {
    RectF area;
    LinkedList<DrawableArea> drawableAreas =new LinkedList<>();
   protected Paint paint;
   private DrawMethod drawMethod;
    protected abstract void initPaint();

    public DrawableArea(RectF area, @Nullable LinkedList<DrawableArea> drawableAreas) {
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
            int l=drawableAreas.size();
            for (int i=0;i<l;i++){
                try {
                    drawableAreas.get(i).draw(canvas);
                }
                catch (Exception e){
                    //
                    LogUtil.logD("DrawableArea","ConCurrentException :"+e.getMessage());
                }
            }
        }
    }

    public LinkedList<DrawableArea> getDrawableAreas() {
        return drawableAreas;
    }
    public void setDrawableAreas(LinkedList<DrawableArea> drawableAreas) {
        this.drawableAreas = drawableAreas;
    }
    public final void addDrawableArea(DrawableArea drawableArea){
        if (drawableAreas==null){
            drawableAreas=new LinkedList<>();
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
}
