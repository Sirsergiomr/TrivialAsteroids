package com.example.trivialasteroids.Controladores.Entidades;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import com.example.trivialasteroids.Controladores.BasicEngine.EasyEngineV1;

public class GraphicObject {
    public final static int TIPO_MARCIANO = 0;
    public final static int TIPO_ASTEROIDE = 1;
    public final static int TIPO_NAVE = 2;

    private int tipo;
    private Drawable drawable;   //Imagen que dibujaremos
    private double posX;
    private double posY;   //Posición
    private double incX, incY;   //Velocidad desplazamiento
    private int angulo, rotacion;//Angulo y velocidad rotación
    private int ancho, alto;     //Dimensiones de la imagen
    private int radioColision;   //Para determinar colisión
    private int frame=0;		 //Si es -1 no hay animación
    private int NFrame=20;		 // Numero de frame que tiene la animación
    private int anchoSprite=160;
    private int altoSprite=120;
    private Bitmap animacion;
    private int iPasoanimacion=50;
    private Boolean bAnima=false;
    private long lastMilisAnimacion;
    private int drawableID;
    private int tiempoEnPantalla=0;
    private boolean activo=false;
    private String respuestaAsociada;
    private boolean verdadero;
    private OnFinishListener onFinishListener;
    private boolean seguimiento=false;

    //Donde dibujamos el gráfico (usada en view.ivalidate)
    private EasyEngineV1 view;
    // Para determinar el espacio a borrar (view.ivalidate)
    public static final int MAX_VELOCIDAD = 50;


    public GraphicObject(EasyEngineV1 view, int drawableID ){
        this.view = view;
        this.drawable = view.getResources().getDrawable(drawableID,null);
        ancho = drawable.getIntrinsicWidth();
        this.drawableID = drawableID;
        alto = drawable.getIntrinsicHeight();
        radioColision = (alto+ancho)/4;
    }

    public void setVerdadero(boolean verdadero){
        this.verdadero = verdadero;
    }

    public boolean getVerdadero(){
        return verdadero;
    }

    public void setRespuestaAsociada(String respuestaAsociada){
        this.respuestaAsociada = respuestaAsociada;
    }

    public String getRespuestaAsociada(){
        return respuestaAsociada;
    }
    public void anima(Boolean bool)
    {
        bAnima=bool;
        if(bAnima)
        {
            frame=0; // Arrancamos la animacion
            lastMilisAnimacion=System.currentTimeMillis();
        }
    }
    //RENDER DEL OBJETO
    public void DrawGraphic(Canvas canvas){
        canvas.save();
        int x=(int) (posX+ancho/2);
        int y=(int) (posY+alto/2);
        canvas.rotate((float) angulo,(float) x,(float) y);
        drawable.setBounds((int)posX, (int)posY,(int)posX+ancho, (int)posY+alto);
        if(bAnima)
        {
            Rect rctSource=new Rect(frame*anchoSprite,0,frame*anchoSprite+anchoSprite,altoSprite);
            canvas.drawBitmap(animacion, rctSource,
                    new Rect((int)posX-anchoSprite/2,(int)posY-altoSprite/2,(int)posX+anchoSprite/2, (int)posY+altoSprite/2), null);
            long currentMilis=System.currentTimeMillis();
            if(currentMilis-lastMilisAnimacion>iPasoanimacion)
            {
                frame=(frame+1)%NFrame;
                lastMilisAnimacion=currentMilis;
            }
        } else {
            drawable.draw(canvas);
        }

        canvas.restore();
        int rInval = (int) Math.hypot(ancho,alto)/2 + MAX_VELOCIDAD;
        view.invalidate(x-rInval, y-rInval, x+rInval, y+rInval);
        //Log.i("posicion","x:"+x+" y:"+y+" angulo:"+angulo);
    }

    public void incrementaPos(double factor){//
        posX+=incX * factor;

        // Si salimos de la pantalla, corregimos posición

        if(posX<-ancho/2) {

            posX=view.getWidth()-ancho/2;

            if(seguimiento){

                posY = (int) view.getNave().getPosY();

            }

            try {

                onFinishListener.onFinish();

            } catch (Exception e) {

                e.printStackTrace();

            }
        }

        if(posX>view.getWidth()-ancho/2) {posX=-ancho/2;}
        posY+=incY * factor;
        if(posY<-alto/2)
        {posY=view.getHeight()-alto/2;}
        if(posY>view.getHeight()-alto/2)
        {posY=-alto/2;}
//        angulo += rotacion * factor; //Actualizamos ángulo
    }
    public void setPos(double PosX, double PosY){
        posX = PosX;
        //Establece nuestra posición
        posY = PosY;
    }
    public double distancia(GraphicObject g) {
        return Math.hypot(posX-g.posX, posY-g.posY);
    }
    public boolean verificaColision(GraphicObject g) {
        return(distancia(g) < (radioColision+g.radioColision));
    }
    public int getTiempoEnPantalla() {
        return tiempoEnPantalla;
    }
    public void setTiempoEnPantalla(int tiempoEnPantalla) {
        this.tiempoEnPantalla = tiempoEnPantalla;
    }
    public boolean isActivo() {
        return activo;
    }
    public void setActivo(boolean activo) {
        this.activo = activo;
    }
    /**
     * @return the drawable
     */
    public Drawable getDrawable() {
        return drawable;
    }
    /**
     * @param drawable the drawable to set
     */
    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }
    /**
     * @return the posX
     */
    public double getPosX() {
        return posX;
    }
    /**
     * @param posX the posX to set
     */
    public void setPosX(int posX) {
        this.posX = posX;
    }
    /**
     * @return the posY
     */
    public double getPosY() {
        return posY;
    }
    /**
     * @param posY the posY to set
     */
    public void setPosY(int posY) {
        this.posY = posY;
    }
    /**
     * @return the incX
     */
    public double getIncX() {
        return incX;
    }
    /**
     * @param incX the incX to set
     */
    public void setIncX(double incX) {
        this.incX = incX;
    }
    /**
     * @return the incY
     */
    public double getIncY() {
        return incY;
    }
    /**
     * @param incY the incY to set
     */
    public void setIncY(double incY) {
        this.incY = incY;
    }
    /**
     * @return the angulo
     */
    public int getAngulo() {
        return angulo;
    }
    /**
     * @param angulo the angulo to set
     */
    public void setAngulo(int angulo) {
        this.angulo = angulo;
    }
    /**
     * @return the rotacion
     */
    public int getRotacion() {
        return rotacion;
    }
    /**
     * @param rotacion the rotacion to set
     */
    public void setRotacion(int rotacion) {
        this.rotacion = rotacion;
    }
    /**
     * @return the ancho
     */
    public int getAncho() {
        return ancho;
    }
    /**
     * @param ancho the ancho to set
     */
    public void setAncho(int ancho) {
        this.ancho = ancho;
    }
    /**
     * @return the alto
     */
    public int getAlto() {
        return alto;
    }
    /**
     * @param alto the alto to set
     */
    public void setAlto(int alto) {
        this.alto = alto;
    }
    /**
     * @return the radioColision
     */
    public int getRadioColision() {
        return radioColision;
    }
    /**
     * @param radioColision the radioColision to set
     */
    public void setRadioColision(int radioColision) {
        this.radioColision = radioColision;
    }
    /**
     * @return the view
     */
    public EasyEngineV1 getView() {
        return view;
    }
    /**
     * @param view the view to set
     */
    public void setView(EasyEngineV1 view) {
        this.view = view;
    }
    /**
     * @return the maxVelocidad
     */
    public static int getMaxVelocidad() {
        return MAX_VELOCIDAD;
    }
    public Bitmap getAnimacion() {
        return animacion;
    }
    public void setAnimacion(Bitmap animacion) {
        this.animacion = animacion;
    }
    /**
     * @return the frame
     */
    public int getFrame() {
        return frame;
    }
    /**
     * @param frame the frame to set
     */
    public void setFrame(int frame) {
        this.frame = frame;
    }
    /**
     * @return the nFrame
     */
    public int getNFrame() {
        return NFrame;
    }
    /**
     * @param nFrame the nFrame to set
     */
    public void setNFrame(int nFrame) {
        NFrame = nFrame;
    }
    /**
     * @return the anchoSprite
     */
    public int getAnchoSprite() {
        return anchoSprite;
    }
    /**
     * @param anchoSprite the anchoSprite to set
     */
    public void setAnchoSprite(int anchoSprite) {
        this.anchoSprite = anchoSprite;
    }
    /**
     * @return the altoSprite
     */
    public int getAltoSprite() {
        return altoSprite;
    }
    /**
     * @param altoSprite the altoSprite to set
     */
    public void setAltoSprite(int altoSprite) {
        this.altoSprite = altoSprite;
    }
    /**
     * @return the iPasoanimacion
     */
    public int getiPasoanimacion() {
        return iPasoanimacion;
    }
    /**
     * @param iPasoanimacion the iPasoanimacion to set
     */
    public void setiPasoanimacion(int iPasoanimacion) {
        this.iPasoanimacion = iPasoanimacion;
    }
    public boolean seguirJugador(){
        return seguimiento;
    }
    public void setSeguimiento(boolean seguimiento){
        this.seguimiento = seguimiento;
    }
    public void setTipo(int tipo, OnFinishListener onFinishListener) {
        this.tipo = tipo;
        this.onFinishListener = onFinishListener;
    }

    public interface OnFinishListener {
        void onFinish();
    }
}
