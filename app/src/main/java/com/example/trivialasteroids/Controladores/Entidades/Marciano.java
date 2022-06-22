package com.example.trivialasteroids.Controladores.Entidades;

import com.example.trivialasteroids.Controladores.BasicEngine.EasyEngineV1;
/**
 * @author sirse
 *
 *
 * */
public class Marciano extends GraphicObject {

    private int height;
    private int VELOCIDAD_MARCIANO;
    private int defaultHeightShip;

    public Marciano(EasyEngineV1 view, int drawableID, int height, int VELOCIDAD_MARCIANO, int defaultHeightShip) {
        super(view, drawableID);
        this.height = height;
        this.VELOCIDAD_MARCIANO = VELOCIDAD_MARCIANO;
        this.defaultHeightShip = defaultHeightShip;
    }
    //La idea es aplicar interfaces, poner metódos especificos, como las Ebras de Generación, para quitar "peso" al Motor, y
    //hacerlo más fácil de entende
    public void movimientoMarciano(){
        if (this.isActivo()) {
            this.incrementaPos(VELOCIDAD_MARCIANO);
            this.setPos(this.getPosX() + this.getIncX(), this.getPosY() + this.getIncY());

            if (this.getPosY() >= height - defaultHeightShip) {
                this.setPosY((int) (this.getPosY() - this.getAlto()));
            }

            if (this.getPosY() <= defaultHeightShip / 2) {
                this.setPosY((int) (this.getPosY() + this.getAlto()));
            }

        }
    }

    public int getVELOCIDAD_MARCIANO() {
        return VELOCIDAD_MARCIANO;
    }

    public void setVELOCIDAD_MARCIANO(int VELOCIDAD_MARCIANO) {
        this.VELOCIDAD_MARCIANO = VELOCIDAD_MARCIANO;
    }
}
