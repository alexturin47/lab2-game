package com.example.lab2_game;


import android.os.CountDownTimer;
import android.util.Log;

public class Bug {
    private static final int  SPEED =5;
    private float X;
    private float Y;
    private float dX;
    private float dY;
    private int angle;
    private int speed;
    private boolean lives;
    private byte img;


    public Bug(float x, float y, float dX, float dY, boolean lives, byte img) {
        X = x;
        Y = y;
        this.dX = dX;
        this.dY = dY;
        this.lives = lives;
        int m = SPEED;
        this.speed = (int) (Math.random() * 9) +2;
        this.angle = 0;
        this.img = img;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }

    public void setX(float x) {
        X = x;
    }

    public void setY(float y) {
        Y = y;
    }

    public void setdX(float dX) {
        this.dX = dX;
    }

    public void setdY(float dY) {
        this.dY = dY;
    }

    public void setLives(boolean lives) {
        this.lives = lives;
    }

    public void setImg(byte img) {
        this.img = img;
    }

    public int getAngle() {
        return angle;
    }

    public float getX() {
        return X;
    }

    public float getY() {
        return Y;
    }

    public float getdX() {
        return dX;
    }

    public float getdY() {
        return dY;
    }

    public boolean isLives() {
        return lives;
    }

    public int getSpeed() {
        return speed;
    }

    public byte getImg() {
        return this.img;
    }
}

class BugThread implements Runnable{

    Thread thrd;
    Bug bug;

    BugThread(Bug bug){
        thrd = new Thread(this);
        this.bug = bug;
    }

    public static BugThread createAndStart(Bug bug){
        BugThread bugThread = new BugThread(bug);

        bugThread.thrd.start();
        return bugThread;
    }


    @Override
    public void run() {
        int d=0;

        while(true){
            try {

                if(bug.isLives() == true) {
                    d = (int) rnd(8);

                    switch(d){
                        case 0: bug.setdY(-bug.getSpeed());
                            bug.setdX(0);
                            bug.setAngle(0);
                            break;
                        case 1: bug.setdX(bug.getSpeed());
                            bug.setdY(-bug.getSpeed());
                            bug.setAngle(45);
                            break;
                        case 2: bug.setdX(bug.getSpeed());
                            bug.setdY(0);
                            bug.setAngle(90);
                            break;
                        case 3: bug.setdX(bug.getSpeed());
                            bug.setdY(bug.getSpeed());
                            bug.setAngle(135);
                            break;
                        case 4: bug.setdX(0);
                            bug.setdY(bug.getSpeed());
                            bug.setAngle(180);
                            break;
                        case 5: bug.setdX(-bug.getSpeed());
                            bug.setdY(bug.getSpeed());
                            bug.setAngle(225);
                            break;
                        case 6: bug.setdX(-bug.getSpeed());
                            bug.setdY(0);
                            bug.setAngle(270);
                            break;
                        case 7: bug.setdX(-bug.getSpeed());
                            bug.setdY(-bug.getSpeed());
                            bug.setAngle(315);
                            break;
                    }
                    float X = bug.getX()+bug.getdX();
                    float Y = bug.getY()+bug.getdY();
                    bug.setX(X);
                    bug.setY(Y);

                    Thread.sleep(2000); //1000 - 1 сек
                } else {
                    Thread.sleep(4000);
                    bug.setLives(true);
                    bug.setX(-48);
                    bug.setY(-48);
                }


            } catch (InterruptedException ex) {
            }
        }


    }

    public static int rnd(int max){
        //int dp = max + Math.abs(min) + 1;
        //return  (int) (Math.random() * dp) - max;
        return (int) (Math.random() * ++max);
    }
}
