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
import com.example.trivialasteroids.Modelos.Pregunta;
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
    float alto, ancho, posX, posY, posAsteroideX = ancho, posAsteroideY = alto / 4;
    private static int PASO_VELOCIDAD_MISIL = 1;

    static final long FPS = 60;
    long ticksPS = 1000 / FPS;
    long sleepTime;
    private long ultimoProceso = 0;

    int nAsteroids = 5;
    int nMarcianos = 5;

    int xAciertos = 0,// Datos en relacion con las respuestas
        yErrores = 0,
        nAciertos = 0,
        nErrores = 0;


    int nPreguntas = 0;//Cantidad de preguntas de ese nivel
    int nPAacertadas = -1;//preguntas acertasdas si hay 3 preuntas y aciertas 3 pasas a la siguiente :D

    int ActualLevel = 0;

    Pregunta currentQuestion;
    JSONObject currentLevel;

    Random random = new Random();
    int posicion = random.nextInt(3);

    GameLoopThread gameLoopThread = new GameLoopThread(this);
    ThreadPoolExecutor threadPoolMarcianos = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
    ThreadPoolExecutor threadPoolAsteroides = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
    ThreadPoolExecutor threadPoolMovimientoAsteroides = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
    ThreadPoolExecutor threadPoolMovimientoMarcianos = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);

    GraphicObject nave;
    GraphicObject marciano;
    GraphicObject asteroide1;

    ArrayList<Respuesta> respuestas = new ArrayList<>();
    ArrayList<JSONObject> niveles = new ArrayList<>();
    ArrayList<Pregunta> preguntas = new ArrayList<>();
    ArrayList<GraphicObject> asteroides = new ArrayList<>();
    ArrayList<GraphicObject> misiles = new ArrayList<>();
    ArrayList<GraphicObject> marcianos = new ArrayList<>();

    Context context;

    Paint pincelTexto = new Paint();
    JSONObject json2, json;
    public void init(){
        //SpaceShip
        generaNave();
        //generas los enemigos
        generaEnemigos();
        while (nAsteroids > asteroides.size()){
            System.out.println("Generando asteroides");
//            ((Juego) this.context).Puente("Generando asteroides");
        }
        while(nMarcianos > marcianos.size()){
            System.out.println("Generando marcianos");
//            ((Juego) this.context).Puente("Generando marcianos");
        }

        //Texto que seguirá al asteroide
        pincelTexto.setTextSize(18);
        pincelTexto.setColor(Color.WHITE);
        pincelTexto.setTextSize(40);
        pincelTexto.setTextAlign(Paint.Align.CENTER);
        Typeface fuente = Typeface.create(String.valueOf(Typeface.BOLD), Typeface.NORMAL);
        pincelTexto.setTypeface(fuente);
        getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                ((Juego)context).setTv_pregunta(currentQuestion.getPregunta());

                System.out.println("Comprobando partida");
                anotarPuntos(nAciertos, nErrores);
                try {
                    gameLoopThread.setRunning(true);
                    System.out.println("Ejecutando hilo gameloop");
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
        gameLoopThread.reanudar();
    }
    public EasyEngine(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        DisplayMetrics pantalla = context.getResources().getDisplayMetrics();
        ancho = pantalla.widthPixels;
        alto = pantalla.heightPixels;

        JsonData();
        Preguntas();
        Respuestas();
        init();

//        anotarPuntos(nAciertos, nErrores);
    }

    //Carga de datos
    public void JsonData() {
        nPreguntas = 0;
        //  String[] respuestas= {"Madrid", "Lisboa", "Paris", "Dublín", "Tokio", "Buenos Aires", "Washington", "Ottawa"};

        try {
            //LV 1
            json2 = new JSONObject("{'result': 'ok', 'message': 'levels retrieved', 'datos':{'niveles':[{'datoslv':{'preguntas':[{'pregunta': 'Sanciones entre 5000 y 6000€','respuestas':[{'contenido': 'tirar basura desde un vehículo', 'valida': True},{'contenido': 'Exceso de velocidad', 'valida': False},{'contenido': 'Execeder la tasa de alcol ', 'valida': True}]},{'pregunta':'Directiva 1999', 'respuestas':[{'contenido': 'Articulo 1 xxxxxxxxxxx', 'valida': False},{'contenido': 'Articulo 2 xxxxxxxxxxx', 'valida': True},{'contenido': 'Articulo 3 xxxxxxxxxxx', 'valida': False}]}]}},{'datoslv':{'preguntas':[{'pregunta': 'Que tiempo hace hoy','respuestas':[{'contenido': 'Soleado', 'valida': True},{'contenido': 'Invernal', 'valida': False},{'contenido': 'Primaveral ', 'valida': True}]},{'pregunta':'Cual es mi comida favorita', 'respuestas':[{'contenido': 'Brócoli', 'valida': False},{'contenido': 'Melocotón', 'valida': True},{'contenido': 'Pizza Suprema', 'valida': True}]}]}}]}}");
            json = json2.getJSONObject("datos");


            JSONArray nivelesJSON =   json.getJSONArray("niveles");

            for(int nivel = 0; nivel < nivelesJSON.length(); nivel++){
                JSONObject jsonLV = (JSONObject) nivelesJSON.get(nivel);
                niveles.add(jsonLV);
            }

            currentLevel = niveles.get(0);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void Preguntas(){
        try {
            JSONArray preguntasJson = currentLevel.getJSONObject("datoslv").getJSONArray("preguntas");
            for(int pregunta = 0; pregunta < preguntasJson.length(); pregunta++){
                JSONObject jsonP = (JSONObject) preguntasJson.get(pregunta);
                System.out.println(jsonP.getString("pregunta"));
                preguntas.add(new Pregunta(jsonP));
                ++nPreguntas;
                System.out.println("Preguntas cargadas = "+nPreguntas+"/"+preguntasJson.length());
            }


            if (preguntas.size() > 0) {
                System.out.println("PRIMERA PREGUNTA = " + preguntas.get(0).getPregunta());
                currentQuestion = preguntas.get(0);
                System.out .println("Primera pregunta cargada correctamente");
            }else{
                System.out.println("Fallo de carga, cargando pregunta por defecto");
    //                System.out.println("Cargando por pregunta por defecto");
                currentQuestion = new Pregunta(new JSONObject("{'pregunta':'Si ves esto es que no hay preguntas', 'respuestas':[{'contenido': 'Falsa', 'valida': False},{'contenido': 'Verdadera', 'valida': True},{'contenido': 'Falsa', 'valida': False}]}"));
            }
        } catch (JSONException e) {
            e.printStackTrace();

        }
    };
    public void Respuestas(){
        try {
            JSONArray lista = currentQuestion.getRespuestas();
            for (int i = 0; i < lista.length(); i++) {
                JSONObject obj = lista.getJSONObject(i);
                String contenido = obj.getString("contenido");
                boolean valido = obj.getBoolean("valida");
                respuestas.add(new Respuesta(contenido, valido));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void generaNave() {
        posX = 100;
        posY = alto / 2;
        nave = new GraphicObject(this, R.drawable.sprite_space_ship);
        nave.setAlto(220);
        nave.setAncho(220);
        nave.setRadioColision((220 + 220) / 4);
        nave.setActivo(true);
    }
    private void generaEnemigos() {
        threadPoolAsteroides.execute(new HebraAsteroides());
        threadPoolMarcianos.execute(new HebraMarcianos());

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX(); // or getRawX();
        float y = event.getY();
        //Control vertical de la nave

        if (x >= 10 && x < (10 + nave.getAncho()) && nave.isActivo()) {
            posY = y;
        }

        return true;
    }

    @Override
    public synchronized void onDraw(Canvas lienzo) {
        //aqui solamente dibujas
        pincelTexto.setColor(Color.WHITE);
        lienzo.drawColor(Color.BLACK);
        if (nave.isActivo()) {
            nave.DrawGraphic(lienzo);
        } else {
            posY = -10;
        }
        for (int a = 0; a < asteroides.size(); a++) {
            GraphicObject asteroide = asteroides.get(a);
            asteroide.DrawGraphic(lienzo);
        }
        for (int m = 0; m < marcianos.size(); m++) {
            GraphicObject marciano = marcianos.get(m);
            marciano.DrawGraphic(lienzo);
            lienzo.drawText(marciano.getRespuestaAsociada(), (float) marciano.getPosX(), (float) marciano.getPosY(), pincelTexto);
        }
        for (int misil = 0; misil < misiles.size(); misil++) {

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
                if (c == null) {
                    System.out.println("C es NULO C ES NULO ");
                } else {
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
                synchronized (gameLoopThread) {
                    gameLoopThread.sleep(sleepTime);
                }
            else
                synchronized (gameLoopThread) {
                    gameLoopThread.sleep(20);
                }
        } catch (Exception ignored) {
        }
        System.out.println("Ticks: " + (System.currentTimeMillis() - startTime));
        System.out.println("SLEEPTIME = " + sleepTime);
    }

    //Update Positions and Collisions
    public void updatePhysics(boolean pausa) {
//        long ahora = System.currentTimeMillis();
//        // No hagas nada si el período de proceso no se ha cumplido.
//        if (ultimoProceso + ticksPS > ahora) {
//            return;
//        }
//        double retardo = ultimoProceso;
//        if (!pausa) {
//            retardo = sleepTime;
//        }
//
//        ultimoProceso = ahora;
        controlThreat();
        threadPoolMovimientoAsteroides.execute(new HebraMovimientoAsteroides());
        threadPoolMovimientoMarcianos.execute(new HebraMovimientoMarcianos());

       int velocidad = 15;
        for (int misil = 0; misil < misiles.size(); misil++) {
            if (misiles.get(misil).isActivo()) {
                misiles.get(misil).incrementaPos(velocidad);
                int tmp = misiles.get(misil).getTiempoEnPantalla();
                misiles.get(misil).setTiempoEnPantalla(tmp - 1);
                if (misiles.get(misil).getPosX() > ancho) {
                    misiles.get(misil).setActivo(false);
                    misiles.remove(misiles.get(misil));
                } else {
                    for (int asteroid = 0; asteroid < asteroides.size(); asteroid++) {
                        //si un misil toca a un marciano lo destruye
                        try {
                            if (misiles.get(misil).verificaColision(asteroides.get(asteroid))) {
                                destruyendoObejeto(asteroid, misil, -1);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    for (int marciano = 0; marciano < marcianos.size(); marciano++) {
                        try {
                            //Fallo 1, 2 y 3 INDEX OF BOUND EXCEPTION EL MISIL/MARCIANO YA NO EXISTE
                            if (misiles.get(misil).verificaColision(marcianos.get(marciano))) {
                                destruyendoObejeto(-1, misil, marciano);
                            }
                        } catch (IndexOutOfBoundsException e) {
                            e.printStackTrace();
                            anotarPuntos(nAciertos,nErrores);
                        }
                    }
                }
            }
        }
    }

    boolean modificador3posicion = false;
    private void posicion(GraphicObject marcianoActual, GraphicObject marcianoActual2) {

        System.out.println("POSICION ACTUAL = "+ posicion);

        if (posicion == 0) {
            modificador3posicion = false;
            marcianoActual.setPos(marcianoActual.getPosX(), marcianoActual.getPosY() + 10);
            marcianoActual2.setPos(marcianoActual2.getPosX(), marcianoActual2.getPosY() - 10);
        } else if (posicion == 1) {
            modificador3posicion = false;
            marcianoActual.setPos(marcianoActual.getPosX() - 20, marcianoActual.getPosY() + 20);
            marcianoActual2.setPos(marcianoActual2.getPosX() - 20, marcianoActual2.getPosY() - 20);
        } else if (posicion == 2){
            marcianoActual.setPos(marcianoActual.getPosX() + 10, marcianoActual.getPosY() + 20);
            marcianoActual2.setPos(marcianoActual2.getPosX() + 10, marcianoActual2.getPosY() - 20);
        }
    }

    private synchronized void destruyendoObejeto(int asteroid, int misil, int marciano) {
        try {
            GraphicObject ObjetoParaBorrar;
            if (asteroid != -1) {
                ObjetoParaBorrar = asteroides.get(asteroid);
                asteroides.remove(ObjetoParaBorrar);
                threadPoolAsteroides.execute(new HebraAsteroides());
            }
            if (marciano != -1) {
                ObjetoParaBorrar = marcianos.get(marciano);
                if (ObjetoParaBorrar.getVerdadero()) {
                    ++nAciertos;
                    anotarPuntos(nAciertos, nErrores);
                } else {
                    ++nErrores;
                    anotarPuntos(nAciertos, nErrores);
                }
                marcianos.remove(ObjetoParaBorrar);
            }

            if (misil != -1) {
                misiles.get(misil).setActivo(false);
                ObjetoParaBorrar = misiles.get(misil);
                misiles.remove(ObjetoParaBorrar);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void anotarPuntos(int nAcertadas, int nErrores) {
        if (context instanceof Juego) {
            ((Juego) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((Juego) context).compruebaPartida(nAcertadas, nErrores, xAciertos,
                            yErrores,nPreguntas,nPAacertadas ,preguntas,
                            niveles, ActualLevel);
                }
            });
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

    public void reinicia() {
        gameLoopThread.pausar();
        modificador3posicion=false;
        nAsteroids = 5;
        nMarcianos = 5;
        nAciertos = 0;
        nErrores = 0;
        xAciertos = 0;
        yErrores = 0;
        nave.setActivo(true);
        asteroides.clear();
        misiles.clear();
        marcianos.clear();
        posicion = random.nextInt(3);
        init();
        anotarPuntos(nAciertos, nErrores);
    }

    public GameLoopThread getGameLoopThread() {
        return gameLoopThread;
    }

    public GraphicObject getNave() {
        return nave;
    }

    public void setNPAcertadas(int nPAcertadas) {
        this.nPAacertadas = nPAcertadas;
    }

    public void setCurrentQuestion(Pregunta pregunta) {
        currentQuestion = pregunta;
        respuestas.clear();
        marcianos.clear();
        Respuestas();
        generaEnemigos();
    }

    public void setNAciertos(int i) {
        nAciertos = i;
    }

    public void setXAciertos(int i) {
        xAciertos = i;
    }

    public void setNErrores(int i) {
        nErrores = i;
    }

    public void setYErrores(int i) {
        yErrores = i;
    }

    public void setLevel(JSONObject jsonObject, int actualLevel) {
        this.ActualLevel = actualLevel;
        currentLevel = jsonObject;
        Preguntas();
    }

    //HEBRAS
    class HebraAsteroides extends Thread {
        @Override
        public void run() {
            super.run();
            while (nAsteroids > asteroides.size()) {
                synchronized (this) {
                    int id;
                    switch (random.nextInt(3)) {
                        case 0:
                            id = R.drawable.asteroide2;
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
//                    if (id == R.drawable.asterioidpro) {
//                        asteroide1.setAncho(223);
//                        asteroide1.setAlto(140);
//                        asteroide1.setRadioColision((223 + 140) / 4);
//                    }

                    posAsteroideY = random.nextInt((int) (alto));//rand.nextInt((int) (ancho /4)
                    if (posAsteroideY <= 200) {
                        posAsteroideY = 200;
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

    class HebraMarcianos extends Thread {
        @Override
        public void run() {
            super.run();
            while (nMarcianos > marcianos.size()) {
                marciano = new GraphicObject(EasyEngine.this, R.drawable.marciano02);
                marciano.setAlto(90);
                marciano.setAncho(120);
                marciano.setRadioColision((90 + 120) / 4);

                Respuesta respuesta = respuestas.get(random.nextInt(respuestas.size()));

                String frase = respuesta.getRespuestaAsociada();
                boolean validez = respuesta.getVerdadero();
                if (validez) {
                    ++xAciertos;
                } else {
                    ++yErrores;
                }
                marciano.setRespuestaAsociada(frase);
                marciano.setVerdadero(validez);

                float posMarcianoX = (int) (ancho / 2) - 2;
                float posMarcianoY = random.nextInt((int) (alto / 1.6));
                marciano.setPos(-posMarcianoX, posMarcianoY);
                posAsteroideY = alto / 2;
                posAsteroideX = (ancho/2)+(ancho/3);//-rand.nextInt((int) (ancho / 2) - 2);
                marciano.setPos(posAsteroideX, posAsteroideY);
                marcianos.add(marciano);
            }
        }
    }

    class HebraMovimientoAsteroides extends Thread{
        @Override
        public void run() {
            super.run();
            int velocidad = random.nextInt(15);

            for (GraphicObject asteroide : asteroides) {
                asteroide.setIncX(-0.5);
                asteroide.incrementaPos(velocidad);
            }

            nave.setPos(posX, posY);
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
                            destruyendoObejeto(asteroid, -1, -1);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

        }
    }

    class HebraMovimientoMarcianos extends Thread{
        @Override
        public void run() {
            super.run();
            int velocidad = random.nextInt(10);
            for (int marciano = 0; marciano < marcianos.size(); marciano++) {
                GraphicObject marcianoActual = marcianos.get(marciano);
                marcianoActual.incrementaPos(velocidad);
                marcianoActual.setIncX(-0.5);
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
                                ((Juego) context).activaGameOver();
                            }
                            //No se llama a destruir objeto para evitar la verificación de puntos y por tanto penalizar directamente al jugador
                            marcianos.remove(marcianoActual);
                            ++nErrores;
                            anotarPuntos(nAciertos, nErrores);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                for (int marciano2 = marciano + 1; marciano2 < marcianos.size(); marciano2++) {
                    GraphicObject marcianoActual2 = marcianos.get(marciano2);
                    if (marcianoActual.verificaColision(marcianoActual2)) {
                        posicion(marcianoActual, marcianoActual2);
                    }
                }
            }
        }
    }
}