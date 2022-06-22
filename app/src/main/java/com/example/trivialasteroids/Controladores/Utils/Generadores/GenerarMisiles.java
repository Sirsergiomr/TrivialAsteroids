package com.example.trivialasteroids.Controladores.Utils.Generadores;

import com.example.trivialasteroids.Controladores.BasicEngine.EasyEngineV1;
import com.example.trivialasteroids.Controladores.ControlDisparo;
import com.example.trivialasteroids.Entidades.GraphicObject;
import com.example.trivialasteroids.Entidades.Misil;
import com.example.trivialasteroids.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
@Deprecated
/**
 * Sigue las mismas normas que en la EasyEngineV1 pero no se sicroniza bien, va mal, crashea
 * han sido 2 horas revisando y cambiando, mejor dejalo como está actualmente.
 *
 * */
public class GenerarMisiles extends Thread{
    private  List<GraphicObject> misiles;
    private  List<GraphicObject> asteroides;
    private  ArrayList<GraphicObject> marcianos ;
    private int nMarcianosSeguiendo = 0;//Enemigos que persiguen al jugador
    private int nAsteroidesSeguiendo = 0;//Enemigos que persiguen al jugador
    private int VELOCIDAD_MISIL;
    private int width;
    private int nAciertos = 0;// Aciertos de la pregunta actual
    private int nErrores = 0;//Errores de la pregunta actual
    private int CANTIDAD_MISILES ;
    private EasyEngineV1 context;
    private GraphicObject nave;
    private boolean dispara = false;
    private int PASO_VELOCIDAD_MISIL;
    private final ThreadPoolExecutor poolMisiles = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
    private Misil misil;
    public GenerarMisiles(int nMarcianosSeguiendo, int nAsteroidesSeguiendo, int VELOCIDAD_MISIL, int width,
                          int nAciertos, int nErrores, int CANTIDAD_MISILES, EasyEngineV1 context,
                          GraphicObject nave, boolean dispara, int PASO_VELOCIDAD_MISIL, List<GraphicObject> misiles, ArrayList<GraphicObject> marcianos, List<GraphicObject> asteroides) {
        this.nMarcianosSeguiendo = nMarcianosSeguiendo;
        this.nAsteroidesSeguiendo = nAsteroidesSeguiendo;
        this.VELOCIDAD_MISIL = VELOCIDAD_MISIL;
        this.width = width;
        this.nAciertos = nAciertos;
        this.nErrores = nErrores;
        this.CANTIDAD_MISILES = CANTIDAD_MISILES;
        this.context = context;
        this.nave = nave;
        this.dispara = dispara;
        this.PASO_VELOCIDAD_MISIL = PASO_VELOCIDAD_MISIL;
        this.misiles = misiles;
        this.marcianos = marcianos;
        this.asteroides = asteroides;

    }

    @Override
    public void run(){
        try {
            Thread.sleep(500);
            dispara();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void dispara() {
        System.out.println("CANTIDAD DE MISILES ACTUAL = " + misiles.size());
        misil = new Misil(context, R.drawable.misil1,   nMarcianosSeguiendo,  nAsteroidesSeguiendo,  VELOCIDAD_MISIL, width, nAciertos,  nErrores, misiles, marcianos, asteroides);

        if (misiles.size() < CANTIDAD_MISILES && this.dispara) {
            misil.setPos(nave.getPosX() + nave.getAncho() / 2 - misil.getAncho() / 2, nave.getPosY() + nave.getAlto() / 2 - misil.getAlto() / 2);
            misil.setAngulo(nave.getAngulo());
            misil.setIncX(Math.cos(Math.toRadians(misil.getAngulo())) * PASO_VELOCIDAD_MISIL);
            misil.setIncY(Math.sin(Math.toRadians(misil.getAngulo())) * PASO_VELOCIDAD_MISIL);
            misil.setActivo(true);
            misiles.add(misil);
            this.dispara = false;
        }

        poolMisiles.execute(new ControlDisparo(misiles,  asteroides,  marcianos,
         VELOCIDAD_MISIL,  width,  nAsteroidesSeguiendo,  nMarcianosSeguiendo,
         nAciertos,  nErrores));
    }

}
