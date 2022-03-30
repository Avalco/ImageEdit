package com.avalco.imagination.render.surface;

import android.content.Context;
import android.graphics.Canvas;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;

import com.avalco.imagination.render.surface.lifecycle.SurfaceLifeCycle;
import com.avalco.imagination.render.surface.trace.FrameTracer;
import com.avalco.imagination.utils.LogUtil;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Avalco
 */
public abstract class BaseDrawableSurfaceView extends SurfaceView implements SurfaceHolder.Callback , SurfaceLifeCycle {
    private  ExecutorService mainExecutorService;
    private SurfaceHolder mHolder;
    private final AtomicBoolean surfaceAlive=new AtomicBoolean(false);
    private final Object lock;
    private short frame;
    private final MessageRunnable messageHandle;
    private final FrameTracer frameTracer;
    private final ThreadFactory mainThreadFactory=new ThreadFactoryBuilder().setNameFormat("SurfaceView-[taskName]-thread-%d").build();
    public BaseDrawableSurfaceView(@NonNull Context context) {
      super(context);
        mainExecutorService=new ThreadPoolExecutor(3,3,0L
                , TimeUnit.MILLISECONDS,new ArrayBlockingQueue<>(1),mainThreadFactory);
        messageHandle=new MessageRunnable();
        mainExecutorService.execute(messageHandle);
        mHolder=getHolder();
        mHolder.addCallback(this);
        lock=new Object();
        frame=200;
        frameTracer=new FrameTracer();
        renderRunnable=new Runnable() {
            final long limit=1000/frame;
            @Override
            public void run() {
                String threadName=Thread.currentThread().getName();
                Thread.currentThread().setName(threadName.replace("[taskName]","render"));
                onSurfaceRenderStart();
                while (surfaceAlive.get()){
                    long before=System.currentTimeMillis();
                    doRender(mHolder);
                    long timeWait=limit-(System.currentTimeMillis()-before);
                    if (timeWait>0){
                        try {
                            synchronized (lock){
                                LogUtil.logThreadD("renderRunnable","time:"+timeWait);
                                lock.wait(timeWait);
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    frameTracer.trace((int) (System.currentTimeMillis()-before));
                }
            }
        };
    }

    @Override
    public final void surfaceCreated(@NonNull SurfaceHolder holder) {
        mHolder=getHolder();
        mainExecutorService=new ThreadPoolExecutor(1,1,0L
                , TimeUnit.MILLISECONDS,new ArrayBlockingQueue<>(1),mainThreadFactory);
        surfaceAlive.set(true);
        mainExecutorService.execute(renderRunnable);
        LogUtil.logD("RenderAbleSurfaceView","surfaceCreated");
        onSurfaceCreated(holder);
    }

    @Override
    public final  void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        LogUtil.logD("RenderAbleSurfaceView","surfaceChanged");
        onSurfaceChanged(holder, format, width, height);
    }

    @Override
    public final void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        LogUtil.logD("RenderAbleSurfaceView","surfaceDestroyed");
        mHolder=null;
        surfaceAlive.set(false);
        onSurfaceDestroyed(holder);
    }

    /**
     *NOTICE
     *<br>
     *This method is called by surface render thread,normally be used to handler logic of draw.
     *you can uses a cached bitmap to avoid flicker,you will draw things you want on cache first,then submit the cache to surface.
     *<br>
     *
     * @param mHolder the one hold the surface you will draw on
     */
    protected final void doRender(SurfaceHolder mHolder) {
        Canvas canvas=null;
        try {
            canvas=mHolder.lockCanvas();
            drawCanvas(canvas);
        }catch (Exception e){
            //ignore
            e.printStackTrace();
        }
        finally {
            if (mHolder!=null&&surfaceAlive.get()){
                mHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    /**
     *only work on you didn't override doRender
     * @param canvas canvas you will draw on
     */
    public abstract void drawCanvas(Canvas canvas);
    @CallSuper
   public void  destroy(){
       mainExecutorService.shutdown();
       frameTracer.destroy();
   }
    public final boolean isSurfaceAlive(){
        return surfaceAlive.get();
    }
    private final Runnable renderRunnable;
    @SuppressWarnings("InnerClassMayBeStatic")
    private final class MessageRunnable implements Runnable{
        private final Object offer=new Object();
        private final Object take=new Object();
        private final Object waitLock=new Object();
        private final LinkedList<Message> messagesQueue=new LinkedList<>();
        private final Map<Integer,Message> waitLists=new HashMap<>();
        @Override
        public void run() {
            String threadName=Thread.currentThread().getName();
            Thread.currentThread().setName(threadName.replace("[taskName]","handler"));
            while (!mainExecutorService.isShutdown()){
                handlerMessage(take());
            }
        }
        private void handlerMessage(@NonNull Message message) {
            LogUtil.logThreadD("BaseDrawableSurfaceView","handlerMessage");
            Message wMassage=waitLists.get(message.code);
            if (wMassage!=null){
               notifyMessage(wMassage);
            }
        }
        public final boolean post(Message message){
            int repeatCount =3;
            synchronized (offer){
                try {
                    boolean b = messagesQueue.offer(message);
                    while (!b&&(--repeatCount>0)){
                        offer.wait();
                        b=messagesQueue.offer(message);
                    }
                    if (!b){
                        throw new TimeoutException();
                    }
                    take.notify();
                    return true;
                }
                catch (InterruptedException |TimeoutException e) {
                    e.printStackTrace();
                }
                return false;
            }
        }
        public final Message take(){
            synchronized (take){
                LogUtil.logThreadD("BaseDrawableSurfaceView","take");
                if (messagesQueue.isEmpty()){
                    try {
                        LogUtil.logThreadD("BaseDrawableSurfaceView","wait take");
                        take.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Message message= messagesQueue.poll();
                offer.notify();
                return message;
            }
        }
        public final void waitCreate(){
            LogUtil.logThreadD("BaseDrawableSurfaceView","waitCreate");
            synchronized (waitLock){
                try {
                    waitLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        public final void  notifyCreate(){
            LogUtil.logThreadD("BaseDrawableSurfaceView","notifyCreate");
            synchronized (waitLock){
                waitLock.notify();
            }
        }
        public final void waitMessage(@NonNull Message message){
            waitLists.put(message.code,message);
            synchronized (message.code){
                try {
                    Message mMessage=waitLists.get(message.code);
                    message.code.wait();
                    assert mMessage != null;
                    message.message=mMessage.message;
                    message.date=mMessage.date;
                    waitLists.remove(message.code);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        public final void notifyMessage(@NonNull Message message){
            synchronized (message.code){
                    message.code.notify();
            }
        }
    }
    @SuppressWarnings("InnerClassMayBeStatic")
    private final class Message{
        final Integer code;
        String message;
        Serializable date;

        private Message(Integer code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }


        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Serializable getDate() {
            return date;
        }

        public void setDate(Serializable date) {
            this.date = date;
        }
    }
    public void setFrame(short frame) {
        this.frame = frame;
    }

    public FrameTracer getFrameTracer() {
        return frameTracer;
    }
}
