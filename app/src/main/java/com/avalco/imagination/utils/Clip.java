package com.avalco.imagination.utils;

public class Clip {
    //draw rect
/*     rootView.setOnTouchListener(new View.OnTouchListener() {
        DrawableArea drawableArea;
        @SuppressWarnings("DuplicateBranchesInSwitch")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getActionMasked()){
                case MotionEvent.ACTION_DOWN:
                    RectF rectF=new RectF(event.getX(),event.getY(),event.getX(),event.getY());
                    drawableArea =new DrawableArea(rectF,null) {
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
                        @Override
                        protected void drawArea(Canvas canvas) {
                            if (getAreaF()==null){
                                return;
                            }
                            paint.setPathEffect(new DashPathEffect(new float[]{30,20},(offset+=5)));
                            canvas.drawRect(getAreaF(),paint);
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
    });*/
   /* public void offerBitmap(Bitmap bitmap) {
        LogUtil.logThreadD("offerBitmap",""+bitmap);
        if (cacheSize.get()<capacity){
            bitmaps.offer(bitmap);
            cacheSize.getAndIncrement();
            return;
        }
        if (cacheQueueSize.get()>=capacity){
            Bitmap bitmap1=bitmapCacheQueue.poll();
            cacheQueueSize.getAndDecrement();
            LogUtil.logThreadD("offerBitmap","bitmapCacheQueue has fulled skip :"+bitmap1);
        }
        bitmapCacheQueue.offer(bitmap);
        cacheQueueSize.getAndIncrement();
    }*/
    /*Bitmap image=null;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onResume() {
        super.onResume();
        try {
            AssetManager assetManager=getAssets();
            InputStream inputStream =assetManager.open("test.png");
            image= BitmapFactory.decodeStream(inputStream).copy(Bitmap.Config.ARGB_8888,true);
            rootView.offerBitmap(image);
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
            long offset=0;
            @SuppressWarnings("DuplicateBranchesInSwitch")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getActionMasked()){
                    case MotionEvent.ACTION_DOWN:
                        LogUtil.logThreadD("ACTION_DOWN","start");
                        rectF=new RectF(event.getX(),event.getY(),event.getX(),event.getY());
                        bitmap= image.copy(Bitmap.Config.ARGB_8888,true);
                        canvas=new Canvas(bitmap);
                        paint.setPathEffect(new DashPathEffect(new float[]{30,20},(offset+=5)));
                        canvas.drawRect(rectF,paint);
                        rootView.offerBitmap(bitmap);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        rectF.bottom=event.getY();
                        rectF.right=event.getX();
                        bitmap= image.copy(Bitmap.Config.ARGB_8888,true);
                        canvas=new Canvas(bitmap);
                        paint.setPathEffect(new DashPathEffect(new float[]{30,20},(offset+=5)));
                        canvas.drawRect(rectF,paint);
                        rootView.offerBitmap(bitmap);
                        break;
                    case MotionEvent.ACTION_UP:
                        rectF.bottom=event.getY();
                        rectF.right=event.getX();
                        bitmap= image.copy(Bitmap.Config.ARGB_8888,true);
                        canvas=new Canvas(bitmap);
                        paint.setPathEffect(new DashPathEffect(new float[]{30,20},(offset+=5)));
                        canvas.drawRect(rectF,paint);
                        rootView.offerBitmap(bitmap);
                        LogUtil.logThreadD("ACTION_UP","end");
                        image=bitmap.copy(Bitmap.Config.ARGB_8888,true);
                        break;
                    default:break;
                }
                return true;
            }
        });
    }*/
}
