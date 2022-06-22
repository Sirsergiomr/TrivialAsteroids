package com.example.trivialasteroids.Controladores.BasicEngine.Deprecated;

/**
 * @author Sirse
 * Este hilo es el que refresca la view del juego mientras running = TRUE
 */
@Deprecated
public class GameLoopThread extends Thread {
    private final EasyEngine view;
    private boolean running = false;
    private boolean pause = false;
    public GameLoopThread(EasyEngine view) {
        this.view = view;
    }
    public void setRunning(boolean run) {
        running = run;
    }
    @Override
    public void run() {
        while (running) {

          synchronized (this){
              while (pause){
                    System.out.println("IN PAUSE");
                    try {
                        this.wait();

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
              }
              view.updatePhysics(pause);
            }
        }
    }

    public void pausar() {
        pause=true;
    }

    public synchronized void reanudar() {
        pause=false;
        this.notify();
    }
}
