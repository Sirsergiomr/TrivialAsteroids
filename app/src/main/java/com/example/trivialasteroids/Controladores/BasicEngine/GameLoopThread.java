package com.example.trivialasteroids.Controladores.BasicEngine;

import android.annotation.SuppressLint;
import android.graphics.Canvas;

/**
 * @author Sirse
 * Este hilo es el que refresca la view del juego mientras running = TRUE
 */

public class GameLoopThread extends Thread {
    static final long FPS = 60;
    private final EasyEngine view;
    private boolean running = false;

    public GameLoopThread(EasyEngine view) {
        this.view = view;
    }
    public void setRunning(boolean run) {
        running = run;
    }
    @SuppressLint("WrongCall")
    @Override
    public void run() {
        long ticksPS = 1000 / FPS;
        long startTime;
        long sleepTime;
        while (running) {
            view.updatePhysics();
            Canvas c = null;
            startTime = System.currentTimeMillis();
            try {
                c = view.getHolder().lockCanvas();
                if(c == null){
                    System.out.println("C es NULO C ES NULO ");
                }

                synchronized (view.getHolder()) {

                    view.onDraw(c);
                }
            } finally {
                if (c != null) {
                    view.getHolder().unlockCanvasAndPost(c);
                }
            }
            sleepTime = ticksPS - (System.currentTimeMillis() - startTime);
            try {
                if (sleepTime > 0)
                    sleep(sleepTime);
                else
                    sleep(20);
            } catch (Exception ignored) {}
        }
    }
}
