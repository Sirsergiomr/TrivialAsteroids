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
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

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
    GraphicObject asteroide1;
    GraphicObject asteroide2;
    GraphicObject asteroide3;
    String frase = "Los cerdos vuelan  y las moscan también :D";
    Paint pincel = new Paint();
    ArrayList<GraphicObject> asteroides = new ArrayList<>();

    public EasyEngine(Context context) {
        super(context);


        DisplayMetrics pantalla = context.getResources().getDisplayMetrics();
        ancho = pantalla.widthPixels;
        alto = pantalla.heightPixels;
        //SpaceShip

        nave = new GraphicObject(this, R.drawable.nave);

        asteroide1 = new GraphicObject(this, R.drawable.asteroide2);
        asteroide2 = new GraphicObject(this, R.drawable.asteroide2);
        asteroide3 = new GraphicObject(this, R.drawable.asteroide2);
        asteroides.add(asteroide1);
        asteroides.add(asteroide2);
        asteroides.add(asteroide3);


        //nº de asteroides
        Random rand = new Random();
        for (int i = 0; i < asteroides.size(); i++) {
            posAsteroideY = rand.nextInt((int) (alto / 4));
            asteroides.get(i).setPos(posAsteroideX, posAsteroideY);

        }

        getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                gameLoopThread.setRunning(true);
                gameLoopThread.start();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format,
                                       int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                Functions.Destroy(gameLoopThread);
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
    public void onDraw(Canvas lienzo) {
        //aqui solamente dibujas
        lienzo.drawColor(Color.BLACK);
        nave.DrawGraphic(lienzo);
        for (int i = 0; i < asteroides.size(); i++) {
            asteroides.get(i).DrawGraphic(lienzo);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

    }

    public void updatePhysics() {
        nave.setPos(posX, posY);
        System.out.println("Udate physics");
        Random rand = new Random();
        for (int i = 0; i < asteroides.size(); i++) {
            int velocidad = rand.nextInt(9);
            asteroides.get(i).setIncX(-velocidad);
            asteroides.get(i).incrementaPos(0.1);
        }

    }
}


