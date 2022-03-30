package com.avalco.imagination.render.image;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.WindowMetrics;

import androidx.annotation.NonNull;

import com.avalco.imagination.render.surface.BaseDrawableSurfaceView;
import com.avalco.imagination.render.surface.BitMapCache;
import com.avalco.imagination.render.surface.DrawableArea;

import com.avalco.imagination.render.surface.utils.SurfaceUtils;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * @author Avalco
 */
public class ImageEditSurfaceView extends BaseDrawableSurfaceView {
    private final DrawableArea rootDrawable=new DrawableArea(new RectF(0,0,0,0),null) {
        long before=0;
        int fps;
        @Override
        protected void initPaint() {
            paint=new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setTextSize(40);
            paint.setColor(Color.parseColor("#FF00FF88"));
        }

        @Override
        public void drawBackground(Canvas canvas) {
           drawableAreas.draw(canvas);
        }

        @Override
        public void drawFront(Canvas canvas) {
             int nFps=getFrameTracer().getFps();
             if (nFps!=fps&&System.currentTimeMillis()-before>500){
                 fps=nFps;
                 before=System.currentTimeMillis();
             }
             //
            String text="FPS: "+fps;
            SurfaceUtils.drawText(canvas,text, (int) (screenWidth-paint.measureText(text)),top,paint);
        }

        @Override
        public void clear() {
            super.clear();
            fps=0;
            drawableAreas.clear();
        }
    };
    ImageDrawableArea drawableAreas ;
    final ConcurrentLinkedQueue<BitMapCache> bitmaps;
    private final Object lock=new Object();
    private final int capacity=3;
    private final ThreadFactory mainThreadFactory=new ThreadFactoryBuilder().setNameFormat("ImageEditSurfaceView-preLoad-thread-%d").build();
    private ExecutorService preLoadService;
    BitMapCache cachedBitmap;
    private int screenWidth;
    private int screenHeight;
    private int top;
    public ImageEditSurfaceView(@NonNull Context context) {
        super(context);
        bitmaps=new ConcurrentLinkedQueue<>();
        drawableAreas=new ImageDrawableArea(new RectF(0,0,0,0),null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
          WindowMetrics windowMetrics=((Activity)(context)).getWindowManager().getCurrentWindowMetrics();
          screenHeight=windowMetrics.getBounds().height();
          screenWidth=windowMetrics.getBounds().width();
          top=context.getDisplay().getCutout().getBoundingRectTop().bottom;
        }
        else {
            DisplayMetrics displayMetrics=new DisplayMetrics();
            ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            screenWidth=displayMetrics.widthPixels;
            screenHeight=displayMetrics.heightPixels;
        }
    }

    @Override
    public void onSurfaceCreated(@NonNull SurfaceHolder holder) {
         super.onSurfaceCreated(holder);
        preLoadService = new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>(1), mainThreadFactory);
        preLoadService.execute(preLoadRunnable);
    }

    @Override
    public void onSurfaceDestroyed(@NonNull SurfaceHolder holder) {
        super.onSurfaceDestroyed(holder);
        preLoadService.shutdownNow();
        bitmaps.clear();
        cachedBitmap=null;
        rootDrawable.clear();
        getFrameTracer().stopTrace();
    }

    @Override
    public void onSurfaceRenderStart() {
        super.onSurfaceRenderStart();
        getFrameTracer().startTrace();
    }

    @Override
    public void drawCanvas(Canvas canvas) {
        if (bitmaps!=null&&!bitmaps.isEmpty())
        {
            cachedBitmap=bitmaps.poll();
        }
        if(cachedBitmap!=null&&!cachedBitmap.get().isRecycled()){
            canvas.drawBitmap(cachedBitmap.get(),cachedBitmap.matrix,null);
        }
    }
   private final Runnable preLoadRunnable=new Runnable() {
       @Override
       public void run() {
           while (!preLoadService.isShutdown()) {
               while (bitmaps.size()>=capacity){
               try {
                   synchronized (lock){
                       lock.wait(20);
                   }
               } catch (InterruptedException e) {
                       e.printStackTrace();
               }
               }
               BitMapCache bitmap= bitmaps.peek();
               if (bitmap==null){
                   if (cachedBitmap==null){
                       cachedBitmap=new BitMapCache(drawableAreas.image.copy(Bitmap.Config.ARGB_8888,true));
                   }
                   bitmap=cachedBitmap;
               }
               BitMapCache newBitMap=new BitMapCache(bitmap.get().copy(Bitmap.Config.ARGB_8888,true));
               rootDrawable.draw(new Canvas(newBitMap.get()));
               bitmaps.offer(newBitMap);
           }
       }
   };
    public ImageDrawableArea getDrawableAreas() {
        return drawableAreas;
    }
    public Bitmap getCachedBitmap() {
        return cachedBitmap.get();
    }
}
