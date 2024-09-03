package com.example.s7dapi;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements Response.Listener<JSONObject>, Response.ErrorListener {
    EditText movieText;
    TextView resultText;

    ImageView posterImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        movieText=findViewById(R.id.movieText);
        resultText=findViewById(R.id.resultText);
        posterImg=findViewById(R.id.posterImg);
    }

    public void show(View view) {
        //request queue
        RequestQueue queue= Volley.newRequestQueue(this);
        String url="https://www.omdbapi.com/?t="+ movieText.getText().toString()+"&apikey=8de0f502";
        JsonObjectRequest request=new JsonObjectRequest(url,this,this);
        queue.add(request);
    }

    @Override
    public void onResponse(JSONObject response) {
        try {
            if (response.toString().contains("Error")){
                Toast.makeText(this, response.getString("Error"), Toast.LENGTH_SHORT).show();
                return;
            }

            String plot=response.getString("Plot");
            resultText.setText(plot);

            String actors=response.getString("Actors");
            resultText.append("\n"+actors);

            JSONArray ratings = response.getJSONArray("Ratings");
            for (int i = 0; i < ratings.length(); i++) {
                String source = ratings.getJSONObject(i).getString("Source");
                String value = ratings.getJSONObject(i).getString("Value");
                resultText.append("\nSource="+source+"-value="+value);

            }

            //show poster
            String poster=response.getString("Poster");
            if (!poster.equals("N/A")){
                Picasso.get().load(poster).placeholder(R.drawable.placeholder).into(posterImg);
            }
        } catch (JSONException e) {
            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(this, "connection error", Toast.LENGTH_SHORT).show();
    }
}