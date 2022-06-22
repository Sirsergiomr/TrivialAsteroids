package com.example.trivialasteroids.Controladores.Entidades;

import com.example.trivialasteroids.Controladores.BasicEngine.EasyEngineV1;
/**
 * @author sirse
 *
 *
 * */
public class Asteroide extends GraphicObject {

    private int height;
    private int VELOCIDAD_ASTEROIDE;
    private int defaultShipHeight;

    public Asteroide(EasyEngineV1 view, int drawableID, int height, int defaultShipHeight, int VELOCIDAD_ASTEROIDE) {
        super(view, drawableID);
        this.height = height;
        this.VELOCIDAD_ASTEROIDE=VELOCIDAD_ASTEROIDE;
        this.defaultShipHeight = defaultShipHeight;
    }
    public void movimientoAsteroide(){
        if (this.isActivo()) {
            this.incrementaPos(VELOCIDAD_ASTEROIDE);

            if (this.getPosY() >= height - defaultShipHeight) {
                this.setPosY((int) (this.getPosY() - this.getAlto()));
            }
            if (this.getPosY() <= defaultShipHeight / 2) {
                this.setPosY((int) (this.getPosY() + this.getAlto()));
            }
        }
    }
}
