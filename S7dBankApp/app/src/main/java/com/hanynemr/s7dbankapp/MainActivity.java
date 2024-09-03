package com.hanynemr.s7dbankapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    EditText amountText,yearText,percentText;
    TextView resultText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        amountText=findViewById(R.id.amountText);
        yearText=findViewById(R.id.yearText);
        percentText=findViewById(R.id.percentText);
        resultText=findViewById(R.id.resultText);

    }

    public void calc(View view) {

        if(amountText.getText().toString().isEmpty() ||
                yearText.getText().toString().isEmpty()||
                percentText.getText().toString().isEmpty()){
            Toast.makeText(this, "please fill all data", Toast.LENGTH_SHORT).show();
            return;
        }
        int amount= Integer.parseInt(amountText.getText().toString());
        int year=Integer.parseInt(yearText.getText().toString());
        int percent = Integer.parseInt(percentText.getText().toString());

        int finalAmount= (int) (amount*Math.pow((1+(percent/100.0)),year));
        String line="\namount=" + amount + "-year=" + year + "-percent=" + percent + "-final amount=" + finalAmount;
        if (resultText.getText().toString().contains(line)){
            Toast.makeText(this, "line exists", Toast.LENGTH_SHORT).show();
            String content=resultText.getText().toString();
            StringBuilder sb=new StringBuilder(resultText.getText().toString());
            sb.delete(content.indexOf(line),content.indexOf(line)+line.length());
            sb.insert(0,line);
            resultText.setText(sb);
//            String content=resultText.getText().toString();
//            content=content.replace(line,"");
//            content=line+content;
//            resultText.setText(content);

        }else{
            resultText.append(line);

        }
    }
}