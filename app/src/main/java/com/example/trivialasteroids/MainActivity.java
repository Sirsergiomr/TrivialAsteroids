package com.example.trivialasteroids;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.trivialasteroids.Controladores.BasicEngine.EasyEngine;

import java.nio.file.attribute.GroupPrincipal;

/**
 *
 * @author Sirse
 **/
public class MainActivity extends AppCompatActivity {
    EasyEngine myGameView;
    Button acribillar;
    int vidas = 3;
    ImageView vida1, vida2,gameOver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//      setContentView(new EasyEngine(this));
        setContentView(R.layout.activity_main);

        myGameView = (EasyEngine) findViewById(R.id.surfaceSpaceShip);
        acribillar =  findViewById(R.id.acribillar);
        vida1 = findViewById(R.id.vida1);
        vida2 = findViewById(R.id.vida2);
        gameOver = findViewById(R.id.game_over);
        
        acribillar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myGameView.Dispara();
            }
        });


    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
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

    public void bajarVida(){
        vidas--;
        if(vidas ==1){
            vida1.setVisibility(View.GONE);
        }
        if(vidas == 2){
            vida2.setVisibility(View.GONE);
        }
        if(vidas == 0){
            activaGameOver();
        }
    }
    
    public int getVidas(){
        return vidas;
    }


    public void activaGameOver() {
        gameOver.setVisibility(View.VISIBLE);
        acribillar.setEnabled(false);
    }
}