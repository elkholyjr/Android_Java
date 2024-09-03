package com.hanynemr.s7dguessapp;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.nisrulz.sensey.Sensey;
import com.github.nisrulz.sensey.ShakeDetector;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener, ShakeDetector.ShakeListener {
    TextView rightWrongText,countText;
    Random r=new Random();
    int x;
    byte wrongs;
    boolean gameStarted=false,sound=true;
    ImageView soundIcon;

//    ArrayList<Integer> user=new ArrayList<>();
//    boolean[] user=new boolean[10];// false false false false
    ArrayList<TextView> views=new ArrayList<>();
    TextToSpeech tts;
    SharedPreferences pref;
    Button startButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rightWrongText=findViewById(R.id.rightWrongText);
        countText=findViewById(R.id.countText);
        soundIcon=findViewById(R.id.soundIcon);
        startButton=findViewById(R.id.startButton);
        tts=new TextToSpeech(this,this);
        pref=getSharedPreferences("settings",MODE_PRIVATE);
        sound=pref.getBoolean("sound",true);
        if (sound)
            soundIcon.setImageResource(R.drawable.sound_on);
        else
            soundIcon.setImageResource(R.drawable.sound_off);

        Sensey.getInstance().init(this);
        Sensey.getInstance().startShakeDetection(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("sound",sound);
        editor.apply();
        tts.stop();
        tts.shutdown();
        Sensey.getInstance().stop();
        Sensey.getInstance().stopShakeDetection(this);

    }
    public void start(View view) {
        begin();
    }

    private void begin() {
        gameStarted=true;
        wrongs=0;
        rightWrongText.setText("");
        countText.setText("");
        for (TextView textView : views) {
            textView.setEnabled(true);
        }
        views.clear();
        x = r.nextInt(9)+1;
        Toast.makeText(this, "random number="+x, Toast.LENGTH_SHORT).show();
    }

    public void guess(View view) {
        if (!gameStarted){
            YoYo.with(Techniques.Shake).duration(700).repeat(2).onEnd(animator -> {
                Toast.makeText(this, "please click start", Toast.LENGTH_SHORT).show();
            }).playOn(startButton);
            return;
        }
        TextView tv= (TextView) view;
        YoYo.with(Techniques.Bounce).duration(700).repeat(3).playOn(tv);
        YoYo.with(Techniques.FadeInDown).duration(1500).playOn(rightWrongText);
        YoYo.with(Techniques.FadeInDown).duration(1500).playOn(countText);

        tv.setEnabled(false);
        views.add(tv);
        int number= Integer.parseInt(tv.getText().toString());
        if (sound)
            tts.speak(tv.getText().toString(),TextToSpeech.QUEUE_FLUSH,null,null);

        if (number==x){
            rightWrongText.setText("right");
            gameStarted=false;
        }else{
            rightWrongText.setText("wrong");
            wrongs++;
            countText.setText(String.valueOf(wrongs));
        }
        if (wrongs==3){
            Toast.makeText(this, "game over", Toast.LENGTH_SHORT).show();
            gameStarted=false;
        }

    }

    @Override
    public void onInit(int status) {
        if (status!=TextToSpeech.ERROR){
            tts.setLanguage(new Locale("en"));
            tts.setSpeechRate(0.7f);
            tts.setPitch(0.7f);
        }
    }

    public void changeSound(View view) {
        if (sound){
            soundIcon.setImageResource(R.drawable.sound_off);
        }else{
            soundIcon.setImageResource(R.drawable.sound_on);
        }
        sound=!sound;
    }

    @Override
    public void onShakeDetected() {

    }

    @Override
    public void onShakeStopped() {
        begin();
    }
}