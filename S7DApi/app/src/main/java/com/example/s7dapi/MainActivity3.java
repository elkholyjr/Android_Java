package com.example.s7dapi;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MainActivity3 extends AppCompatActivity {

    TextView nameText;

    ExecutorService pool;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        nameText=findViewById(R.id.nameText);
        pool= Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    @Override
    protected void onStop() {
        pool.shutdown();
        super.onStop();
    }

    public void show(View view) {
        pool.execute(() -> {
            Future<String> f1 = pool.submit(() -> {
                try {
                    Thread.sleep(3000);
                    return "done task1";
//                runOnUiThread(()->nameText.append("done task1"));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            Future<String> f2 = pool.submit(() -> {
                try {
                    Thread.sleep(2000);
                    return "done task2";
//                runOnUiThread(()->nameText.append("done task2"));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });

            try {
                String result1=f1.get();
                String result2=f2.get();
                runOnUiThread(()->nameText.setText(result1+" -- "+result2));
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        });

//        Thread thread=new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(5000);
//                    runOnUiThread(()->nameText.setText("done"));
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        });
//        thread.start();



    }
}