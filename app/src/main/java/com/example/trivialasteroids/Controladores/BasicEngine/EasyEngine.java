package com.example.trivialasteroids.Controladores.BasicEngine;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.provider.CalendarContract;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.example.trivialasteroids.MainActivity;
import com.example.trivialasteroids.Modelos.GraphicObject;
import com.example.trivialasteroids.R;

import java.time.format.TextStyle;
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

    String frase = "Te viah reventah aweohano, muere iron man";

    private static int PERIODO_PROCESO = 60;
    private long ultimoProceso = 0;
    ArrayList<GraphicObject> asteroides = new ArrayList<>();
    ArrayList<GraphicObject> misiles = new ArrayList<>();
    ArrayList<GraphicObject> marcianos = new ArrayList<>();
    Random rand = new Random();
    Context context;
    Paint pincelTexto = new Paint();


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
        nave.setRadioColision((220 + 220) / 4);
        nave.setActivo(true);
        int nEnemys = 5;
        for (int i = 0; i < nEnemys; i++) {
            marciano1 = new GraphicObject(this, R.drawable.marciano02);
            marciano1.setAlto(90);
            marciano1.setAncho(120);
            marciano1.setRadioColision((90 + 120) / 4);
            marcianos.add(marciano1);
        }
        //Asteroides
        int nAsteroids = 15;
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
            if (id == R.drawable.asterioidpro) {
                asteroide1.setAncho(223);
                asteroide1.setAlto(140);
                asteroide1.setRadioColision((223 + 140) / 4);
            }
            asteroides.add(asteroide1);
        }

        for (int i = 0; i < asteroides.size(); i++) {
            posAsteroideY = rand.nextInt((int) (alto));
            posAsteroideX = rand.nextInt((int) (ancho /1.6));
            asteroides.get(i).setPos(-posAsteroideX, posAsteroideY);
        }

        //establecer las posiciones de los marcianos en el eje y sin que se salgan de la pantalla

        for (int marciano = 0; marciano < marcianos.size(); marciano++) {
            float posMarcianoX = rand.nextInt((int) (ancho / 1.2));
            float posMarcianoY = rand.nextInt((int) (alto / 1.6));
            marcianos.get(marciano).setPos(-posMarcianoX, posMarcianoY);

        }


        for (int i = 0; i < marcianos.size(); i++) {
//            if (i != 0) {
//                posAsteroideY = (alto / i + 1);// 0+1 1+1 2+1 3+1 4+1
//            } else {
//                posAsteroideY = (float) ((alto / 1.5) - marcianos.get(i).getAlto());
//            }

            posAsteroideY = alto / 2;
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
        //Texto que seguirá al asteroide

        pincelTexto.setTextSize(18);
        pincelTexto.setColor(Color.WHITE);
        pincelTexto.setTextSize(40);
        pincelTexto.setTextAlign(Paint.Align.CENTER);
        Typeface fuente = Typeface.create(String.valueOf(Typeface.BOLD), Typeface.NORMAL);
        pincelTexto.setTypeface(fuente);
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
        if (nave.isActivo()) {
            nave.DrawGraphic(lienzo);
        }
        for (GraphicObject asteroide : asteroides) {
            asteroide.DrawGraphic(lienzo);
        }
        for (GraphicObject marciano : marcianos) {
            marciano.DrawGraphic(lienzo);
            lienzo.drawText(frase, (float) marciano.getPosX(), (float) marciano.getPosY(), pincelTexto);
        }
        for (int misil = 0 ; misil < misiles.size(); misil++) {

            GraphicObject misilActual = misiles.get(misil);
            if (misilActual.isActivo()) {
                misilActual.DrawGraphic(lienzo);
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
        if (ultimoProceso + PERIODO_PROCESO > ahora) {
            return;
        }
        double retardo = (ahora - ultimoProceso) / PERIODO_PROCESO;
        ultimoProceso = ahora;
        int velocidad = rand.nextInt(25);

        for (GraphicObject asteroide : asteroides) {
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
                        if (((MainActivity) context).getVidas() == 0) {
                            ((MainActivity) context).activaGameOver();
                            nave.setActivo(false);
                        }
                        destruyendoAsteroide(asteroid, -1, -1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        velocidad = rand.nextInt(10);
        for (int marciano = 0; marciano < marcianos.size(); marciano++) {
            GraphicObject marcianoActual = marcianos.get(marciano);
            marcianoActual.setIncX(-velocidad);
            marcianoActual.incrementaPos(retardo);
            //hacer que los marcianos no se toquen entre sí
            if (nave.verificaColision(marcianoActual)) {
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
                        if (((MainActivity) context).getVidas() == 0) {
                            nave.setActivo(false);
                            ((MainActivity)context).activaGameOver();
                        }
                        destruyendoAsteroide(-1, -1, marciano);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            //Evitar que dos o mas marcianos se choquen entre ellos
            for (int marciano2 = marciano + 1; marciano2 < marcianos.size(); marciano2++) {
                GraphicObject marcianoActual2 = marcianos.get(marciano2);
                if (marcianoActual.verificaColision(marcianoActual2)) {
                    marcianoActual.setPos(marcianoActual.getPosX() , marcianoActual.getPosY()+ 10);
                    marcianoActual2.setPos(marcianoActual2.getPosX() , marcianoActual2.getPosY()-10);
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
                        //si un misil toca a un marciano lo destruye
                        try {
                            if (misiles.get(misil).verificaColision(asteroides.get(asteroid))) {
                                destruyendoAsteroide(asteroid, misil, -1);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    for (int marciano = 0; marciano < marcianos.size(); marciano++) {
                        try {
                            if (misiles.get(misil).verificaColision(marcianos.get(marciano))) {
                                destruyendoAsteroide(-1, misil, marciano);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    private synchronized void destruyendoAsteroide(int asteroid, int misil, int marciano) {
        if (asteroid != -1) {
            asteroides.remove(asteroides.get(asteroid));
        }
        if (marciano != -1) {
            marcianos.remove(marciano);
        }
//      puntuacion+=1000;
        if (misil != -1) {
            misiles.get(misil).setActivo(false);
            misiles.remove(misil);
        }
    }

    public void Dispara() {
        if (misiles.size() < 5) {
            GraphicObject misil = new GraphicObject(this, R.drawable.misil1);
            misil.setPos(nave.getPosX() + nave.getAncho() / 2 - misil.getAncho() / 2, nave.getPosY() + nave.getAlto() / 2 - misil.getAlto() / 2);
            misil.setAngulo(nave.getAngulo());
            misil.setIncX(Math.cos(Math.toRadians(misil.getAngulo())) * PASO_VELOCIDAD_MISIL);
            misil.setIncY(Math.sin(Math.toRadians(misil.getAngulo())) * PASO_VELOCIDAD_MISIL);
            misil.setActivo(true);
            misil.setTiempoEnPantalla((int) Math.min(ancho / Math.abs(misil.getIncX()), alto / Math.abs(misil.getIncY())) - 2);
            misiles.add(misil);
        }
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
        misiles.clear();
    }
}