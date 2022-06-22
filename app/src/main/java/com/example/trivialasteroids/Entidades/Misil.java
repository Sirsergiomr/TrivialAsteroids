package com.example.trivialasteroids.Entidades;

import static com.example.trivialasteroids.Controladores.Utils.Funciones.compruebaColision;

import com.example.trivialasteroids.Controladores.BasicEngine.EasyEngineV1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**Es distinta ya que el movimiento es llevado por otro Hilo ControlDisparo llamado por GenerarMisiles
 * Para más info dirigete a GenerarMisiles*/


public class Misil extends GraphicObject{
    private  List<GraphicObject> misiles = Collections.synchronizedList(new ArrayList<>());
    private  List<GraphicObject> asteroides = Collections.synchronizedList(new ArrayList<>());
    private  ArrayList<GraphicObject> marcianos = new ArrayList<>();
    private int nMarcianosSeguiendo ;//Enemigos que persiguen al jugador
    private int nAsteroidesSeguiendo ;//Enemigos que persiguen al jugador
    private int VELOCIDAD_MISIL;
    private int width;
    private int nAciertos = 0;// Aciertos de la pregunta actual
    private int nErrores = 0;//Errores de la pregunta actual

    public Misil(EasyEngineV1 view, int drawableID, int nMarcianosSeguiendo, int nAsteroidesSeguiendo, int VELOCIDAD_MISIL, int width, int nAciertos, int nErrores, List<GraphicObject> misiles, ArrayList<GraphicObject> marcianos, List<GraphicObject> asteroides) {
        super(view, drawableID);
        this.nMarcianosSeguiendo = nMarcianosSeguiendo;
        this.nAsteroidesSeguiendo = nAsteroidesSeguiendo;
        this.VELOCIDAD_MISIL = VELOCIDAD_MISIL;
        this.width = width;
        this.nAciertos = nAciertos;
        this.nErrores = nErrores;
        this.misiles = misiles;
        this.marcianos = marcianos;
        this.asteroides = asteroides;
    }

}
