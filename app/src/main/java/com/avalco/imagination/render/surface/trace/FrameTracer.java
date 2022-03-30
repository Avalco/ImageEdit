package com.avalco.imagination.render.surface.trace;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Avalco
 */
public class FrameTracer implements Runnable{
    float avgDuration = 0.f;
    /**
     *  采样数设置为100
      */
    float alpha = 1.f / 100.f;
    int frameCount = 0;
    int fps;
    private final int maxCacheSize=100;
    private final AtomicBoolean tracing;
    ConcurrentLinkedDeque<Integer> cachePoints;
    private final ThreadFactory mainThreadFactory=new ThreadFactoryBuilder().setNameFormat("Tracer-fps-Thread-%d").build();
    private final ExecutorService executorService;
    public FrameTracer() {
        tracing =new AtomicBoolean(false);
        cachePoints=new ConcurrentLinkedDeque<>();
        fps=0;
        executorService=new ThreadPoolExecutor(1,1,0, TimeUnit.MILLISECONDS,new LinkedBlockingDeque<>(1),mainThreadFactory);
    }

    public void stopTrace() {
        this.tracing.set(false);
        cachePoints.clear();
        fps=0;
    }
    public void startTrace(){
        this.tracing.set(true);
        executorService.execute(this);
    }
    public void trace(int deltaTime){
        if (!tracing.get()){
            return;
        }
        cachePoints.offer(deltaTime);
    }
    private void traceFrame(int deltaTime){
        ++frameCount;
        if (1 == frameCount)
        {
            avgDuration = deltaTime;
        }
        else
        {
            avgDuration = avgDuration * (1 - alpha) + deltaTime * alpha;
        }
        fps = (int) (1.f / avgDuration * 1000);
    }
    public int getFps(){
        return fps;
    }

    @Override
    public void run() {
        while (tracing.get()&&!executorService.isShutdown()){
            if (!cachePoints.isEmpty()){
                Integer i=cachePoints.poll();
                if (i==null){
                    continue;
                }
                int deltaT=i;
                traceFrame(deltaT);
            }
        }
    }
    public void destroy(){
        executorService.shutdown();
    }
}
