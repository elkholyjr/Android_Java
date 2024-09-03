package com.hanynemr.s7dquizapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Person;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class StatActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, TextWatcher {
    ListView statLV;
    Spinner sortSpinner;
    ArrayList<Player> players,original=new ArrayList<>();
    PlayerAdapter adapter;
    EditText filterText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stat);
        statLV=findViewById(R.id.statLV);
        sortSpinner=findViewById(R.id.sortSpinner);
        filterText=findViewById(R.id.filterText);
        filterText.addTextChangedListener(this);
        players = (ArrayList<Player>) getIntent().getSerializableExtra("players");
        Collections.sort(players, Comparator.comparingInt(Player::getScore).reversed());
        original.addAll(players);
        adapter = new PlayerAdapter(this,players);
        statLV.setAdapter(adapter);
        sortSpinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position==0){
            Collections.sort(players, Comparator.comparingInt(Player::getScore).reversed());
        }else if (position==2){
            Collections.sort(players, Comparator.comparing(Player::getName).reversed());
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.length()==0){
            players.clear();
            players.addAll(original);
        }else{
            List<Player> filter = players.stream()
                    .filter(player -> player.getName().startsWith(filterText.getText().toString()))
                    .collect(Collectors.toList());
            if (!filter.isEmpty()){
                players.clear();
                players.addAll(filter);
            }
        }
        adapter.notifyDataSetChanged();

    }

    class PlayerAdapter extends ArrayAdapter<Player>{
        public PlayerAdapter(@NonNull Context context, List<Player> players) {
            super(context, 0,players);
        }
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null)
                convertView = getLayoutInflater().inflate(R.layout.player_item, parent, false);

            TextView piName = convertView.findViewById(R.id.piName);
            TextView piScore = convertView.findViewById(R.id.piScore);
            if (getItem(position).getScore() == -1) {
                return new View(parent.getContext());
            }
            piName.setText(getItem(position).getName());
            piScore.setText(String.valueOf(getItem(position).getScore()));
            return convertView;
        }
    }

}