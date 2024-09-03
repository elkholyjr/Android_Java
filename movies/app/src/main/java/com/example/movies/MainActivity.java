package com.example.movies;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements Response.ErrorListener, Response.Listener<JSONObject> {

    EditText filmText;
    TextView result;

    ImageView poster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        filmText=findViewById(R.id.filmText);
        result=findViewById(R.id.result);
        poster=findViewById(R.id.Poster);
    }

    public void show(View view) {
        RequestQueue q = Volley.newRequestQueue(this);
        String url="https://www.omdbapi.com/?t="+filmText.getText().toString()+"&apikey=1f4bdf1c";
        JsonObjectRequest request=new JsonObjectRequest(url,this,this);
        q.add(request);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        Toast.makeText(this, "Error 404", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        try {
            JSONArray ratings=jsonObject.getJSONArray("Ratings");
            for (int i = 0; i <ratings.length(); i++) {
                    result.setText(ratings.getJSONObject(i).getString("Source")+'\n'+ratings.getJSONObject(i).getString("Value"));
            }

            if(!jsonObject.getString("Poster").equals("N/A")){
                Picasso.get().load(jsonObject.getString("Poster")).placeholder(R.drawable.placeholder).into(poster);
            }
        } catch (JSONException e) {
            Toast.makeText(this, "Error 303", Toast.LENGTH_SHORT).show();
        }
    }
}