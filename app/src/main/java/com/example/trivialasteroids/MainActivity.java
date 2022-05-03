package com.example.trivialasteroids;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Trace;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trivialasteroids.Controladores.BasicEngine.EasyEngine;

import java.nio.file.attribute.GroupPrincipal;
import java.util.AbstractCollection;

/**
 *
 * @author Sirse
 **/
public class MainActivity extends AppCompatActivity {
    private Button bt_start;
    ProgressBar pg_cargando;
    TextView tv_carga;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//      setContentView(new EasyEngine(this));
        setContentView(R.layout.maininstance_layout);
        bt_start = findViewById(R.id.bt_start);
        pg_cargando = findViewById(R.id.pg_cargando);
        tv_carga = findViewById(R.id.tv_carga);

        bt_start.setOnClickListener(view -> {
            start();
        });
    }
    public void start(){
        pg_cargando.setVisibility(View.VISIBLE);
        bt_start.setVisibility(View.GONE);
        tv_carga.setVisibility(View.VISIBLE);
        setEstadoCarga();
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }
    public void mainPause(){
        synchronized (this){
            try {
                wait(2500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public void setEstadoCarga(){

        int size = 0;
        while (5 > size){
            System.out.println("Generando asteroides");
            tv_carga.setText("Generando asteroides");

            tv_carga.setText("Generando asteroides.");

            tv_carga.setText("Generando asteroides..");

            tv_carga.setText("Generando asteroides...");

            ++size;
        }
        size = 0;
        while(5 > size){

            tv_carga.setText("Generando marcianos.");

            tv_carga.setText("Generando marcianos..");

            tv_carga.setText("Generando marcianos...");
            ++size;
        }

        Intent iniciarPartida = new Intent(this, Juego.class);
        startActivity(iniciarPartida);
    }
    @Override
    protected void onResume() {
        super.onResume();
        pg_cargando.setVisibility(View.GONE);
        bt_start.setVisibility(View.VISIBLE);
        tv_carga.setVisibility(View.GONE);
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }
}