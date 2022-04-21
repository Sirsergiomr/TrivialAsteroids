package com.example.trivialasteroids.Controladores.BasicEngine;

import android.annotation.SuppressLint;
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

import com.example.trivialasteroids.Juego;
import com.example.trivialasteroids.MainActivity;
import com.example.trivialasteroids.Modelos.GraphicObject;
import com.example.trivialasteroids.Modelos.Respuesta;
import com.example.trivialasteroids.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import static java.lang.Thread.sleep;

/**
 * @author Sirse
 */
public class EasyEngine extends SurfaceView {
    ThreadPoolExecutor threadPoolAsteroides =  (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
    float alto, ancho, posX = 10, posY = 10, posAsteroideX = ancho, posAsteroideY = alto / 4;
    GameLoopThread gameLoopThread = new GameLoopThread(this);
    GraphicObject nave;
    GraphicObject marciano1;
    Random random = new Random();
    private static int PASO_VELOCIDAD_MISIL = 1;
    GraphicObject asteroide1;
    int puntuacion = 0;
   //  String[] respuestas= {"Madrid", "Lisboa", "Paris", "Dublín", "Tokio", "Buenos Aires", "Washington", "Ottawa"};
    ArrayList<Respuesta> respuestas = new ArrayList<>();
    static final long FPS = 60;
    long ticksPS = 1000 / FPS;
    long sleepTime;
    int nAsteroids=2;
    private long ultimoProceso = 0;
    ArrayList<GraphicObject> asteroides = new ArrayList<>();
    ArrayList<GraphicObject> misiles = new ArrayList<>();
    ArrayList<GraphicObject> marcianos = new ArrayList<>();

    Random rand = new Random();
    Context context;

    Paint pincelTexto = new Paint();
    JSONObject json2, json;
    boolean pausa = false;

    public EasyEngine(Context context, AttributeSet attrs) {
        super(context, attrs);
        try {
            json2 = new JSONObject("{'result': 'ok', 'message': 'Datos de nivel', 'datos':{ 'pregunta':'Sanciones entre 5000 y 6000€', 'respuestas':[{'contenido': 'Norma X', 'valida': True},{'contenido': 'Norma Y', 'valida': False}]}}");
            json = json2.getJSONObject("datos");
            JSONArray lista = json.getJSONArray("respuestas");
            for(int i = 0; i<lista.length(); i++){
                JSONObject obj = lista.getJSONObject(i);
                String contenido = obj.getString("contenido");
                boolean valido = obj.getBoolean("valida");
                respuestas.add(new Respuesta(contenido , valido));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.context = context;
        DisplayMetrics pantalla = context.getResources().getDisplayMetrics();
        ancho = pantalla.widthPixels;
        alto = pantalla.heightPixels;
        posX = 10;
        posY = alto/2;
        //SpaceShip
        nave = new GraphicObject(this, R.drawable.sprite_space_ship);
        nave.setAlto(220);
        nave.setAncho(220);
        nave.setRadioColision((220 + 220) / 4);
        nave.setActivo(true);
        generaEnemigos();
        getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                    gameLoopThread.setRunning(true);
                try {
                    gameLoopThread.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
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

    private void generaEnemigos() {
        int nEnemys = 0;
        for (int i = 0; i < nEnemys; i++) {
            marciano1 = new GraphicObject(this, R.drawable.marciano02);
            marciano1.setAlto(90);
            marciano1.setAncho(120);
            marciano1.setRadioColision((90 + 120) / 4);

            String frase = respuestas.get(random.nextInt(respuestas.size())).getRespuestaAsociada();
            boolean validez =respuestas.get(random.nextInt(respuestas.size())).getVerdadero();

            marciano1.setRespuestaAsociada(frase);
            marciano1.setVerdadero(validez);
            marcianos.add(marciano1);

        }
        threadPoolAsteroides.execute(new HebraAsteroides());
                //Asteroides
//        nAsteroids = 5;
//        for (int i = 0; i < nAsteroids; i++) {
//            int id;
//            switch (rand.nextInt(3)) {
//                case 0:
//                    id = R.drawable.asterioidpro;
//                    break;
//                case 1:
//                    id = R.drawable.asteroide2;
//                    break;
//                case 2:
//                    id = R.drawable.asteroide3;
//                    break;
//                default:
//                    id = R.drawable.asteroide2;
//            }
//            asteroide1 = new GraphicObject(this, id);
//            if (id == R.drawable.asterioidpro) {
//                asteroide1.setAncho(223);
//                asteroide1.setAlto(140);
//                asteroide1.setRadioColision((223 + 140) / 4);
//            }
//            asteroides.add(asteroide1);
//        }

//        for (int i = 0; i < asteroides.size(); i++) {
//            posAsteroideY = rand.nextInt((int) (alto));//rand.nextInt((int) (ancho /4)
//            posAsteroideX = ancho+200;
//            asteroides.get(i).setPos(posAsteroideX, posAsteroideY);
//        }

        //establecer las posiciones de los marcianos en el eje y sin que se salgan de la pantalla

        for (int marciano = 0; marciano < marcianos.size(); marciano++) {
            float posMarcianoX = (int) (ancho /2)-2;
            float posMarcianoY = rand.nextInt((int) (alto / 1.6));
            marcianos.get(marciano).setPos(-posMarcianoX, posMarcianoY);
        }

        for (int i = 0; i < marcianos.size(); i++) {
            posAsteroideY = alto / 2;
            posAsteroideX = -rand.nextInt((int) (ancho / 2) - 2);
            marcianos.get(i).setPos(posAsteroideX, posAsteroideY);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX(); // or getRawX();
        float y = event.getY();
        //Control vertical de la nave

        if (x >= posX && x < (posX + nave.getAncho()) && nave.isActivo()) {
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
        }else{
            posY = -10;
        }
        for (int a = 0 ; a < asteroides.size(); a++) {
            GraphicObject asteroide = asteroides.get(a);
            asteroide.DrawGraphic(lienzo);
        }
        for (int m= 0 ; m < marcianos.size(); m++) {
            GraphicObject marciano = marcianos.get(m);
            marciano.DrawGraphic(lienzo);
            lienzo.drawText(marciano.getRespuestaAsociada(), (float) marciano.getPosX(), (float) marciano.getPosY(), pincelTexto);
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

    @SuppressLint("WrongCall")
    void controlThreat() {
        Canvas c = null;
        long startTime = System.currentTimeMillis();
        try {
            c = this.getHolder().lockCanvas();
            synchronized (this.getHolder()) {
                if(c == null){
                    System.out.println("C es NULO C ES NULO ");
                }else{
                    this.onDraw(c);
                }
            }
        } finally {
            if (c != null) {
                this.getHolder().unlockCanvasAndPost(c);
            }
        }

        sleepTime = ticksPS - (System.currentTimeMillis() - startTime);
        try {
            if (sleepTime > 0)
                synchronized (gameLoopThread){
                    gameLoopThread.sleep(sleepTime);
                }
            else
                synchronized (gameLoopThread){
                    gameLoopThread.sleep(20);
                }
        } catch (Exception ignored) {}
        System.out.println("Ticks: " + (System.currentTimeMillis() - startTime));
        System.out.println("SLEEPTIME = "+sleepTime);
    }

    public void updatePhysics(boolean pausa) {
        long ahora = System.currentTimeMillis();
        // No hagas nada si el período de proceso no se ha cumplido.
        if (ultimoProceso + ticksPS > ahora) {
            return;
        }
        controlThreat();
        double retardo = ultimoProceso;
        if(!pausa){
            retardo = sleepTime;
        }

        ultimoProceso = ahora;
        int velocidad = rand.nextInt(15);

        for (GraphicObject asteroide : asteroides) {
            asteroide.setIncX(-0.5);
            asteroide.incrementaPos(velocidad);
        }

        nave.setPos(posX, posY);
        Random rand = new Random();

        for (int asteroid = 0; asteroid < asteroides.size(); asteroid++) {
            GraphicObject asteroideActual = asteroides.get(asteroid);
            if (nave.verificaColision(asteroideActual)) {
                if (context instanceof Juego) {
                    try {
                        ((Juego) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ((Juego) context).bajarVida();
                            }
                        });
                        if (((Juego) context).getVidas() > 0) {
                            posY = alto / 2;
                            nave.setPos(posX, posY);
                        }
                        if (((Juego) context).getVidas() == 0) {
                            ((Juego) context).activaGameOver();
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
            marcianoActual.setIncX(-0.5);
            marcianoActual.incrementaPos(retardo);
            //hacer que los marcianos no se toquen entre sí
            if (nave.verificaColision(marcianoActual)) {
                if (context instanceof Juego) {
                    try {
                        ((Juego) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ((Juego) context).bajarVida();
                            }
                        });
                        if (((Juego) context).getVidas() > 0) {
                            posY = alto / 2;
                            nave.setPos(posX, posY);
                        }
                        if (((Juego) context).getVidas() == 0) {
                            nave.setActivo(false);
                            ((Juego)context).activaGameOver();
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
                    marcianoActual.setPos(marcianoActual.getPosX() , marcianoActual.getPosY()+ 20);
                    marcianoActual2.setPos(marcianoActual2.getPosX() , marcianoActual2.getPosY()-20);
                }  
            }
        }


        for (int misil = 0; misil < misiles.size(); misil++) {
            if (misiles.get(misil).isActivo()) {
                misiles.get(misil).incrementaPos(retardo);
                int tmp = misiles.get(misil).getTiempoEnPantalla();
                misiles.get(misil).setTiempoEnPantalla(tmp - 1);
                if (misiles.get(misil).getPosX()> ancho) {

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
        try {
            GraphicObject ObjetoParaBorrar ;
            if (asteroid != -1) {
                ObjetoParaBorrar = asteroides.get(asteroid);
                asteroides.remove(ObjetoParaBorrar);
                threadPoolAsteroides.execute(new HebraAsteroides());
            }
            if (marciano != -1) {
                ObjetoParaBorrar =marcianos.get(marciano);
                if (ObjetoParaBorrar.getVerdadero()) {
                    puntuacion+=1000;
                    if (context instanceof Juego) {
                        ((Juego) context).setPts(puntuacion);
                    }
                }
                marcianos.remove(ObjetoParaBorrar);
            }

            if (misil != -1) {
                misiles.get(misil).setActivo(false);
                ObjetoParaBorrar =  misiles.get(misil);
                misiles.remove(ObjetoParaBorrar);
            }
        } catch (Exception e) {
            e.printStackTrace();
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

    public void reinicia(){
        nAsteroids=1;
        nave.setActivo(true);
        asteroides.clear();
        misiles.clear();
        marcianos.clear();
        generaEnemigos();
    }

    public GameLoopThread getGameLoopThread() {
        return gameLoopThread;
    }

    class HebraAsteroides extends Thread{
        @Override
        public void run() {
            super.run();
            while (nAsteroids>asteroides.size()){
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
                asteroide1 = new GraphicObject(EasyEngine.this, id);
                if (id == R.drawable.asterioidpro) {
                    asteroide1.setAncho(223);
                    asteroide1.setAlto(140);
                    asteroide1.setRadioColision((223 + 140) / 4);
                }

                posAsteroideY = rand.nextInt((int) (alto));//rand.nextInt((int) (ancho /4)
                if(posAsteroideY<=200){
                    posAsteroideY=200;
                }
                posAsteroideX = ancho;
                asteroide1.setPos(posAsteroideX, posAsteroideY);
                asteroides.add(asteroide1);
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}