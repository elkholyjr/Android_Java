package com.hanynemr.s7dquizapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    TextView questionText,questionCount,helloText;
    Spinner answerText;
    Button startButton,nextButton;
    List<Question> questions;
    byte score=0,index;
    ArrayList<String> items=new ArrayList<>();
    MediaPlayer player;
    ArrayList<Byte> scoreList=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        questionText=findViewById(R.id.questionText);
        answerText=findViewById(R.id.answerText);
        startButton=findViewById(R.id.startButton);
        nextButton=findViewById(R.id.nextButton);
        questionCount=findViewById(R.id.questionCount);
        helloText=findViewById(R.id.helloText);
        String name = getIntent().getStringExtra("name");
        helloText.setText("hello "+name);
        questions=Arrays.asList(
                new Question("egypt","cairo"),
                new Question("usa","ws"),
                new Question("france","paris"),
                new Question("uk","london"));
    }
    @Override
    public void onBackPressed() {
        if (player!=null)
            player.stop();
        super.onBackPressed();
    }
    public void start(View view) {
        if (player!=null)
            player.stop();
        items.clear();
        Collections.shuffle(questions);
        Collections.addAll(items,"please select",
                "cairo",
                "baghdad",
                "khartoum",
                "gaza",
                "ws",
                "tokyo",
                "london",
                "beijing",
                "tripoli",
                "paris");
        ArrayAdapter adapter=new ArrayAdapter(this
                , android.R.layout.simple_list_item_1,items);
        answerText.setAdapter(adapter);
        index=0;
        score=0;
        nextButton.setEnabled(true);
        answerText.setEnabled(true);
       questionText.setText("what is the capital of "+questions.get(index).getCountry());
       questionCount.setText("Question 1 of "+questions.size());
    }
    public void next(View view) {
        String answer=answerText.getSelectedItem().toString();
        if (answerText.getSelectedItemPosition()==0){
            Toast.makeText(this, "please choose an answer", Toast.LENGTH_SHORT).show();
            return;
        }
        if (answer.equalsIgnoreCase(questions.get(index).getCity())) {
            score++;
            items.remove(answer);
        }
        index++;
        if (index<questions.size()){
            questionText.setText("what is the capital of "+questions.get(index).getCountry());
            questionCount.setText("Question "+(index+1)+" of "+questions.size());
        }else{
            scoreList.add(score);
            nextButton.setEnabled(false);
            if (score>questions.size()/2){
                player = MediaPlayer.create(this,R.raw.success);
                player.start();
            }else{
                player=MediaPlayer.create(this,R.raw.faill);
                player.start();
            }

            Intent a=new Intent(this, PlayerActivity.class);
            a.putExtra("score",score);
            setResult(RESULT_OK,a);// return to the previous screen
            finish();

        }
        answerText.setSelection(0);
        Collections.shuffle(items.subList(1,items.size()));
    }
}