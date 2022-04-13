package com.example.trivialasteroids.Controladores.BasicEngine;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.provider.CalendarContract;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.trivialasteroids.MainActivity;
import com.example.trivialasteroids.Modelos.GraphicObject;
import com.example.trivialasteroids.R;

import java.util.ArrayList;
import java.util.Random;

/**
 * @author Sirse
 */
public class EasyEngine extends SurfaceView {
    float alto, ancho, posX = 10, posY = 10, posAsteroideX = ancho, posAsteroideY = alto / 4;
    GameLoopThread gameLoopThread = new GameLoopThread(this);
    GraphicObject nave;
    GraphicObject marciano1;

    private static int PASO_VELOCIDAD_MISIL = 60;
    GraphicObject asteroide1;

    String frase = "Los cerdos vuelan  y las moscan también :D";

    private static int PERIODO_PROCESO = 60;
    private long ultimoProceso=0;
    ArrayList<GraphicObject> asteroides = new ArrayList<>();
    ArrayList<GraphicObject> misiles = new ArrayList<>();
    ArrayList<GraphicObject> marcianos = new ArrayList<>();
    Random rand = new Random();
    Context context;

    public EasyEngine(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context = context;
        DisplayMetrics pantalla = context.getResources().getDisplayMetrics();
        ancho = pantalla.widthPixels;
        alto = pantalla.heightPixels;

        //SpaceShip
        nave = new GraphicObject(this, R.drawable.sprite_space_ship);
        nave.setAlto(220);
        nave.setAncho(220);
        nave.setRadioColision((220+220)/4);
        nave.setActivo(true);
        int nEnemys = 5;
        for (int i = 0; i < nEnemys; i++) {
            marciano1 = new GraphicObject(this, R.drawable.marciano02);
            marciano1.setAlto(90);
            marciano1.setAncho(120);
            marcianos.add(marciano1);
        }
        //Asteroides
        //Todo Si te chocas con uno mueres
        // las frases son naves espaciales o marcianos.
        int nAsteroids = 14;
        for (int i = 0; i < nAsteroids; i++) {
            int id;
            switch (rand.nextInt(3)) {
                case 0:
                    id = R.drawable.asterioidpro;
                    break;
                case 1:
                    id = R.drawable.asteroide2;
                    break;
                case 2:
                    id = R.drawable.asteroide3;
                    break;
                default:
                    id = R.drawable.asteroide2;
            }
            asteroide1 = new GraphicObject(this, id);
            if(id == R.drawable.asterioidpro){
                asteroide1.setAncho(223);
                asteroide1.setAlto(140);
                asteroide1.setRadioColision((223+140)/4);
            }
            asteroides.add(asteroide1);
        }

        for (int i = 0; i < asteroides.size(); i++) {
            posAsteroideY = rand.nextInt((int) (alto / 1.6));
            posAsteroideX = rand.nextInt((int) (ancho / 1.2));
            asteroides.get(i).setPos(-posAsteroideX, posAsteroideY);
        }

        //marcianos
        for (int i = 0; i < marcianos.size(); i++) {
            posAsteroideY = rand.nextInt((int) (alto / 1.7));
            posAsteroideX = rand.nextInt((int) (ancho / 2) - 2);
            marcianos.get(i).setPos(-posAsteroideX, posAsteroideY);
        }

        getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                gameLoopThread.setRunning(true);
                gameLoopThread.start();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
//                Functions.Destroy(gameLoopThread); //Para parar el juego
            }
        });

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX(); // or getRawX();
        float y = event.getY();
        //Control vertical de la nave
        if (x >= posX && x < (posX + nave.getAncho())) {
            posY = y;
        }
        return true;
    }

    @Override
    public synchronized void onDraw(Canvas lienzo) {
        //aqui solamente dibujas
        lienzo.drawColor(Color.BLACK);
        if(nave.isActivo()){
            nave.DrawGraphic(lienzo);
        }
        for (GraphicObject asteroide : asteroides) {
            asteroide.DrawGraphic(lienzo);
        }
        for (GraphicObject marciano : marcianos) {
            marciano.DrawGraphic(lienzo);
        }
        for (GraphicObject misil : misiles) {
            if (misil.isActivo()) {
                misil.DrawGraphic(lienzo);
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }
    public void updatePhysics() {
        long ahora = System.currentTimeMillis();
        // No hagas nada si el período de proceso no se ha cumplido.
        if(ultimoProceso + PERIODO_PROCESO > ahora) {
            return;
        }
        double retardo = (ahora - ultimoProceso) / PERIODO_PROCESO;
        ultimoProceso = ahora;
        int velocidad = rand.nextInt(25);

        for (GraphicObject asteroide :  asteroides) {
            asteroide.setIncX(-velocidad);
            asteroide.incrementaPos(retardo);
        }
        nave.setPos(posX, posY);
        Random rand = new Random();


        for (int asteroid = 0; asteroid < asteroides.size(); asteroid++) {
            GraphicObject asteroideActual = asteroides.get(asteroid);
            if (nave.verificaColision(asteroideActual)) {
                if (context instanceof MainActivity) {
                    try {
                        ((MainActivity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ((MainActivity) context).bajarVida();
                            }
                        });
                        if (((MainActivity) context).getVidas() > 0) {
                            posY = alto / 2;
                            nave.setPos(posX, posY);
                        }
                        if(((MainActivity) context).getVidas() == 0){
                            ((MainActivity) context).activaGameOver();
                            nave.setActivo(false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }finally {
                        destruyendoAsteroide(asteroid, -1);
                    }
                }
            }
        }
        velocidad = rand.nextInt(20);
        for (int marciano = 0; marciano < marcianos.size(); marciano++) {
            GraphicObject marcianoActual = marcianos.get(marciano);
            marcianoActual.setIncX(-velocidad);
            marcianoActual.incrementaPos(retardo);
            if (marciano < marcianos.size() && marciano > 0) {
                GraphicObject marcianoVecino = marcianos.get(marciano - 1);
                if (marcianoActual.verificaColision(marcianoVecino)) {
                    marcianoActual.setPosY((int) (marcianoVecino.getPosY() * 2));
                } else {
                    if (marcianoActual.getPosY() >= alto) {
                        marcianoActual.setPosY((int) (marcianoActual.getPosY() - marcianoActual.getAlto()));
                    } else if (marcianoActual.getPosY() <= 0) {
                        marcianoActual.setPosY((int) (marcianoActual.getPosY() + marcianoActual.getAlto()));
                    }
                }
            }
        }

        for (int misil = 0; misil < misiles.size(); misil++) {
            if (misiles.get(misil).isActivo()) {
                misiles.get(misil).incrementaPos(retardo);
                int tmp = misiles.get(misil).getTiempoEnPantalla();
                misiles.get(misil).setTiempoEnPantalla(tmp - 1);
                if (misiles.get(misil).getTiempoEnPantalla() < 0) {
                    misiles.get(misil).setActivo(false);
                    misiles.remove(misiles.get(misil));
                } else {
                    for (int asteroid = 0; asteroid < asteroides.size(); asteroid++) {
                        if (misiles.get(misil).verificaColision(asteroides.get(asteroid))) {
                            destruyendoAsteroide(asteroid, misil);
                            break;
                        }
                    }
                }
            }
        }
    }

    private synchronized void destruyendoAsteroide(int asteroid, int misil) {
        asteroides.remove(asteroid);
//      puntuacion+=1000;
        if(misil != -1){
            misiles.get(misil).setActivo(false);
            misiles.remove(misil);
        }
    }

    public void Dispara() {
        if (misiles.size() < 5 ) {
            GraphicObject misil = new GraphicObject(this, R.drawable.misil1);
            misil.setPos(nave.getPosX() + nave.getAncho() / 2 - misil.getAncho() / 2, nave.getPosY() + nave.getAlto() / 2 - misil.getAlto() / 2);
            misil.setAngulo(nave.getAngulo());
            misil.setIncX(Math.cos(Math.toRadians(misil.getAngulo())) * PASO_VELOCIDAD_MISIL);
            misil.setIncY(Math.sin(Math.toRadians(misil.getAngulo())) * PASO_VELOCIDAD_MISIL);
            misil.setActivo(true);
            misil.setTiempoEnPantalla((int) Math.min(ancho / Math.abs(misil.getIncX()), alto / Math.abs(misil.getIncY()))-2);
            misiles.add(misil);
        }
    }
}