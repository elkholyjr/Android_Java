package com.example.s7dapi;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity2 extends AppCompatActivity implements Response.Listener<JSONObject>, Response.ErrorListener {

    EditText fromText;
    TextView toText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        fromText=findViewById(R.id.fromText);
        toText=findViewById(R.id.toText);
    }

    public void translate(View view) {
        RequestQueue queue= Volley.newRequestQueue(this);
        String url="https://api.mymemory.translated.net/get?q="+fromText.getText() +"&langpair=en|ar";
        JsonObjectRequest request=new JsonObjectRequest(url,this,this);
        queue.add(request);
    }

    @Override
    public void onResponse(JSONObject response) {
        try {
            String translation = response.getJSONObject("responseData").getString("translatedText");
//            if (!translation.equals("null"))
                toText.setText(!translation.equals("null") ? translation : "no translation");
        } catch (JSONException e) {
            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
    }
}