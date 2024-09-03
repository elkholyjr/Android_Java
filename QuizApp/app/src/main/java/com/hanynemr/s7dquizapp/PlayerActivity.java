package com.hanynemr.s7dquizapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;

public class PlayerActivity extends AppCompatActivity implements TextWatcher {

    EditText nameText;
    Button playButton,statButton;
    TextView lastWinner;
    ArrayList<Player> players=new ArrayList<>();
    SharedPreferences pref;
    String name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_player);
        nameText=findViewById(R.id.nameText);
        playButton=findViewById(R.id.playButton);
        statButton=findViewById(R.id.statButton);
        lastWinner=findViewById(R.id.lastWinner);
        nameText.addTextChangedListener(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        pref=getSharedPreferences("players",MODE_PRIVATE);
        String name=pref.getString("winner","");
        if (!name.isEmpty())
            lastWinner.setText("Last winner is "+name);

    }

    @Override
    public void onBackPressed() {
        Optional<Player> player = players.stream().max(Comparator.comparingInt(Player::getScore));
        if (player.isPresent()){
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("winner",player.get().getName());
            editor.apply();
        }
        super.onBackPressed();
    }
    public void play(View view) {
        name=nameText.getText().toString();
        if (players.stream().anyMatch(player -> player.getName().equalsIgnoreCase(name))) {
            Optional<Player> first = players.stream().filter(player -> player.getName().equalsIgnoreCase(name)).findFirst();
            if (first.isPresent()&&first.get().getScore()!=-1){
                Toast.makeText(this, "player got "+first.get().getScore(), Toast.LENGTH_SHORT).show();
                return;
            }else if(first.get().getScore() == -1 && first.get().getTurns()>1){
                Toast.makeText(this, first.get().getName()+" Reached the maximum number of turns.", Toast.LENGTH_SHORT).show();
                return;
            }
        }else{
            players.add(new Player(name,(byte) -1));
        }

        Intent a=new Intent(this, MainActivity.class);
        a.putExtra("name",name);
        startActivityForResult(a,1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Optional<Player> first = players.stream().filter(player -> player.getName().equalsIgnoreCase(name)).findFirst();
        if (requestCode==1000){
            if (data!=null){
                byte score=data.getByteExtra("score", (byte) -1);
                Toast.makeText(this, "return score=" + score, Toast.LENGTH_SHORT).show();
                if (first.isPresent()) {
                    first.get().setScore(score);
                }
                nameText.setText("");
                byte size=0;
                for (Player player : players){
                    if(player.getScore()!=-1) size++;
                    if(size==2)break;
                }
                if(size == 2) statButton.setEnabled(true);
            }else{
                Toast.makeText(this, "incomplete turn", Toast.LENGTH_SHORT).show();
                first.get().turns();
            }
        }
        }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }
    @Override
    public void afterTextChanged(Editable s) {
        if (nameText.length()==0)
            playButton.setEnabled(false);
        else
            playButton.setEnabled(true);


    }
    public void stats(View view) {
        Intent a=new Intent(this, StatActivity.class);
        a.putExtra("players",players);
        startActivity(a);
    }
}