package com.example.trivialasteroids.Controladores;

import com.example.trivialasteroids.Controladores.BasicEngine.EasyEngineV1;
import com.example.trivialasteroids.Controladores.Utils.Funciones;
import com.example.trivialasteroids.Controladores.Entidades.Asteroide;
import com.example.trivialasteroids.Controladores.Entidades.GraphicObject;
import com.example.trivialasteroids.Controladores.Entidades.Marciano;
import com.example.trivialasteroids.Controladores.Entidades.Misil;

import java.util.List;


public class ControlDisparoPts extends Thread {
    private List<GraphicObject> misiles;
    private List<GraphicObject> asteroides;
    private List<GraphicObject> marcianos;
    private int VELOCIDAD_MISIL;
    private int width;
    private int nAsteroidesSeguiendo, nMarcianosSeguiendo, nAciertos, nErrores;
    private EasyEngineV1 context;
    public ControlDisparoPts(EasyEngineV1 context, int VELOCIDAD_MISIL, int width) {
        this.misiles = context.getMisiles();
        this.asteroides = context.getAsteroides();
        this.marcianos = context.getMarcianos();
        this.VELOCIDAD_MISIL = VELOCIDAD_MISIL;
        this.width = width;
        this.nAsteroidesSeguiendo = context.getnAsteroidesSeguiendo();
        this.nMarcianosSeguiendo = context.getnMarcianosSeguiendo();
        this.nAciertos = context.getnAciertos();
        this.nErrores = context.getnErrores();
        this.context = context;
    }

    @Override
    public void run() {
        while (misiles.size() > 0) {
            //Movimiento de los misiles
            for (int i = 0; i < context.getMisiles().size(); i++) {
                Misil misil = (Misil) context.getMisiles().get(i);
                misil.incrementaPos(VELOCIDAD_MISIL);
                misil.setPos(misil.getPosX() + misil.getIncX(), misil.getPosY());
                if (misil.getPosX() > width) {
                    //Eliminar el misil
                   misiles.remove(i);
                   context.setMisiles(misiles);
                } else {
                    //Eliminar el asteroide
                    for (int j = 0; j < asteroides.size(); j++) {
                        Asteroide asteroide = (Asteroide) asteroides.get(j);
                        if (Funciones.compruebaColision(misil, asteroide, misiles, asteroides)) {
                            if (asteroide.seguirJugador()) {
                                nAsteroidesSeguiendo = context.getnAsteroidesSeguiendo()-1;
                                context.setnAsteroidesSeguiendo(nAsteroidesSeguiendo);
                            }
                        }
                    }
                    //Eliminar el marciano
                    for (int j = 0; j < marcianos.size(); j++) {
                        Marciano marciano = (Marciano) marcianos.get(j);
                        if (Funciones.compruebaColision(misil, marciano, misiles, marcianos)) {
                            //Eliminar el marciano y se comprueba si la respuesta asociada es correcta
                            System.out.println("marciano eliminado");
                            if (marciano.seguirJugador()) {
                                nMarcianosSeguiendo = context.getnMarcianosSeguiendo() -1;
                                context.setnMarcianosSeguiendo(nMarcianosSeguiendo);
                            }
                            if (marciano.getVerdadero()) {
                                System.out.println("Acierto anotado");
                                nAciertos = context.getnAciertos()+1;
                                context.setNAciertos(nAciertos);
                                context.anotarPuntos(nAciertos, nErrores);
                            } else {
                                System.out.println("Error Anotado");
                                nErrores = context.getnErrores()+1;
                                context.setNErrores(nErrores);
                                context.anotarPuntos(nAciertos, nErrores);
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

