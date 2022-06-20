package com.example.trivialasteroids.Controladores.Utils.Generadores;

import com.example.trivialasteroids.Controladores.BasicEngine.EasyEngineV1;
import com.example.trivialasteroids.Controladores.Modelos.GraphicObject;
import com.example.trivialasteroids.Controladores.Utils.Funciones;
import com.example.trivialasteroids.Entidades.Asteroide;
import com.example.trivialasteroids.R;
import java.util.List;


public class GenerarAsteroides extends Thread{
    private int VELOCIDAD_ASTEROIDE;
    private EasyEngineV1 context;
    private int nAsteroids = 5;//Cantidad de enemigos
    private int width, height, defaultShipHeight;
    private double PASO_VELOCIDAD_ASTEROIDE;
    private int nAsteroidesSeguiendo;
    private  List<GraphicObject> asteroides;

    public GenerarAsteroides(int VELOCIDAD_ASTEROIDE, EasyEngineV1 context, int nAsteroids, int width, int height,
                             int defaultShipHeight, double PASO_VELOCIDAD_ASTEROIDE, int nAsteroidesSeguiendo,
                             List<GraphicObject> asteroides) {
        this.VELOCIDAD_ASTEROIDE = VELOCIDAD_ASTEROIDE;
        this.context = context;
        this.nAsteroids = nAsteroids;
        this.width = width;
        this.height = height;
        this.defaultShipHeight = defaultShipHeight;
        this.PASO_VELOCIDAD_ASTEROIDE = PASO_VELOCIDAD_ASTEROIDE;
        this.nAsteroidesSeguiendo = nAsteroidesSeguiendo;
        this.asteroides = asteroides;
    }

    @Override
    public void run() {
        try {
            Thread.sleep((long) (Funciones.getRandomNumber(1000, 2000) + 500));
            crearAsteroide();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void crearAsteroide() {
        //Generar asteroides de tal forma que no toquen los bordes de la pantalla
        if (asteroides.size() < nAsteroids) {
            Asteroide asteroide = new Asteroide(context, R.drawable.asteroide1, height ,defaultShipHeight,
                    VELOCIDAD_ASTEROIDE);
            int posX = width;
            int posY = (int) (Math.random() * (height - asteroide.getAlto()));

            asteroide.setIncX(-PASO_VELOCIDAD_ASTEROIDE);

            asteroide.setPos(posX + asteroide.getIncX(), posY);

            asteroide.setActivo(true);

            asteroides.add(asteroide);

            if (nAsteroidesSeguiendo < 2) {
                nAsteroidesSeguiendo++;
                asteroide.setSeguimiento(true);
            }
        }
    }
}
