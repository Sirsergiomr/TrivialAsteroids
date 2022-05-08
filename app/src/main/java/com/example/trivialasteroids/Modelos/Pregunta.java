package com.example.trivialasteroids.Modelos;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Pregunta implements Serializable {
    String pregunta = "null";
    JSONArray respuestas;
    public  Pregunta(JSONObject json){

        try {
            pregunta = json.getString("pregunta");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            respuestas = json.getJSONArray("respuestas");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getPregunta() {
        return pregunta;
    }

    public void setPregunta(String pregunta) {
        this.pregunta = pregunta;
    }

    public JSONArray getRespuestas() {
        return respuestas;
    }

    public void setRespuestas(JSONArray respuestas) {
        this.respuestas = respuestas;
    }
}
