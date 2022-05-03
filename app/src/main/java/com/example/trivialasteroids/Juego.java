package com.example.trivialasteroids;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.trivialasteroids.Controladores.BasicEngine.EasyEngine;
import com.example.trivialasteroids.Controladores.BasicEngine.Functions;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Juego extends AppCompatActivity {
    EasyEngine myGameView;
    Button acribillar, tryAgain, bt_pause, bt_come_back;
    int vidasExtras = 3;
    ImageView vida1, vida2, gameOver, iv_win;
    boolean pause=false, win =false, gameover=false;
    TextView tv_pause;
    TextView tv_partida;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_main);
        myGameView = (EasyEngine) findViewById(R.id.surfaceSpaceShip);
        acribillar = findViewById(R.id.acribillar);
        vida1 = findViewById(R.id.vida1);
        vida2 = findViewById(R.id.vida2);
        gameOver = findViewById(R.id.game_over);
        tryAgain = findViewById(R.id.try_again);
        bt_pause = findViewById(R.id.bt_pause);
        tv_partida = findViewById(R.id.tv_partida);
        tv_pause = findViewById(R.id.tv_pause);

        iv_win = findViewById(R.id.iv_win);
        bt_come_back = findViewById(R.id.bt_come_back);
        acribillar.setOnClickListener(view -> {
            myGameView.Dispara();
        });
        tryAgain.setOnClickListener(view -> {
            //TODO Volver a 0 todos los valores.
            reinicia();
        });
        bt_pause.setOnClickListener(view -> {
            setPause();
        });
        bt_come_back.setOnClickListener(view -> {
            onBackPressed();
        });
    }
    public void setPause(){
        if(!pause){
            pause=true;
            myGameView.getGameLoopThread().pausar();
            tryAgain.setVisibility(View.VISIBLE);
            tv_pause.setVisibility(View.VISIBLE);
        }else{
            tv_pause.setVisibility(View.GONE);
            pause=false;
            myGameView.getGameLoopThread().reanudar();
            tryAgain.setVisibility(View.GONE);
        }
    }
    @Override
    protected void onRestart() {
        Log.i("Estado","juegos.onRestart");
        super.onRestart();
    }


    @Override
    protected void onResume() {
        Log.i("Estado","juegos.onResume");
        super.onResume();
        myGameView.getGameLoopThread().reanudar();
    }
    @Override
    protected void onPause() {
        super.onPause();
        myGameView.getGameLoopThread().pausar();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
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

    public void bajarVida() {
        vidasExtras--;
        if (vidasExtras == 1) {
            vida1.setVisibility(View.INVISIBLE);
        }
        if (vidasExtras == 2) {
            vida2.setVisibility(View.INVISIBLE);
        }
        if (vidasExtras == 0 && win== false) {
            activaGameOver();
        }
    }

    public int getVidas() {
        return vidasExtras;
    }

    public void activaGameOver() {
        gameover=true;
        win = false;
        gameOver.setVisibility(View.VISIBLE);
        acribillar.setEnabled(false);
        tryAgain.setVisibility(View.VISIBLE);
        bt_pause.setVisibility(View.GONE);
        myGameView.getNave().setActivo(false);
    }

    public void reinicia() {
        gameover=false;
        win = false;
        pause=false;
        acribillar.setEnabled(true);
        bt_pause.setVisibility(View.VISIBLE);
        tryAgain.setVisibility(View.GONE);
        gameOver.setVisibility(View.GONE);
        vidasExtras = 3;
        vida1.setVisibility(View.VISIBLE);
        vida2.setVisibility(View.VISIBLE);
        tv_pause.setVisibility(View.GONE);
        myGameView.reinicia();
    }

    public void compruebaPartida(int nAciertos, int nErrores, int xAciertos, int yErrores){
        if(nAciertos == xAciertos && gameover == false){
            tv_partida.setText("Aciertos = "+nAciertos +"/"+xAciertos+" Errores = "+nErrores+"/"+yErrores);
            activaWin();
        }
        if(nErrores == yErrores && win== false){
            activaGameOver();
        }
    }

    private void activaWin() {
        win= true;
        gameover = false;
        tv_partida.setVisibility(View.VISIBLE);
        acribillar.setEnabled(false);
        bt_pause.setVisibility(View.GONE);
        iv_win.setVisibility(View.VISIBLE);
        bt_come_back.setVisibility(View.VISIBLE);
        myGameView.getNave().setActivo(false);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        myGameView.getGameLoopThread().reanudar();
        Functions.Destroy(myGameView.getGameLoopThread());
    }
}