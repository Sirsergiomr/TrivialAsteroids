package com.example.trivialasteroids.Controladores.Utils.Generadores;

import com.example.trivialasteroids.Controladores.BasicEngine.EasyEngineV1;
import com.example.trivialasteroids.Controladores.Modelos.GraphicObject;
import com.example.trivialasteroids.Controladores.Modelos.Respuesta;
import com.example.trivialasteroids.Controladores.Utils.Funciones;
import com.example.trivialasteroids.Entidades.Marciano;
import com.example.trivialasteroids.R;

import java.util.ArrayList;
import java.util.Random;

public class GenerarMarcianos extends Thread{
    private  ArrayList<GraphicObject> marcianos;
    private  ArrayList<Respuesta> respuestas ;
    private int nMarcianos;//Cantidad de enemigos
    private EasyEngineV1 context;
    private int height, width, defaultShipHeight, VELOCIDAD_MARCIANO, nMarcianosSeguiendo;
    private double PASO_VELOCIDAD_ASTEROIDE;
    private GraphicObject nave;
    private Random random = new Random();

    public GenerarMarcianos(int nMarcianos, EasyEngineV1 context, int height, int width, int defaultShipHeight,
                            int VELOCIDAD_MARCIANO, int nMarcianosSeguiendo,
                            double PASO_VELOCIDAD_ASTEROIDE, GraphicObject nave, ArrayList<Respuesta> respuestas, ArrayList<GraphicObject> marcianos  ) {
        this.nMarcianos = nMarcianos;
        this.context = context;
        this.height = height;
        this.width = width;
        this.defaultShipHeight = defaultShipHeight;
        this.VELOCIDAD_MARCIANO = VELOCIDAD_MARCIANO;
        this.nMarcianosSeguiendo = nMarcianosSeguiendo;
        this.PASO_VELOCIDAD_ASTEROIDE = PASO_VELOCIDAD_ASTEROIDE;
        this.nave = nave;
        this.respuestas = respuestas;
        this.marcianos = marcianos;
    }

    @Override
    public void run() {
        try {
            Thread.sleep((long) (Funciones.getRandomNumber(1000, 2000) + 500));
            crearMarciano();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void crearMarciano() {
        if (marcianos.size() < nMarcianos ) {
            try {
                Marciano marciano = new Marciano(context, R.drawable.marciano02, height, VELOCIDAD_MARCIANO, defaultShipHeight);

                marciano.setTipo(GraphicObject.TIPO_MARCIANO, new GraphicObject.OnFinishListener() {
                    @Override
                    public void onFinish() {
                        if(marciano.seguirJugador()){
                            nMarcianosSeguiendo--;
                        }
                        marcianos.remove(marciano);
                    }
                });

                int posX = width;
                int posY = (int) (Math.random() * (height - marciano.getAlto()));

                marciano.setPos(posX, posY);
                marciano.setIncX(-PASO_VELOCIDAD_ASTEROIDE);
                marciano.setActivo(true);

                //---SE ESTABLECE LA RESPUESTA--------------

                Respuesta respuesta = respuestas.get(random.nextInt(respuestas.size()));

                String frase = respuesta.getRespuestaAsociada();
                boolean validez = respuesta.getVerdadero();

                marciano.setRespuestaAsociada(frase);
                marciano.setVerdadero(validez);

                //-------------------------------------------

                marciano.setAlto(90);
                marciano.setAncho(120);
                marciano.setRadioColision((90 + 120) / 4);
                if(nMarcianosSeguiendo < 2){
                    nMarcianosSeguiendo++;
                    marciano.setSeguimiento(true);
                    marciano.setPosY((int) nave.getPosY());
                }
                marcianos.add(marciano);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
