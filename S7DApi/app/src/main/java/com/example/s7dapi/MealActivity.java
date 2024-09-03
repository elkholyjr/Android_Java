package com.example.s7dapi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.zip.GZIPOutputStream;

public class MealActivity extends AppCompatActivity implements Response.ErrorListener, Response.Listener<JSONArray> {
    ListView MealLv;
    ProgressBar bar;

    Meal[] meals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal);
        MealLv=findViewById(R.id.mealLV);
        bar=findViewById(R.id.bar);

//        MealLv.setOnItemClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        RequestQueue queue= Volley.newRequestQueue(this);
        String url="https://560057.youcanlearnit.net/services/json/itemsfeed.php";
        JsonArrayRequest request=new JsonArrayRequest(url,this,this);
        queue.add(request);
        bar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        bar.setVisibility(View.INVISIBLE);
        Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onResponse(JSONArray response) {
        bar.setVisibility(View.INVISIBLE);
        Gson gson=new Gson();

        meals = gson.fromJson(response.toString(), Meal[].class);

        MealAdapter adapter=new MealAdapter(this,meals);
        MealLv.setAdapter(adapter);

//        ArrayList<Meal> meals=new ArrayList<>();
//        for (int i = 0; i < response.length(); i++) {
//            try {
//                Meal m=new Meal();
//                m.setItemName(response.getJSONObject(i).getString("itemName"));
//                m.setCategory(response.getJSONObject(i).getString("category"));
//                m.setDescription(response.getJSONObject(i).getString("description"));
//                m.setPrice(response.getJSONObject(i).getString("price"));
//                m.setSort(response.getJSONObject(i).getString("sort"));
//                m.setImage(response.getJSONObject(i).getString("image"));
//
//                meals.add(m);
//
//            } catch (JSONException e) {
//                throw new RuntimeException(e);
//            }
//        }

        //custom layout
        //custom adapter
        //show meals
//        Toast.makeText(this, meals[0].getItemName(), Toast.LENGTH_SHORT).show();


    }

//    @Override
//    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//        Toast.makeText(this, "clicked "+meals[i].getItemName(), Toast.LENGTH_SHORT).show();
//    }

    class MealAdapter extends ArrayAdapter<Meal> implements View.OnClickListener {

        public MealAdapter(@NonNull Context context, Meal[] meals) {
            super(context, 0,meals);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView==null)
                convertView=getLayoutInflater().inflate(R.layout.meal_layout,parent,false);

            ImageView mlPoster=convertView.findViewById(R.id.mlPoster);
            TextView mlName=convertView.findViewById(R.id.mlName);
            TextView mlPrice=convertView.findViewById(R.id.mlPrice);

            mlName.setText(getItem(position).getItemName());
            mlPrice.setText(getItem(position).getPrice());
            Picasso.get().load("https://560057.youcanlearnit.net/services/images/"+getItem(position).getImage())
                    .into(mlPoster);

            //set lsiteners
//            mlName.setOnClickListener(view -> {
//                Toast.makeText(MealActivity.this, getItem(position).getItemName(), Toast.LENGTH_SHORT).show();
//
//            });
            mlName.setTag(getItem(position));
            mlName.setOnClickListener(this);

            return convertView;
        }

        @Override
        public void onClick(View view) {
            Meal m= (Meal) view.getTag();
            Toast.makeText(MealActivity.this, "clicked "+m.getItemName(), Toast.LENGTH_SHORT).show();
        }
    }
}