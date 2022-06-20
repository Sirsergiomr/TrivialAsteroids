package com.example.trivialasteroids;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.trivialasteroids.Controladores.BasicEngine.EasyEngineV1;
import com.example.trivialasteroids.Controladores.Modelos.Pregunta;

import org.json.JSONObject;

import java.util.ArrayList;

public class Juego extends AppCompatActivity {
    private EasyEngineV1 rocket;
    private Button acribillar, tryAgain, bt_pause, bt_come_back;
    private int vidasExtras = 3;
    private ImageView vida1, vida2, gameOver, iv_win;
    private boolean pause=false, win =false, gameover=false;
    private TextView tv_pause;
    private TextView tv_partida;
    private TextView tv_pregunta;
    private TextView tv_level;
    private ObjectAnimator animator = null;
    private int ndisparos=0;
    private int siguiente_pregunta = 1;//Contador para que pase a la siguiente pregunta;
    private boolean finalLv = false;//Indica si estas en el último nivel o no;

    public void initUi(){
        vida1 = findViewById(R.id.vida1);
        vida2 = findViewById(R.id.vida2);
        gameOver = findViewById(R.id.game_over);
        tryAgain = findViewById(R.id.try_again);
        bt_pause = findViewById(R.id.bt_pause);
        tv_partida = findViewById(R.id.tv_partida);
        tv_pause = findViewById(R.id.tv_pause);
        tv_pregunta = findViewById(R.id.tv_pregunta);
        tv_level = findViewById(R.id.tv_level);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_main);

        initUi();

        try {
            rocket = findViewById(R.id.surfaceSpaceShip);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        iv_win = findViewById(R.id.iv_win);
        bt_come_back = findViewById(R.id.bt_come_back);

        tryAgain.setOnClickListener(view -> {
            reinicia();
        });

        bt_pause.setOnClickListener(view -> {
            ndisparos++;
            System.out.println("DISPARA DESDE UI, DISPAROS TOTALES = "+ ndisparos);
            rocket.dispara();
        });

        bt_come_back.setOnClickListener(view -> {
            onBackPressed();
        });
    }

    public void setPause(){
        if(!pause){
            pause=true;
//            myGameView.getGameLoopThread().pausar();
            tryAgain.setVisibility(View.VISIBLE);
            tv_pause.setVisibility(View.VISIBLE);
        }else{
            tv_pause.setVisibility(View.GONE);
            pause=false;
//            myGameView.getGameLoopThread().reanudar();
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
//        myGameView.getGameLoopThread().reanudar();
    }
    @Override
    protected void onPause() {
        super.onPause();
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
    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
        rocket.stopDrawThread();
        gameover=true;
        win = false;
        gameOver.setVisibility(View.VISIBLE);
//        acribillar.setEnabled(false);
        tryAgain.setVisibility(View.VISIBLE);
        bt_pause.setVisibility(View.GONE);
        rocket.getNave().setActivo(false);
    }

    public void reinicia() {
        gameover=false;
        win = false;
        pause=false;
//        acribillar.setEnabled(true);
        bt_pause.setVisibility(View.VISIBLE);
        tryAgain.setVisibility(View.GONE);
        gameOver.setVisibility(View.GONE);
        vidasExtras = 3;
        vida1.setVisibility(View.VISIBLE);
        vida2.setVisibility(View.VISIBLE);
        tv_pause.setVisibility(View.GONE);
        rocket.reinicio();
//        myGameView.reinicia();
    }

    public void nextLevel(int ActualLevel, ArrayList<JSONObject> niveles){

        System.out.println("NIVEL ACTUAL = "+ActualLevel);
        System.out.println("NIVEL TOTAL = "+(niveles.size()-1));
        if(niveles.size()-1 == ActualLevel){//size = 2; 2-1 == 0 no, 2-1 = 1 si final true
            finalLv = true;
        }else{
            ++ActualLevel;//0 - > 1
            tv_level.setText("LV "+ActualLevel);

            animationLevel();

            rocket.setLevel(niveles.get(ActualLevel), ActualLevel);// niveles.get(1), 1
        }
    }

    public void rebootBasicVariables(){
        rocket.setNAciertos(0);
        rocket.setNErrores(0);
    }

    public void compruebaPartida(int nAciertos, int nErrores, int xAciertos,
                                 int maxErrores, int nPreguntas, int nPAcertadas,
                                 ArrayList<Pregunta> preguntas,
                                 ArrayList<JSONObject> niveles, int ActualLevel){
        if(animator!= null){
            animator.end();
        }
        tv_partida.setVisibility(View.VISIBLE);
        tv_partida.setText("Aciertos = "+nAciertos +"/"+xAciertos+" Errores = "+nErrores+"/"+maxErrores);
        System.out.println("NPREGUNTAS ACERTADAS = "+nPAcertadas);

        if(nAciertos == xAciertos){//10 == 10 a
            System.out.println("NPREGUNTAS ACERTADAS = "+nPAcertadas + "NPREGUNTAS = "+nPreguntas+" NIVEL = "+ActualLevel+" NIVEL TOTAL = "+(niveles.size()-1));
            if(nPreguntas == nPAcertadas && gameover == false && nPAcertadas!=0){
                rebootBasicVariables();
                //Se realiza el cambio de nivel y se activa win
                nextLevel(ActualLevel, niveles);
                if(finalLv){
                    activaWin();
                }
            }

            try {
                ++siguiente_pregunta;
                System.out.println("NPREGUNTA ACTUAL = "+siguiente_pregunta);

                if(siguiente_pregunta <= preguntas.size()){
                    rebootBasicVariables();
                    tv_pregunta.setText(preguntas.get(siguiente_pregunta - 1).getPregunta());
                    rocket.setCurrentQuestion(preguntas.get(siguiente_pregunta - 1));
                    rocket.setNPAcertadas(siguiente_pregunta);
                    tv_partida.setText("¡Cambio de pregunta!");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //Y si las fallas todas en la mima pregunta game over y se reinicia al primer lv
        if(nErrores == maxErrores && !win && nErrores!=0){
            activaGameOver();
        }

        //Variables info
        System.out.println("");
    }

    private void activaWin() {
        win= true;
        gameover = false;
        tv_partida.setVisibility(View.VISIBLE);
//        acribillar.setEnabled(false);
        bt_pause.setVisibility(View.GONE);
        iv_win.setVisibility(View.VISIBLE);
        bt_come_back.setVisibility(View.VISIBLE);
        rocket.getNave().setActivo(false);
        rocket.clearMisiles();
    }

    public void setTv_pregunta (String pregunta){
        tv_pregunta.setText(pregunta);
    }

    public void setTv_level (String level){
        tv_level.setText(level);
    }

    public void animationLevel(){
        // adding the color to be shown
        animator = ObjectAnimator.ofInt(tv_level, "textColor", Color.BLUE, Color.RED, Color.GREEN);

        // duration of one color
        animator.setDuration(500);
        animator.setEvaluator(new ArgbEvaluator());

        // color will be show in reverse manner
        animator.setRepeatCount(Animation.REVERSE);

        // It will be repeated up to infinite time
        animator.setRepeatCount(Animation.INFINITE);
        animator.start();

    }


}