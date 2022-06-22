package com.example.trivialasteroids.Controladores.Utils;

import com.example.trivialasteroids.Controladores.Entidades.GraphicObject;

import java.util.List;

public class Funciones {
    public static double getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
    public static boolean compruebaColision(GraphicObject objetoA, GraphicObject objetoB, List<GraphicObject> listaA, List<GraphicObject> listaB) {
        if (objetoA != null && objetoB != null) {
            boolean colision = objetoA.verificaColision(objetoB);
            if (colision) {
                listaA.remove(objetoA);

                if (listaB != null) {
                    listaB.remove(objetoB);
                }
            }
            return colision;
        } else {
            return false;
        }
    }
}
