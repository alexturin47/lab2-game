package com.example.lab2_game;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class DrawActivity extends AppCompatActivity {

    private DrawView drawView;
    public static int WIDTH_DEVICE;
    public static int HEIGTH_DEVICE;
    private Bug bug;
    private static List<Bug> bugs;
    private int bug_count;
    public int score = 0;
    private static final int TIME = 120;
    int time = 0;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        drawView = new DrawView(this);
        setContentView(drawView);

        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics metricsB = new DisplayMetrics();
        display.getMetrics(metricsB);
        WIDTH_DEVICE = metricsB.widthPixels;
        HEIGTH_DEVICE = metricsB.heightPixels;

        startService(new Intent(getApplicationContext(), MusicService.class));

        Intent intent = getIntent();

        bug_count = intent.getIntExtra("bugs", 10);

        bugs = new ArrayList<>();
        BugThread bugThread;
        for(int i=0; i< bug_count; i++){
            int img = (int) (Math.random() * 2);
            float X = (float) Math.random() * WIDTH_DEVICE;
            float Y = (float) Math.random() * HEIGTH_DEVICE;
            bug = new Bug(X,Y, 0, 0,true, (byte)img);
            bugs.add(bug);
            bugThread = BugThread.createAndStart(bug);
        }

        time = TIME;

        new CountDownTimer(TIME *1000, 1000){

            @Override
            public void onTick(long millisUntilFinished) {
                --time;
            }

            @Override
            public void onFinish() {
                Intent intent = new Intent();
                intent.putExtra("SCORE", score);
                setResult(RESULT_OK, intent);
                onBackPressed();
            }
        }.start();


    }


    @Override
    protected void onPause() {
        stopService(new Intent(this, MusicService.class));
        super.onPause();
    }

    @Override
    protected void onResume() {
        startService(new Intent(getApplicationContext(), MusicService.class));
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        drawView.Close();
        stopService(new Intent(this, MusicService.class));
        super.onBackPressed();
    }


    public class DrawView extends SurfaceView implements SurfaceHolder.Callback {

        private DrawThread drawThread;
        private Bitmap bitmap;
        private Matrix matrix;
        private Paint paint;
        private SoundPool sounds;
        private int crack;

        @SuppressLint("ClickableViewAccessibility")
        public DrawView(Context context) {
            super(context);

            // установим обработчик касаний
            this.setOnTouchListener((v, event) -> {

                for(Bug bug: bugs){
                    int X = (int )event.getX();
                    int Y = (int) event.getY();

                    if( (bug.getX()+24 >= X && bug.getX()+24 <= X+bitmap.getWidth()) && (bug.getY()+24 >= Y && bug.getY()+24 <= Y+bitmap.getHeight())) {
                        bug.setLives(false);
                        sounds.play(crack, 1.0f, 1.0f, 0, 0, 1.5f);
                        bug.setdX(0);
                        bug.setdY(0);
                        score += bug.getSpeed();
                    }
                }
                return false;
            });


            paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            matrix = new Matrix();

            sounds = new SoundPool(10, AudioManager.STREAM_MUSIC,0);
            crack = sounds.load(context, R.raw.cracks_1, 1);

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
                }
            }

        }

        public void Close(){
            surfaceDestroyed(getHolder());
        }


        class DrawThread extends Thread{

            private boolean runFlag = false;
            private SurfaceHolder surfaceHolder;


            public DrawThread(SurfaceHolder surfaceHolder, Resources resources){
                this.surfaceHolder = surfaceHolder;
                matrix = new Matrix();
            }

            public void setRunning(boolean run){
                runFlag = run;
            }

            @Override
            public void run() {
                Canvas canvas;

                while(runFlag && time != 0){

                    canvas = null;
                    try{
                        canvas = surfaceHolder.lockCanvas(null);
                        if (canvas == null)
                            continue;

                            synchronized (surfaceHolder){

                                canvas.drawColor(Color.GREEN);

                                for (Bug bug: bugs ) {
                                    bug.setX(bug.getX()+bug.getdX());
                                    bug.setY(bug.getY()+ bug.getdY());


                                    // выбираем нужный спрайт

                                    if(bug.isLives()){
                                        switch (bug.getImg()) {
                                            case 0: bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.bug);
                                                break;
                                            case 1: bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.bee);
                                                break;
                                        }

                                        matrix.reset();
                                        matrix.postTranslate(-bitmap.getWidth() / 2, -bitmap.getHeight() / 2); // Центрируем матрицу по спрайту
                                        matrix.postRotate(bug.getAngle()); // поворачиваем матрицу спрайта по направлению движения
                                        onScreenOut(bug); // проверяем на выход за границы экрана

                                        matrix.postTranslate(bug.getX(), bug.getY());  // смещаем матрицу спрайта на новые координаты


                                        canvas.drawBitmap(bitmap, matrix, paint); // рисуем спрайт с применной матрицей трансформаций

                                    } else {

                                        switch (bug.getImg()) {
                                            case 0: bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bug_dead);
                                                break;
                                            case 1: bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bee_dead);
                                                break;
                                        }

                                        matrix.reset();
                                        matrix.postTranslate(-bitmap.getWidth() / 2, -bitmap.getHeight() / 2); // Центрируем матрицу по спрайту
                                        matrix.postRotate(bug.getAngle()); // поворачиваем матрицу спрайта по направлению движения
                                        matrix.postTranslate(bug.getX(), bug.getY());  // смещаем матрицу спрайта на новые координаты
                                        canvas.drawBitmap(bitmap, matrix, paint); // рисуем спрайт с применной матрицей трансформаций

                                    }
                                }

                                paint.setColor(Color.RED);
                                paint.setTextSize(64);
                                canvas.drawText("Очки: " + score,12,60, paint);

                                paint.setColor(Color.BLUE);
                                paint.setTextSize(64);
                                canvas.drawText("Время: " + timeFormat(time),WIDTH_DEVICE - 360,60, paint);
                            }

                    }

                    finally {
                        if(canvas != null){
                            surfaceHolder.unlockCanvasAndPost(canvas);
                        }
                    }
                }
            }



            private String timeFormat(int time) {
                int s = time % 60;
                int m = time / 60;
                String sm;
                if (s < 10) {
                    sm = "0"+s;
                }else sm = ""+s;
                return ""+m+":"+sm;
            }

            private void onScreenOut(Bug bug) {
                float X = bug.getX();
                float Y = bug.getY();
                if (X + 24 < 0 ) X = WIDTH_DEVICE -24 ;
                if (X > WIDTH_DEVICE) X = 0;
                if (Y + 24 < 0) Y = HEIGTH_DEVICE - 24;
                if (Y > HEIGTH_DEVICE) Y = 0;
                bug.setX(X);
                bug.setY(Y);
            }
        }

    }

}
