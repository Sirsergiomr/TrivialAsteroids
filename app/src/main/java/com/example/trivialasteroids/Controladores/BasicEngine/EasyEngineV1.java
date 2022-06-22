package com.example.trivialasteroids.Controladores.BasicEngine;

import static com.example.trivialasteroids.Controladores.Utils.Funciones.compruebaColision;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.example.trivialasteroids.Controladores.Utils.Generadores.GenerarAsteroides;
import com.example.trivialasteroids.Controladores.Utils.Generadores.GenerarMarcianos;
import com.example.trivialasteroids.Controladores.Utils.Generadores.GenerarMisiles;
import com.example.trivialasteroids.Entidades.Asteroide;
import com.example.trivialasteroids.Entidades.Marciano;
import com.example.trivialasteroids.Entidades.Misil;
import com.example.trivialasteroids.Juego;
import com.example.trivialasteroids.Entidades.GraphicObject;
import com.example.trivialasteroids.Controladores.Modelos.Pregunta;
import com.example.trivialasteroids.Controladores.Modelos.Respuesta;
import com.example.trivialasteroids.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * MJ03-ROCKET ENGINE
 * @author Sirse
 * @version 1.0
 * @since 1.0
 */

public class EasyEngineV1 extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private static final double PASO_VELOCIDAD = 0.5;
    private Context c;
    private SurfaceHolder holder;
    private Thread drawThread;
    private boolean surfaceReady = false;
    private boolean drawingActive = false;
    private static final int MAX_FRAME_TIME = (int) (1000.0 / 60.0);
    private static final String LOGTAG = "surface";
    private static int width, height;
    public final int x = 100;//Posicion de la nave
    private int y;
    public int velY;//Multiplicador de velocidad sobre el eje Y (solo aplica a la nave)

    private Rect screen;
    private int touchY;

    //Elementos para la lógica del juego
    private final List<GraphicObject> misiles = Collections.synchronizedList(new ArrayList<>());
    private final List<GraphicObject> asteroides = Collections.synchronizedList(new ArrayList<>());
    private final ArrayList<JSONObject> niveles = new ArrayList<>();
    private final ArrayList<Pregunta> preguntas = new ArrayList<>();
    private final ArrayList<Respuesta> respuestas = new ArrayList<>();
    private final ArrayList<GraphicObject> marcianos = new ArrayList<>();

    private final GraphicObject nave = new GraphicObject(this, R.drawable.sprite_space_ship);
    private Marciano marciano;
    private GraphicObject asteroide1;
    private GraphicObject bala = new GraphicObject(this, R.drawable.ic_whatshot);
    private Pregunta currentQuestion;
    private JSONObject currentLevel;
    private static final int PASO_VELOCIDAD_MISIL = 1;
    private int VELOCIDAD_MISIL = 10;
    private int VELOCIDAD_ASTEROIDE = 5;
    private int VELOCIDAD_MARCIANO = 5;

    private final int nAsteroids = 5;//Cantidad de enemigos
    private final int nMarcianos = 5;//Cantidad de enemigos
    private int CANTIDAD_MISILES = 5;
    // Datos en relacion con las respuestas
    private int xAciertos = 10;//Cantidad de aciertos que se le otorga al usuario
    private int maxErrores = 10;//Errores maximos a cometer por el usuario
    private int nAciertos = 0;// Aciertos de la pregunta actual
    private int nErrores = 0;//Errores de la pregunta actual
    private int nPreguntas = 0;//Cantidad de preguntas de ese nivel
    private int nPAacertadas = -1;//preguntas acertasdas si hay 3 preuntas y aciertas 3 pasas a la siguiente :D
    private int ActualLevel = 0;//Contador de niveles

    private final Random random = new Random();
    private boolean dispara = false;
    private final Paint pincelTexto = new Paint();
    private final Paint pincelRectAst = new Paint();
    private final Paint pincelRectMar = new Paint();

    private final ThreadPoolExecutor poolMarcianos = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
    private final ThreadPoolExecutor poolMisiles = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
    private final ThreadPoolExecutor poolAsteroides = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
    private final ThreadPoolExecutor poolDisparo = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);

    private int nAsteroidesSeguiendo = 0;//Enemigos que persiguen al jugador
    private int nMarcianosSeguiendo = 0;//Enemigos que persiguen al jugador

    public EasyEngineV1(Context context) {
        super(context);
        init(context);
    }

    public EasyEngineV1(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public EasyEngineV1(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public EasyEngineV1(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    public void init(Context c) {
        this.c = c;
        holder = getHolder();
        holder.addCallback(this);
        setFocusable(true);

        /** DISPLAY**/
        WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;
        screen = new Rect(0, 0, width - 50, height - 50);
        //-------------------------------------------

        nave.setAlto(220);
        nave.setAncho(220);
        nave.setRadioColision((220 + 220) / 4);
        nave.setActivo(true);

        bala.setAlto(228);
        bala.setAncho(228);
        bala.setPos(width - nave.getAncho(), height - nave.getAlto());
        y = height / 2 - nave.getAlto();//- PLAYER_BMP.getHeight()
        touchY = y;
        nave.setPos(x, y);

        //Cargar niveles
        jsonData();
        //Cargar preguntas
        preguntas();
        //Cargar respuestas
        respuestas();

        pincelTexto.setTextSize(18);
        pincelTexto.setColor(Color.WHITE);
        pincelTexto.setTextSize(40);
        pincelTexto.setTextAlign(Paint.Align.CENTER);
        Typeface fuente = Typeface.create(String.valueOf(Typeface.BOLD), Typeface.NORMAL);
        pincelTexto.setTypeface(fuente);
        pincelTexto.setColor(Color.WHITE);


        pincelRectAst.setColor(Color.RED);
        pincelRectMar.setColor(Color.BLUE);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        this.holder = holder;

        if (drawThread != null) {
            Log.d(LOGTAG, "draw thread still active..");
            drawingActive = false;
            try {
                drawThread.join();
            } catch (InterruptedException e) {
            }
        }

        surfaceReady = true;
        startDrawThread();

        ((Juego) c).setTv_pregunta(currentQuestion.getPregunta());
        Log.d(LOGTAG, "Created");
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {
//        if (width == 0 || height == 0){
//            return;
//        }
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
        // Surface is not used anymore - stop the drawing thread
        stopDrawThread();
        // and release the surface
        holder.getSurface().release();

        this.holder = null;
        surfaceReady = false;
        Log.d(LOGTAG, "Destroyed");
    }

    public void dispara() {
//        poolDisparo.execute(new GenerarMisiles( nMarcianosSeguiendo,  nAsteroidesSeguiendo,  VELOCIDAD_MISIL,  width,
//                nAciertos,  nErrores,  CANTIDAD_MISILES,  this, nave,  dispara,  PASO_VELOCIDAD_MISIL, misiles, marcianos, asteroides));
        System.out.println("CANTIDAD DE MISILES ACTUAL = " + misiles.size());
        if (misiles.size() < CANTIDAD_MISILES && dispara) {
            GraphicObject misil = new GraphicObject(EasyEngineV1.this, R.drawable.misil1);
            misil.setPos(nave.getPosX() + nave.getAncho() / 2 - misil.getAncho() / 2, nave.getPosY() + nave.getAlto() / 2 - misil.getAlto() / 2);
            misil.setAngulo(nave.getAngulo());
            misil.setIncX(Math.cos(Math.toRadians(misil.getAngulo())) * PASO_VELOCIDAD_MISIL);
            misil.setIncY(Math.sin(Math.toRadians(misil.getAngulo())) * PASO_VELOCIDAD_MISIL);
            misil.setActivo(true);
            misiles.add(misil);
            dispara = false;
        }
        poolDisparo.execute(new HebraMisiles());
    }

    //Renderizado del juego
    public void render(Canvas c) {
        c.drawColor(Color.BLACK);
        if (nave.isActivo()) {
            nave.DrawGraphic(c);
        }

        bala.DrawGraphic(c);

        //Dibujar misiles
        for (int i = 0; i < misiles.size(); i++) {
            GraphicObject misil = misiles.get(i);
            misil.DrawGraphic(c);
        }
        //Dibujar asteroides
        for (int j = 0; j < asteroides.size(); j++) {
            GraphicObject asteroide = asteroides.get(j);
            c.drawRect(getasteroidBound(asteroide), pincelRectAst);
            asteroide.DrawGraphic(c);
        }
        //Dibujar marcianos
        for (int k = 0; k < marcianos.size(); k++) {
            GraphicObject marciano = marcianos.get(k);
            marciano.DrawGraphic(c);
            c.drawRect(getMarcianoBound(marciano), pincelRectMar);
            c.drawText(marciano.getRespuestaAsociada(), (float) marciano.getPosX(), (float) marciano.getPosY(), pincelTexto);
        }
    }

    //Lógica principal
    public void tick() {
        //Movimiento y restricciones del jugador :D
        if (nave.isActivo()) {
            if (touchY < nave.getAlto() / 2) {
                touchY = nave.getAlto() / 2;
            } else if (touchY > height - nave.getAlto()) {
                touchY = height - nave.getAlto();
            } else {
                y = touchY;
                nave.setPos(x, y);
            }
        }

        poolAsteroides.execute(new GenerarAsteroides(VELOCIDAD_ASTEROIDE,
                this, nAsteroids, width, height, nave.getAlto(),
                PASO_VELOCIDAD, nAsteroidesSeguiendo, asteroides));

        poolMarcianos.execute(new GenerarMarcianos(nMarcianos,this, height, width, nave.getAlto(),
                VELOCIDAD_MARCIANO, nMarcianosSeguiendo, PASO_VELOCIDAD, nave, respuestas, marcianos));

        //Movimiento de los asteroides de izquierda a derecha y colision con la nave
        for (int i = 0; i < asteroides.size(); i++) {
            Asteroide asteroide = (Asteroide) asteroides.get(i);
            asteroide.movimientoAsteroide();
            if (compruebaColision(asteroide, nave, asteroides, null)) {
                //Se le quita vida
                System.out.println("LA NAVE MUERE");
                if (asteroide.seguirJugador()) {
                    nAsteroidesSeguiendo--;
                }
            }
        }

        //Movimiento de los marcianos de izquierda a derecha y colision con la nave
        for (int i = 0; i < marcianos.size(); i++) {
            Marciano marciano = (Marciano) marcianos.get(i);
            marciano.movimientoMarciano();
            if (compruebaColision(marciano, nave, marcianos, null)) {
                //Se le quita vida
                System.out.println("Cuando colisiona fuera del objeto");

                System.out.println("LA NAVE MUERE");
                if(marciano.seguirJugador()){
                    nMarcianosSeguiendo--;
                }
                ++nErrores;
                anotarPuntos(nAciertos, nErrores);
            }
        }
    }

    @Override
    public void run() {
        //Hilo que lleva control del dibujo

        Log.d("Game", "Draw thread started");
        long frameStartTime;
        long frameTime;

        /*
         * In order to work reliable on Nexus 7, we place ~500ms delay at the start of drawing thread
         * (AOSP - Issue 58385)
         */
        if (android.os.Build.BRAND.equalsIgnoreCase("google") && android.os.Build.MANUFACTURER.equalsIgnoreCase("asus") && android.os.Build.MODEL.equalsIgnoreCase("Nexus 7")) {
            Log.w("Game", "Sleep 500ms (Device: Asus Nexus 7)");
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {
            }
        }

        while (drawingActive) {
            if (holder == null) {
                return;
            }

            frameStartTime = System.nanoTime();
            Canvas canvas = getHolder().lockCanvas();

            if (canvas != null) {
                try {
                    synchronized (holder) {
                        tick();
                        render(canvas);
                    }
                } finally {

                    holder.unlockCanvasAndPost(canvas);
                }
            }

            // calculate the time required to draw the frame in ms
            frameTime = (System.nanoTime() - frameStartTime) / 1000000;

            if (frameTime < MAX_FRAME_TIME) {
                try {
                    Thread.sleep(MAX_FRAME_TIME - frameTime);
                } catch (InterruptedException e) {
                    // ignore
                }
            }

        }
        Log.d("Game", "Draw thread finished");
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getActionMasked();
        // Get the index of the pointer associated with the action.
        int index = ev.getActionIndex();

        float ejeX;
        float ejeY;
        if (ev.getPointerCount() > 1) {
            //Multitouch event
            ejeX = (int) ev.getX(index);
            ejeY = (int) ev.getY(index);
        } else {
            //Single touch event
            ejeX = (int) ev.getX(index);
            ejeY = (int) ev.getY(index);
        }

        //Colision con la nave y los bordes superiores y inferiores y que la nave siga la misma posicion que el dedo
        if (ejeX >= 0 && ejeX < (x + nave.getAncho()) && MotionEvent.ACTION_MOVE == action) {
            touchY = (int) ejeY;
        }
        //Disparo de misiles en la esquina inferior derecha de la pantalla
        if (ejeX >= width - nave.getAncho() && ejeY >= height - nave.getAlto()) {
            dispara = true;
//            poolDisparo.execute(new GenerarMisiles( nMarcianosSeguiendo,  nAsteroidesSeguiendo,
//                    VELOCIDAD_MISIL,  width, nAciertos,  nErrores,
//                    CANTIDAD_MISILES,  this, nave,  dispara,
//                    PASO_VELOCIDAD_MISIL, misiles, marcianos, asteroides));
            poolMisiles.execute(new HebraDisparo());
        }

        return true;
    }

    /**
     * Stops the drawing thread
     */
    public void stopDrawThread() {
        if (drawThread == null) {
            Log.d(LOGTAG, "DrawThread is null");
            return;
        }
        drawingActive = false;
        while (true) {
            try {
                Log.d(LOGTAG, "Request last frame");
                drawThread.join(5000);
                break;
            } catch (Exception e) {
                Log.e(LOGTAG, "Could not join with draw thread");
            }
        }
        drawThread = null;
    }

    /**
     * Creates a new draw thread and starts it.
     */
    public void startDrawThread() {
        if (surfaceReady && drawThread == null) {
            drawThread = new Thread(this, "Draw thread");
            drawingActive = true;
            drawThread.start();
        }
    }

    private Rect getasteroidBound(GraphicObject asteroide) {
        return new Rect((int) asteroide.getPosX(), (int) asteroide.getPosY(), (int) asteroide.getPosX() + asteroide.getAncho(), (int) asteroide.getPosY() + asteroide.getAlto());
    }

    private Rect getMarcianoBound(GraphicObject marciano) {
        return new Rect((int) marciano.getPosX(), (int) marciano.getPosY(), (int) marciano.getPosX() + marciano.getAncho(), (int) marciano.getPosY() + marciano.getAlto());
    }

    //-------------------------------------------

    //------------------------Carga de datos-----
    public void jsonData() {
        nPreguntas = 0;
        try {
            JSONObject json2 = new JSONObject("{'result': 'ok', 'message': 'levels retrieved', 'datos':{'niveles':[{'datoslv':{'preguntas':[{'pregunta': 'Sanciones entre 5000 y 6000€','respuestas':[{'contenido': 'tirar basura desde un vehículo', 'valida': True},{'contenido': 'Exceso de velocidad', 'valida': False},{'contenido': 'Execeder la tasa de alcol ', 'valida': True}]},{'pregunta':'Directiva 1999', 'respuestas':[{'contenido': 'Articulo 1 xxxxxxxxxxx', 'valida': False},{'contenido': 'Articulo 2 xxxxxxxxxxx', 'valida': True},{'contenido': 'Articulo 3 xxxxxxxxxxx', 'valida': False}]}]}},{'datoslv':{'preguntas':[{'pregunta': 'Que tiempo hace hoy','respuestas':[{'contenido': 'Soleado', 'valida': True},{'contenido': 'Invernal', 'valida': False},{'contenido': 'Primaveral ', 'valida': True}]},{'pregunta':'Cual es mi comida favorita', 'respuestas':[{'contenido': 'Brócoli', 'valida': False},{'contenido': 'Melocotón', 'valida': True},{'contenido': 'Pizza Suprema', 'valida': True}]}]}}]}}");
            JSONObject json = json2.getJSONObject("datos");

            JSONArray nivelesJSON = json.getJSONArray("niveles");

            for (int nivel = 0; nivel < nivelesJSON.length(); nivel++) {
                JSONObject jsonLV = (JSONObject) nivelesJSON.get(nivel);
                niveles.add(jsonLV);
            }
            System.out.println("Niveles: " + niveles.size());

            currentLevel = niveles.get(0);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void preguntas() {
        try {
            JSONArray preguntasJson = currentLevel.getJSONObject("datoslv").getJSONArray("preguntas");
            for (int pregunta = 0; pregunta < preguntasJson.length(); pregunta++) {
                JSONObject jsonP = (JSONObject) preguntasJson.get(pregunta);
                System.out.println(jsonP.getString("pregunta"));
                preguntas.add(new Pregunta(jsonP));
                ++nPreguntas;
                System.out.println("Preguntas cargadas = " + nPreguntas + "/" + preguntasJson.length());
            }


            if (preguntas.size() > 0) {
                System.out.println("PRIMERA PREGUNTA = " + preguntas.get(0).getPregunta());
                currentQuestion = preguntas.get(0);
                System.out.println("Primera pregunta cargada correctamente");
            } else {
                System.out.println("Fallo de carga, cargando pregunta por defecto");
                //                System.out.println("Cargando por pregunta por defecto");
                currentQuestion = new Pregunta(new JSONObject("{'pregunta':'Si ves esto es que no hay preguntas', 'respuestas':[{'contenido': 'Falsa', 'valida': False},{'contenido': 'Verdadera', 'valida': True},{'contenido': 'Falsa', 'valida': False}]}"));
            }
        } catch (JSONException e) {
            e.printStackTrace();

        }
    }

    public void respuestas() {
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
    //-------------------------------------------

    //Control de partida
    public void anotarPuntos(int nAcertadas, int nErrores) {
        if (c instanceof Juego) {
            ((Juego) c).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((Juego) c).compruebaPartida(nAcertadas, nErrores, xAciertos,
                            maxErrores, nPreguntas, nPAacertadas, preguntas,
                            niveles, ActualLevel);
                }
            });
        }
    }

    //Getter and Setter
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
        nMarcianosSeguiendo=0;
        respuestas();
    }

    public void setNAciertos(int i) {
        nAciertos = i;
    }

    public void setNErrores(int i) {
        nErrores = i;
    }

    public void setLevel(JSONObject jsonObject, int actualLevel) {
        this.ActualLevel = actualLevel;
        currentLevel = jsonObject;
        preguntas();
        respuestas();
    }
    public int getnAsteroidesSeguiendo() {
        return nAsteroidesSeguiendo;
    }

    public void setnAsteroidesSeguiendo(int nAsteroidesSeguiendo) {
        this.nAsteroidesSeguiendo = nAsteroidesSeguiendo;
    }

    public int getnMarcianosSeguiendo() {
        return nMarcianosSeguiendo;
    }

    public void setnMarcianosSeguiendo(int nMarcianosSeguiendo) {
        this.nMarcianosSeguiendo = nMarcianosSeguiendo;
    }

    public void clearMisiles() {
        misiles.clear();
    }


    //Reinicio del juego
    public void reinicio() {
        nAciertos = 0;
        nErrores = 0;
        nPAacertadas = 0;
        nPreguntas = 0;
        misiles.clear();
        marcianos.clear();
        asteroides.clear();
        nave.setActivo(true);
        nAsteroidesSeguiendo = 0;
        nMarcianosSeguiendo = 0;
        startDrawThread();
    }

    public void setdispara(boolean dispara) {
        this.dispara = dispara;
    }
    //Hebras
    private class HebraDisparo extends Thread {
        @Override
        public void run() {
            try {
                Thread.sleep(500);
                dispara();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
    private class HebraMisiles extends Thread {
        @Override
        public void run() {
            while (misiles.size() > 0) {
                //Movimiento de los misiles
                for (int i = 0; i < misiles.size(); i++) {
                    GraphicObject misil = misiles.get(i);
                    misil.incrementaPos(VELOCIDAD_MISIL);
                    misil.setPos(misil.getPosX() + misil.getIncX(), misil.getPosY());
                    if (misil.getPosX() > width) {
                        //Eliminar el misil
                        misiles.remove(i);
                    } else {
                        //Eliminar el asteroide
                        for (int j = 0; j < asteroides.size(); j++) {
                            Asteroide asteroide = (Asteroide) asteroides.get(j);
                            if (compruebaColision(misil, asteroide, misiles, asteroides)) {
                                if (asteroide.seguirJugador()) {
                                    nAsteroidesSeguiendo--;
                                }
                            }
                        }
                        //Eliminar el marciano
                        for (int j = 0; j < marcianos.size(); j++) {
                            Marciano marciano = (Marciano) marcianos.get(j);
                            if (compruebaColision(misil, marciano, misiles, marcianos)) {
                                //Eliminar el marciano y se comprueba si la respuesta asociada es correcta
                                System.out.println("marciano eliminado");
                                if(marciano.seguirJugador()){
                                    nMarcianosSeguiendo--;
                                }
                                if (marciano.getVerdadero()) {
                                    System.out.println("Acierto anotado");
                                    ++nAciertos;
                                    anotarPuntos(nAciertos, nErrores);
                                } else {
                                    System.out.println("Error Anotado");
                                    ++nErrores;
                                    anotarPuntos(nAciertos, nErrores);
                                }
                            }
                        }
                    }
                }
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}


