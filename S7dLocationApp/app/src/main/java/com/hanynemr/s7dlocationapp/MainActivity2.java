package com.hanynemr.s7dlocationapp;

import androidx.appcompat.app.AppCompatActivity;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

public class    MainActivity2 extends AppCompatActivity {

    EditText addText,add2Text;
    TextView resText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        addText=findViewById(R.id.addText);
        resText=findViewById(R.id.resText);
        add2Text=findViewById(R.id.add2Text);
    }

    public void show(View view) {
        Geocoder geocoder =new Geocoder(this);
        String address=addText.getText().toString();
        String address2=add2Text.getText().toString();
        try {
            List<Address> addressList = geocoder.getFromLocationName(address, 1);
            List<Address> addressList2 = geocoder.getFromLocationName(address2, 1);
            if(addressList.isEmpty() || addressList2.isEmpty()){
                Toast.makeText(this, "places not found", Toast.LENGTH_SHORT).show();
                return;
            }
            double latitude = addressList.get(0).getLatitude();
            double longitude = addressList.get(0).getLongitude();
            resText.setText("lat="+latitude+"-long="+longitude);
            Location loc1=new Location("");
            loc1.setLatitude(latitude);
            loc1.setLongitude(longitude);


            latitude = addressList2.get(0).getLatitude();
            longitude = addressList2.get(0).getLongitude();
            resText.append("\nlat="+latitude+"-long="+longitude);
            Location loc2=new Location("");
            loc2.setLatitude(latitude);
            loc2.setLongitude(longitude);

            float distance = loc1.distanceTo(loc2);
            distance=distance/1000;
            resText.append("\n"+distance);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }
}