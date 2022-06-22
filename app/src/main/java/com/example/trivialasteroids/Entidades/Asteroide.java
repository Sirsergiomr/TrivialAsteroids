package com.example.trivialasteroids.Entidades;

import com.example.trivialasteroids.Controladores.BasicEngine.EasyEngineV1;

public class Asteroide extends GraphicObject {

    private int height;
    private int VELOCIDAD_ASTEROIDE;
    private int defaultShipHeight;
    private OnColisionListener onColisionListener;

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
            try {
                onColisionListener.colision();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public interface OnColisionListener{
        void colision();
    }

    public OnColisionListener getOnColisionListener() {
        return onColisionListener;
    }

    public void setOnColisionListener(OnColisionListener onColisionListener) {
        this.onColisionListener = onColisionListener;
    }

}
