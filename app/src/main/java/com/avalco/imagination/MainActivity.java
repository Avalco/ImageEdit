package com.avalco.imagination;

import androidx.appcompat.app.AppCompatActivity;


import android.annotation.SuppressLint;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;

import android.view.View;
import android.view.ViewGroup;

import android.view.WindowManager;
import android.view.WindowMetrics;
import android.widget.FrameLayout;

import com.avalco.imagination.render.surface.DrawableArea;

import com.avalco.imagination.render.image.ImageEditSurfaceView;


import java.io.IOException;
import java.io.InputStream;

/**
 * @author Avalco
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ImageEditSurfaceView rootView;
    private int screenWidth;
    private int screenHeight;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(lp);
        }
        rootView=new ImageEditSurfaceView(this);
        FrameLayout.LayoutParams rootLayoutParams=new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        setContentView(rootView,rootLayoutParams);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowMetrics windowMetrics=getWindowManager().getCurrentWindowMetrics();
            screenHeight=windowMetrics.getBounds().height();
            screenWidth=windowMetrics.getBounds().width();
        }
        else {
            DisplayMetrics displayMetrics=new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            screenWidth=displayMetrics.widthPixels;
            screenHeight=displayMetrics.heightPixels;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return true;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    |View.SYSTEM_UI_FLAG_FULLSCREEN
                    |View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    |View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    |View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }
    Bitmap image=null;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onResume() {
        super.onResume();
        try {
            AssetManager assetManager=getAssets();
            InputStream inputStream =assetManager.open("test.png");
            image=BitmapFactory.decodeStream(inputStream).copy(Bitmap.Config.ARGB_8888,true);
            rootView.getDrawableAreas().setImage(Bitmap.createScaledBitmap(image,screenWidth,screenHeight,false));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (image==null){
            return;
        }
        Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setAntiAlias(true);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(8.25f);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        rootView.setOnTouchListener(new View.OnTouchListener() {
            Bitmap bitmap;
            Canvas canvas;
            RectF rectF;
            DrawableArea drawableArea;
            long offset=0;
            @SuppressWarnings("DuplicateBranchesInSwitch")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getActionMasked()){
                        case MotionEvent.ACTION_DOWN:
                            RectF rectF=new RectF(event.getX(),event.getY(),event.getX(),event.getY());
                            drawableArea =new DrawableArea(rectF,null) {
                                @Override
                                public void drawBackground(Canvas canvas) {
                                    if (getAreaF()==null){
                                        return;
                                    }
                                    paint.setPathEffect(new DashPathEffect(new float[]{30,20},(offset+=5)));
                                    canvas.drawRect(getAreaF(),paint);
                                }

                                @Override
                                public void drawFront(Canvas canvas) {

                                }

                                int offset=0;
                                @Override
                                protected void initPaint() {
                                    paint=new Paint(Paint.ANTI_ALIAS_FLAG);
                                    paint.setAntiAlias(true);
                                    paint.setColor(Color.RED);
                                    paint.setStyle(Paint.Style.STROKE);
                                    paint.setStrokeWidth(8.25f);
                                    paint.setStrokeCap(Paint.Cap.ROUND);
                                    paint.setStrokeJoin(Paint.Join.ROUND);
                                }
                            };
                            rootView.getDrawableAreas().addDrawableArea(drawableArea);
                            break;
                        case MotionEvent.ACTION_MOVE:
                            drawableArea.getAreaF().right=event.getX();
                            drawableArea.getAreaF().bottom=event.getY();
                            break;
                        case MotionEvent.ACTION_UP:
                            drawableArea.getAreaF().right=event.getX();
                            drawableArea.getAreaF().bottom=event.getY();
                            break;
                        default:break;
                    }
                    return true;
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        rootView.destroy();
    }
}