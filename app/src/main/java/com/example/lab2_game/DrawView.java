package com.example.lab2_game;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

public class DrawView extends SurfaceView implements SurfaceHolder.Callback {

    private DrawThread drawThread;
    private Bitmap bitmap;
    private Matrix matrix;

    public DrawView(Context context) {
        super(context);
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {

        drawThread = new DrawThread(getHolder(), getResources());
        drawThread.setRunning(true);
        drawThread.start();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

        boolean retry = true;

        drawThread.setRunning(false);
        while(retry){
            try{
                drawThread.join();
                retry = false;
            } catch (InterruptedException e) {
               // e.printStackTrace();
            }
        }

    }

    public void Close(){
        //SurfaceHolder holder = getHolder();
        surfaceDestroyed(getHolder());
    }


    class DrawThread extends Thread{

        private boolean runFlag = false;
        private SurfaceHolder surfaceHolder;
        private long prevTime;

        public DrawThread(SurfaceHolder surfaceHolder, Resources resources){

            this.surfaceHolder = surfaceHolder;

            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bug);

            matrix = new Matrix();
            matrix.postRotate(45.0f);
            matrix.postTranslate(20.0f, 50.0f);
            prevTime = System.currentTimeMillis();

        }

        public void setRunning(boolean run){
            runFlag = run;
        }

        @Override
        public void run() {
            Canvas canvas;

            while(runFlag){

                long now = System.currentTimeMillis();
                long elapsedTime = now - prevTime;

                if(elapsedTime < 30){
                    prevTime = now;
                    matrix.preRotate(2.0f,bitmap.getWidth()/2, bitmap.getHeight()/2);
                }

                canvas = null;
                try{
                    canvas = surfaceHolder.lockCanvas(null);
                    synchronized (surfaceHolder){
                        canvas.drawColor(Color.GREEN);
                        canvas.drawBitmap(bitmap, matrix, null);
                    }
                }

                finally {
                    if(canvas != null){
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }
    }

}


