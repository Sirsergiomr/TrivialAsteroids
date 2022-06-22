package com.example.trivialasteroids.Controladores;

import com.example.trivialasteroids.Controladores.Utils.Funciones;
import com.example.trivialasteroids.Entidades.Asteroide;
import com.example.trivialasteroids.Entidades.GraphicObject;
import com.example.trivialasteroids.Entidades.Marciano;
import com.example.trivialasteroids.Entidades.Misil;

import java.util.List;

/***
 * Tener en cuenta que no se está utilizando nada de esto, todavía estan en el apartado Hebras de EasyEnginev1
 */
public class ControlDisparo extends Thread {
    private List<GraphicObject> misiles;
    private List<GraphicObject> asteroides;
    private List<GraphicObject> marcianos;
    private int VELOCIDAD_MISIL;
    private int width;
    private int nAsteroidesSeguiendo, nMarcianosSeguiendo, nAciertos, nErrores;

    public ControlDisparo(List<GraphicObject> misiles, List<GraphicObject> asteroides, List<GraphicObject> marcianos,
                          int VELOCIDAD_MISIL, int width, int nAsteroidesSeguiendo, int nMarcianosSeguiendo,
                          int nAciertos, int nErrores) {
        this.misiles = misiles;
        this.asteroides = asteroides;
        this.marcianos = marcianos;
        this.VELOCIDAD_MISIL = VELOCIDAD_MISIL;
        this.width = width;
        this.nAsteroidesSeguiendo = nAsteroidesSeguiendo;
        this.nMarcianosSeguiendo = nMarcianosSeguiendo;
        this.nAciertos = nAciertos;
        this.nErrores = nErrores;
    }

    @Override
    public void run() {
        while (misiles.size() > 0) {
            //Movimiento de los misiles
            for (int i = 0; i < misiles.size(); i++) {
                Misil misil = (Misil) misiles.get(i);
                misil.incrementaPos(VELOCIDAD_MISIL);
                misil.setPos(misil.getPosX() + misil.getIncX(), misil.getPosY());
                if (misil.getPosX() > width) {
                    //Eliminar el misil
                    misiles.remove(i);
                } else {
                    //Eliminar el asteroide
                    for (int j = 0; j < asteroides.size(); j++) {
                        Asteroide asteroide = (Asteroide) asteroides.get(j);
                        if (Funciones.compruebaColision(misil, asteroide, misiles, asteroides)) {
                            if (asteroide.seguirJugador()) {
                                nAsteroidesSeguiendo--;
                                misil.getView().setnAsteroidesSeguiendo(nAsteroidesSeguiendo);
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
                                nMarcianosSeguiendo--;
                                misil.getView().setnMarcianosSeguiendo(nMarcianosSeguiendo);
                            }
                            if (marciano.getVerdadero()) {
                                System.out.println("Acierto anotado");
                                ++nAciertos;
                                misil.getView().anotarPuntos(nAciertos, nErrores);
                            } else {
                                System.out.println("Error Anotado");
                                ++nErrores;
                                misil.getView().anotarPuntos(nAciertos, nErrores);
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

