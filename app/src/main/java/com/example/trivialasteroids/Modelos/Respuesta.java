package com.example.trivialasteroids.Modelos;

import java.io.Serializable;

public class Respuesta implements Serializable {

    private String respuestaAsociada;
    private boolean verdadero;

    public Respuesta(String respuestaAsociada, boolean verdadero){
        this.verdadero = verdadero;
        this.respuestaAsociada = respuestaAsociada;
    }
    public String getRespuestaAsociada(){
        return respuestaAsociada;
    }
    public boolean getVerdadero(){
        return verdadero;
    }
}
